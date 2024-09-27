/**
 * Purpose: Main Game Loop
 * 
 * Owen Colley
 * 5/4/24
 * 
 */

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.*;
import javax.swing.*;
import javax.swing.JLayeredPane;
import java.util.*;
import java.awt.geom.AffineTransform;

public class MyTimerTask extends TimerTask {
    private DisplayImage displayImage;
    private MyKeyListener listener;
    private Animation animator;
    private double playerX, playerY;
    private double lastPlayerX, lastPlayerY;
    private long time, changeTime1 = 0, changeTime2 = 0, dashTime = 0;
    private int velocityX = 0, velocityY = 0;
    private double groundedTime = 0;
    private boolean hovering = false;
    private int jumps = 0;
    private int dashes = 0;
    private int x, y;
    private int maxVel;
    private int stepHeight;
    private int color;
    private int count;
    private int temp1, temp2;
    private double temp3, temp4;
    private double stepAmountX, stepAmountY;
    private double beforeStepPlayerX, beforeStepPlayerY;
    private boolean breakMove;
    private boolean hitTop, hitBottom, hitLeft, hitRight;
    private boolean stepping, undoStep;
    private int dashX, dashY;
    private final int DELAY = 100; // milliseconds between frames
    private final int GROUND_COLOR = new Color( 204, 236, 255 ).getRGB(); // ground hitbox
    private final int HURT_COLOR = new Color( 201, 59, 49 ).getRGB(); // enemy hitbox
    private final int MAX_DASHES = 2; // number of dashes
    private final int DASH_DURATION = 250; // length of dash time
    private final int DASH_STRENGTH = 60; // strength of dash
    private final int MAX_JUMPS = 2; // number of jumps
    private final int JUMP_BUFFERING = 200; // milliseconds jump buffering
    private final int COYOTE_TIME = 200; // milliseconds coyote time
    private final int JUMP_POWER = 27; // jump height
    private final int WALL_JUMP_POWER_X = 40; // strength of wall jumps
    private final int WALL_JUMP_POWER_Y = 60;
    private final int HOVER_POWER = 4; // hover strength
    private final int GRAVITY = -6; // gravity
    private final int SPEED = 20; // movement speed
    private final int RESPAWN_X = 50; // respawn points
    private final int RESPAWN_Y = 50;
    private final int MAX_STEP_HEIGHT = 40; // slope climbing
    private final int SPRITE_SPEED = 20; // animation update speed
    
    public MyTimerTask( DisplayImage displayImage, MyKeyListener listener, Animation animator ) {
        this.displayImage = displayImage;
        this.listener = listener;
        this.animator = animator;
        playerX = displayImage.getPlayerLocX();
        playerY = -displayImage.getPlayerLocY();
    }
    
