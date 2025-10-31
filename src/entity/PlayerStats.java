package entity;

public class PlayerStats {
    // ระดับอัพเกรดต่างๆ (0-3)
    public int growthSpeedLevel = 0;     // ความเร็วการเติบโตของพืช
    public int harvestYieldLevel = 0;    // จำนวนผลผลิตที่เก็บได้
    public int marketPriceLevel = 0;     // ราคาขายที่สูงขึ้น
    public int toolEfficiencyLevel = 0;  // ประสิทธิภาพเครื่องมือ

    public PlayerStats() {
        // เริ่มต้นทุกอย่างที่ level 0
    }

    // คำนวณ Growth Speed Multiplier
    // Level 0 = 1.0x (ปกติ)
    // Level 1 = 1.25x (เร็วขึ้น 25%)
    // Level 2 = 1.67x (เร็วขึ้น 67%)
    // Level 3 = 2.5x (เร็วขึ้น 150%)
    public float getGrowthSpeedMultiplier() {
        switch (growthSpeedLevel) {
            case 1: return 1.25f;
            case 2: return 1.67f;
            case 3: return 2.5f;
            default: return 1.0f;
        }
    }

    // คำนวณจำนวนผลผลิตเพิ่มเติม
    public int getBonusYield() {
        return harvestYieldLevel; // Level 1 = +1, Level 2 = +2, Level 3 = +3
    }

    // คำนวณราคาขายเพิ่มเติม (เป็น %)
    public float getPriceMultiplier() {
        switch (marketPriceLevel) {
            case 1: return 1.25f;  // +25%
            case 2: return 1.5f;   // +50%
            case 3: return 1.75f;  // +75%
            default: return 1.0f;
        }
    }

    // ตรวจสอบว่าพรวนดินได้ 3x3 หรือไม่
    public boolean hasAreaHoe() {
        return toolEfficiencyLevel >= 1;
    }

    // ตรวจสอบว่าตัดไม้ได้หลายต้นหรือไม่
    public boolean hasAreaCut() {
        return toolEfficiencyLevel >= 2;
    }
}