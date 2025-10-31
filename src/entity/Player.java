package entity;
import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;

    public Map<String, Integer> inventory;
    public Map<String, BufferedImage> itemImages;
    public int money = 1000;
    public final int screenX;
    public final int screenY;

    // <<<< เพิ่ม PlayerStats
    public PlayerStats stats;

    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.MaxScreenWidth/2 - (gp.tileSize/2);
        screenY = gp.MaxScreenHeight/2 - (gp.tileSize/2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;

        inventory = new HashMap<>();
        itemImages = new HashMap<>();
        stats = new PlayerStats(); // <<<< สร้าง PlayerStats

        setDefaultValues();
        getPlayerImage();
        loadItemImages();
    }

    public void addItemToInventory(String itemName, int amount) {
        if (inventory.containsKey(itemName)) {
            inventory.put(itemName, inventory.get(itemName) + amount);
        } else {
            inventory.put(itemName, amount);
        }
        System.out.println(itemName + " collected! Current count: " + inventory.get(itemName));
    }

    public void setDefaultValues(){
        worldX = gp.tileSize * 8;
        worldY = gp.tileSize * 13;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage(){
        try{
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/up_2.png"));
            down1= ImageIO.read(getClass().getResourceAsStream("/player/down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/right_2.png"));

            attackDown1 = ImageIO.read(getClass().getResourceAsStream("/player/attack_down_1.png"));
            attackDown2 = ImageIO.read(getClass().getResourceAsStream("/player/attack_down_2.png"));
            attackUp1 = ImageIO.read(getClass().getResourceAsStream("/player/attack_up_1.png"));
            attackUp2 = ImageIO.read(getClass().getResourceAsStream("/player/attack_up_2.png"));
            attackLeft1 = ImageIO.read(getClass().getResourceAsStream("/player/attack_left_1.png"));
            attackLeft2 = ImageIO.read(getClass().getResourceAsStream("/player/attack_left_2.png"));
            attackRight1 = ImageIO.read(getClass().getResourceAsStream("/player/attack_right_1.png"));
            attackRight2 = ImageIO.read(getClass().getResourceAsStream("/player/attack_right_2.png"));

            hoeUp1 = ImageIO.read(getClass().getResourceAsStream("/player/hoe_up_1.png"));
            hoeUp2 = ImageIO.read(getClass().getResourceAsStream("/player/hoe_up_2.png"));
            hoeDown1 = ImageIO.read(getClass().getResourceAsStream("/player/hoe_down_1.png"));
            hoeDown2 = ImageIO.read(getClass().getResourceAsStream("/player/hoe_down_2.png"));
            hoeLeft1 = ImageIO.read(getClass().getResourceAsStream("/player/hoe_left_1.png"));
            hoeLeft2 = ImageIO.read(getClass().getResourceAsStream("/player/hoe_left_2.png"));
            hoeRight1 = ImageIO.read(getClass().getResourceAsStream("/player/hoe_right_1.png"));
            hoeRight2 = ImageIO.read(getClass().getResourceAsStream("/player/hoe_right_2.png"));

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadItemImages(){
        try{
            itemImages.put("Wood", ImageIO.read(getClass().getResourceAsStream("/items/wood.png")));
            itemImages.put("Wheat", ImageIO.read(getClass().getResourceAsStream("/items/wheat.png")));
            itemImages.put("Wheat Seed", ImageIO.read(getClass().getResourceAsStream("/items/wheat_seed.png")));
            itemImages.put("Corn", ImageIO.read(getClass().getResourceAsStream("/items/corn.png")));
            itemImages.put("Corn Seed", ImageIO.read(getClass().getResourceAsStream("/items/corn_seed.png")));
            itemImages.put("Tomato", ImageIO.read(getClass().getResourceAsStream("/items/tomato.png")));
            itemImages.put("Tomato Seed", ImageIO.read(getClass().getResourceAsStream("/items/tomato_seed.png")));
            itemImages.put("Carrot", ImageIO.read(getClass().getResourceAsStream("/items/carrot.png")));
            itemImages.put("Carrot Seed", ImageIO.read(getClass().getResourceAsStream("/items/carrot_seed.png")));
            itemImages.put("Pumpkin", ImageIO.read(getClass().getResourceAsStream("/items/pumpkin.png")));
            itemImages.put("Pumpkin Seed", ImageIO.read(getClass().getResourceAsStream("/items/pumpkin_seed.png")));
            itemImages.put("Strawberry", ImageIO.read(getClass().getResourceAsStream("/items/strawberry.png")));
            itemImages.put("Strawberry Seed", ImageIO.read(getClass().getResourceAsStream("/items/strawberry_seed.png")));
            itemImages.put("Cherry", ImageIO.read(getClass().getResourceAsStream("/items/cherry.png")));
            itemImages.put("Cherry Seed", ImageIO.read(getClass().getResourceAsStream("/items/cherry_seed.png")));

        }catch (Exception e){
            System.out.println("ERROR loading item images:");
            e.printStackTrace();
        }
    }

    public void update(){
        if(hoeing == true){
            spriteCounter++;
            if(spriteCounter <= 5){
                spriteNum = 1;
            }
            if(spriteCounter > 5 && spriteCounter <= 25){
                spriteNum = 2;
            }
            if(spriteCounter > 25){
                spriteNum = 1;
                spriteCounter = 0;
                interactWithTile();
                hoeing = false;
            }
        }
        if(attacking == true){
            spriteCounter++;
            if(spriteCounter <= 5){
                spriteNum = 1;
            }
            if(spriteCounter > 5 && spriteCounter <= 25){
                spriteNum = 2;
            }
            if(spriteCounter > 25){
                spriteNum = 1;
                spriteCounter = 0;
                interactWithTile();
                attacking = false;
            }

        } else if(keyH.upPressed == true || keyH.downPressed == true ||
                keyH.leftPressed == true || keyH.rightPressed == true){

            if(keyH.upPressed == true){
                direction ="up";
            }else if(keyH.downPressed == true){
                direction ="down";
            }else if(keyH.leftPressed == true){
                direction ="left";
            }else if(keyH.rightPressed == true){
                direction ="right";
            }

            collosiOn = false;
            gp.cChecker.checkTile(this);

            if(collosiOn == false){
                switch (direction){
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            }

            spriteCounter++;
            if(spriteCounter>12){
                if(spriteNum == 1){
                    spriteNum =2;
                }
                else if(spriteNum ==2){
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }

        if (keyH.attackPressed) {
            if (!attacking && !hoeing && !keyH.upPressed && !keyH.downPressed && !keyH.leftPressed && !keyH.rightPressed) {
                attacking = true;
            }
            keyH.attackPressed = false;
        }
        if (keyH.hoePressed) {
            if (!attacking && !hoeing && !keyH.upPressed && !keyH.downPressed && !keyH.leftPressed && !keyH.rightPressed) {
                hoeing = true;
            }
            keyH.hoePressed = false;
        }
    }

    public void draw(Graphics2D g2){
        BufferedImage image = null;

        if(attacking == true) {
            switch (direction) {
                case "up":
                    image = (spriteNum == 1) ? attackUp1 : attackUp2;
                    break;
                case "down":
                    image = (spriteNum == 1) ? attackDown1 : attackDown2;
                    break;
                case "left":
                    image = (spriteNum == 1) ? attackLeft1 : attackLeft2;
                    break;
                case "right":
                    image = (spriteNum == 1) ? attackRight1 : attackRight2;
                    break;
            }
        } else if (hoeing == true) {
            switch (direction) {
                case "up":
                    image = (spriteNum == 1) ? hoeUp1 : hoeUp2;
                    break;
                case "down":
                    image = (spriteNum == 1) ? hoeDown1 : hoeDown2;
                    break;
                case "left":
                    image = (spriteNum == 1) ? hoeLeft1 : hoeLeft2;
                    break;
                case "right":
                    image = (spriteNum == 1) ? hoeRight1 : hoeRight2;
                    break;
            }
        }else {
            switch (direction){
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
        }
        g2.drawImage(image,screenX,screenY,gp.tileSize,gp.tileSize,null);
    }

    public void interactWithTile() {
        int playerCol = (worldX + solidArea.x + solidArea.width / 2) / gp.tileSize;
        int playerRow = (worldY + solidArea.y + solidArea.height / 2) / gp.tileSize;

        int targetCol = playerCol;
        int targetRow = playerRow;

        switch (direction) {
            case "up":
                targetRow--;
                break;
            case "down":
                targetRow++;
                break;
            case "left":
                targetCol--;
                break;
            case "right":
                targetCol++;
                break;
        }

        if (targetCol >= 0 && targetCol < gp.maxWorldCol && targetRow >= 0 && targetRow < gp.maxWorldRow) {
            int tileNum = gp.tileM.mapTileNum[targetCol][targetRow];

            // ปุ่ม 1 (attacking) = ตัดไม้เท่านั้น
            if (attacking) {
                // <<<< ตรวจสอบว่ามี Area Cut หรือไม่
                if (stats.hasAreaCut()) {
                    // ตัดได้หลายต้น (3x3)
                    cutTreesInArea(targetCol, targetRow);
                } else {
                    // ตัดได้ทีละต้น
                    if (tileNum == 15 || tileNum == 20 || tileNum == 21 || tileNum == 19) {
                        String itemToCollect = "Wood";
                        addItemToInventory(itemToCollect, 1);

                        if (tileNum == 15) {
                            final int SAND_TILE_ID = 12;
                            gp.tileM.mapTileNum[targetCol][targetRow] = SAND_TILE_ID;
                        } else {
                            gp.tileM.mapTileNum[targetCol][targetRow] = 0;
                        }
                    }
                }
            }

            // ปุ่ม 2 (hoeing) = พรวนดินเท่านั้น
            if (hoeing) {
                final int HOED_TILE_ID = 22;

                // <<<< ตรวจสอบว่ามี Area Hoe หรือไม่
                if (stats.hasAreaHoe()) {
                    // พรวนได้ 3x3
                    hoeArea(targetCol, targetRow);
                } else {
                    // พรวนได้ทีละช่อง
                    if (tileNum == 0) {
                        gp.tileM.mapTileNum[targetCol][targetRow] = HOED_TILE_ID;
                    }
                }
            }
        }
    }

    // <<<< ฟังก์ชันพรวนดินแบบ 3x3
    private void hoeArea(int centerCol, int centerRow) {
        final int HOED_TILE_ID = 22;
        int hoedCount = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int col = centerCol + dx;
                int row = centerRow + dy;

                if (col >= 0 && col < gp.maxWorldCol && row >= 0 && row < gp.maxWorldRow) {
                    int tileNum = gp.tileM.mapTileNum[col][row];
                    if (tileNum == 0) { // ต้องเป็นหญ้าเท่านั้น
                        gp.tileM.mapTileNum[col][row] = HOED_TILE_ID;
                        hoedCount++;
                    }
                }
            }
        }

        if (hoedCount > 0) {
            gp.showMessage("Hoed " + hoedCount + " tiles!");
        }
    }

    // <<<< ฟังก์ชันตัดไม้แบบ 3x3
    private void cutTreesInArea(int centerCol, int centerRow) {
        int cutCount = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int col = centerCol + dx;
                int row = centerRow + dy;

                if (col >= 0 && col < gp.maxWorldCol && row >= 0 && row < gp.maxWorldRow) {
                    int tileNum = gp.tileM.mapTileNum[col][row];

                    if (tileNum == 15 || tileNum == 20 || tileNum == 21 || tileNum == 19) {
                        addItemToInventory("Wood", 1);
                        cutCount++;

                        if (tileNum == 15) {
                            final int SAND_TILE_ID = 12;
                            gp.tileM.mapTileNum[col][row] = SAND_TILE_ID;
                        } else {
                            gp.tileM.mapTileNum[col][row] = 0;
                        }
                    }
                }
            }
        }

        if (cutCount > 0) {
            gp.showMessage("Cut " + cutCount + " trees!");
        }
    }
}