    @Override
    public void run() {
        time = System.currentTimeMillis();
        
        // walk
        if ( time - dashTime > DASH_DURATION )
            dashTime = 0;
        if ( dashTime <= 0 )
            velocityX = 0;
        if ( dashTime <= 0 && listener.getRight() && !listener.getLeft() ) { // if going right only
            velocityX += SPEED;
            animator.setFlipped( false );
        } else if ( dashTime <= 0 && listener.getLeft() && !listener.getRight() ) { // if going left only
            velocityX -= SPEED;
            animator.setFlipped( true );
        }
        
        // jump
        if ( time - groundedTime > COYOTE_TIME ) // coyote time
            jumps = Math.min( jumps, MAX_JUMPS - 1 );
        if ( time - listener.getJumpTime() > JUMP_BUFFERING ) // jump buffering
            listener.setJumpTime0();
        if ( listener.getJumpTime() > 0 && hitLeft && listener.getLeft() && !stepLegal() && !stepping && !undoStep ) {
            hovering = false;
            jumps = Math.max( jumps, MAX_JUMPS - 1 );
            
            velocityX = WALL_JUMP_POWER_X;
            velocityY = WALL_JUMP_POWER_Y;
        } else if ( listener.getJumpTime() > 0 && hitRight && listener.getRight() && !stepLegal() && !stepping && !undoStep ) {
            hovering = false;
            listener.setJumpTime0();
            jumps = Math.max( jumps, MAX_JUMPS - 1 );
            
            velocityX = -WALL_JUMP_POWER_X;
            velocityY = WALL_JUMP_POWER_Y;
        } else if ( listener.getJumpTime() > 0 && jumps > 0 ) { // jump
            hovering = jumps == MAX_JUMPS; // only hover if ground jump
            listener.setJumpTime0();
            jumps--;
            
            velocityY = JUMP_POWER;
        }
        
        // dash
        if ( !listener.getE() || dashes <= 0 ) {
            
        } else if ( listener.getRight() && listener.getUp() ) {
            dashX = ( int )( DASH_STRENGTH * 0.7 );
            dashY = ( int )( DASH_STRENGTH * 0.7 );
        } else if ( listener.getRight() && listener.getDown() ) {
            dashX = ( int )( DASH_STRENGTH * 0.7 );
            dashY = ( int )( -DASH_STRENGTH * 0.7 );
        } else if ( listener.getLeft() && listener.getDown() ) {
            dashX = ( int )( -DASH_STRENGTH * 0.7 );
            dashY = ( int )( -DASH_STRENGTH * 0.7 );
        } else if ( listener.getLeft() && listener.getUp() ) {
            dashX = ( int )( -DASH_STRENGTH * 0.7 );
            dashY = ( int )( DASH_STRENGTH * 0.7 );
        } else if ( listener.getLeft() ) {
            dashX = -DASH_STRENGTH;
            dashY = 0;
        } else if ( listener.getRight() ) {
            dashX = DASH_STRENGTH;
            dashY = 0;
        } else if ( listener.getUp() ) {
            dashY = DASH_STRENGTH;
            dashX = 0;
        } else if ( listener.getDown() ) {
            dashY = -DASH_STRENGTH;
            dashX = 0;
        } else if ( !animator.getFlipped() ) {
            dashX = DASH_STRENGTH;
            dashY = 0;
        } else {
            dashX = -DASH_STRENGTH;
            dashY = 0;
        }
        
        if ( listener.getE() && dashes > 0 ) {
            dashes--;
            dashTime = time;
            hovering = false;
        }
        
        if ( dashX == 0 && dashY == 0 ) {
            dashTime = 0;
        } else if ( dashTime > 0 ) {
            velocityX = dashX;
            velocityY = dashY;
        }
        
        hovering = hovering && !listener.getHoverStop(); // stop hovering if let go
        listener.setHoverStop( false );
        
        if ( hovering ) // change velocity if still hovering
            velocityY += HOVER_POWER; // adjust velocity for gravity
        if ( dashTime <= 0 )
            velocityY += GRAVITY;
        
        hitTop = false;
        hitBottom = false;
        hitLeft = false;
        hitRight = false;
        
        beforeStepPlayerX = playerX;
        lastPlayerX = playerX;
        lastPlayerY = playerY;
        breakMove = false;
        stepping = false;
        undoStep = false;
        
        updatePosition();
        
        if ( velocityX <= Math.abs( SPEED ) )
            velocityX = 0;
        else if ( velocityX > 0 ) {
            velocityX -= SPEED;
            velocityX -= 3;
        } else if ( velocityX < 0 ) {
            velocityX += SPEED;
            velocityX += 3;
        }
        
        displayImage.moveBackground( -( int )playerX, ( int )playerY );
        updateAnimation();
        resetVelocities();
        
    }
    
    public int getDelay() {
        return DELAY;
    }
    
    // kill velocity upon hitting wall
    public void resetVelocities() {
        if ( hitRight && !stepping ) {
            velocityX = 0;
            velocityY = -GRAVITY / 3;
            dashX = 0;
        } else if ( hitLeft && !stepping ) {
            velocityX = 0;
            velocityY = -GRAVITY / 3;
            dashX = 0;
        } if ( hitTop ) {
            velocityY = 0;
            dashY = 0;
        } else if ( hitBottom ) {
            // reset grounded stats
            velocityY = 0;
            jumps = MAX_JUMPS;
            dashes = MAX_DASHES;
            groundedTime = System.currentTimeMillis();
            hovering = false;
            dashY = 0;
        }
    }
    
