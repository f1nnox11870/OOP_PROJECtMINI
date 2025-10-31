package entity;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NPC extends Entity {
    GamePanel gp;

    public Map<String, Integer> shopItems;
    public Map<String, Integer> buyPrices;

    public String npcName;
    public String npcType; // "SHOP", "UPGRADE", "DIALOGUE"

    public NPC(GamePanel gp, int worldX, int worldY, String npcType, String name) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.npcType = npcType;
        this.npcName = name;

        speed = 0;
        direction = "down";

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;

        if (npcType.equals("SHOP")) {
            setupShop();
        }

        getNPCImage();
    }

    public void setupShop() {
        shopItems = new HashMap<>();
        buyPrices = new HashMap<>();

        shopItems.put("Wheat Seed", 10);
        shopItems.put("Corn Seed", 15);
        shopItems.put("Tomato Seed", 20);
        shopItems.put("Carrot Seed", 12);
        shopItems.put("Strawberry Seed", 18); // <<<< เพิ่มใหม่
        shopItems.put("Pumpkin Seed", 25);    // <<<< เพิ่มใหม่
        shopItems.put("Cherry Seed", 22);

        // <<<< ใช้ราคาพื้นฐาน (จะคูณด้วย multiplier ใน GamePanel)
        buyPrices.put("Wood", 5);
        buyPrices.put("Wheat", 25);
        buyPrices.put("Corn", 30);
        buyPrices.put("Tomato", 40);
        buyPrices.put("Carrot", 28);
        buyPrices.put("Strawberry", 35); // <<<< เพิ่มใหม่
        buyPrices.put("Pumpkin", 50);    // <<<< เพิ่มใหม่
        buyPrices.put("Cherry", 38);   // <<<< เพิ่มใหม่
    }

    public void getNPCImage() {
        try {
            // <<<< ถ้าเป็น UPGRADE NPC ใช้รูปอื่น
            if (npcType.equals("UPGRADE")) {
                down1 = ImageIO.read(getClass().getResourceAsStream("/npc/wizard_down_1.png"));
                down2 = ImageIO.read(getClass().getResourceAsStream("/npc/wizard_down_2.png"));
            } else {
                down1 = ImageIO.read(getClass().getResourceAsStream("/npc/merchant_down_1.png"));
                down2 = ImageIO.read(getClass().getResourceAsStream("/npc/merchant_down_2.png"));
            }

            up1 = down1;
            up2 = down2;
            left1 = down1;
            left2 = down2;
            right1 = down1;
            right2 = down2;
        } catch (IOException e) {
            System.out.println("Warning: NPC image not found, using placeholder");
            // สร้าง placeholder image ถ้าไม่มีรูป
            down1 = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            down2 = down1;
            up1 = down1;
            up2 = down1;
            left1 = down1;
            left2 = down1;
            right1 = down1;
            right2 = down1;
        }
    }

    public void update() {
        spriteCounter++;
        if (spriteCounter > 20) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            switch (direction) {
                case "up":
                    image = (spriteNum == 1) ? up1 : up2;
                    break;
                case "down":
                    image = (spriteNum == 1) ? down1 : down2;
                    break;
                case "left":
                    image = (spriteNum == 1) ? left1 : left2;
                    break;
                case "right":
                    image = (spriteNum == 1) ? right1 : right2;
                    break;
            }

            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}