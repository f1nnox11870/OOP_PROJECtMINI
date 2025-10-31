package entity;

import main.GamePanel;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class CropManager {
    GamePanel gp;

    // เก็บข้อมูลพืชแต่ละช่อง: (col, row) -> Crop
    public Map<String, Crop> crops;

    public CropManager(GamePanel gp) {
        this.gp = gp;
        this.crops = new HashMap<>();
    }

    // ปลูกพืช
    public boolean plantCrop(int col, int row, String cropType) {
        String key = col + "," + row;

        System.out.println("=== PlantCrop Debug ===");
        System.out.println("Position: col=" + col + ", row=" + row);
        System.out.println("Crop type: " + cropType);

        // ตรวจสอบว่าอยู่ในขอบเขตแผนที่หรือไม่
        if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
            System.out.println("ERROR: Position out of bounds!");
            return false;
        }

        // ตรวจสอบว่าช่องนี้ปลูกพืชแล้วหรือยัง
        if (crops.containsKey(key)) {
            System.out.println("ERROR: This tile already has a crop!");
            return false;
        }

        // ตรวจสอบว่าเป็นดินที่พรวนแล้วหรือไม่ (tile 22)
        int tileNum = gp.tileM.mapTileNum[col][row];
        System.out.println("Current tile ID: " + tileNum);

        if (tileNum != 22) {
            System.out.println("ERROR: You need to hoe this tile first! (tile must be 22, but is " + tileNum + ")");
            return false;
        }

        // สร้างพืชใหม่
        Crop newCrop = new Crop(cropType);
        crops.put(key, newCrop);

        // เปลี่ยน tile เป็นต้นอ่อน
        int newTileID = newCrop.getTileID();
        gp.tileM.mapTileNum[col][row] = newTileID;

        System.out.println("SUCCESS: Planted " + cropType + " at (" + col + ", " + row + ")");
        System.out.println("New tile ID: " + newTileID);
        System.out.println("======================");
        return true;
    }

    // อัปเดตพืชทั้งหมด
    public void update() {
        for (Map.Entry<String, Crop> entry : crops.entrySet()) {
            String key = entry.getKey();
            Crop crop = entry.getValue();

            int oldStage = crop.growthStage;
            crop.update();

            // ถ้าขั้นเติบโตเปลี่ยน ให้อัปเดต tile
            if (crop.growthStage != oldStage) {
                String[] coords = key.split(",");
                int col = Integer.parseInt(coords[0]);
                int row = Integer.parseInt(coords[1]);

                gp.tileM.mapTileNum[col][row] = crop.getTileID();

                if (crop.isHarvestable()) {
                    System.out.println(crop.cropType + " at (" + col + ", " + row + ") is ready to harvest!");
                }
            }
        }
    }

    // เก็บเกี่ยวพืช
    public boolean harvestCrop(int col, int row) {
        String key = col + "," + row;

        System.out.println("=== HarvestCrop Debug ===");
        System.out.println("Position: col=" + col + ", row=" + row);
        System.out.println("Key: " + key);
        System.out.println("Crops in manager: " + crops.size());
        System.out.println("Crop keys: " + crops.keySet());

        if (!crops.containsKey(key)) {
            System.out.println("ERROR: No crop at this position!");
            System.out.println("========================");
            return false;
        }

        Crop crop = crops.get(key);

        System.out.println("Found crop: " + crop.cropType);
        System.out.println("Growth stage: " + crop.growthStage + "/3");

        if (!crop.isHarvestable()) {
            System.out.println("ERROR: Crop is not ready yet! Stage: " + crop.growthStage + "/3");
            long timePassed = System.currentTimeMillis() - crop.plantTime;
            long timeNeeded = crop.growthDuration * 3;
            long timeRemaining = timeNeeded - timePassed;
            System.out.println("Time remaining: " + (timeRemaining / 1000) + " seconds");
            System.out.println("========================");
            return false;
        }

        // เพิ่มผลผลิตในกระเป๋า
        gp.player.addItemToInventory(crop.cropType, 1);

        // ลบพืชออกจากระบบ
        crops.remove(key);

        // เปลี่ยน tile กลับเป็นดินพรวน
        gp.tileM.mapTileNum[col][row] = 22;

        System.out.println("SUCCESS: Harvested " + crop.cropType + "!");
        System.out.println("========================");
        return true;
    }

    // ตรวจสอบว่าช่องนี้มีพืชหรือไม่
    public boolean hasCrop(int col, int row) {
        String key = col + "," + row;
        return crops.containsKey(key);
    }

    // ดึงข้อมูลพืชที่ช่องนั้น
    public Crop getCrop(int col, int row) {
        String key = col + "," + row;
        return crops.get(key);
    }
}