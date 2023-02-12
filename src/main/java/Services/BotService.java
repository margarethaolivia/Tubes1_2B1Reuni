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
        System.out.println(getGameState().getWorld().radius);
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

            // 6. Daftar player lain (lawan) dan ukurannya yang ada di dalam map
            var otherPlayerListSize = gameState.getPlayerGameObjects()
                // Ambil yang object type nya player, tapi bukan id kita (alias orang lain)
                .stream().filter(item -> item.getId() != bot.id)
                .sorted(Comparator.comparing(item -> getSizeBetween(bot, item)))
                .collect(Collectors.toList());

            // 7. Daftar teleport dan jaraknya yang ada di dalam map
            var teleporterList = gameState.getGameObjects()
                // Ambil yang object type nya gas clouds
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
            
            // B. Greedy Implementation here
            // Greedy by other player -- fokus ke nembak
            // 0. Pertama kali make sure ada pemain lain, atau list pemain lain tidak kosong
            if (!otherPlayerList.isEmpty()) {
                // 2. Mencari ukuran maksimal antara bot dan pemain lain terdekat sebagai perantara
                int jarakmaks, jarakmin; 
                if (bot.getSize() > otherPlayerList.get(0).getSize()) {
                    jarakmaks = bot.getSize();
                    jarakmin = otherPlayerList.get(0).getSize();   
                } else {
                    jarakmaks = otherPlayerList.get(0).getSize();
                    jarakmin = bot.getSize();
                }

                // 3. Cek Kondisi pemain, jika pemain dekat ujung peta, lakukan kemungkinan aksi
                // untuk menerkam
                if (getDistanceToPoint(centralMap) > 0.6 * (getGameState().getWorld().radius)) {
                    System.out.println("Bahaya kena ujung! menghindar ke tengah");
                    playerAction.heading = getHeadingToPoint(centralMap);
                    playerAction.action = PlayerActions.FORWARD;
                } 
                // 6. Kalo pemain tidak diujung peta, lakukan pencarian musuh
                else {
                    System.out.println("Mencari musuh...");
                    playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                    playerAction.action = PlayerActions.FORWARD;
                    // 7. Kalo jarak pemain dan bot dekat, cek kondisi ukuran musuh
                    if ((getDistanceBetween(bot, otherPlayerList.get(0)) < 1.5 * bot.getSize())) {
                        playerAction.heading = (-1 * getHeadingBetween(otherPlayerList.get(0))) % 360;
                        playerAction.action = PlayerActions.FIRETELEPORT;
                        if (getDistanceBetween(bot, otherPlayerList.get(0)) < 0.5 * bot.getSize()) {
                            playerAction.action = PlayerActions.TELEPORT;
                        }
                    } 
                    else if ((getDistanceBetween(bot, otherPlayerList.get(0)) <= bot.getSize() + otherPlayerList.get(0).getSize() + jarakmaks)) {
                        System.out.println("Musuh dekat!");
                        // 8. Kalo musuh lebih besar
                        if ((otherPlayerList.get(0).getSize() >= bot.getSize())) {
                            System.out.println("Ukuran lebih besarrr");
                            // 9. Kalo ukuran bot diatas 50, artinya bot bisa nembak
                            if (bot.getSize() >= 50) {
                                System.out.println("Tembak!");
                                playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                                playerAction.action = PlayerActions.FIRETORPEDOES;
                            } 
                            // 10. Kalo ga sampe 50, mending kabur ke awarh belawanan si bot lawan
                            else {
                                System.out.println("Kaburrrr");
                                playerAction.heading = (-1 * getHeadingBetween(otherPlayerList.get(0))) % 360;
                                playerAction.action = PlayerActions.FORWARD;
                            }
                        } 
                        // 11. Kalo musuh lebih kecil
                        else {
                            System.out.println("Ukuran lebih kecil nih");
                            // 12. Kalo ukuran pemain lawan terdekat kurang dari 1.5 kali ukuran bot, makan bot lawan
                            if ((otherPlayerList.get(0).getSize() <= 1.5 * bot.getSize())) {
                                System.out.println("Makann");
                                playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                                playerAction.action = PlayerActions.FORWARD;
                            } 
                            // 13. Kalo ukuran pemain lawan terdekat kurang dari 1.5 kali ukuran bot, makan bot lawan
                            else if ((otherPlayerList.get(0).getSize() > 1.5 * bot.getSize()) && bot.getSize() >= 50) {
                                System.out.println("Tembak!");
                                playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                                playerAction.action = PlayerActions.FIRETORPEDOES;
                            } 
                            // 14. Kalo ukuran pemain lawan terdekat kurang dari 1.5 kali ukuran bot, makan bot lawan
                            else {
                                System.out.println("Tembak!");
                                playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                                playerAction.action = PlayerActions.FIRETORPEDOES;
                            }
                        }
                    }
                    // 15. Kalo jarak pemain dan bot jauh, melakukan pencarian makan
                    else if ((getDistanceBetween(bot, otherPlayerList.get(0)) > bot.getSize() + otherPlayerList.get(0).getSize() + jarakmaks)) {
                        System.out.println("Musuh terlalu jauh...");
                        // 16. Cek kondisi wormhole, kalo aja, jalankan skema wormhole
                        if (!wormHoleList.isEmpty()) {
                            System.out.println("Ada wormhole! sabi sambil nyari");
                            // 17. Kalo wormhole lebih dekat dari superfood, pilih wormhole
                            if (getDistanceBetween(bot, superFoodList.get(0)) > 1.5 * getDistanceBetween(bot, wormHoleList.get(0))) {
                                if (wormHoleList.get(0).getSize() > bot.getSize()) {
                                    System.out.println("kejar the wormhole then");
                                    playerAction.heading = getHeadingBetween(wormHoleList.get(0)) % 360;
                                    playerAction.action = PlayerActions.FORWARD;
                                }
                            } 
                            // 18. Kalo jarak ke superfood > 1.25 * food dengan pembobotan, atur makan foodlist
                            else if (getDistanceBetween(bot, superFoodList.get(0)) > 1.25 * getDistanceBetween(bot, foodList.get(0))) {
                                System.out.println("gaada superfood terdekat, ambil food");
                                // 19. Kalo ukuran kapal udah gede, jangan makan lagi
                                if (5 * bot.size >= getGameState().getWorld().radius) {
                                    System.out.println("Ukuranku udah kegedean :( jangan makan");
                                } 
                                // 20. Kalo blm, ya silakan makan
                                else {
                                    System.out.println("Aku masih kecil butuh asupan food :)");
                                    playerAction.heading = getHeadingBetween(foodList.get(0));
                                    playerAction.action = PlayerActions.FORWARD;
                                }
                            } 
                            // 21. Kalo jarak ke superfood <= 1.25 * food dengan pembobotan, atur makan superFoodlist
                            else if (getDistanceBetween(bot, superFoodList.get(0)) <= 1.25 * getDistanceBetween(bot, foodList.get(0))) {
                                System.out.println("Deket sama superfood nih");
                                // 22. Kalo ukuran kapal udah gede, jangan makan lagi
                                if (5 * bot.size >= getGameState().getWorld().radius) {
                                    System.out.println("Ukuranku udah kegedean :( jangan makan");
                                } 
                                // 23. Kalo blm, ya silakan makan
                                else {
                                    System.out.println("Aku masih kecil butuh asupan superfood :)");
                                    playerAction.heading = getHeadingBetween(superFoodList.get(0));
                                    playerAction.action = PlayerActions.FORWARD;
                                }
                            } 
                            // 24. Kalo gaada superfood terdekat dan wormhole empty, nyari makan biasa
                            else {
                                System.out.println("gaada superfood terdekat, wormhole empty");
                                // 25. Kalo ukuran kapal udah gede, jangan makan lagi
                                if (5 * bot.size >= getGameState().getWorld().radius) {
                                    System.out.println("Ukuranku udah kegedean :( jangan makan");
                                } 
                                // 26. Kalo blm, ya silakan makan
                                else {
                                    System.out.println("Aku masih kecil butuh asupan food :)");
                                    playerAction.heading = getHeadingBetween(foodList.get(0));
                                    playerAction.action = PlayerActions.FORWARD;
                                }
                            }
                        } 
                        // 27. Kalo gaada wormhole sama sekali di peta
                        else {
                            System.out.println("Gaada yaudah nyari makan");    
                            // 28. Kalo jarak ke superfood > 1.25 * food dengan pembobotan, atur makan foodlist
                            if (getDistanceBetween(bot, superFoodList.get(0)) > 1.25 * getDistanceBetween(bot, foodList.get(0))) {
                                System.out.println("gaada superfood terdekat, ambil food");
                                // 29. Kalo ukuran kapal udah gede, jangan makan lagi
                                if (5 * bot.size >= getGameState().getWorld().radius) {
                                    System.out.println("Ukuranku udah kegedean :( jangan makan");
                                } 
                                // 30. Kalo blm, ya silakan makan
                                else {
                                    System.out.println("Aku masih kecil butuh asupan food :)");
                                    playerAction.heading = getHeadingBetween(foodList.get(0));
                                    playerAction.action = PlayerActions.FORWARD;
                                }
                            } 
                            // 31. Kalo jarak ke superfood <= 1.25 * food dengan pembobotan, atur makan superFoodlist
                            else if (getDistanceBetween(bot, superFoodList.get(0)) <= 1.25 * getDistanceBetween(bot, foodList.get(0))) {
                                System.out.println("Deket sama superfood nih");
                                // 32. Kalo ukuran kapal udah gede, jangan makan lagi
                                if (5 * bot.size >= getGameState().getWorld().radius) {
                                    System.out.println("Ukuranku udah kegedean :( jangan makan");
                                } 
                                // 33. Kalo blm, ya silakan makan
                                else {
                                    System.out.println("Aku masih kecil butuh asupan superfood :)");
                                    playerAction.heading = getHeadingBetween(superFoodList.get(0));
                                    playerAction.action = PlayerActions.FORWARD;
                                }
                            } // 34. Kalo gaada superfood terdekat dan wormhole empty, nyari makan biasa
                            else {
                                System.out.println("gaada superfood terdekat, wormhole empty");
                                // 35. Kalo ukuran kapal udah gede, jangan makan lagi
                                if (5 * bot.size >= getGameState().getWorld().radius) {
                                    System.out.println("Ukuranku udah kegedean :( jangan makan");
                                } 
                                // 36. Kalo blm, ya silakan makan
                                else {
                                    System.out.println("Aku masih kecil butuh asupan food :)");
                                    playerAction.heading = getHeadingBetween(foodList.get(0));
                                    playerAction.action = PlayerActions.FORWARD;
                                }
                            }
                        }
                    } 
                    // 37. Kalo misal posisi pas ditengah, prioritas pilih ke pusat map
                    else {
                        System.out.println("Aku jauh sama apapun ;( aku ketengah ajadeh");
                        playerAction.heading = getHeadingToPoint(centralMap);
                        playerAction.action = PlayerActions.FORWARD;
                    }
                

                    // Cek gas clouds, kalo ada dia bakal nimpa state yang ada
                    /* if (!gasCloudList.isEmpty()) {
                        if (getDistanceBetween(bot, gasCloudList.get(0)) > 0.75 * gasCloudList.get(0).getSize()) {
                            System.out.println("Deket gas cloud!");
                            if (bot.size >= 170) {
                                System.out.println("Cabut GAASS");
                                playerAction.heading = (getHeadingBetween(gasCloudList.get(0)) + 90) % 360; // validasi antara 0 dan 360
                                // playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                                playerAction.action = PlayerActions.STARTAFTERBURNER;
                            } else {
                                System.out.println("Menghindar pelan");
                                playerAction.heading = (getHeadingBetween(gasCloudList.get(0)) + 90) % 360; // validasi antara 0 dan 360
                                // playerAction.heading = getHeadingBetween(otherPlayerList.get(0));
                                playerAction.action = PlayerActions.FORWARD;
                            }
                        }
                    } */
                }

                // Bot akan menembakkan teleport ke musuh dimana (size bot - 20) > size musuh;
                if (bot.getSize() > otherPlayerListSize.get(0).getSize() + jarakmin) { // jarakmin = nilai toleransi
                    playerAction.heading = getHeadingBetween((otherPlayerListSize.get(0)));
                    playerAction.action = PlayerActions.FIRETELEPORT;
                    Position targetPosition = otherPlayerListSize.get(0).getPosition();
                    if (bot.getPosition() == targetPosition) {
                        playerAction.action = PlayerActions.TELEPORT;
                    }
                }
            } 
            // 38. Jika list pemain kosong, maka menang
            else {
                System.out.println("MENANG!!");
            }
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

    // Fungsi buatan Austin - getSizeBetween
    // Nyari perbedaan size bot 1 dengan lainnya
    private int getSizeBetween(GameObject object1, GameObject object2) {
        int size1 = object1.getSize();
        int size2 = object2.getSize();
        int result = 0;
        if (size1 > size2) {
            result = size1 - size2;
        } else {
            result = size2 - size1;
        }
        return result;
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

/*

// Aktifkan Shield untuk proteksi diri dari teleporter orang
if (!teleporterList.isEmpty()) {
    if (getDistanceBetween(bot, otherTeleporterList.get(0)) < 0.5 * bot.getSize()) {
        playerAction.action = PlayerActions.ACTIVATESHIELD;
        playerAction.heading = (-1 * getHeadingBetween(otherTeleporterList.get(0))) % 360;
    }
}

*Tambahan*
// 8. Daftar torpedo salvo dan jaraknya yang ada di dalam map
var torpedoSalvoList = gameState.getGameObjects()
    // Ambil yang object type nya gas clouds
    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO)
    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
    .collect(Collectors.toList());

// Aktifkan Shield untuk proteksi diri dari torpedo salvo orang
if (!torpedoSalvoList.isEmpty()) {
    if (getDistanceBetween(bot, torpedoSalvoList.get(0)) < 0.25 * bot.getSize()) {
        playerAction.action = PlayerActions.ACTIVATESHIELD;
        playerAction.heading = (-1 * getHeadingBetween(otherTeleporterList.get(0))) % 360;
    }
}

 */
}