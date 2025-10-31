package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

// <<<< เปลี่ยนเป็น abstract class
public abstract class Entity {
    public int worldX, worldY;
    public int speed;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;

    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2,
            attackLeft1, attackLeft2, attackRight1, attackRight2;

    public BufferedImage hoeUp1, hoeUp2, hoeDown1, hoeDown2,
            hoeLeft1, hoeLeft2, hoeRight1, hoeRight2;

    public Rectangle solidArea;
    public boolean collosiOn = false;
    public boolean attacking = false;
    public boolean hoeing = false;

    // <<<< Abstract methods - บังคับให้ subclass ต้อง implement
    public abstract void update();
    public abstract void draw(Graphics2D g2);
}