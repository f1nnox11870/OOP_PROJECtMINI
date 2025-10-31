package tile;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp){
        this.gp = gp;
        tile = new Tile[80]; // <<<< เพิ่มจาก 30 เป็น 40
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap("/maps/map01.txt");
    }

    public void getTileImage(){
        try{
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wood.png"));
            tile[1].collision = true;

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/window.png"));
            tile[2].collision = true;

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/door1.png"));
            tile[3].collision = true;

            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/door2.png"));
            tile[4].collision = true;

            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/roof.png"));
            tile[5].collision = true;

            tile[6] = new Tile();
            tile[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/roofleft.png"));
            tile[6].collision = true;

            tile[7] = new Tile();
            tile[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/roofright.png"));
            tile[7].collision = true;

            tile[8] = new Tile();
            tile[8].image = ImageIO.read(getClass().getResourceAsStream("/tiles/earth.png"));

            tile[9] = new Tile();
            tile[9].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water0.png"));
            tile[9].collision = true;

            tile[10] = new Tile();
            tile[10].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water2.png"));
            tile[10].collision = true;

            tile[11] = new Tile();
            tile[11].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water3.png"));
            tile[11].collision = true;

            tile[12] = new Tile();
            tile[12].image = ImageIO.read(getClass().getResourceAsStream("/tiles/sand.png"));

            tile[13] = new Tile();
            tile[13].image = ImageIO.read(getClass().getResourceAsStream("/tiles/sandgrass.png"));

            tile[14] = new Tile();
            tile[14].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water1.png"));
            tile[14].collision = true;

            tile[15] = new Tile();
            tile[15].image = ImageIO.read(getClass().getResourceAsStream("/tiles/coconuttree.png"));
            tile[15].collision = true;

            tile[16] = new Tile();
            tile[16].image = ImageIO.read(getClass().getResourceAsStream("/tiles/rwater.png"));

            tile[17] = new Tile();
            tile[17].image = ImageIO.read(getClass().getResourceAsStream("/tiles/bwater.png"));

            tile[18] = new Tile();
            tile[18].image = ImageIO.read(getClass().getResourceAsStream("/tiles/lwater.png"));

            tile[19] = new Tile();
            tile[19].image = ImageIO.read(getClass().getResourceAsStream("/tiles/cmtree.png"));
            tile[19].collision = true;

            tile[20] = new Tile();
            tile[20].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tree1.png"));
            tile[20].collision = true;

            tile[21] = new Tile();
            tile[21].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tree2.png"));
            tile[21].collision = true;

            tile[22] = new Tile();
            tile[22].image = ImageIO.read(getClass().getResourceAsStream("/tiles/potting_soil.png"));

            // <<<< เพิ่ม tile สำหรับข้าวสาลี (Wheat) - ขั้น 0-3
            tile[23] = new Tile();
            tile[23].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wheat_stage0.png"));

            tile[24] = new Tile();
            tile[24].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wheat_stage1.png"));

            tile[25] = new Tile();
            tile[25].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wheat_stage2.png"));

            tile[26] = new Tile();
            tile[26].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wheat_stage3.png"));

            // <<<< เพิ่ม tile สำหรับข้าวโพด (Corn) - ขั้น 0-3
            tile[27] = new Tile();
            tile[27].image = ImageIO.read(getClass().getResourceAsStream("/tiles/corn_stage0.png"));

            tile[28] = new Tile();
            tile[28].image = ImageIO.read(getClass().getResourceAsStream("/tiles/corn_stage1.png"));

            tile[29] = new Tile();
            tile[29].image = ImageIO.read(getClass().getResourceAsStream("/tiles/corn_stage2.png"));

            tile[30] = new Tile();
            tile[30].image = ImageIO.read(getClass().getResourceAsStream("/tiles/corn_stage3.png"));

            // <<<< เพิ่ม tile สำหรับมะเขือเทศ (Tomato) - ขั้น 0-3
            tile[31] = new Tile();
            tile[31].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tomato_stage0.png"));

            tile[32] = new Tile();
            tile[32].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tomato_stage1.png"));

            tile[33] = new Tile();
            tile[33].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tomato_stage2.png"));

            tile[34] = new Tile();
            tile[34].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tomato_stage3.png"));

            tile[35] = new Tile();
            tile[35].image = ImageIO.read(getClass().getResourceAsStream("/tiles/carrot_stage0.png"));

            tile[36] = new Tile();
            tile[36].image = ImageIO.read(getClass().getResourceAsStream("/tiles/carrot_stage1.png"));

            tile[37] = new Tile();
            tile[37].image = ImageIO.read(getClass().getResourceAsStream("/tiles/carrot_stage2.png"));

            tile[38] = new Tile();
            tile[38].image = ImageIO.read(getClass().getResourceAsStream("/tiles/carrot_stage3.png"));



            tile[39] = new Tile();
            tile[39].image = ImageIO.read(getClass().getResourceAsStream("/tiles/strawberry_stage0.png"));

            tile[40] = new Tile();
            tile[40].image = ImageIO.read(getClass().getResourceAsStream("/tiles/strawberry_stage1.png"));

            tile[41] = new Tile();
            tile[41].image = ImageIO.read(getClass().getResourceAsStream("/tiles/strawberry_stage2.png"));

            tile[42] = new Tile();
            tile[42].image = ImageIO.read(getClass().getResourceAsStream("/tiles/strawberry_stage3.png"));


            tile[43] = new Tile();
            tile[43].image = ImageIO.read(getClass().getResourceAsStream("/tiles/pumpkin_stage0.png"));

            tile[44] = new Tile();
            tile[44].image = ImageIO.read(getClass().getResourceAsStream("/tiles/pumpkin_stage1.png"));

            tile[45] = new Tile();
            tile[45].image = ImageIO.read(getClass().getResourceAsStream("/tiles/pumpkin_stage2.png"));

            tile[46] = new Tile();
            tile[46].image = ImageIO.read(getClass().getResourceAsStream("/tiles/pumpkin_stage3.png"));


            tile[47] = new Tile();
            tile[47].image = ImageIO.read(getClass().getResourceAsStream("/tiles/cherry_stage0.png"));

            tile[48] = new Tile();
            tile[48].image = ImageIO.read(getClass().getResourceAsStream("/tiles/cherry_stage1.png"));

            tile[49] = new Tile();
            tile[49].image = ImageIO.read(getClass().getResourceAsStream("/tiles/cherry_stage2.png"));

            tile[50] = new Tile();
            tile[50].image = ImageIO.read(getClass().getResourceAsStream("/tiles/cherry_stage3.png"));

            tile[51] = new Tile();
            tile[51].image = ImageIO.read(getClass().getResourceAsStream("/tiles/sandseaBt.png"));

            tile[52] = new Tile();
            tile[52].image = ImageIO.read(getClass().getResourceAsStream("/tiles/sandseaBt.png"));

            tile[53] = new Tile();
            tile[53].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0001sea.png"));

            tile[54] = new Tile();
            tile[54].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0011sea.png"));

            tile[55] = new Tile();
            tile[55].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0012sea.png"));


            tile[56] = new Tile();
            tile[56].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0002sea.png"));

            tile[57] = new Tile();
            tile[57].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0023sea.png"));

            tile[58] = new Tile();
            tile[58].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0024sea.png"));

            tile[59] = new Tile();
            tile[59].image = ImageIO.read(getClass().getResourceAsStream("/tiles/carrots.png"));
            tile[59].collision = true;

            tile[60] = new Tile();
            tile[60].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wooden_pole.png"));
            tile[60].collision = true;

            tile[61] = new Tile();
            tile[61].image = ImageIO.read(getClass().getResourceAsStream("/tiles/roofShopL.png"));
            tile[61].collision = true;

            tile[62] = new Tile();
            tile[62].image = ImageIO.read(getClass().getResourceAsStream("/tiles/roofShopC.png"));
            tile[62].collision = true;

            tile[63] = new Tile();
            tile[63].image = ImageIO.read(getClass().getResourceAsStream("/tiles/roofShopR.png"));
            tile[63].collision = true;

            tile[64] = new Tile();
            tile[64].image = ImageIO.read(getClass().getResourceAsStream("/tiles/roofWL.png"));
            tile[64].collision = true;

            tile[65] = new Tile();
            tile[65].image = ImageIO.read(getClass().getResourceAsStream("/tiles/roofWR.png"));
            tile[65].collision = true;

            tile[66] = new Tile();
            tile[66].image = ImageIO.read(getClass().getResourceAsStream("/tiles/roofWC.png"));
            tile[66].collision = true;

            tile[67] = new Tile();
            tile[67].image = ImageIO.read(getClass().getResourceAsStream("/tiles/potW.png"));
            tile[67].collision = true;








        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath){
        try{
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;
            while (col < gp.maxWorldCol && row < gp.maxWorldRow){
                String line = br.readLine();
                while (col < gp.maxWorldCol) {
                    String numbers[] = line.split(" ");

                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[col][row] = num;
                    col++;
                }
                if(col == gp.maxWorldCol){
                    col = 0;
                    row++;
                }
            }
            br.close();
        }catch (Exception e){

        }
    }

    public void draw(Graphics2D g2){
        int worldCol = 0;
        int worldRow = 0;

        while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow){
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                    worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                    worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                    worldY - gp.tileSize < gp.player.worldY + gp.player.screenY){
                g2.drawImage(tile[tileNum].image,screenX,screenY, gp.tileSize,gp.tileSize,null);
            }

            worldCol++;
            if(worldCol == gp.maxWorldCol){
                worldCol = 0;
                worldRow++;
            }
        }
    }
}