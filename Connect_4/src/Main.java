import java.util.*;

public class Main{
    public static class BST<Key extends Comparable<Key>, Value>{

        private Node root;

        private class Node{
            private Key key;
            private List<Value> values;
            private Node left, right;

            public Node(Key key, Value value) {
                this.key = key;
                this.values = new ArrayList<>();
                this.values.add(value);
                this.left = null;
                this.right = null;
            }
        }
        public void put(Key key, Value value){
            root = put(root, key, value);
        }
        private Node put(Node x, Key key, Value value){
            if(x == null){
                return new Node(key, value);
            }
            int cmp = key.compareTo(x.key);
            if(cmp < 0){
                x.left = put(x.left, key, value);
            }else if (cmp > 0){
                x.right = put(x.right, key, value);
            }else{
                if(!x.values.contains(value)){
                    x.values.add(value);
                }
            }
            return x;
        }
        public List<Value> get(Key key){return get(root, key);}
        private List<Value> get(Node x, Key key){
            if(x == null){return null;}
            int cmp = key.compareTo(x.key);
            if(cmp < 0){
                return get(x.left, key);
            }else if(cmp > 0){
                return get(x.right, key);
            }else{
                return x.values;
            }
        }
        public void removeValue(Key key, Value valueToRemove){
            root = removeValue(root, key, valueToRemove);
        }
        private Node removeValue(Node x, Key key, Value valueToRemove){
            if(x == null){
                return null;
            }
            int cmp = key.compareTo(x.key);
            if(cmp < 0){
                x.left = removeValue(x.left, key, valueToRemove);
            }else if(cmp > 0){
                x.right = removeValue(x.right, key, valueToRemove);
            }else{
                x.values.remove(valueToRemove);
                if(x.values.isEmpty()){
                    if(x.left == null) return x.right;
                    if(x.right == null) return x.left;
                    Node t = x;
                    x = min(t.right);
                    x.right = deleteMin(t.right);
                    x.left = t.left;
                }
            }
            return x;
        }
        private Node min(Node x){
            if(x.left == null) return x;
            return min(x.left);
        }
        private Node deleteMin(Node x){
            if(x.left == null) return x.right;
            x.left = deleteMin(x.left);
            return x;
        }
        public Key ceiling(Key key){
            Node x = ceiling(root, key);
            if (x == null) return null;
            return x.key;
        }
        private Node ceiling(Node x, Key key){
            if(x == null) return null;
            int cmp = key.compareTo(x.key);
            if(cmp == 0) return x;
            if(cmp > 0) return ceiling(x.right, key);
            Node t = ceiling(x.left, key);
            if(t != null) return t;
            return x;
        }
        public List<Player> getPlayersInWinRange(int lo, int hi, HashST<String, Player> allPlayers){
            List<Player> players = new ArrayList<>();
            getPlayersInWinRange(root, lo, hi, allPlayers, players);
            return players;
        }
        private void getPlayersInWinRange(Node x, int lo, int hi, HashST<String, Player> allPlayers, List<Player> players){
            if(x == null) return;
            int currentWins = (Integer) x.key;

            if(currentWins > lo && x.left != null){
                getPlayersInWinRange(x.left, lo, hi, allPlayers, players);
            }
            if(currentWins >= lo && currentWins <= hi){
                for(Value playerNameObj : x.values){
                    String playerName = (String) playerNameObj;
                    Player p = allPlayers.get(playerName);
                    if(p != null && !players.contains(p)){
                        players.add(p);
                    }
                }
            }
            if(currentWins < hi && x.right != null){
                getPlayersInWinRange(x.right, lo, hi, allPlayers, players);
            }
        }
        public List<Player> getWinSuccessorPlayers(int wins, HashST<String, Player> allPlayers){
            Key successorKey = ceiling((Key) Integer.valueOf(wins + 1));
            if(successorKey == null){
                return new ArrayList<>();
            }
            List<Value> playerNames = get(successorKey);
            List<Player> players = new ArrayList<>();
            if(playerNames != null){
                for(Value playerNameObj : playerNames){
                    String playerName = (String) playerNameObj;
                    Player p = allPlayers.get(playerName);
                    if(p != null && !players.contains(p)){
                        players.add(p);
                    }
                }
            }
            return players;
        }
    }
    public static class HashST<Key, Value>{

        private static final int dafault_capacity= 10;
        private int size;
        private LinkedList<Entry<Key, Value>>[] st;