    // update the player sprite depending on the player movement that frame
    public void updateAnimation() {
        if ( dashTime > 0 ) {
            animator.dashing();
        } else if ( ( int )( playerY - lastPlayerY ) < 0 ) { // if falling
            animator.falling();
        } else if ( ( int )( playerY - lastPlayerY ) > 0 ) { // if jumping
            if ( velocityY != 0 && time - changeTime1 > SPRITE_SPEED / velocityY ) {
                changeTime1 = time;
                animator.jumping();
            }
        } else if ( ( int )( playerX - lastPlayerX ) != 0 ) { // if walking
            if ( velocityX != 0 && time-changeTime2>SPRITE_SPEED / velocityX ) {
                changeTime2 = time;
                animator.walking();
            }
        } else // if standing
            animator.standing();
    }
    
    // reset all player stats related to respawn
    public void respawn() {
        System.out.println( "You died!" );
        playerX = RESPAWN_X;
        playerY = RESPAWN_Y;
        velocityX = 0;
        velocityY = 0;
        jumps = 0;
        listener.setJumpTime0();
        hovering = false;
        dashTime = 0;
        breakMove = true;
    }
    
    // find step height and return whether a step may be possible
    public boolean stepLegal() {
        stepHeight = 0;
        for ( int y = -( int )playerY + displayImage.getPlayerLocY() + displayImage.getPlayer().getHeight() - 1; y >= -( int )playerY + displayImage.getPlayerLocY(); y-- ) {
            if ( displayImage.getBackground().getRGB( x, y ) != GROUND_COLOR ) // if danger respawn
                break;
            stepHeight++;
        }
        
        return ( !undoStep // don't try to step if reverting one
                && ( !stepping || beforeStepPlayerY != playerY ) // step hasn't failed
                && velocityX != 0 // it's still moving horizontally
                && stepHeight <= MAX_STEP_HEIGHT // step height isn't too large
                && stepHeight > 0 ); // step height is big enough
    }
    
    // step up character, revert if it fails
    public void step() {
        temp1 = velocityX;
        temp2 = velocityY;
        temp3 = playerX;
        temp4 = playerY;
        
        velocityY = stepHeight;
        velocityX -= playerX - beforeStepPlayerX; // subtract distance traveled this frame already
        
        beforeStepPlayerX = playerX;
        beforeStepPlayerY = playerY;
        stepping = true;

        updatePosition();
        
        if ( playerY == beforeStepPlayerY ) {
            velocityY = temp2;
            playerX = temp3;
            playerY = temp4;
            undoStep = true;
            updatePosition();
        }
        
        velocityX = temp1;
        velocityY = temp2;
        breakMove = true;
    }
    
    // check every pixel on left of player for ground, possible step, or danger
    public boolean hitLeft() {
        x = ( int )playerX + displayImage.getPlayerLocX() - 1; // left of character
        if ( x < 0 ) { // respawn if off screen
            respawn();
            return false;
        }
        for ( int y = -( int )playerY + displayImage.getPlayerLocY() + displayImage.getPlayer().getHeight() - 1; y >= -( int )playerY + displayImage.getPlayerLocY(); y -= 4 ) {
            color = displayImage.getBackground().getRGB( x, y );
            if ( color == HURT_COLOR ) { // if danger respawn
                respawn();
                return false;
            } if ( color == GROUND_COLOR ) { // if ground move out of it
                if ( stepLegal() )
                    step();
                
                hitLeft = true;
                return true;
            }
        } return false;
    }
        
    // check every pixel on right of player for ground, possible step, or danger
    public boolean hitRight() {
        x = ( int )playerX + displayImage.getPlayerLocX() + displayImage.getPlayer().getWidth() + 1; // right of character
        if ( x >= displayImage.getBackground().getWidth() ) { // respawn if off screen
            respawn();
            return false;
        }
        for ( int y = -( int )playerY + displayImage.getPlayerLocY() + displayImage.getPlayer().getHeight() - 1; y >= -( int )playerY + displayImage.getPlayerLocY(); y -= 4 ) {
            color = displayImage.getBackground().getRGB( x, y );
            if ( color == HURT_COLOR ) { // if danger respawn
                respawn();
                return false;
            } if ( color == GROUND_COLOR ) { // if ground move out of it
                if ( stepLegal() )
                    step();
                
                hitRight = true;
                return true;
            }
        } return false;
    }
    
