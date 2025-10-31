package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean interactPressed = false;
    public boolean attackPressed = false;
    public boolean hoePressed = false;
    public boolean harvestPressed = false;
    public boolean plantPressed = false;
    public boolean inventoryPressed = false;
    public boolean upgradePressed = false;
    public boolean tabPressed = false;
    public boolean enterPressed = false;
    public boolean escPressed = false;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W){
            upPressed = true;
        }
        if(code == KeyEvent.VK_A){
            leftPressed = true;
        }
        if(code == KeyEvent.VK_S){
            downPressed = true;
        }
        if(code == KeyEvent.VK_D){
            rightPressed = true;
        }
        if(code == KeyEvent.VK_1){
            attackPressed = true;
        }
        if(code == KeyEvent.VK_2){
            hoePressed = true;
        }
        if(code == KeyEvent.VK_MINUS){
            harvestPressed = true;
        }
        if(code == KeyEvent.VK_EQUALS){
            plantPressed = true;
        }
        if(code == KeyEvent.VK_F){
            interactPressed = true;
        }
        if(code == KeyEvent.VK_E){
            inventoryPressed = true;
        }
        if(code == KeyEvent.VK_U){
            upgradePressed = true;
        }
        if(code == KeyEvent.VK_TAB){
            tabPressed = true;
        }
        if(code == KeyEvent.VK_ENTER){
            enterPressed = true;
        }
        if(code == KeyEvent.VK_ESCAPE){
            escPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W){
            upPressed = false;
        }
        if(code == KeyEvent.VK_A){
            leftPressed = false;
        }
        if(code == KeyEvent.VK_S){
            downPressed = false;
        }
        if(code == KeyEvent.VK_D){
            rightPressed = false;
        }
        if(code == KeyEvent.VK_1){
            attackPressed = false;
        }
        if(code == KeyEvent.VK_2){
            hoePressed = false;
        }
        if(code == KeyEvent.VK_MINUS){
            harvestPressed = false;
        }
        if(code == KeyEvent.VK_EQUALS){
            plantPressed = false;
        }
        if(code == KeyEvent.VK_F){
            interactPressed = false;
        }
        if(code == KeyEvent.VK_E){
            inventoryPressed = false;
        }
        if(code == KeyEvent.VK_U){
            upgradePressed = false;
        }
        if(code == KeyEvent.VK_TAB){
            tabPressed = false;
        }
        if(code == KeyEvent.VK_ENTER){
            enterPressed = false;
        }
        if(code == KeyEvent.VK_ESCAPE){
            escPressed = false;
        }
    }
}