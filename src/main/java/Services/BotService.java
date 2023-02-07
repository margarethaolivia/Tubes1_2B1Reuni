package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }

    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        // Initial actionnya maju
        playerAction.action = PlayerActions.FORWARD;

        if (!gameState.getGameObjects().isEmpty()) {
            // A. Defining some important data yang sekiranya bakal membantu selama proses pembuatan bot
            // 1. Daftar food dan jaraknya yang ada di dalam map
            var foodList = gameState.getGameObjects()
                // Ambil yang object type nya food
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
            
            // 2. Daftar player lain (lawan) dan jaraknya yang ada di dalam map
            var otherPlayerList = gameState.getGameObjects()
                // Ambil yang object type nya player, tapi bukan id kita (alias orang lain)
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER && item.getId() != bot.id)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
            
            // 3. Daftar gas cloud dan jaraknya yang ada di dalam map
            var gasCloudList = gameState.getGameObjects()
                // Ambil yang object type nya gas clouds
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
            
            // B. Possible simple actions yang bisa dilakuin
            // 0. Bikin dulu arah yang bakal dilaluin, defaultnya nyari makan terdekat
            int heading = getHeadingBetween(foodList.get(0));
            // 1. Kalo pas nyari makan ternyata makanan terdekatnya deket sama ujung peta,
            // dia mengarah ke tengah (pusat peta)
            if (getDistanceToCenter() > getGameState().getWorld().getRadius() - bot.getSize() * 2) {
                heading = getHeadingToCenter();
            }
            // 2. Menjauhi gas cloud terdekat Pastiin dulu kalo ada gas clouds
            else if (!gasCloudList.isEmpty()) {
                if (getDistanceBetween(bot, gasCloudList.get(0)) <= bot.getSize() * 2 +  gasCloudList.get(0).getSize()) {
                    heading = (getHeadingBetween(gasCloudList.get(0)) + 90) % 360; // validasi antara 0 dan 360
                }
            }
            // 3. Interaksi terhadap other player
            else if (!otherPlayerList.isEmpty()) {
                // Kalo player terdekat lebih gede, kaburrr
                if ((getDistanceBetween(bot, otherPlayerList.get(0)) <= bot.getSize() * 2 +  otherPlayerList.get(0).getSize()) && (otherPlayerList.get(0).getSize() > bot.getSize())) {
                    // putar balik
                    heading = -1 * getHeadingBetween(otherPlayerList.get(0));
                }
                // Kalo sama atau lebih gede? coba handle disini yaa
            }
            
            // C. Insert greedy algorithm implementation here

            // mastiin ukuran bot cukup besar buat fire torpedo dan jarak kita deket sama lawan
            if (bot.getSize() >= 18 && (getDistanceBetween(bot, otherPlayerList.get(0)) < bot.getSize() * 2 + otherPlayerList.get(0).getSize())) {
                
                // Use torpedo salvo when lawan kita udah gede (kira2 2 till 3 times)
                if ((otherPlayerList.get(0).getSize() > 2 * bot.getSize())) {
                    // heading = getHeadingBetween(otherPlayerList.get(0));
                    playerAction.action = PlayerActions.FIRETORPEDOES;
                }
            }


            // D. Arahin player ke heading yang kita tuju sesuai state yang dibaca sama bot
            playerAction.heading = heading;
        }

        // Return in implicit pointer values to pass
        this.playerAction = playerAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    // Fungsi buatan Leon - getDistanceToCenter
    // Tujuannya simply nyari jarak antara kita ke tengah
    private double getDistanceToCenter() {
        var triangleX = Math.abs(bot.getPosition().x);
        var triangleY = Math.abs(bot.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    // Fungsi buatan Leon - getHeadingToCenter
    // Ngarahin si bot ke tengah peta
    private int getHeadingToCenter() {
        var direction = toDegrees(Math.atan2(-1 * bot.getPosition().y, -1 * bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }
}