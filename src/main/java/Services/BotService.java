package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private Position centralMap = new Position(0, 0);

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
        if (!gameState.getGameObjects().isEmpty()) {
            // A. Defining some important data yang sekiranya bakal membantu selama proses pembuatan bot
            // 1. Daftar food dan jaraknya yang ada di dalam map
            var foodList = gameState.getGameObjects()
                // Ambil yang object type nya food
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

            // 2. Daftar superfood dan jaraknya yang ada di dalam map
            var superFoodList = gameState.getGameObjects()
                // Ambil yang object type nya food
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
            
            // 3. Daftar player lain (lawan) dan jaraknya yang ada di dalam map
            var otherPlayerList = gameState.getPlayerGameObjects()
                // Ambil yang object type nya player, tapi bukan id kita (alias orang lain)
                .stream().filter(item -> item.getId() != bot.id)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
            
            // 4. Daftar gas cloud dan jaraknya yang ada di dalam map
            var gasCloudList = gameState.getGameObjects()
                // Ambil yang object type nya gas clouds
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

            // 5. Daftar wormhole dan jaraknya yang ada di dalam map
            var wormHoleList = gameState.getGameObjects()
                // Ambil yang object type nya wormhole
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.WORMHOLE)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
            
            // B. Greedy Implementation here
            // 1. Greedy by other player -- fokus ke nembak
            if (!otherPlayerList.isEmpty()) {
                // 0 . cek aja kalo tadinya afterburnernya nyala, matiin
                if (playerAction.action == PlayerActions.STARTAFTERBURNER) {
                    System.out.println("Matiin AB biar ga boros");
                    playerAction.action = PlayerActions.STOPAFTERBURNER;
                }
                // Kalo player terdekat lebih gede, kaburrr
                System.out.println("Mencari musuh...");
                playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                playerAction.action = PlayerActions.FORWARD;
                int jarakmaks = 0;  // nilai awal
                // cari yang lebih besar
                if (bot.getSize() > otherPlayerList.get(0).getSize()) {
                    jarakmaks = bot.getSize();   
                } else {
                    jarakmaks = otherPlayerList.get(0).getSize();
                }
                if ((getDistanceBetween(bot, otherPlayerList.get(0)) <= bot.getSize() + otherPlayerList.get(0).getSize() + jarakmaks)) {
                    System.out.println("Musuh dekat!");
                    if ((otherPlayerList.get(0).getSize() >= bot.getSize())) {
                        System.out.println("Ukuran lebih besarrr");
                        if (bot.getSize() >= 75) {
                            // kalo cukup nembak
                            System.out.println("Tembak!");
                            playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                            playerAction.action = PlayerActions.FIRETORPEDOES;
                        } else {
                            // kalo ga, putar balik
                            System.out.println("Kaburrrr");
                            playerAction.heading = (-1 * getHeadingBetween(otherPlayerList.get(0))) % 360;
                            playerAction.action = PlayerActions.FORWARD;
                        }
                    } else {
                        System.out.println("Ukuran lebih kecil nih");
                        if (bot.getSize() >= 75) {
                            // kalo cukup nembak
                            System.out.println("Tembak!");
                            playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                            playerAction.action = PlayerActions.FIRETORPEDOES;
                        } 
                        if ((otherPlayerList.get(0).getSize() <= 1.25 * bot.getSize())) {
                            // kalo agak jauh mending makan
                            System.out.println("Makann");
                            playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                            playerAction.action = PlayerActions.FORWARD;
                        }
                    }
                } else {
                    System.out.println("Musuh terlalu jauh, mencari makan terdekat..");
                    if (2 * getDistanceBetween(bot, superFoodList.get(0)) <= 2.5 * getDistanceBetween(bot, foodList.get(0))) {
                        System.out.println("Deket sama superfood nih");
                        if (5 * bot.size >= getGameState().getWorld().radius) {
                            System.out.println("Ukuranku udah kegedean :( jangan makan");
                        } else {
                            System.out.println("Aku masih kecil butuh asupan superfood :)");
                            playerAction.heading = getHeadingBetween(superFoodList.get(0));
                            playerAction.action = PlayerActions.FORWARD;
                        }
                    } else {
                        System.out.println("gaada superfood terdekat");
                        if (5 * bot.size >= getGameState().getWorld().radius) {
                            System.out.println("Ukuranku udah kegedean :( jangan makan");
                        } else {
                            System.out.println("Aku masih kecil butuh asupan food :)");
                            playerAction.heading = getHeadingBetween(foodList.get(0));
                            playerAction.action = PlayerActions.FORWARD;
                        }
                    }
                }
            } else {
                System.out.println("MENANG!!");
            }

            if (!wormHoleList.isEmpty()) {
                if (getDistanceBetween(bot, wormHoleList.get(0)) > getDistanceToPoint(centralMap)) {
                    if (wormHoleList.get(0).getSize() > bot.getSize()) {
                        playerAction.heading = getHeadingBetween(wormHoleList.get(0)) % 360; // validasi antara 0 dan 360
                        playerAction.action = PlayerActions.FORWARD;
                    }
                }
            }

            // Kepepet mechanism
            if (!gasCloudList.isEmpty()) {
                if (getDistanceBetween(bot, gasCloudList.get(0)) > 0.75 * gasCloudList.get(0).getSize()) {
                    System.out.println("Deket gas cloud!");
                    if (bot.size >= 350) {
                        System.out.println("Cabut GAASS");
                        // playerAction.heading = (getHeadingBetween(gasCloudList.get(0)) + 90) % 360; // validasi antara 0 dan 360
                        playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                        playerAction.action = PlayerActions.STARTAFTERBURNER;
                    } else {
                        System.out.println("Menghindar pelan");
                        // playerAction.heading = (getHeadingBetween(gasCloudList.get(0)) + 90) % 360; // validasi antara 0 dan 360
                        playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                        playerAction.action = PlayerActions.FORWARD;
                    }
                }
            }

            if (getDistanceToPoint(centralMap) > 0.75 * (getGameState().getWorld().radius)) {
                System.out.println("Bahaya kena ujung! menghindar ke tengah");
                if (bot.size >= 350) {
                    System.out.println("Cabut GAASS");
                    // playerAction.heading = getHeadingToPoint(centralMap);
                    playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                    playerAction.action = PlayerActions.STARTAFTERBURNER;
                } else {
                    System.out.println("Menghindar pelan");
                    // playerAction.heading = getHeadingToPoint(centralMap);
                    playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                    playerAction.action = PlayerActions.FORWARD;
                }
            }
            
            // C. Nota
            System.out.println(otherPlayerList);
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
    private double getDistanceToPoint(Position target) {
        var triangleX = Math.abs(bot.getPosition().x - target.x);
        var triangleY = Math.abs(bot.getPosition().y - target.y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    // Fungsi buatan Leon - getHeadingToCenter
    // Ngarahin si bot ke tengah peta
    private int getHeadingToPoint(Position target) {
        var direction = toDegrees(Math.atan2(target.y - bot.getPosition().y, target.x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }
}