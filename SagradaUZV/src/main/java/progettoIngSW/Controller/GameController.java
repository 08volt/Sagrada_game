package progettoIngSW.Controller;

import progettoIngSW.Exceptions.FullLobbyException;
import progettoIngSW.Model.Game;

import java.util.*;

public class GameController {

    //
    //ATTRIBUTES
    //
    private Game g;
    private Timer t;
    private static ArrayList<PlayerController> playerControllers;
    private final Object monitor = new Object();
    private static long playerTimer;
    private static long turnTimer;
    private static long patternTimer;

    //
    //CONSTRUCTOR
    //
    private static GameController gc = null;

    public static GameController getGameController() {
        if(gc == null) {
            gc = new GameController();
        }
        return gc;
    }

    public static void resetGameController(){
        gc = null;
    }


    private GameController(){
        this.g = Game.getGame();
        this.playerControllers = new ArrayList<>();
    }


    //
    //METHODS
    //

    /**
     * This Method handle the match:
     * It shuffle the players, pull out private cards, assign the patterns to each player,
     * and lunch the gameSetup (which will build all the variable at model level)
     * After that, it will assign the turn to each playerController, restarting a timer each time
     * After the match it will call the endGame mathod to notify the players with their points
     */
    public void play() {
        while(!g.isGameStarted()) {
            try {
                synchronized (monitor) {
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        g.shufflePlayers();
        g.extractPrivateCards();
        assignPatterns();

        for(PlayerController playerController: playerControllers)
            playerController.setG(g);

        g.gameSetup();

        while(g.isGameStarted()) {
            PlayerController currentPC = getCurrentPlayerController();

            if(currentPC.isConnected()) {
                currentPC.play();
                t = new Timer();
                t.schedule(new TurnTimer(currentPC), turnTimer*1000);
                while (currentPC.isPlaying()) {
                    try {
                        synchronized (currentPC.getMonitor()) {
                            currentPC.getMonitor().wait();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                t.cancel();
            }
            g.changeTurn();
        }
        for(PlayerController playerController: playerControllers)
            playerController.endGame(g.getRanking());
    }

    /**
     * This method handle the situation in which all the players but one disconnets from the server
     * This last player will be chosen as the winner
     * @param pos is the position on the array of players of the remaining player
     */
    public void endGameWithWinner(int pos) {

        ArrayList<Integer> ranking = new ArrayList<>();
        ranking.add(pos);
        for(int i = 0; i<playerControllers.size();i++){
            if(i != pos)
                ranking.add(i);
        }
        g.setRanking(ranking);

        g.setGameStarted(false);
    }

    /**
     * This method handle the timer of the match
     * @param playerTimer handle the waiting players when there are >= 2 or < 4 players already connected
     * @param turnTimer handle the duration of the players turns
     * @param patternTimer handle the time to choose a pattern
     */
    public void setTimers(int playerTimer, int turnTimer, int patternTimer) {
        this.playerTimer = playerTimer;
        this.turnTimer = turnTimer;
        this.patternTimer = patternTimer;
    }


    /**
     * Class that assign a Turn timer to a player
     */
    class TurnTimer extends TimerTask{

        PlayerController pc;

        public TurnTimer(PlayerController player){
            pc = player;
        }

        @Override
        public void run() {
            pc.endTimer();
        }
    }

    /**
     * Class that menage the timer that waits for more player
     */
    class GameTimer extends TimerTask{

        Game g;

        public GameTimer(Game game){
            g = game;
        }
        @Override
        public void run() {
            synchronized (monitor) {
                g.setGameStarted(true);
                monitor.notifyAll();
            }
        }
    }

    /**
     * This method add a new PlayerController to the match, it adds the player to the list
     * @param pc Player that asks to play
     * @throws FullLobbyException if the match has already started
     */
    public void registerPlayer(PlayerController pc) throws FullLobbyException {
        //se i player sono gia 4 non crea nulla
        if(playerControllers.size() >= 4 || g.isGameStarted()) throw new FullLobbyException();

        playerControllers.add(pc);
        g.addPlayer(pc.getPlayer());

        //se i player sono 2 parte il timer per iniziare la partita
        if(playerControllers.size() == 2){
            t = new Timer();
            t.schedule(new GameTimer(g),playerTimer*1000);
        }
        if(playerControllers.size() == 4) //una volta che i player sono 4 viene iniziata la partita
        {
            t.cancel();
            synchronized (monitor) {
                g.setGameStarted(true);
                monitor.notifyAll();
            }
        }
    }

    /**
     * Method that handle an unexpected disconnection from a player before the choosing of the patterns
     * The player that disconnect wont be considered later during the match
     * @param pc playerController of the player that has disconnected
     */
    public void unregisterPlayer(PlayerController pc){
        g.getPlayers().remove(pc.getPlayer());
        this.playerControllers.remove(pc);
        if(playerControllers.size()<2){
            if(t != null)
                t.cancel();
        }
    }

    /**
     * Method that handle the assignment of the patterns to choose from to the players
     * it initialize the timer to choose the pattern
     */
    public void assignPatterns(){
        for (PlayerController pc: playerControllers ) {
            Timer t1 = new Timer();
            t1.schedule(new TurnTimer(pc),patternTimer*1000);
            pc.choosePattern(g.extractPattern());
            t1.cancel();
        }
    }

    //
    //GET AND SET METHODS
    //

    public PlayerController getCurrentPlayerController(){
        for (PlayerController pc: playerControllers) {
            if(pc.getPlayer().equals(g.getPlayers().get(g.getCurrentPlayer())))
                return pc;
        }
        return null;

    }
    public ArrayList<PlayerController> getPlayerControllers() {
        return playerControllers;
    }

    public Game getGame() {
        return g;
    }

    public synchronized Object getMonitor() {
        return monitor;
    }

    public static void setPlayerControllers(ArrayList<PlayerController> playerControllers) {
        GameController.playerControllers = playerControllers;
    }


}
