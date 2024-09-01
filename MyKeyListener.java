/**
 * Purpose: Watch arrow keys for input
 * 
 * Owen Colley
 * 5/4/24
 * 
 */

import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

//Detect arrow keys
public class MyKeyListener implements KeyListener {
    private boolean up = false, down = false, left = false, right = false;
    private boolean W = false, A = false, S = false, D = false, E = false;
    private double jumpTime = 0;
    private boolean hoverStop = false;
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                up = true;
                jumpTime = System.currentTimeMillis();
                return;
            case KeyEvent.VK_DOWN:
                down = true;
                return;
            case KeyEvent.VK_LEFT:
                left = true;
                return;
            case KeyEvent.VK_RIGHT:
                right = true;
                return;
            case KeyEvent.VK_W:
                W = true;
                jumpTime = System.currentTimeMillis();
                return;
            case KeyEvent.VK_A:
                A = true;
                return;
            case KeyEvent.VK_S:
                S = true;
                return;
            case KeyEvent.VK_D:
                D = true;
                return;
            case KeyEvent.VK_E:
                E = true;
                return;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                up = false;
                hoverStop = true;
                return;
            case KeyEvent.VK_DOWN:
                down = false;
                hoverStop = true;
                return;
            case KeyEvent.VK_LEFT:
                left = false;
                return;
            case KeyEvent.VK_RIGHT:
                right = false;
                return;
            case KeyEvent.VK_W:
                W = false;
                hoverStop = true;
                return;
            case KeyEvent.VK_A:
                A = false;
                return;
            case KeyEvent.VK_S:
                S = false;
                return;
            case KeyEvent.VK_D:
                D = false;
                return;
            case KeyEvent.VK_E:
                E = false;
                return;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }
    
    public boolean getUp() {
        return up || W;
    }
    
    public boolean getDown() {
        return down || S;
    }
    
    public boolean getLeft() {
        return left || A;
    }
    
    public boolean getRight() {
        return right || D;
    }
    
    public boolean getE() {
        return E;
    }
    
    public double getJumpTime() {
        return jumpTime;
    }
    
    public void setJumpTime0() {
        jumpTime = 0;
    }
    
    public boolean getHoverStop() {
        return hoverStop;
    }
    
    public void setHoverStop( boolean hoverStop ) {
        this.hoverStop = hoverStop;
    }
    
}