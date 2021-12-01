package progettoIngSW.Network.Server;

import progettoIngSW.Controller.GameController;
import progettoIngSW.Controller.PlayerController;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * Classe che si occupa  di avviare la partita e di resettarla alla fine della stessa.
 * È observer della classe game e invia gli aggiornamenti a tutti i client connessi.
 *
 */

public class Server extends Thread implements Observer {

    private ArrayList<String> names = new ArrayList<>();
    private static GameController gameController;

    private ServerRMI serverRMI;
    private ServerSocketImpl serverSocket;

    private int playerTimer;
    private int turnTimer;
    private int patternTimer;

    private static Registry registry;


    public Server(int playerTimer, int turnTimer, int patternTimer) {

        gameController = GameController.getGameController();
        this.playerTimer = playerTimer;
        this.turnTimer = turnTimer;
        this.patternTimer = patternTimer;

    }

    public void setServerRMI(ServerRMI serverRMI) {
        this.serverRMI = serverRMI;
    }

    public void setServerSocket(ServerSocketImpl serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Inizializza la partita, il server RMI e il serverSocket.
     */
    @Override
    public void run() {

        while (true) {

            GameController.resetGameController();
            Game.resetGame();


            System.out.println("Waiting for invocations from clients...");


            gameController = GameController.getGameController();
            gameController.setTimers(playerTimer, turnTimer, patternTimer);
            Game g = Game.getGame();
            g.addObserver(this);
            g.getDraft().addObserver(this);
            g.getTrack().addObserver(this);
            gameController.play();
            try {
                serverSocket.close();
            } catch (IOException ignored) {

            }


        }


    }


    /**
     * chiede al client con username "user" se vuole aumentare o diminuire di 1 il valore del dado selezionato
     *
     * @param user nome dell'utente al quale fare la richiesta
     * @return true-> aumenta, false->diminuisci
     * @throws EndTimerException    quando scade il timer prima che l'utente abbia risposto
     * @throws MoveStoppedException quando l'utente decide di annullare la mossa
     */
    public boolean askIncrease(String user) throws EndTimerException, MoveStoppedException {
        try {
            return serverRMI.askIncrease(user);

        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(user);
        } catch (UserNotFoundException e) {
            try {
                return serverSocket.askIncrease(user);
            } catch (UserNotFoundException ignored) {
            }
        }
        return false;
    }

    /**
     * chiede al client con username "user" che cella della windowsframe vuole usare per eseguire la mossa
     *
     * @param user      nome dell'utente al quale fare la richiesta
     * @param placement true-> dove piazzare il dado, false->da dove prendere il dado
     * @return la posizione nella windowsframe scelta dall'utente
     * @throws EndTimerException    quando scade il timer prima che l'utente abbia risposto
     * @throws MoveStoppedException quando l'utente decide di annullare la mossa
     */
    public int askWindowPos(String user, boolean placement) throws EndTimerException, MoveStoppedException {
        try {
            return serverRMI.askWindowPos(user, placement);

        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(user);
        } catch (UserNotFoundException e) {
            try {
                return serverSocket.askWindowPos(user, placement);
            } catch (UserNotFoundException ignored) {
            }
        }
        return 0;
    }

    /**
     * chiede all'utente una posizione nel roundtrack
     *
     * @param user nome dell'utente al quale fare la richiesta
     * @return la posizione nel roundtrack scelta
     * @throws EndTimerException    quando scade il timer prima che l'utente abbia risposto
     * @throws MoveStoppedException quando l'utente decide di annullare la mossa
     */
    public int[] askRoundTrackPos(String user) throws EndTimerException, MoveStoppedException {
        try {
            return serverRMI.askRoundTrackPos(user);
        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(user);
        } catch (UserNotFoundException e) {
            try {
                return serverSocket.askRoundTrackPos(user);
            } catch (UserNotFoundException ignored) {

            }
        }
        return null;
    }

    /**
     * chiede all'utente che numero vuole nel dado estratto
     *
     * @param c    colore del dado
     * @param user nome dell'utente al quale fare la richiesta
     * @return numero scelto del dado
     * @throws EndTimerException    quando scade il timer prima che l'utente abbia risposto
     * @throws MoveStoppedException quando l'utente decide di annullare la mossa
     */
    public int askNumber(Colors c, String user) throws EndTimerException, MoveStoppedException {
        try {
            return serverRMI.askNumber(c, user);
        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(user);
        } catch (UserNotFoundException e) {
            try {
                return serverSocket.askNumber(c, user);
            } catch (UserNotFoundException ignored) {

            }
        }
        return 0;
    }

    /**
     * chiede all'utente quanti dadi vuole muovere
     *
     * @param user nome dell'utente al quale fare la richiesta
     * @return true -> 2, false -> 1
     * @throws EndTimerException    quando scade il timer prima che l'utente abbia risposto
     * @throws MoveStoppedException quando l'utente decide di annullare la mossa
     */
    public boolean askHowMany(String user) throws EndTimerException, MoveStoppedException {
        try {
            return serverRMI.askHowMany(user);
        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(user);
        } catch (UserNotFoundException e) {
            try {
                return serverSocket.askHowMany(user);
            } catch (UserNotFoundException ignored) {

            }
        }
        return false;
    }

    /**
     * chiede all'utente una posizione nel draft
     *
     * @param user nome dell'utente al quale fare la richiesta
     * @return la posizione nel draft scelta
     * @throws EndTimerException    quando scade il timer prima che l'utente abbia risposto
     * @throws MoveStoppedException quando l'utente decide di annullare la mossa
     */
    public int askDraftPos(String user) throws EndTimerException, MoveStoppedException {

        try {
            return serverRMI.askDraftPos(user);
        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(user);
        } catch (UserNotFoundException e) {
            try {
                return serverSocket.askDraftPos(user);
            } catch (UserNotFoundException e1) {

            }
        }
        return 0;
    }

    /**
     * dice all'utente di iniziale il turno
     *
     * @param user nome dell'utente al quale fare la richiesta
     */
    public void startTurn(String user) {
        try {
            serverRMI.startTurn(user);
        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(user);
        } catch (UserNotFoundException e) {
            try {
                serverSocket.startTurn(user);
            } catch (UserNotFoundException e1) {

            }
        }
    }

    /**
     * chiede all'utente di selezionare uno dei 4 pattern scelti
     *
     * @param patterns array di 4 pattern tra i quali l'utente puo scegliere
     * @param user     nome dell'utente al quale fare la richiesta
     * @return da 0 a 3 (indice del pattern scelto), 4 scelta casuale nel player controller
     */
    public int askWhichPattern(Pattern[] patterns, String user) {
        try {
            return serverRMI.askWhichPattern(patterns, user);
        } catch (RemoteException e) {
            clientDisconnection(user);
            return 4;

        } catch (UserNotFoundException e) {
            try {
                return serverSocket.askWhichPattern(patterns, user);
            } catch (UserNotFoundException e1) {

            }
        }
        return 4;
    }


    /**
     * avvisa l'utente che la partita è terminata inviando la classifica finale
     *
     * @param user    nome dell'utente al quale fare la richiesta
     * @param ranking array di interi che indicano gli indici dei player relativi all'array di player del game ordinati per punteggio
     *                ranking[0] -> indice  nell'array di player del game del vincitore
     */
    public void endGame(String user, ArrayList<Integer> ranking) {
        try {
            serverRMI.endGame(user, ranking);
        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(user);

        } catch (UserNotFoundException e) {
            try {
                serverSocket.endGame(user, ranking);
            } catch (UserNotFoundException e1) {

            }
        }
    }

    /**
     * da vedere all'utente il dado estratto.
     *
     * @param dice     dado estratto da mostrare all'utente
     * @param username nome dell'utente al quale fare la richiesta
     */

    public void printDice(Dice dice, String username) {
        try {
            serverRMI.printDice(dice, username);
        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(username);
        } catch (UserNotFoundException e) {
            try {
                serverSocket.printDice(dice, username);
            } catch (UserNotFoundException ignored) {

            }
        }

    }


    /**
     * richiama i metodi update delle classi ServerRMI e ServerSocket che si occupano di trasferirli
     * a tutti i client connessi
     *
     * @param o   oggetto observable che richiama l'update, può essere Game, Draftpool o Roundtrack
     * @param arg oggetto aggiornato
     */
    @Override
    public void update(Observable o, Object arg) {
        try {
            if (o.getClass() == Game.class) {

                if (arg.getClass() == Game.class) {

                    serverRMI.updateGame((Game) arg);
                    serverSocket.updateGame((Game) arg);
                } else if (arg.getClass() == Integer.class) {

                    serverRMI.updateCurrentPlayer((Integer) arg);
                    serverSocket.updateCurrentPlayer((Integer) arg);

                } else if (arg.getClass() == Turns.class) {

                    serverRMI.updateGameTurn((Turns) arg);
                    serverSocket.updateGameTurn((Turns) arg);

                } else if (arg.getClass() == ArrayList.class) {
                    if (((ArrayList) arg).get(0).getClass() == Integer.class) {
                        serverRMI.updateTokens((ArrayList) arg);
                        serverSocket.updateTokens((ArrayList) arg);
                    }
                } else if (arg.getClass() == Player.class) {

                    for (int i = 0; i < names.size(); i++) {
                        if (names.get(i).equals(((Player) arg).getUsername())) {
                            serverRMI.updatePlayerState((Player) arg, i);
                            serverSocket.updatePlayerState((Player) arg, i);
                            return;
                        }
                    }

                } else if (arg.getClass() == WindowFrame.class) {

                    serverRMI.updateWindowsFrame((WindowFrame) arg);
                    serverSocket.updateWindowsFrame((WindowFrame) arg);

                }

            }

            if (o.getClass() == DraftPool.class) {

                serverRMI.updateDraftPool((ArrayList<Dice>) arg);
                serverSocket.updateDraftPool((ArrayList<Dice>) arg);

            }
            if (o.getClass() == RoundTrack.class) {

                serverRMI.updateRoundTrack((HashMap<Integer, ArrayList<Dice>>) arg);
                serverSocket.updateRoundTrack((HashMap<Integer, ArrayList<Dice>>) arg);

            }
        } catch (ConcurrentModificationException we) {
        }

    }

    /**
     * avvisa tutti quando un nouvo client si è connesso alla partita
     *
     * @param username del client che si è appena connesso
     */
    public void clientConnection(String username) {
        serverSocket.sendConnectionOf(username);
        try {
            serverRMI.sendConnectionOf(username);
        } catch (RemoteException ignored) {

        }
    }

    /**
     * avvisa tutti quando un client si è sconnesso dalla partita,
     * <p>
     * se la partita è iniziata
     * - imposta il parametro connected a false del player controller associato
     * - se vi è rimasto un solo player controller connesso termina la partita impostando come vincitore l'unico client attivo
     * <p>
     * <p>
     * altrimenti rimuove il playercontroller e il player associati in modo che poi la partita possa iniziare con 4 player attivi
     *
     * @param username del client che si è appena sconnesso
     */
    public synchronized void clientDisconnection(String username) {

        if (gameController.getGame().isGameStarted()) {

            PlayerController playerDisconected = null;
            for (PlayerController p : gameController.getPlayerControllers()) {
                if (username.equals(p.getPlayer().getUsername())) {
                    playerDisconected = p;
                    break;
                }
            }
            if (playerDisconected != null && playerDisconected.isConnected()) {
                serverSocket.sendDisconnectionOf(username);
                try {
                    serverRMI.sendDisconnectionOf(username);
                } catch (RemoteException ignore) {
                }
                playerDisconected.setConnected(false);
            }
            if (activeUsers() == 1) {
                for (PlayerController p : gameController.getPlayerControllers()) {
                    if (p.isConnected()) {
                        gameController.endGameWithWinner(gameController.getGame().getPlayers().indexOf(p.getPlayer()));
                        break;
                    }

                }
            }
            for (PlayerController p : gameController.getPlayerControllers()) {
                if (username.equals(p.getPlayer().getUsername())) {
                    if (p.isPlaying())
                        p.endTurn();
                    break;
                }
            }
        } else {
            for (PlayerController p : gameController.getPlayerControllers()) {
                if (username.equals(p.getPlayer().getUsername())) {
                    gameController.unregisterPlayer(p);
                    serverSocket.sendDisconnectionOf(username);
                    try {
                        serverRMI.sendDisconnectionOf(username);
                    } catch (RemoteException ignored) {
                    }
                    break;

                }
            }
        }

    }


    /**
     * conta qunti player controller sono connessi ai relativi client
     *
     * @return il numero di player controller connessi
     */
    private int activeUsers() {
        int i = 0;
        for (PlayerController p : gameController.getPlayerControllers()) {
            if (p.isConnected())
                i++;
        }
        return i;
    }

    /**
     * esegue un update generale di game
     */
    public void askForUpdate() {
        Game g = gameController.getGame();

        this.update(g, g);

    }

    /**
     * comunica all'user che il timer è scaduto
     *
     * @param username nome dell'utente al quale fare la richiesta
     */

    public void endTimer(String username) {
        try {
            serverRMI.endTimer(username);
        } catch (RemoteException e) {
            if (e.getClass() == java.rmi.ConnectException.class)
                clientDisconnection(username);
        } catch (UserNotFoundException e) {
            try {
                serverSocket.endTimer(username);
            } catch (UserNotFoundException ignored) {

            }
        }
    }

    /**
     * associa il player controller sconnesso all'utente con il medesimo username o crea un nuovo player controller
     *
     * @param username nome dell'utente che desidera connettersi
     * @param password password dell'utente che desidera connettersi
     * @return true-> connesso tramite password, la partita era gia iniziata; false-> login riuscito e il giocatore è in attesa che la partita inizi
     * @throws FullLobbyException        la partita è iniziata e tutti i giocatori sono connessi, l'utente deve asoettare la prossima partita per giocare
     * @throws NameNotAvailableException il nome scelto è gia un uso da un altro utente
     * @throws WrongPasswordException    il player controller era sconnesso, il nome utente è corretto ma la password è errata e non è stato possibile loggarsi all partita
     */

    public boolean login(String username, String password) throws FullLobbyException, NameNotAvailableException, WrongPasswordException {

        if (names.contains(username)) {
            for (PlayerController p : gameController.getPlayerControllers()) {
                if (p.getPlayer().getUsername().equals(username)) {
                    if (p.isConnected()) throw new NameNotAvailableException();
                    else if (p.getPlayer().getPassword().equals(password)) {
                        p.setConnected(true);
                        clientConnection(username);
                        return true;
                    } else throw new WrongPasswordException();
                }
            }
        }


        synchronized (gameController.getMonitor()) {
            gameController.registerPlayer(new PlayerController(username, password, this));
            gameController.getMonitor().notifyAll();
        }
        names.add(username);
        clientConnection(username);
        return false;


    }

    public void placeDice(int posDraft, int i, String username) throws CellNotEmptyException, NotPlayingException, DiceNotFoundException, RulesBreakException {
        for (PlayerController p : gameController.getPlayerControllers()) {
            if (p.getPlayer().getUsername().equals(username)) {
                p.placeDice(posDraft, i);
                break;
            }
        }
    }

    public void useToolCard(int toolPos, String username) throws DiceNotFoundException, RulesBreakException, CellNotEmptyException, NotPlayingException, InvalidParamsException, NotValidCellException, DraftFullException, EndTimerException {
        for (PlayerController p : gameController.getPlayerControllers()) {
            if (p.getPlayer().getUsername().equals(username)) {
                p.useToolCard(toolPos);
                break;
            }
        }
    }

    public void endTurn(String user) {
        for (PlayerController p : gameController.getPlayerControllers()) {
            if (p.getPlayer().getUsername().equals(user)) {
                p.endTurn();
                break;
            }
        }
    }

    /**
     * selva su un file il pattern ricevuto
     * percorso del file: "./CustomPatterns/" + pattern.getName() + ".txt"
     *
     * @param pattern pattern da salvare
     */

    public void uploadPattern(Pattern pattern) {


        FileWriter w = null;
        try {
            w = new FileWriter("./" + pattern.getName() + ".txt");
        } catch (IOException e) {
            return;
        }
        try {
            w.write("4 5\n" + pattern.getName() + "\n");
            StringBuilder restrictions = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                switch (pattern.getColors()[i]) {
                    case BLUE:
                        restrictions.append("b");
                        continue;
                    case RED:
                        restrictions.append("r");
                        continue;
                    case GREEN:
                        restrictions.append("g");
                        continue;
                    case PURPLE:
                        restrictions.append("p");
                        continue;

                    case YELLOW:
                        restrictions.append("y");
                        continue;
                }
                restrictions.append(pattern.getNumbers()[i]);
            }

            w.write(restrictions.toString() + "\n" + pattern.getDifficult());
            w.flush();
        } catch (IOException ignored) {

        } finally {
            try {
                w.close();
            } catch (IOException | NullPointerException ignored) {

            }

        }


    }
}
