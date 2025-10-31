package entity;

import main.GamePanel;
import java.util.ArrayList;
import java.util.List;

public class UpgradeManager {
    GamePanel gp;

    public UpgradeManager(GamePanel gp) {
        this.gp = gp;
    }

    // คลาสสำหรับเก็บข้อมูลอัพเกรดแต่ละตัว
    public static class Upgrade {
        public String name;
        public String description;
        public int level;           // ระดับปัจจุบัน
        public int maxLevel;        // ระดับสูงสุด
        public int goldCost;        // ราคาเงิน
        public String itemRequired; // ไอเทมที่ต้องการ (null ถ้าไม่ต้องการ)
        public int itemAmount;      // จำนวนไอเทม
        public String upgradeType;  // "GROWTH", "YIELD", "PRICE", "TOOL"

        public Upgrade(String name, String desc, int level, int maxLevel,
                       int goldCost, String itemRequired, int itemAmount, String upgradeType) {
            this.name = name;
            this.description = desc;
            this.level = level;
            this.maxLevel = maxLevel;
            this.goldCost = goldCost;
            this.itemRequired = itemRequired;
            this.itemAmount = itemAmount;
            this.upgradeType = upgradeType;
        }

        public boolean isMaxLevel() {
            return level >= maxLevel;
        }

        public String getDisplayText() {
            if (isMaxLevel()) {
                return name + " [MAX]";
            }
            return name + " [Lv." + level + "/" + maxLevel + "]";
        }

        public String getCostText() {
            if (isMaxLevel()) return "MAX LEVEL";

            String cost = goldCost + "G";
            if (itemRequired != null && itemAmount > 0) {
                cost += " + " + itemAmount + " " + itemRequired;
            }
            return cost;
        }
    }

    // ดึงรายการอัพเกรดทั้งหมด
    public List<Upgrade> getAvailableUpgrades() {
        List<Upgrade> upgrades = new ArrayList<>();
        PlayerStats stats = gp.player.stats;

        // 1. Growth Speed Upgrades
        if (stats.growthSpeedLevel < 3) {
            int nextLevel = stats.growthSpeedLevel + 1;
            int goldCost = 0;
            String itemReq = null;
            int itemAmt = 0;
            String desc = "";

            switch (nextLevel) {
                case 1:
                    goldCost = 200;
                    desc = "Crops grow 25% faster";
                    break;
                case 2:
                    goldCost = 500;
                    itemReq = "Wheat";
                    itemAmt = 10;
                    desc = "Crops grow 67% faster";
                    break;
                case 3:
                    goldCost = 1000;
                    itemReq = "Corn";
                    itemAmt = 10;
                    desc = "Crops grow 150% faster";
                    break;
            }
            upgrades.add(new Upgrade("Growth Speed", desc, stats.growthSpeedLevel,
                    3, goldCost, itemReq, itemAmt, "GROWTH"));
        }

        // 2. Harvest Yield Upgrades
        if (stats.harvestYieldLevel < 3) {
            int nextLevel = stats.harvestYieldLevel + 1;
            int goldCost = 0;
            String itemReq = null;
            int itemAmt = 0;
            String desc = "";

            switch (nextLevel) {
                case 1:
                    goldCost = 300;
                    desc = "Get +1 extra crop per harvest";
                    break;
                case 2:
                    goldCost = 800;
                    itemReq = "Tomato";
                    itemAmt = 20;
                    desc = "Get +2 extra crops per harvest";
                    break;
                case 3:
                    goldCost = 1500;
                    desc = "Get +3 extra crops per harvest";
                    break;
            }
            upgrades.add(new Upgrade("Harvest Yield", desc, stats.harvestYieldLevel,
                    3, goldCost, itemReq, itemAmt, "YIELD"));
        }

        // 3. Market Price Upgrades
        if (stats.marketPriceLevel < 3) {
            int nextLevel = stats.marketPriceLevel + 1;
            int goldCost = 0;
            String itemReq = null;
            int itemAmt = 0;
            String desc = "";

            switch (nextLevel) {
                case 1:
                    goldCost = 400;
                    desc = "Sell crops for 25% more";
                    break;
                case 2:
                    goldCost = 1000;
                    itemReq = "Wood";
                    itemAmt = 30;
                    desc = "Sell crops for 50% more";
                    break;
                case 3:
                    goldCost = 2000;
                    desc = "Sell crops for 75% more";
                    break;
            }
            upgrades.add(new Upgrade("Market Price", desc, stats.marketPriceLevel,
                    3, goldCost, itemReq, itemAmt, "PRICE"));
        }

        // 4. Tool Efficiency Upgrades
        if (stats.toolEfficiencyLevel < 2) {
            int nextLevel = stats.toolEfficiencyLevel + 1;
            int goldCost = 0;
            String desc = "";

            switch (nextLevel) {
                case 1:
                    goldCost = 250;
                    desc = "Hoe in 3x3 area";
                    break;
                case 2:
                    goldCost = 600;
                    desc = "Cut multiple trees at once";
                    break;
            }
            upgrades.add(new Upgrade("Tool Efficiency", desc, stats.toolEfficiencyLevel,
                    2, goldCost, null, 0, "TOOL"));
        }

        return upgrades;
    }

    // ซื้ออัพเกรด
    public boolean purchaseUpgrade(Upgrade upgrade) {
        Player player = gp.player;

        // ตรวจสอบเงิน
        if (player.money < upgrade.goldCost) {
            gp.showMessage("Not enough gold!");
            return false;
        }

        // ตรวจสอบไอเทม
        if (upgrade.itemRequired != null && upgrade.itemAmount > 0) {
            if (!player.inventory.containsKey(upgrade.itemRequired)) {
                gp.showMessage("You need " + upgrade.itemAmount + " " + upgrade.itemRequired + "!");
                return false;
            }
            int currentAmount = player.inventory.get(upgrade.itemRequired);
            if (currentAmount < upgrade.itemAmount) {
                gp.showMessage("You need " + upgrade.itemAmount + " " + upgrade.itemRequired + "!");
                return false;
            }
        }

        // หักเงิน
        player.money -= upgrade.goldCost;

        // หักไอเทม
        if (upgrade.itemRequired != null && upgrade.itemAmount > 0) {
            int remaining = player.inventory.get(upgrade.itemRequired) - upgrade.itemAmount;
            if (remaining <= 0) {
                player.inventory.remove(upgrade.itemRequired);
            } else {
                player.inventory.put(upgrade.itemRequired, remaining);
            }
        }

        // อัพเกรด
        switch (upgrade.upgradeType) {
            case "GROWTH":
                player.stats.growthSpeedLevel++;
                break;
            case "YIELD":
                player.stats.harvestYieldLevel++;
                break;
            case "PRICE":
                player.stats.marketPriceLevel++;
                break;
            case "TOOL":
                player.stats.toolEfficiencyLevel++;
                break;
        }

        gp.showMessage("Upgraded " + upgrade.name + "!");
        System.out.println("✓ Upgraded: " + upgrade.name + " to Level " + upgrade.level);
        return true;
    }
}