        private static class Entry<Key, Value>{
            Key key;
            Value value;
            public Entry(Key key, Value value){
                this.key = key;
                this.value = value;
            }
            public boolean equals(Object o){
                if(this == o) return true;
                if(o == null || getClass() != o.getClass()) return false;
                Entry<?, ?> entry = (Entry<?, ?>) o;
                //"cualquier tipo" recibe cualquier tipo de key o value.
                return key.equals(entry.key);
            }
            public int hashCode(){return Objects.hash(key);}
        }
        public HashST(){this(dafault_capacity);}
        public HashST(int capacity){
            st = (LinkedList<Entry<Key, Value>>[]) new LinkedList[capacity];
            for(int i = 0; i < capacity; i++){
                st[i] = new LinkedList<>();
            }
            size = 0;
        }
        private int hash(Key key){
            return(key.hashCode() & Integer.MAX_VALUE) % st.length;
        }
        public void put(Key key, Value value){
            int i = hash(key);
            for(Entry<Key, Value> entry : st[i]){
                if(entry.key.equals(key)){
                    entry.value = value;
                    return;
                }
            }
            st[i].add(new Entry<>(key, value));
            size++;
        }
        public Value get(Key key){
            int i = hash(key);
            for(Entry<Key, Value> entry : st[i]){
                if(entry.key.equals(key)){return entry.value;}
            }
            return null;
        }
        public boolean contains(Key key) {return get(key) != null;}
        public int size() {return size;}
    }
    public static class Player{
        private String playerName;
        private int wins;
        private int draws;
        private int losses;

        public Player(String playerName) {
            this.playerName = playerName;
            this.wins = 0;
            this.draws = 0;
            this.losses = 0;
        }
        public String getPlayerName() {return playerName;}
        public int getWins() {return wins;}
        public int getDraws() {return draws;}
        public int getLosses() {return losses;}
        public void addWin() {this.wins++;}
        public void addDraw() {this.draws++;}
        public void addLoss() {this.losses++;}
        public double winRate() {
            int totalGames = wins + draws + losses;
            if(totalGames == 0){return 0.0;}
            return (double) wins / totalGames;
        }
        public boolean equals(Object o){
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            Player player = (Player) o;
            return playerName.equals(player.playerName);
        }
        public int hashCode() {return Objects.hash(playerName);}
    }
    public static class Scoreboard{
        private BST<Integer, String> winTree;
        private HashST<String, Player> players;
        private int playedGames;

        public Scoreboard(BST<Integer, String> bst, HashST<String, Player> hashSt){
            this.winTree = bst;
            this.players = hashSt;
            this.playedGames = 0;
        }
        public void addGameResult(String winnerPlayerName, String looserPlayerName, boolean draw){
            playedGames++;
            Player winner = players.get(winnerPlayerName);
            Player looser = players.get(looserPlayerName);
            if(winner == null){
                System.err.println("Advertencia: Jugador ganador '"
                        + winnerPlayerName + "' no registrado. Registrándolo.");
                registerPlayer(winnerPlayerName);
                winner = players.get(winnerPlayerName);
            }
            if(looser == null){
                System.err.println("Advertencia: Jugador perdedor '"
                        + looserPlayerName + "' no registrado. Registrándolo.");
                registerPlayer(looserPlayerName);
                looser = players.get(looserPlayerName);
            }
            if(draw){
                if (winner != null) winner.addDraw();
                if (looser != null) looser.addDraw();
            }else{
                if(winner != null){
                    int oldWins = winner.getWins();
                    winTree.removeValue(Integer.valueOf(oldWins), winner.getPlayerName());
                    winner.addWin();
                    winTree.put(Integer.valueOf(winner.getWins()), winner.getPlayerName());
                }
                if(looser != null){looser.addLoss();}
            }
        }
        public void registerPlayer(String playerName){
            if(!players.contains(playerName)){
                Player newPlayer = new Player(playerName);
                players.put(playerName, newPlayer);
                winTree.put(Integer.valueOf(newPlayer.getWins()), newPlayer.getPlayerName());
            }
        }
        public boolean checkPlayer(String playerName){
            return players.contains(playerName);
        }
        public Player[] winRange(int lo, int hi){
            List<Player> playersInRange = winTree.getPlayersInWinRange(lo, hi, players);
            return playersInRange.toArray(new Player[0]);
        }
        public Player[] winSuccessor(int wins){
            List<Player> successorPlayers = winTree.getWinSuccessorPlayers(wins, players);
            return successorPlayers.toArray(new Player[0]);
        }
    }
    public static class ConnectFour{
        private static final int rows = 6;
        private static final int cols = 7;
        private char[][] grid;
        private char currentSymbol;

