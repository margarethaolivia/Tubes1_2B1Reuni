package Services;

import Enums.*;
import Models.*;

public class Makan {
    // 1. Fungsi untuk mengambil arah yang diberikan objek bot ke otherObject
    private int getHeadingBetween(GameObject bot, GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
            otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    // 2. Fungsi yang mengambil besarnya jarak antara dua objek
    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    // 3. Fungsi yang melakukan konversi dari radian menjadi derajat
    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }
    
    // 4. Prosedur aksi yang dilakukan pemain bot mencari makan
    public void cariMakan(GameObject bot, PlayerAction playerAction, int radiusPeta, GameObject superFoodTerdekat, GameObject superFoodTerdekat2, GameObject foodTerdekat, GameObject foodTerdekat2) {
        // 4.1. Kalo jarak ke superfood > 1.25 * food dengan pembobotan, atur makan foodlist
        if (getDistanceBetween(bot, superFoodTerdekat) > 1.25 * getDistanceBetween(bot, foodTerdekat)) {
            // Kalo ukuran kapal udah gede (1/5 radius peta), jangan makan lagi
            if (5 * bot.size >= radiusPeta) {} 
            // Kalo jarak ke 2 makanan berbeda itu sama, pilih indeks terkecil
            else if (getDistanceBetween(bot, foodTerdekat) == getDistanceBetween(bot, foodTerdekat2)) {
                playerAction.heading = getHeadingBetween(bot, foodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            } 
            // Kasus normal, cukup ambil makanan terdekat
            else {
                playerAction.heading = getHeadingBetween(bot, foodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            }
        } 
        // 4.2. Kalo jarak ke superfood <= 1.25 * food dengan pembobotan, atur makan superFoodlist
        else {
            // Kalo ukuran kapal udah gede (1/5 radius peta), jangan makan lagi
            if (5 * bot.size >= radiusPeta) {}
            // Kalo jarak ke 2 makanan berbeda itu sama, pilih indeks terkecil
            else if (getDistanceBetween(bot, superFoodTerdekat) == getDistanceBetween(bot, superFoodTerdekat2)) {
                playerAction.heading = getHeadingBetween(bot, superFoodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            } 
            // Kasus normal, cukup ambil makanan terdekat
            else {
                playerAction.heading = getHeadingBetween(bot, superFoodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            }
        }
    }
}