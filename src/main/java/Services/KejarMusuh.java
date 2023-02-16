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

    // 2. Fungsi yang melakukan konversi dari radian menjadi derajat
    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    // 3. Prosedur aksi yang dilakukan pemain saat bertemu pemain lawan
    public void ketemuMusuh(GameObject bot, PlayerAction playerAction, GameObject pemainLainTerdekat) {
        // 3.1. Kalo musuh lebih besar
        if ((pemainLainTerdekat.getSize() >= bot.getSize())) {
            // System.out.println("Ukuran lebih besarrr");
            // Kalo ukuran bot diatas 50, artinya bot bisa nembak
            if (bot.getSize() >= 50) {
                // System.out.println("Tembak!");
                playerAction.heading = getHeadingBetween(bot, pemainLainTerdekat);
                playerAction.action = PlayerActions.FIRETORPEDOES;
            } 
            // Kalo ga sampe 50, mending kabur ke arah belawanan si bot lawan
            else {
                // System.out.println("Kabur");
                playerAction.heading = (-1 * getHeadingBetween(bot, pemainLainTerdekat)) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
        } 
        // 3.2. Kalo musuh lebih kecil
        else {
            // System.out.println("Ukuran lebih kecil nih");
            // Kalo ukuran bot diatas 50, artinya bot bisa nembak
            if (bot.getSize() >= 50) {
                // System.out.println("Tembak!");
                playerAction.heading = getHeadingBetween(bot, pemainLainTerdekat);
                playerAction.action = PlayerActions.FIRETORPEDOES;
            } 
            // Kalo ga sampe 50, setidaknya kita lebih besar buat makan dia, arahkan ke dekatnya
            else {
                // System.out.println("Makan");
                playerAction.heading = getHeadingBetween(bot, pemainLainTerdekat);
                playerAction.action = PlayerActions.FORWARD;
            }
        }
    }
}
