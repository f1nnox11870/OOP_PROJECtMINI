package main;
import entity.Player;
import entity.NPC;
import entity.CropManager;
import entity.UpgradeManager; // <<<< เพิ่ม
import tile.TileManager;

import java.awt.*;
import javax.swing.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable{
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int MaxScreenWidth = tileSize * maxScreenCol;
    public final int MaxScreenHeight = tileSize * maxScreenRow;

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int screenWidth = tileSize * maxWorldCol;
    public final int screenHeight = tileSize * maxWorldRow;

    public final int PLAY_STATE = 1;
    public final int PAUSE_STATE = 2;
    public final int INVENTORY_STATE = 3;
    public final int SHOP_STATE = 4;
    public final int PLANT_STATE = 5;
    public final int UPGRADE_STATE = 6; // <<<< เพิ่ม state ใหม่
    public int gameState = PLAY_STATE;

    int FPS = 60;

    public TileManager tileM = new TileManager(this);
    public CropManager cropM = new CropManager(this);
    public UpgradeManager upgradeM; // <<<< เพิ่ม UpgradeManager
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public CollisionChecker cChecker = new CollisionChecker(this);

    public Player player = new Player(this,keyH);
    public NPC[] npcs = new NPC[10];
    public int currentNPCIndex = -1;

    public String shopMode = "BUY";
    public int shopSelectedIndex = 0;
    public int plantSelectedIndex = 0;
    public int upgradeSelectedIndex = 0; // <<<< เลือกอัพเกรด

    public String message = "";
    public long messageTime = 0;

    public GamePanel(){
        this.setPreferredSize(new Dimension(MaxScreenWidth, MaxScreenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);

        upgradeM = new UpgradeManager(this); // <<<< สร้าง UpgradeManager
        setupNPCs();
    }

    public void setupNPCs() {
        npcs[0] = new NPC(this, tileSize * 16, tileSize * 10, "SHOP", "Merchant");
        npcs[1] = new NPC(this, tileSize * 20, tileSize * 10, "UPGRADE", "Wizard");
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run(){
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;
            if(delta >= 1){
                update();
                repaint();
                delta--;
                drawCount++;
            }
            if(timer >= 1000000000){
                System.out.println("FPS:" + drawCount);
                drawCount =0;
                timer = 0;
            }
        }
    }

    public void update(){
        // จัดการปุ่ม E สำหรับ Inventory
        if (keyH.inventoryPressed) {
            if (gameState == PLAY_STATE) {
                gameState = INVENTORY_STATE;
                keyH.inventoryPressed = false;
            } else if (gameState == INVENTORY_STATE) {
                gameState = PLAY_STATE;
                keyH.inventoryPressed = false;
            }
        }

        // <<<< จัดการปุ่ม U สำหรับ Upgrade Menu
        if (keyH.upgradePressed) {
            if (gameState == PLAY_STATE) {
                gameState = UPGRADE_STATE;
                upgradeSelectedIndex = 0;
                keyH.upgradePressed = false;
            } else if (gameState == UPGRADE_STATE) {
                gameState = PLAY_STATE;
                keyH.upgradePressed = false;
            }
        }

        if (keyH.tabPressed && gameState == INVENTORY_STATE) {
            keyH.tabPressed = false;
        }

        // จัดการปุ่ม F สำหรับโต้ตอบกับ NPC
        if (keyH.interactPressed && gameState == PLAY_STATE) {
            checkNPCInteraction();
            keyH.interactPressed = false;
        }

        // จัดการปุ่ม = สำหรับเปิดเมนูปลูกพืช
        if (keyH.plantPressed && gameState == PLAY_STATE) {
            if (hasAnySeed()) {
                gameState = PLANT_STATE;
                plantSelectedIndex = 0;
            } else {
                showMessage("You don't have any seeds!");
            }
            keyH.plantPressed = false;
        }

        // จัดการปุ่ม - สำหรับเก็บเกี่ยว
        if (keyH.harvestPressed && gameState == PLAY_STATE) {
            harvestCropInFront();
            keyH.harvestPressed = false;
        }

        // จัดการ UI ร้านค้า
        if (gameState == SHOP_STATE) {
            handleShopInput();
        }

        // จัดการ UI ปลูกพืช
        if (gameState == PLANT_STATE) {
            handlePlantInput();
        }

        // <<<< จัดการ UI อัพเกรด
        if (gameState == UPGRADE_STATE) {
            handleUpgradeInput();
        }

        // อัปเดต player, NPCs, และพืชผล
        if(gameState == PLAY_STATE) {
            player.update();

            for (int i = 0; i < npcs.length; i++) {
                if (npcs[i] != null) {
                    npcs[i].update();
                }
            }

            cropM.update();
        }
    }

    // <<<< ฟังก์ชันจัดการ Input สำหรับเมนูอัพเกรด
    public void handleUpgradeInput() {
        List<UpgradeManager.Upgrade> upgrades = upgradeM.getAvailableUpgrades();

        // กด W/S เลือกอัพเกรด
        if (keyH.upPressed) {
            upgradeSelectedIndex--;
            if (upgradeSelectedIndex < 0) upgradeSelectedIndex = 0;
            keyH.upPressed = false;
        }
        if (keyH.downPressed) {
            upgradeSelectedIndex++;
            if (upgradeSelectedIndex >= upgrades.size()) upgradeSelectedIndex = upgrades.size() - 1;
            keyH.downPressed = false;
        }

        // กด Enter ซื้ออัพเกรด
        if (keyH.enterPressed) {
            if (upgradeSelectedIndex < upgrades.size()) {
                UpgradeManager.Upgrade selectedUpgrade = upgrades.get(upgradeSelectedIndex);
                if (!selectedUpgrade.isMaxLevel()) {
                    upgradeM.purchaseUpgrade(selectedUpgrade);
                    upgradeSelectedIndex = 0; // รีเซ็ตตำแหน่ง
                }
            }
            keyH.enterPressed = false;
        }


        if (keyH.escPressed || keyH.upgradePressed) {
            gameState = PLAY_STATE;
            keyH.escPressed = false;
            keyH.upgradePressed = false;
        }
    }

    public boolean hasAnySeed() {
        return player.inventory.containsKey("Wheat Seed") ||
                player.inventory.containsKey("Corn Seed") ||
                player.inventory.containsKey("Tomato Seed")||
                player.inventory.containsKey("Carrot Seed") ||
                player.inventory.containsKey("Strawberry Seed") ||
                player.inventory.containsKey("Pumpkin Seed") ||
                player.inventory.containsKey("Cherry Seed");
    }

    public void showMessage(String msg) {
        message = msg;
        messageTime = System.currentTimeMillis();
        System.out.println("MESSAGE: " + msg);
    }

    public void harvestCropInFront() {
        int playerCol = (player.worldX + player.solidArea.x + player.solidArea.width / 2) / tileSize;
        int playerRow = (player.worldY + player.solidArea.y + player.solidArea.height / 2) / tileSize;

        int targetCol = playerCol;
        int targetRow = playerRow;

        switch (player.direction) {
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

        System.out.println("Trying to harvest at col=" + targetCol + ", row=" + targetRow);

        entity.Crop crop = cropM.getCrop(targetCol, targetRow);
        if (crop != null && !crop.isHarvestable()) {
            long timeRemaining = crop.getRemainingTime();
            int secondsRemaining = (int)(timeRemaining / 1000);
            showMessage("Not ready! Wait " + secondsRemaining + " more seconds");
            return;
        }

        if (cropM.harvestCrop(targetCol, targetRow)) {
            // <<<< แสดงข้อความรวมโบนัส
            int bonus = player.stats.getBonusYield();
            if (bonus > 0) {
                showMessage("Harvested " + (1 + bonus) + " crops! (+" + bonus + " bonus)");
            } else {
                showMessage("Harvested crop!");
            }
        } else {
            showMessage("No crop here!");
        }
    }

    public void handlePlantInput() {
        ArrayList<String> seedList = new ArrayList<>();
        if (player.inventory.containsKey("Wheat Seed")) seedList.add("Wheat Seed");
        if (player.inventory.containsKey("Corn Seed")) seedList.add("Corn Seed");
        if (player.inventory.containsKey("Tomato Seed")) seedList.add("Tomato Seed");
        if (player.inventory.containsKey("Carrot Seed")) seedList.add("Carrot Seed");
        if (player.inventory.containsKey("Strawberry Seed")) seedList.add("Strawberry Seed"); // <<<< เพิ่มใหม่
        if (player.inventory.containsKey("Pumpkin Seed")) seedList.add("Pumpkin Seed");       // <<<< เพิ่มใหม่
        if (player.inventory.containsKey("Cherry Seed")) seedList.add("Cherry Seed");

        if (keyH.upPressed) {
            plantSelectedIndex--;
            if (plantSelectedIndex < 0) plantSelectedIndex = 0;
            keyH.upPressed = false;
        }
        if (keyH.downPressed) {
            plantSelectedIndex++;
            if (plantSelectedIndex >= seedList.size()) plantSelectedIndex = seedList.size() - 1;
            keyH.downPressed = false;
        }

        if (keyH.enterPressed) {
            if (plantSelectedIndex < seedList.size()) {
                String selectedSeed = seedList.get(plantSelectedIndex);
                plantSelectedCrop(selectedSeed);
            }
            keyH.enterPressed = false;
        }

        if (keyH.escPressed || keyH.inventoryPressed) {
            gameState = PLAY_STATE;
            keyH.escPressed = false;
            keyH.inventoryPressed = false;
        }
    }

    public void plantSelectedCrop(String seedName) {
        int playerCol = (player.worldX + player.solidArea.x + player.solidArea.width / 2) / tileSize;
        int playerRow = (player.worldY + player.solidArea.y + player.solidArea.height / 2) / tileSize;

        int targetCol = playerCol;
        int targetRow = playerRow;

        switch (player.direction) {
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

        String cropType = seedName.replace(" Seed", "");

        if (cropM.plantCrop(targetCol, targetRow, cropType)) {
            int count = player.inventory.get(seedName);
            if (count <= 1) {
                player.inventory.remove(seedName);
            } else {
                player.inventory.put(seedName, count - 1);
            }

            // <<<< แสดงข้อความรวมโบนัสความเร็ว
            float speedMult = player.stats.getGrowthSpeedMultiplier();
            if (speedMult > 1.0f) {
                showMessage("Planted " + cropType + "! (" + String.format("%.0f", (speedMult - 1) * 100) + "% faster)");
            } else {
                showMessage("Planted " + cropType + "!");
            }
            gameState = PLAY_STATE;
        } else {
            showMessage("Cannot plant here! Hoe the ground first (Press 2)");
        }
    }

    public void checkNPCInteraction() {
        int targetX = player.worldX;
        int targetY = player.worldY;

        switch (player.direction) {
            case "up":
                targetY -= tileSize;
                break;
            case "down":
                targetY += tileSize;
                break;
            case "left":
                targetX -= tileSize;
                break;
            case "right":
                targetX += tileSize;
                break;
        }

        for (int i = 0; i < npcs.length; i++) {
            if (npcs[i] != null) {
                int distance = Math.abs(npcs[i].worldX - targetX) + Math.abs(npcs[i].worldY - targetY);
                if (distance < tileSize) {
                    currentNPCIndex = i;
                    // <<<< ตรวจสอบประเภท NPC
                    if (npcs[i].npcType.equals("SHOP")) {
                        gameState = SHOP_STATE;
                        shopMode = "BUY";
                        shopSelectedIndex = 0;
                    } else if (npcs[i].npcType.equals("UPGRADE")) {
                        gameState = UPGRADE_STATE;
                        upgradeSelectedIndex = 0;
                    }
                    return;
                }
            }
        }
    }

    public void handleShopInput() {
        NPC currentNPC = npcs[currentNPCIndex];

        if (keyH.tabPressed) {
            shopMode = shopMode.equals("BUY") ? "SELL" : "BUY";
            shopSelectedIndex = 0;
            keyH.tabPressed = false;
        }

        if (keyH.upPressed) {
            shopSelectedIndex--;
            if (shopSelectedIndex < 0) shopSelectedIndex = 0;
            keyH.upPressed = false;
        }
        if (keyH.downPressed) {
            int maxIndex = shopMode.equals("BUY") ?
                    currentNPC.shopItems.size() - 1 : player.inventory.size() - 1;
            shopSelectedIndex++;
            if (shopSelectedIndex > maxIndex) shopSelectedIndex = maxIndex;
            keyH.downPressed = false;
        }

        if (keyH.enterPressed) {
            if (shopMode.equals("BUY")) {
                buyItem();
            } else {
                sellItem();
            }
            keyH.enterPressed = false;
        }

        if (keyH.inventoryPressed || keyH.escPressed) {
            gameState = PLAY_STATE;
            currentNPCIndex = -1;
            keyH.inventoryPressed = false;
            keyH.escPressed = false;
        }
    }

    public void buyItem() {
        NPC currentNPC = npcs[currentNPCIndex];
        ArrayList<String> itemList = new ArrayList<>(currentNPC.shopItems.keySet());

        if (shopSelectedIndex < itemList.size()) {
            String itemName = itemList.get(shopSelectedIndex);
            int price = currentNPC.shopItems.get(itemName);

            if (player.money >= price) {
                player.money -= price;
                player.addItemToInventory(itemName, 1);
                System.out.println("Bought " + itemName + " for " + price + " gold!");
            } else {
                System.out.println("Not enough money!");
            }
        }
    }

    public void sellItem() {
        NPC currentNPC = npcs[currentNPCIndex];
        ArrayList<String> itemList = new ArrayList<>(player.inventory.keySet());

        if (shopSelectedIndex < itemList.size()) {
            String itemName = itemList.get(shopSelectedIndex);

            if (currentNPC.buyPrices.containsKey(itemName)) {
                // <<<< ใช้ราคาพื้นฐานคูณด้วย multiplier
                int basePrice = currentNPC.buyPrices.get(itemName);
                float priceMultiplier = player.stats.getPriceMultiplier();
                int finalPrice = (int)(basePrice * priceMultiplier);

                int count = player.inventory.get(itemName);

                if (count > 0) {
                    player.money += finalPrice;
                    player.inventory.put(itemName, count - 1);
                    if (player.inventory.get(itemName) == 0) {
                        player.inventory.remove(itemName);
                    }

                    // <<<< แสดงโบนัสราคา
                    if (priceMultiplier > 1.0f) {
                        System.out.println("Sold " + itemName + " for " + finalPrice + "G! (+" +
                                (int)((priceMultiplier - 1) * 100) + "% bonus)");
                    } else {
                        System.out.println("Sold " + itemName + " for " + finalPrice + "G!");
                    }
                }
            } else {
                System.out.println("NPC doesn't buy this item!");
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        tileM.draw(g2);

        for (int i = 0; i < npcs.length; i++) {
            if (npcs[i] != null) {
                npcs[i].draw(g2);
            }
        }

        player.draw(g2);

        if (gameState == INVENTORY_STATE) {
            drawInventory(g2);
        } else if (gameState == SHOP_STATE) {
            drawShop(g2);
        } else if (gameState == PLANT_STATE) {
            drawPlantMenu(g2);
        } else if (gameState == UPGRADE_STATE) { // <<<< วาด UI อัพเกรด
            drawUpgradeMenu(g2);
        }

        drawMoney(g2);
        drawControls(g2);
        drawCropInfo(g2);
        drawMessage(g2);
        drawPlayerStats(g2); // <<<< แสดงสถานะอัพเกรดปัจจุบัน

        g2.dispose();
    }

    // <<<< UI แสดงสถานะอัพเกรดของผู้เล่น
    private void drawPlayerStats(Graphics2D g2) {
        if (gameState != PLAY_STATE) return;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14F));
        g2.setColor(new Color(255, 255, 255, 200));

        int x = MaxScreenWidth - 180;
        int y = 60;

        // พื้นหลัง
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(x - 10, y - 20, 170, 90);
        g2.setColor(Color.WHITE);
        g2.drawRect(x - 10, y - 20, 170, 90);

        g2.setColor(Color.CYAN);
        g2.drawString("Upgrades:", x, y);

        g2.setColor(Color.WHITE);
        y += 20;
        g2.drawString("Growth: Lv." + player.stats.growthSpeedLevel, x, y);
        y += 18;
        g2.drawString("Yield: Lv." + player.stats.harvestYieldLevel, x, y);
        y += 18;
        g2.drawString("Price: Lv." + player.stats.marketPriceLevel, x, y);
        y += 18;
        g2.drawString("Tool: Lv." + player.stats.toolEfficiencyLevel, x, y);
    }

    // <<<< UI เมนูอัพเกรด
    private void drawUpgradeMenu(Graphics2D g2) {
        int frameX = tileSize * 2;
        int frameY = tileSize;
        int frameWidth = tileSize * 12;
        int frameHeight = tileSize * 10;

        g2.setColor(new Color(20, 20, 60, 230));
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(new Color(100, 200, 255));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
        g2.setColor(new Color(255, 255, 100));
        g2.drawString("⚡ Upgrades ⚡", frameX + 150, frameY + 45);

        List<UpgradeManager.Upgrade> upgrades = upgradeM.getAvailableUpgrades();

        if (upgrades.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
            g2.setColor(Color.GREEN);
            g2.drawString("All Upgrades Maxed!", frameX + 120, frameY + 200);
        } else {
            int itemY = frameY + 90;
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));

            for (int i = 0; i < upgrades.size(); i++) {
                UpgradeManager.Upgrade upgrade = upgrades.get(i);

                if (i == upgradeSelectedIndex) {
                    g2.setColor(new Color(100, 255, 255, 150));
                    g2.fillRect(frameX + 20, itemY - 28, frameWidth - 40, 70);
                }

                // ชื่ออัพเกรด
                g2.setColor(Color.YELLOW);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
                g2.drawString(upgrade.getDisplayText(), frameX + 30, itemY);

                // คำอธิบาย
                g2.setColor(Color.WHITE);
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18F));
                g2.drawString(upgrade.description, frameX + 30, itemY + 22);

                // ราคา
                g2.setColor(upgrade.isMaxLevel() ? Color.GREEN : Color.ORANGE);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
                g2.drawString(upgrade.getCostText(), frameX + 30, itemY + 42);

                itemY += 85;
            }
        }

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16F));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("W/S: Select | Enter: Purchase | U/ESC: Close", frameX + 80, frameY + frameHeight - 15);
    }

    private void drawControls(Graphics2D g2) {
        if (gameState == PLAY_STATE) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16F));
            g2.setColor(Color.WHITE);
            int y = MaxScreenHeight - 20;
            g2.drawString("1:Cut 2:Hoe =:Plant -:Harvest F:Talk E:Inv U:Upgrade", 10, y);
        }
    }

    private void drawMessage(Graphics2D g2) {
        if (!message.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - messageTime < 3000) {
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
                g2.setColor(new Color(0, 0, 0, 150));
                int msgWidth = g2.getFontMetrics().stringWidth(message);
                int x = (MaxScreenWidth - msgWidth) / 2;
                int y = MaxScreenHeight / 2;
                g2.fillRect(x - 10, y - 30, msgWidth + 20, 40);
                g2.setColor(Color.WHITE);
                g2.drawString(message, x, y);
            } else {
                message = "";
            }
        }
    }

    private void drawCropInfo(Graphics2D g2) {
        if (gameState != PLAY_STATE) return;

        int playerCol = (player.worldX + player.solidArea.x + player.solidArea.width / 2) / tileSize;
        int playerRow = (player.worldY + player.solidArea.y + player.solidArea.height / 2) / tileSize;

        int targetCol = playerCol;
        int targetRow = playerRow;

        switch (player.direction) {
            case "up": targetRow--; break;
            case "down": targetRow++; break;
            case "left": targetCol--; break;
            case "right": targetCol++; break;
        }

        entity.Crop crop = cropM.getCrop(targetCol, targetRow);
        if (crop != null) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));

            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(10, 60, 220, 80);
            g2.setColor(Color.WHITE);
            g2.drawRect(10, 60, 220, 80);

            g2.setColor(Color.YELLOW);
            g2.drawString(crop.cropType, 20, 85);

            g2.setColor(Color.WHITE);
            String[] stageNames = {"Planted", "Seedling", "Growing", "Harvestable"};
            g2.drawString("Stage: " + stageNames[crop.growthStage], 20, 110);

            int barWidth = 180;
            int barHeight = 15;
            int barX = 25;
            int barY = 120;

            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(barX, barY, barWidth, barHeight);

            long timePassed = System.currentTimeMillis() - crop.plantTime;
            long adjustedDuration = (long)(crop.growthDuration / crop.speedMultiplier);
            long totalTime = adjustedDuration * 3;
            float progress = Math.min(1.0f, (float)timePassed / totalTime);

            Color barColor;
            if (crop.isHarvestable()) {
                barColor = Color.GREEN;
            } else if (crop.growthStage >= 2) {
                barColor = Color.YELLOW;
            } else {
                barColor = Color.ORANGE;
            }

            g2.setColor(barColor);
            g2.fillRect(barX, barY, (int)(barWidth * progress), barHeight);

            g2.setColor(Color.WHITE);
            g2.drawRect(barX, barY, barWidth, barHeight);
        }
    }

    private void drawPlantMenu(Graphics2D g2) {
        int frameX = tileSize * 5;
        int frameY = tileSize * 3;
        int frameWidth = tileSize * 6;
        int frameHeight = tileSize * 6;

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
        g2.setColor(Color.GREEN);
        g2.drawString("Select Seed", frameX + 60, frameY + 40);

        ArrayList<String> seedList = new ArrayList<>();
        if (player.inventory.containsKey("Wheat Seed")) seedList.add("Wheat Seed");
        if (player.inventory.containsKey("Corn Seed")) seedList.add("Corn Seed");
        if (player.inventory.containsKey("Tomato Seed")) seedList.add("Tomato Seed");
        if (player.inventory.containsKey("Carrot Seed")) seedList.add("Carrot Seed");
        if (player.inventory.containsKey("Strawberry Seed")) seedList.add("Strawberry Seed"); // <<<< เพิ่มใหม่
        if (player.inventory.containsKey("Pumpkin Seed")) seedList.add("Pumpkin Seed");       // <<<< เพิ่มใหม่
        if (player.inventory.containsKey("Cherry Seed")) seedList.add("Cherry Seed");     // <

        int itemY = frameY + 80;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22F));

        for (int i = 0; i < seedList.size(); i++) {
            String seedName = seedList.get(i);
            int count = player.inventory.get(seedName);

            if (i == plantSelectedIndex) {
                g2.setColor(new Color(100, 255, 100, 150));
                g2.fillRect(frameX + 20, itemY - 25, frameWidth - 40, 40);
            }

            java.awt.image.BufferedImage itemImage = player.itemImages.get(seedName);
            if (itemImage != null) {
                g2.drawImage(itemImage, frameX + 30, itemY - 20, 32, 32, null);
            }

            g2.setColor(Color.WHITE);
            g2.drawString(seedName + " x" + count, frameX + 70, itemY);

            itemY += 50;
        }

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16F));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("W/S: Select | Enter: Plant | ESC: Cancel", frameX + 30, frameY + frameHeight - 20);
    }

    private void drawMoney(Graphics2D g2) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
        g2.setColor(Color.YELLOW);
        g2.drawString("Gold: " + player.money, 10, 30);
    }

    private void drawInventory(Graphics2D g2) {
        int frameX = tileSize * 4;
        int frameY = tileSize * 2;
        int frameWidth = tileSize * 8;
        int frameHeight = tileSize * 8;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
        g2.setColor(Color.WHITE);
        g2.drawString("Inventory", frameX + frameWidth/2 - 60, frameY + 40);

        int slotX = frameX + 30;
        int slotY = frameY + 70;
        int slotSize = tileSize;
        int slotSpacing = 10;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));

        for (Map.Entry<String, Integer> entry : player.inventory.entrySet()) {
            String itemName = entry.getKey();
            int itemCount = entry.getValue();

            g2.setColor(new Color(60, 60, 60));
            g2.fillRect(slotX, slotY, slotSize, slotSize);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(slotX, slotY, slotSize, slotSize);

            java.awt.image.BufferedImage itemImage = player.itemImages.get(itemName);
            if (itemImage != null) {
                g2.drawImage(itemImage, slotX + 4, slotY + 4, slotSize - 8, slotSize - 8, null);
            }

            g2.setColor(Color.WHITE);
            String countText = "x" + itemCount;
            g2.drawString(countText, slotX + slotSize + 10, slotY + slotSize/2 + 8);
            g2.drawString(itemName, slotX + slotSize + 50, slotY + slotSize/2 + 8);

            slotY += slotSize + slotSpacing;
        }
    }

    private void drawShop(Graphics2D g2) {
        int frameX = tileSize * 2;
        int frameY = tileSize;
        int frameWidth = tileSize * 12;
        int frameHeight = tileSize * 10;

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
        g2.setColor(Color.YELLOW);
        NPC currentNPC = npcs[currentNPCIndex];
        g2.drawString(currentNPC.npcName + "'s Shop", frameX + 20, frameY + 35);

        int tabY = frameY + 60;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));

        if (shopMode.equals("BUY")) {
            g2.setColor(Color.GREEN);
        } else {
            g2.setColor(Color.GRAY);
        }
        g2.drawString("BUY [Tab]", frameX + 20, tabY);

        if (shopMode.equals("SELL")) {
            g2.setColor(Color.GREEN);
        } else {
            g2.setColor(Color.GRAY);
        }
        g2.drawString("SELL", frameX + 150, tabY);

        int itemY = tabY + 40;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));

        if (shopMode.equals("BUY")) {
            drawBuyList(g2, frameX, itemY);
        } else {
            drawSellList(g2, frameX, itemY);
        }

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16F));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("W/S: Select | Enter: Confirm | E/ESC: Close", frameX + 20, frameY + frameHeight - 15);
    }

    private void drawBuyList(Graphics2D g2, int x, int startY) {
        NPC currentNPC = npcs[currentNPCIndex];
        ArrayList<String> itemList = new ArrayList<>(currentNPC.shopItems.keySet());

        int index = 0;
        for (String itemName : itemList) {
            int itemY = startY + (index * 50);
            int price = currentNPC.shopItems.get(itemName);

            if (index == shopSelectedIndex) {
                g2.setColor(new Color(100, 100, 255, 100));
                g2.fillRect(x + 15, itemY - 25, tileSize * 11 - 30, 40);
            }

            java.awt.image.BufferedImage itemImage = player.itemImages.get(itemName);
            if (itemImage != null) {
                g2.drawImage(itemImage, x + 20, itemY - 20, 32, 32, null);
            }

            g2.setColor(Color.WHITE);
            g2.drawString(itemName, x + 60, itemY);
            g2.setColor(Color.YELLOW);
            g2.drawString(price + "G", x + 250, itemY);

            index++;
        }
    }

    private void drawSellList(Graphics2D g2, int x, int startY) {
        NPC currentNPC = npcs[currentNPCIndex];
        ArrayList<String> itemList = new ArrayList<>(player.inventory.keySet());

        int index = 0;
        for (String itemName : itemList) {
            int itemY = startY + (index * 50);
            int count = player.inventory.get(itemName);

            if (index == shopSelectedIndex) {
                g2.setColor(new Color(100, 100, 255, 100));
                g2.fillRect(x + 15, itemY - 25, tileSize * 11 - 30, 40);
            }

            java.awt.image.BufferedImage itemImage = player.itemImages.get(itemName);
            if (itemImage != null) {
                g2.drawImage(itemImage, x + 20, itemY - 20, 32, 32, null);
            }

            g2.setColor(Color.WHITE);
            g2.drawString(itemName + " x" + count, x + 60, itemY);

            if (currentNPC.buyPrices.containsKey(itemName)) {
                // <<<< แสดงราคาที่ปรับแล้ว
                int basePrice = currentNPC.buyPrices.get(itemName);
                float priceMultiplier = player.stats.getPriceMultiplier();
                int finalPrice = (int)(basePrice * priceMultiplier);

                g2.setColor(Color.YELLOW);
                g2.drawString(finalPrice + "G", x + 250, itemY);

                // แสดงโบนัส
                if (priceMultiplier > 1.0f) {
                    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14F));
                    g2.setColor(Color.GREEN);
                    g2.drawString("(+" + (int)((priceMultiplier - 1) * 100) + "%)", x + 310, itemY);
                    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
                }
            } else {
                g2.setColor(Color.RED);
                g2.drawString("--", x + 250, itemY);
            }

            index++;
        }
    }
}