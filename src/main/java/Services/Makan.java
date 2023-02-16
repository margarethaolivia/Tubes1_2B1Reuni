package Services;

import Enums.*;
import Models.*;

public class Makan {
    private int getHeadingBetween(GameObject bot, GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
            otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }
    
    public void cariMakan(GameObject bot, PlayerAction playerAction, int radiusPeta, GameObject superFoodTerdekat, GameObject superFoodTerdekat2, GameObject foodTerdekat, GameObject foodTerdekat2) {
        // 28. Kalo jarak ke superfood > 1.25 * food dengan pembobotan, atur makan foodlist
        if (getDistanceBetween(bot, superFoodTerdekat) > 1.25 * getDistanceBetween(bot, foodTerdekat)) {
            // System.out.println("gaada superfood terdekat, ambil food");
            // 29. Kalo ukuran kapal udah gede, jangan makan lagi
            if (5 * bot.size >= radiusPeta) {
                // System.out.println("Ukuranku udah kegedean :( jangan makan");
            } 
            // 30. Kalo blm, ya silakan makan
            else if (getDistanceBetween(bot, foodTerdekat) == getDistanceBetween(bot, foodTerdekat2)) {
                // System.out.println("Aku masih kecil butuh asupan food 1 :)");
                playerAction.heading = getHeadingBetween(bot, foodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            } else {
                // System.out.println("Aku masih kecil butuh asupan food 2 :)");
                playerAction.heading = getHeadingBetween(bot, foodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            }
        } 
        // 31. Kalo jarak ke superfood <= 1.25 * food dengan pembobotan, atur makan superFoodlist
        else if (getDistanceBetween(bot, superFoodTerdekat) <= 1.25 * getDistanceBetween(bot, foodTerdekat)) {
            // System.out.println("Deket sama superfood nih");
            // 32. Kalo ukuran kapal udah gede, jangan makan lagi
            if (5 * bot.size >= radiusPeta) {
                // System.out.println("Ukuranku udah kegedean :( jangan makan");
            } 
            // 33. Kalo blm, ya silakan makan
            else if (getDistanceBetween(bot, superFoodTerdekat) == getDistanceBetween(bot, superFoodTerdekat2)) {
                // System.out.println("Aku masih kecil butuh asupan superfood 1 :)");
                playerAction.heading = getHeadingBetween(bot, superFoodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            } else {
                // System.out.println("Aku masih kecil butuh asupan superfood 2 :)");
                playerAction.heading = getHeadingBetween(bot, superFoodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            }
        } // 34. Kalo gaada superfood terdekat dan wormhole empty, nyari makan biasa
        else {
            // System.out.println("gaada superfood terdekat, wormhole empty");
            // 35. Kalo ukuran kapal udah gede, jangan makan lagi
            if (5 * bot.size >= radiusPeta) {
                // System.out.println("Ukuranku udah kegedean :( jangan makan");
            } 
            // 36. Kalo blm, ya silakan makan
            else if (getDistanceBetween(bot, foodTerdekat) == getDistanceBetween(bot, foodTerdekat2)) {
                // System.out.println("Aku masih kecil butuh asupan food 1 :)");
                playerAction.heading = getHeadingBetween(bot, foodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            } else {
                // System.out.println("Aku masih kecil butuh asupan food 2 :)");
                playerAction.heading = getHeadingBetween(bot, foodTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            }
        }
    }
}
