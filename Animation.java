/**
 * Purpose: Animate player
 * 
 * Owen Colley
 * 5/4/24
 * 
 */

import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.util.ArrayList;

public class Animation {
    private final int JUMP_LIST_SIZE, WALK_LIST_SIZE;
    private final BufferedImage falling, dashing, standing;
    private final ArrayList<BufferedImage> jumping, walking;
    private final DisplayImage displayImage;
    private final AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
    private int currentFrame = 0, imageWidth;
    private boolean flipped;
    private ImageIcon icon;
    private Graphics2D g;
    private BufferedImage image, flippedImage;
    
    public Animation(BufferedImage falling, BufferedImage dashing, ArrayList<BufferedImage> jumping, ArrayList<BufferedImage> walking, BufferedImage standing, DisplayImage displayImage) {
        this.falling = falling;
        this.dashing = dashing;
        this.jumping = jumping;
        this.walking = walking;
        this.standing = standing;
        this.displayImage = displayImage;
        JUMP_LIST_SIZE = jumping.size();
        WALK_LIST_SIZE = walking.size();
        image = displayImage.getPlayer();
    }
    
    public boolean getFlipped() {
        return flipped;
    }
    
    public void setFlipped(boolean isFlipped) {
        if ( flipped == isFlipped ) // return if redundant
            return;
        
        flipped = isFlipped;
        if(flipped)
            flipPlayer();
        else
            undoFlip();
    }
    
    public void falling() {
        if ( image == falling ) // return if redundant
            return;
        
        image = falling;
        if(flipped)
            flipPlayer();
        else
            displayImage.setPlayer(image);
    }
    
    public void dashing() {
        if ( image == dashing ) // return if redundant
            return;
        
        image = dashing;
        if(flipped)
            flipPlayer();
        else
            displayImage.setPlayer(image);
    }
    
    public void jumping() {
        if(currentFrame >= JUMP_LIST_SIZE)
            currentFrame = 0;
        image = jumping.get(currentFrame);
        currentFrame++;
        if(flipped)
            flipPlayer();
        else
            displayImage.setPlayer(image);
    }
    
    public void walking() {
        if(currentFrame >= WALK_LIST_SIZE)
            currentFrame = 0;
        image = walking.get(currentFrame);
        currentFrame++;
        if(flipped)
            flipPlayer();
        else
            displayImage.setPlayer(image);
    }
    
    public void standing() {
        if ( image == standing ) // return if redundant
            return;
        
        image = standing;
        if(flipped)
            flipPlayer();
        else
            displayImage.setPlayer(image);
    }
    
    public void flipPlayer() {
        imageWidth = image.getWidth();
        
        transform.translate(-imageWidth, 0);
        
        flippedImage = new BufferedImage(imageWidth, image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        g = flippedImage.createGraphics();
        g.drawImage(image, transform, null);
        
        displayImage.setPlayer(flippedImage);
        transform.translate(imageWidth, 0);
    }
    
    public void undoFlip() {
        displayImage.setPlayer(image);
    }
    
}