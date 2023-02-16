package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    static KejarMusuh kejarmusuh = new KejarMusuh();
    static Makan makan = new Makan();
    static ProteksiLawan proteksilawan = new ProteksiLawan();
    static ProteksiMap proteksimap = new ProteksiMap();
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
                // Ambil yang object type nya superfood
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
            
            // 3. Daftar player lain (lawan) dan jaraknya yang ada di dalam map
            var otherPlayerList = gameState.getPlayerGameObjects()
                // Ambil yang object type nya player, tapi bukan id kita (alias orang lain)
                .stream().filter(item -> item.getId() != bot.id)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item))) // urut berdasar jarak
                .collect(Collectors.toList());
            
            // 4. Daftar gas cloud dan jaraknya yang ada di dalam map
            var gasCloudList = gameState.getGameObjects()
                // Ambil yang object type nya gas clouds
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

            // 5. Daftar player lain (lawan) dan ukurannya yang ada di dalam map
            var otherPlayerListSize = gameState.getPlayerGameObjects()
                // Ambil yang object type nya player, tapi bukan id kita (alias orang lain)
                .stream().filter(item -> item.getId() != bot.id)
                .sorted(Comparator.comparing(item -> getSizeBetween(bot, item))) // urut berdasar ukuran
                .collect(Collectors.toList());

            // 6. Daftar teleport dan jaraknya yang ada di dalam map
            var teleporterList = gameState.getGameObjects()
                // Ambil yang object type nya teleporter
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

            // 7. Daftar torpedo salvo dan jaraknya yang ada di dalam map
            var torpedoSalvoList = gameState.getGameObjects()
                // Ambil yang object type nya torpedo salvo
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
            
            // B. Greedy Implementation -- Greedy Implementation
            // xx. Pertama kali ambil state minimum yang mungkin didefinisikan
            int radiusPeta = getGameState().getWorld().radius;
            GameObject pemainLainTerdekat = otherPlayerList.get(0);
            GameObject pemainKecilLainTerdekat = otherPlayerListSize.get(0);
            GameObject superFoodTerdekat = superFoodList.get(0);
            GameObject superFoodTerdekat2 = superFoodList.get(1);
            GameObject foodTerdekat = foodList.get(0);
            GameObject foodTerdekat2 = foodList.get(1);

            // a. Pastikan masih ada pemain lain dalam peta, jika ada, jalankan algoritma greedy
            if (!otherPlayerList.isEmpty()) {
                System.out.println(torpedoSalvoList);
                // 1.a. State utama - Edge Map Handling
                if (getDistanceToPoint(centralMap) > 0.75 * (getGameState().getWorld().radius)) {
                    System.out.println("Bahaya kena ujung! menghindar ke tengah");
                    playerAction.heading = getHeadingToPoint(centralMap);
                    playerAction.action = PlayerActions.FORWARD;
                } 
                // 1.b. Normal Case - Greedy Algorithm
                else {
                    // i. Berada pada zona tembak musuh dan ukuran masih kecil
                    if (zonaTembakMusuh(pemainLainTerdekat) && masihKecil()) {
                        System.out.println("Strategi Jelajah 1 - Masih Kecil");
                        makan.cariMakan(bot, playerAction, radiusPeta, superFoodTerdekat, superFoodTerdekat2, foodTerdekat, foodTerdekat2);
                    }
                    // ii. Tidak berada pada zona tembak musuh dan ukuran masih kecil
                    else if (!zonaTembakMusuh(pemainLainTerdekat) && masihKecil()) {
                        System.out.println("Strategi Jelajah 2 - Bukan Zona Tembak");
                        makan.cariMakan(bot, playerAction, radiusPeta, superFoodTerdekat, superFoodTerdekat2, foodTerdekat, foodTerdekat2);
                    }
                    // iii. Berada pada zona tembak musuh tetapi ukuran sudah besar
                    else if (zonaTembakMusuh(pemainLainTerdekat) && !masihKecil()) {
                        System.out.println("Strategi Serang - Sudah Matang");
                        kejarmusuh.ketemuMusuh(bot, playerAction, pemainLainTerdekat);
                    }
                    // iv. Tidak berada pada zona tembak musuh tetapi ukuran sudah besar 
                    else {
                        System.out.println("Mendekatkan ke Zona Serang");
                        playerAction.heading = getHeadingBetween(bot, pemainLainTerdekat);
                        playerAction.action = PlayerActions.FORWARD;
                    }
                }

                // 2. State kedua - Shield for Teleporter
                if (!teleporterList.isEmpty()) {
                    // Kondisi teleporter berada pada -20 <= heading <= 20 dari bot
                    if ((teleporterList.get(0).currentHeading - getHeadingBetween(teleporterList.get(0), bot)) >= -20 && (teleporterList.get(0).currentHeading - getHeadingBetween(teleporterList.get(0), bot)) <= 20) {
                        System.out.println("Addcons 2 terpenuhi, ada teleporter lawan");
                        // Pendefinisian persamaan kinetika 1 dimensi
                        double jarak = getDistanceBetween(bot, teleporterList.get(0));
                        int vtelp = 20;
                        int init = radiusPeta;
                        double finals = init - (jarak / vtelp);
                        int finpos = (int) Math.round(finals);
                        // Memenuhi kondisi waktu melalui radius peta
                        if ((radiusPeta - finpos >= -2) && (radiusPeta - finpos <= 2)) {
                            System.out.println("Aktivasi shield");
                            playerAction.action = PlayerActions.ACTIVATESHIELD;
                        }
                    }
                }

                // 3. State ketiga - Fire Teleporter
                if (bot.getSize() - 20 > pemainKecilLainTerdekat.getSize() && bot.getSize() >= 80) {
                    // Kondisi ada player dengan ukuran lebih kecil tapi ukuran kita udah cukup stabil
                    System.out.println("Addcons 1 terpenuhi, ready to fire teleport!");
                    // Pendefinisian persamaan kinetika 1 dimensi
                    playerAction.heading = getHeadingBetween(bot, pemainKecilLainTerdekat);
                    playerAction.action = PlayerActions.FIRETELEPORT;
                    double jarak = getDistanceBetween(bot, pemainKecilLainTerdekat);
                    int vtelp = 20;
                    int init = radiusPeta;
                    double finals = init - (jarak / vtelp);
                    int finpos = (int) Math.round(finals);
                    // Memenuhi kondisi waktu tiba yang tepat sasaran
                    if ((radiusPeta - finpos >= -2) && (radiusPeta - finpos <= 2)) {
                        for (int i = 0; i < teleporterList.size(); i++) {
                            // Memastikan posisi dari teleporter, benar punya kita
                            if ((teleporterList.get(i).currentHeading - getHeadingBetween(teleporterList.get(i), pemainKecilLainTerdekat)) >= -20 && (teleporterList.get(i).currentHeading - getHeadingBetween(teleporterList.get(i), pemainKecilLainTerdekat)) <= 20) {
                                System.out.println("Teleported!!");
                                playerAction.action = PlayerActions.TELEPORT;
                            }
                        }
                    }
                }

                // 4. State keempat - Shield for Torpedo Salvo
                if (!torpedoSalvoList.isEmpty()) {
                    // Kondisi torpedoSalvo berada pada -20 <= heading <= 20 dari bot
                    if ((torpedoSalvoList.get(0).currentHeading - getHeadingBetween(torpedoSalvoList.get(0), bot)) >= -20 && (torpedoSalvoList.get(0).currentHeading - getHeadingBetween(torpedoSalvoList.get(0), bot)) <= 20) {
                        System.out.println("Addcons 3 terpenuhi, ada lawan yang nembak");
                        // Pendefinisian persamaan kinetika 1 dimensi
                        double jarak = getDistanceBetween(bot, torpedoSalvoList.get(0));
                        int vtor = 60;
                        int init = radiusPeta;
                        double finals = init - (jarak / vtor);
                        int finpos = (int) Math.round(finals);
                        // Memenuhi kondisi waktu melalui radius peta
                        if ((radiusPeta - finpos >= -2) && (radiusPeta - finpos <= 2)) {
                            System.out.println("Aktivasi shield");
                            playerAction.action = PlayerActions.ACTIVATESHIELD;
                        }
                    }
                }

                // 5. State kelima - Gas Clouds Handling
                if (!gasCloudList.isEmpty()) {
                    // Mengecek kondisi pribadi dan lokasi gas cloud
                    if (getDistanceBetween(bot, gasCloudList.get(0)) <= 0.8 * gasCloudList.get(0).getSize()) {
                        // Skema menghindari dengan perputaran 90 derajat
                        System.out.println("Addcons 4 terpenuhi,Deket gas cloud!");
                        playerAction.heading = (getHeadingBetween(bot, gasCloudList.get(0)) + 90) % 360;
                        playerAction.action = PlayerActions.FORWARD;
                    }
                }
            }

            // b. Jika sudah tidak ada pemain lain, maka menang :)
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

    public boolean zonaTembakMusuh(GameObject pemainLainTerdekat) {
        return getDistanceBetween(bot, pemainLainTerdekat) <= (getGameState().getWorld().radius) / 1.25;
    }

    public boolean masihKecil() {
        return bot.getSize() < (getGameState().getWorld().radius) / 16;
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

    private int getHeadingBetween(GameObject otherObject1, GameObject otherObject2) {
        var direction = toDegrees(Math.atan2(otherObject2.getPosition().y - otherObject1.getPosition().y, otherObject2.getPosition().x - otherObject1.getPosition().x));
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
}