    // check every pixel on top of player for ground or danger
    public boolean hitTop() {
        y = -( int )playerY + displayImage.getPlayerLocY() - 1; // top of character
        if ( y < 0 ) { // respawn if off screen
            respawn();
            return false;
        }
        for ( int x = ( int )playerX + displayImage.getPlayerLocX(); x < ( int )playerX + displayImage.getPlayerLocX() + displayImage.getPlayer().getWidth(); x += 4 ) {
            color = displayImage.getBackground().getRGB( x, y );
            if ( color == HURT_COLOR ) { // if danger respawn
                respawn();
                return false;
            } if ( color == GROUND_COLOR ) { // if ground move out of it
                hitTop = true;
                return true;
            }
        } return false;
    }
    
    // check every pixel on top of player for ground or danger
    public boolean hitBottom() {
        y = -( int )playerY + displayImage.getPlayerLocY() + displayImage.getPlayer().getHeight() + 1; // bottom of character
        if ( y >= displayImage.getBackground().getHeight() ) { // respawn if off screen
            respawn();
            return false;
        }
        for ( int x = ( int )playerX + displayImage.getPlayerLocX(); x < ( int )playerX + displayImage.getPlayerLocX() + displayImage.getPlayer().getWidth(); x += 4 ) {
            color = displayImage.getBackground().getRGB( x, y );
            if ( color == HURT_COLOR ) { // if danger respawn
                respawn();
                return false;
            } if ( color == GROUND_COLOR ) { // if ground move out of it
                hitBottom = true;
                return true;
            }
        } return false;
    }
    
    // checks every instance between start and end point for ground, possible steps, or danger
    public void updatePosition() {
        maxVel = Math.max( Math.abs( velocityX ), Math.abs( velocityY ) );
        stepAmountX = ( double )velocityX / maxVel;
        stepAmountY = ( double )velocityY / maxVel;
        count = 0;
        
        if ( velocityX > 0 && velocityY > 0 ) { // Moving diagonally up-right
            while ( count < maxVel ) {
                playerX += stepAmountX;
                playerY += stepAmountY;
                if ( hitTop() )
                    playerY -= stepAmountY;
                if ( hitRight() )
                    playerX -= stepAmountX;
                count++;
                
                if ( breakMove )
                    return;
            }
        } else if ( velocityX > 0 && velocityY < 0 ) { // Moving diagonally down-right
            while ( count < maxVel ) {
                playerX += stepAmountX;
                playerY += stepAmountY;
                if ( hitBottom() )
                    playerY -= stepAmountY;
                if ( hitRight() )
                    playerX -= stepAmountX;
                count++;
                
                if ( breakMove )
                    return;
            }
        } else if ( velocityX < 0 && velocityY > 0 ) { // Moving diagonally up-left
            while ( count < maxVel ) {
                playerX += stepAmountX;
                playerY += stepAmountY;
                if ( hitTop() )
                    playerY -= stepAmountY;
                if ( hitLeft() )
                    playerX -= stepAmountX;
                count++;
                
                if ( breakMove )
                    return;
            }
        } else if ( velocityX < 0 && velocityY < 0 ) { // Moving diagonally down-left
            while ( count < maxVel ) {
                playerX += stepAmountX;
                playerY += stepAmountY;
                if ( hitBottom() )
                    playerY -= stepAmountY;
                if ( hitLeft() )
                    playerX -= stepAmountX;
                count++;
                
                if ( breakMove )
                    return;
            }
        } else if ( velocityX > 0 ) { // Moving right
            while ( count < maxVel ) {
                playerX += stepAmountX;
                if ( hitRight() )
                    playerX -= stepAmountX;
                count++;
                
                if ( breakMove )
                    return;
            }
        } else if ( velocityX < 0 ) { // Moving left
            while ( count < maxVel ) {
                playerX += stepAmountX;
                if ( hitLeft() )
                    playerX -= stepAmountX;
                count++;
                
                if ( breakMove )
                    return;
            }
        } else if ( velocityY > 0 ) { // Moving up
            while ( count < maxVel ) {
                playerY += stepAmountY;
                if ( hitTop() )
                    playerY -= stepAmountY;
                count++;
                
                if ( breakMove )
                    return;
            }
        } else if ( velocityY < 0 ) { // Moving down
            while ( count < maxVel ) {
                playerY += stepAmountY;
                if ( hitBottom() )
                    playerY -= stepAmountY;
                count++;
                
                if ( breakMove )
                    return;
            }
        } else { // Stationary
            
        }
        
    }
    
}
