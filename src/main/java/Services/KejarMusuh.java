package Services;

import Enums.*;
import Models.*;

public class KejarMusuh {
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
    
    // 4. Prosedur Teleport yang dilkaukan bot
    public void teleportSekarang(GameObject bot, PlayerAction playerAction, int radiusPeta, GameObject object) {
        playerAction.heading = getHeadingBetween(bot, object);
        playerAction.action = PlayerActions.FIRETELEPORT;
        // Pendefinisian persamaan kinetika 1 dimensi
        double jarak = getDistanceBetween(bot, object);
        int vtelp = 20;
        int init = radiusPeta;
        double finals = init - (jarak / vtelp);
        int finpos = (int) Math.round(finals);
        // Memenuhi kondisi waktu melalui radius peta
        if ((radiusPeta - finpos >= -2) && (radiusPeta - finpos <= 2)) {
            System.out.println("Teleported!!");
            playerAction.action = PlayerActions.TELEPORT;
        }
    }

    // 5. Prosedur aksi yang dilakukan pemain saat bertemu pemain lawan
    public void ketemuMusuh(GameObject bot, PlayerAction playerAction, GameObject pemainLainTerdekat) {
        if ((pemainLainTerdekat.getSize() >= bot.getSize())) {
            System.out.println("Ukuran lebih besarrr");
            // 9. Kalo ukuran bot diatas 50, artinya bot bisa nembak
            if (bot.getSize() >= 50) {
                System.out.println("Tembak!");
                playerAction.heading = getHeadingBetween(bot, pemainLainTerdekat);
                playerAction.action = PlayerActions.FIRETORPEDOES;
            } 
            // 10. Kalo ga sampe 50, mending kabur ke awarh belawanan si bot lawan
            else {
                System.out.println("Kabur");
                playerAction.heading = (-1 * getHeadingBetween(bot, pemainLainTerdekat)) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
        } 
        // 11. Kalo musuh lebih kecil
        else {
            System.out.println("Ukuran lebih kecil nih");
            // 12. Kalo ukuran pemain lawan terdekat kurang dari 1.5 kali ukuran bot, makan bot lawan
            if (bot.getSize() >= 50) {
                System.out.println("Tembak!");
                playerAction.heading = getHeadingBetween(bot, pemainLainTerdekat);
                playerAction.action = PlayerActions.FIRETORPEDOES;
            } 
            // 14. Kalo ukuran pemain lawan terdekat kurang dari 1.5 kali ukuran bot, makan bot lawan
            else {
                System.out.println("Makan");
                playerAction.heading = getHeadingBetween(bot, pemainLainTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            }
        }
    }
}
