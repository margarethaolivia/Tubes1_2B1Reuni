package Services;

import Enums.*;
import Models.*;

public class ProteksiMap {
    // Fungsi buatan Leon - getDistanceToCenter
    // Tujuannya simply nyari jarak antara kita ke tengah
    private double getDistanceToPoint(GameObject bot, Position target) {
        var triangleX = Math.abs(bot.getPosition().x - target.x);
        var triangleY = Math.abs(bot.getPosition().y - target.y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    // Fungsi buatan Leon - getHeadingToCenter
    // Ngarahin si bot ke tengah peta
    private int getHeadingToPoint(GameObject bot, Position target) {
        var direction = toDegrees(Math.atan2(target.y - bot.getPosition().y, target.x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }
    
    // Fungsi buatan Leon
    public boolean dekatUjung(GameObject bot, Position centralMap, int radiusPeta) {
        return getDistanceToPoint(bot, centralMap) > 0.6 * (radiusPeta);
    }

    public void bahayaUjung(GameObject bot, Position centralMap, PlayerAction playerAction, int radiusPeta) {
        if (getDistanceToPoint(bot, centralMap) > 0.6 * radiusPeta) {
            System.out.println("Bahaya kena ujung! menghindar ke tengah");
            playerAction.heading = getHeadingToPoint(bot, centralMap);
            playerAction.action = PlayerActions.FORWARD;
        } else {
            // Panggil method cari musuh
        }
    }
}