        public ConnectFour(){
            grid = new char[rows][cols];
            for(int r = 0; r < rows; r++){
                for(int c = 0; c < cols; c++){
                    grid[r][c] = ' ';
                }
            }
            currentSymbol = 'X';
        }
        public char getCurrentSymbol(){return currentSymbol;}
        public char[][] getGrid(){return grid;}

        public boolean makeMove(int col){
            if(col < 0 || col >= cols){return false;}
            int rowToPlace = -1;
            for(int r = rows - 1; r >= 0; r--){
                if(grid[r][col] == ' '){
                    rowToPlace = r;
                    break;
                }
            }
            if(rowToPlace == -1){return false;}
            grid[rowToPlace][col] = currentSymbol;
            currentSymbol = (currentSymbol == 'X') ? 'O' : 'X';
            return true;
        }
        public GameState isGameOver(){
            if(checkWin('X')){return GameState.Xwins;}
            if(checkWin('O')){return GameState.Owins;}
            boolean boardFull = true;
            for(int r = 0; r < rows; r++){
                for(int c = 0; c < cols; c++){
                    if(grid[r][c] == ' '){
                        boardFull = false;
                        break;
                    }
                }
                if(!boardFull) break;
            }
            if(boardFull) {return GameState.Draw;}
            return GameState.InProgress;
        }
        private boolean checkWin(char symbol){
            for(int r = 0; r < rows; r++){
                for(int c = 0; c <= cols - 4; c++){
                    if(grid[r][c] == symbol &&
                            grid[r][c+1] == symbol &&
                            grid[r][c+2] == symbol &&
                            grid[r][c+3] == symbol){
                        return true;
                    }
                }
            }
            for(int r = 0; r <= rows - 4; r++){
                for(int c = 0; c < cols; c++){
                    if(grid[r][c] == symbol &&
                            grid[r+1][c] == symbol &&
                            grid[r+2][c] == symbol &&
                            grid[r+3][c] == symbol){
                        return true;
                    }
                }
            }
            for(int r = 0; r <= rows - 4; r++){
                for(int c = 0; c <= cols - 4; c++){
                    if(grid[r][c] == symbol &&
                            grid[r+1][c+1] == symbol &&
                            grid[r+2][c+2] == symbol &&
                            grid[r+3][c+3] == symbol){
                        return true;
                    }
                }
            }
            for(int r = 3; r < rows; r++){
                for(int c = 0; c <= cols - 4; c++){
                    if(grid[r][c] == symbol &&
                            grid[r-1][c+1] == symbol &&
                            grid[r-2][c+2] == symbol &&
                            grid[r-3][c+3] == symbol){
                        return true;
                    }
                }
            }
            return false;
        }
        public void printBoard(){
            System.out.println(" 0 1 2 3 4 5 6");
            System.out.println("-----------------");
            for(int r = 0; r < rows; r++){
                System.out.print("|");
                for(int c = 0; c < cols; c++){
                    System.out.print(grid[r][c] + "|");
                }
                System.out.println();
            }
            System.out.println("-----------------");
        }
        public enum GameState{InProgress, Xwins, Owins, Draw}
    }
    public static class Game{
        private String status;
        private String winnerPlayerName;
        private String playerNameA;
        private String playerNameB;
        private ConnectFour connectFour;
        private Scoreboard scoreboard;

        public Game(String playerNameA, String playerNameB, Scoreboard scoreboard){
            this.playerNameA = playerNameA;
            this.playerNameB = playerNameB;
            this.connectFour = new ConnectFour();
            this.status = "En progreso";
            this.winnerPlayerName = "";
            this.scoreboard = scoreboard;
        }
        public String getStatus(){return status;}
        public String getWinnerPlayerName(){return winnerPlayerName;}

