package entity;

public class Crop {
    public String cropType;
    public int growthStage;
    public long plantTime;
    public long growthDuration;
    public float speedMultiplier = 1.0f; // <<<< เพิ่ม multiplier สำหรับความเร็ว

    public Crop(String cropType) {
        this.cropType = cropType;
        this.growthStage = 0;
        this.plantTime = System.currentTimeMillis();

        switch(cropType) {
            case "Wheat":
                this.growthDuration = 15000; // 15 วินาทีต่อขั้น
                break;
            case "Corn":
                this.growthDuration = 20000; // 20 วินาทีต่อขั้น
                break;
            case "Tomato":
                this.growthDuration = 25000; // 25 วินาทีต่อขั้น
                break;
            case "Carrot": // <<<< เพิ่มใหม่
                this.growthDuration = 18000; // 18 วิ
                break;
            default:
                this.growthDuration = 20000;
        }
    }

    // <<<< ฟังก์ชันตั้งค่า speed multiplier
    public void setSpeedMultiplier(float multiplier) {
        this.speedMultiplier = multiplier;
    }

    // <<<< อัปเดตสถานะการเติบโต (รองรับ multiplier)
    public void update() {
        if (growthStage < 3) {
            long currentTime = System.currentTimeMillis();
            long timePassed = currentTime - plantTime;

            // <<<< คำนวณระยะเวลาที่ปรับด้วย multiplier
            long adjustedDuration = (long)(growthDuration / speedMultiplier);

            int newStage = (int)(timePassed / adjustedDuration);
            if (newStage > 3) {
                newStage = 3;
            }
            growthStage = newStage;
        }
    }

    public boolean isHarvestable() {
        return growthStage == 3;
    }

    public int getTileID() {
        switch(cropType) {
            case "Wheat":
                return 23 + growthStage;
            case "Corn":
                return 27 + growthStage;
            case "Tomato":
                return 31 + growthStage;
            case "Carrot": // <<<< เพิ่มใหม่
                return 35 + growthStage;
            case "Strawberry":
                return 39 + growthStage;
            case "Pumpkin":
                return 43 + growthStage;
            case "Cherry":
                return 47 + growthStage;
            default:
                return 23;
        }
    }

    // <<<< ฟังก์ชันคำนวณเวลาที่เหลือ (สำหรับแสดงผล)
    public long getRemainingTime() {
        if (growthStage >= 3) return 0;

        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - plantTime;
        long adjustedDuration = (long)(growthDuration / speedMultiplier);
        long totalTime = adjustedDuration * 3;
        long remaining = totalTime - timePassed;

        return Math.max(0, remaining);
    }
}