        public String play(){
            Scanner scanner = new Scanner(System.in);
            String currentPlayerName = playerNameA;

            scoreboard.registerPlayer(playerNameA);
            scoreboard.registerPlayer(playerNameB);

            while(status.equals("En progreso")){
                System.out.println("\nTurno de " + currentPlayerName + " ("
                        + connectFour.getCurrentSymbol() + ")");
                connectFour.printBoard();

                int col = -1;
                boolean validMove = false;
                while(!validMove){
                    System.out.print("Ingrese columna (0-6): ");
                    if(scanner.hasNextInt()){
                        col = scanner.nextInt();
                        validMove = connectFour.makeMove(col);
                        if(!validMove){
                            System.out.println("Movimiento inválido o columna llena. Intente de nuevo.");
                        }
                    }else{
                        System.out.println("Entrada inválida. Por favor, ingrese un número.");
                        scanner.next();
                    }
                }
                ConnectFour.GameState gameState = connectFour.isGameOver();

                if(gameState == ConnectFour.GameState.Xwins){
                    status = "Victoria";
                    winnerPlayerName = playerNameA;
                    System.out.println("\n¡" + playerNameA + " gana ");
                    scoreboard.addGameResult(playerNameA, playerNameB, false);
                }else if(gameState == ConnectFour.GameState.Owins){
                    status = "Victoria";
                    winnerPlayerName = playerNameB;
                    System.out.println("\n¡" + playerNameB + " gana ");
                    scoreboard.addGameResult(playerNameB, playerNameA, false);
                }else if(gameState == ConnectFour.GameState.Draw){
                    status = "Empate";
                    winnerPlayerName = "";
                    System.out.println("\nEs un empate ");
                    scoreboard.addGameResult(playerNameA, playerNameB, true);
                }else{
                    currentPlayerName = (currentPlayerName.equals(playerNameA)) ? playerNameB : playerNameA;
                }
            }
            connectFour.printBoard();
            return winnerPlayerName;
        }
    }
    public static void main(String[] args) {
        Scanner mainScanner = new Scanner(System.in);

        BST<Integer, String> winTree = new BST<>();
        HashST<String, Player> playersHashST = new HashST<>();

        Scoreboard scoreboard = new Scoreboard(winTree, playersHashST);

        System.out.println("Bienvenido a Conecta 4");

        System.out.print("Ingrese el nombre del Jugador A: ");
        String playerA = mainScanner.nextLine();
        scoreboard.registerPlayer(playerA);

        System.out.print("Ingrese el nombre del Jugador B: ");
        String playerB = mainScanner.nextLine();
        scoreboard.registerPlayer(playerB);

        while(true){
            System.out.println("\n--- Iniciando nueva partida ---");
            Game game = new Game(playerA, playerB, scoreboard);
            game.play();

            System.out.println("\n--- Marcador actual ---");
            Player pA = playersHashST.get(playerA);
            if(pA != null){
                System.out.println(pA.getPlayerName() + ": Victorias = "
                        + pA.getWins() + ", Empates= " + pA.getDraws()
                        + ", Derrotas = " + pA.getLosses() + ", Tasa de victorias= "
                        + String.format("%.2f", pA.winRate() * 100) + "%");
            }//"%.2f" es una manera de formatear el % de victorias con 2 decimales.
            Player pB = playersHashST.get(playerB);
            if(pB != null){
                System.out.println(pB.getPlayerName() + ": Victorias = "
                        + pB.getWins() + ", Empates = " + pB.getDraws()
                        + ", Derrotas = " + pB.getLosses() + ", Tasa de victorias = "
                        + String.format("%.2f", pB.winRate() * 100) + "%");
            }
            System.out.println("\nJugadores con 0-1 victorias:");
            Player[] rangePlayers = scoreboard.winRange(0, 1);
            if(rangePlayers.length > 0){
                for(Player p : rangePlayers){
                    System.out.println("- " + p.getPlayerName()
                            + " (Victorias: " + p.getWins() + ")");
                }
            }else{
                System.out.println("No hay jugadores en este rango.");
            }
            System.out.println("Jugadores con el siguiente número de victorias más alto después de "
                    + pA.getWins() + ":");
            Player[] successorPlayers = scoreboard.winSuccessor(pA.getWins());
            if(successorPlayers.length > 0){
                for(Player p : successorPlayers){
                    System.out.println("- " + p.getPlayerName()
                            + " (Victorias: " + p.getWins() + ")");
                }
            }else{
                System.out.println("No se encontraron jugadores con más victorias.");
            }
            System.out.print("\n¿Jugar otra partida? (si/no): ");
            String playAgain = mainScanner.nextLine().trim().toLowerCase();
            if(!playAgain.equals("si")){
                break;
            }
        }
        mainScanner.close();
        System.out.println("Gracias por jugar");
    }
}
