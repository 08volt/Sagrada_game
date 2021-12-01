package progettoIngSW.View.Gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import progettoIngSW.CommandInterface;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;
import progettoIngSW.Network.Client.ConnectionType;
import progettoIngSW.ViewInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;


public class GUI extends Application implements ViewInterface, Runnable {

    private static final Object monitor = new Object();
    private static Stage stage;
    private static ConnectionController connection;
    private static HomeController home;
    private static PatternController pattern;
    private static GamePlayController gamePlay;
    private static EndGameController endGame;
    private static CreatePatternController createPattern;
    private Button stopAlert;

    private String username;
    private static Colors privateCard;

    private static boolean state;

    private CommandInterface player;

    public GUI(){
        state = false;
    }

    //FIXME vedere update gamestart gestione

    /**
     * Metodo ereditato dalla interfaccia Runnable per lanciare la GUI in un thread separato che non blocchi
     * l'esecuzione del gioco
     */
    @Override
    public void run() {
        Application.launch(GUI.class);
    }

    /**
     * Metodo ereditato dalla classe Application per lanciare la GUI
     * @param stage parametro usuale del metodo start
     */
    @Override
    public void start(Stage stage) {

        Scene scene = null;
        FXMLLoader connectionLoader = null;
        try {
            connectionLoader = new FXMLLoader(getClass().getResource("/fxml/connection.fxml"));
            Parent firstPane = connectionLoader.load();
            scene = new Scene(firstPane);
        } catch (IOException e) {
            System.out.println("Problemi nel caricamento del gioco.");
        }
        connection = connectionLoader.getController();
        stage.setTitle("Sagrada");
        stage.setScene(scene);
        GUI.stage = stage;
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        stage.setResizable(false);
        stage.show();
        synchronized (monitor) {
            state = true;
            monitor.notifyAll();
        }
    }

    /**
     * Metodo che aspetta il caricamento della schermata fxml di connessione, è il primo metodo che viene richiamato
     * dal client dopo che è stato fatto partire il gioco
     * @return l'indirizzo del server al quale connettersi inserito dall'utente nella schermat
     */
    @Override
    public String askServerAddress() {
        if(connection == null)
            waitLoading();
        while(connection.getServerAddress() == null){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        state = false;
        return connection.getServerAddress();
    }

    /**
     * @return il tipo di connessione scelto dall'utente per connettersi al server
     * ConnectionType può essere RMI o socket
     */
    @Override
    public ConnectionType askConnectionType() {
        return connection.getConnection();
    }

    /**
     * Metodo che comunica al controller che si occupa di connettersi al server (connection) che ci sono stati
     * problemi di connessione col server
     */
    @Override
    public void connectionError() {
        connection.ioException();
    }

    /**
     * Metodo che fa partire la schermata della gui, gestita dal controller home, dove l'utente sceglie
     * se connettersi alla partita per giocare o creare un nuovo pattern con restrizioni personalizzate
     * @return la scelta dell'utente:
     *          true --> l'utente vuole giocare
     *          false --> l'utente vuole creare pattern personalizzati
     */
    @Override
    public boolean askToPlay() {
        Platform.runLater(() -> {
            FXMLLoader homeLoader;
            homeLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent pane = null;
            Scene scene = null;
            try {
                pane = homeLoader.load();
                scene = new Scene(pane);
            } catch (IOException e) {
                System.out.println("Problemi nel caricamento del gioco.");
            }
            home = homeLoader.getController();
            pane.setStyle("-fx-background-image: url('/img/schermatagioco.jpg');" + "-fx-background-size: cover;");
            GUI.stage.setScene(scene);
            synchronized (monitor) {
                state = true;
                monitor.notifyAll();
            }
        });
        waitLoading();
        while(home.getChoice() == -1){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        state = false;
        return home.getChoice() != 0;
    }

    /**
     * Metodo che fa partire la schermata che si occupa di gestire il caricamento di pattern personalizzati
     * e si mette in attesa della risposta dal controller una volta completato il pattern
     * @return il pattern con il nome, la difficoltà e le restrizioni scelte dall'utente
     */
    @Override
    public Pattern writeNewPattern() {
        Platform.runLater(() -> {
            FXMLLoader newPatternLoader;
            newPatternLoader = new FXMLLoader(getClass().getResource("/fxml/creationPattern.fxml"));
            Parent pane;
            Scene scene = null;
            try {
                pane = newPatternLoader.load();
                scene = new Scene(pane);
            } catch (IOException e) {
                System.out.println("Problemi nel caricamento del gioco.");
            }
            createPattern = newPatternLoader.getController();
            GUI.stage.setScene(scene);
            synchronized (monitor) {
                state = true;
                monitor.notifyAll();
            }
        });
        if(createPattern == null)
            waitLoading();
        while(createPattern.getPattern().getName() == null){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        state = false;
        return createPattern.getPattern();
    }

    /**
     * Metodo che salva nel campo username il nome inserito dall'utente nella schermata home e richiede sempre lo
     * username necessario per fare il login, si mette in attesa nel caso in cui l'utente inserisca uno username
     * già scelto e quindi debba richiederlo, fino a quando è diverso da null
     * @return lo username scelto dall'utente
     */
    @Override
    public String askName() {
        while(home.getUsername() == null)
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            }
        username = home.getUsername();
        return home.getUsername();
    }

    /**
     * Metodo che richiede la password inserita dall'utente per connettersi al server
     * @return
     */
    @Override
    public String askPassword() {
        return home.getPass();
    }

    /**
     *Metodo che notifica che il server è pieno (4 giocatori)
     */
    @Override
    public void fullLobby() {
        home.fullLobby();
    }

    /**
     * Metodo che notifica che il nome non è disponibile (già utilizzato da un altro giocatore)
     */
    @Override
    public void nameNotAvailable() {
        home.nameNotAvailable();
    }

    /**
     * Metodo che notifica che il nome è giusto ma la password è sbagliata (metodo richiamato durante
     * la riconnessione.
     */
    @Override
    public void wrongPassword() {
        home.wrongPassword();
    }

    /**
     * Metodo che comunica all'utente se la connessione è avvenuta con successo o meno
     * @param b è il risultato della connessione:
     *              true --> connessione avvenuta con successo
     *              false --> connessione non avvenuta
     */
    @Override
    public void setLogged(boolean b) {
        showAlert(b ? "Connessione riuscita.\nAspetta altri giocatori per iniziare la partita" : "Connesione non riuscita");
    }

    /**
     * Metodo che fa partire la schermata che si occupa della scelta del pattern
     * @param patterns è la lista dei 4 pattern inviati dal server tra i quali deve scegliere l'utente per giocare
     * @return 0-3: posizione del pattern scelto dall'utente
     *         4: timer della scelta dei pattern scaduto
     */
    @Override
    public int askWhichPattern(Pattern[] patterns) {
        Platform.runLater(() -> {
            FXMLLoader patternLoader;
            patternLoader = new FXMLLoader(getClass().getResource("/fxml/pattern.fxml"));
            Parent pane;
            Scene scene = null;
            try {
                pane = patternLoader.load();
                scene = new Scene(pane);
            } catch (IOException e) {
                System.out.println("Problemi nel caricamento del gioco.");
            }
            pattern = patternLoader.getController();
            stopAlert.fire();
            GUI.stage.setScene(scene);
            synchronized (monitor) {
                state = true;
                monitor.notifyAll();
            }
        });
        if(pattern == null)
            waitLoading();
        pattern.askPattern(patterns, privateCard);
        while(pattern.getChoice() == -1){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        state = false;
        showAlert(pattern.getChoice() == 4?"È scaduto il tempo, è stato scelto un pattern casuale"
                :"Hai scelto il pattern "+patterns[pattern.getChoice()].getName()+
                "\nAspetta che anche gli altri giocatori scelgano.");
        return pattern.getChoice();
    }

    /**
     * Metodo che notifica al controller della schermata di gioco (gamePlay) che è iniziato il turno dell'utente
     * @throws EndTimerException quando scade il timer del turno
     */
    @Override
    public void startTurn() throws EndTimerException {
        gamePlay.startTurn();
    }

    /**
     * Metodo che richiede al controller della schermata di gioco (gamePlay) la posizione del dado della
     * riserva (draftPool)
     * @return 0-players.size()*2: la posizione del dado scelto dall'utente
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    @Override
    public int askDraftPos() throws EndTimerException, MoveStoppedException {
        return gamePlay.askDraftPos();
    }

    /**
     * Metodo che richiede al controller della schermata di gioco (gamePlay)
     * se vuole aumentare o diminuire il dado scelto (toolCard1)
     * @return la scelta dell'utente:
     *          true --> l'utente vuole aumentare il valore del dado
     *          false --> l'utente vuole diminuire il valore del dado
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    @Override
    public boolean askIncrease() throws EndTimerException, MoveStoppedException {
        return gamePlay.askIncrease();
    }

    /**
     * Metodo che richiede al controller della schermata di gioco (gamePlay) la posizione della cella
     * della windowframe
     * @param placement tipo di richiesta:
     *                  true --> posizione dove l'utente vuole posizionare il dado
     *                  false --> posizione dove sta il dado che l'utente vuole spostare
     * @return la posizione scelta dell'utente
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    @Override
    public int askWindowPos(boolean placement) throws EndTimerException, MoveStoppedException {
        return gamePlay.askWindowPos(placement);
    }

    /**
     * Metodo che richiede al controller della schermata di gioco (gamePlay) la posizione del dado
     * del roundTrack, utilizzato dalla toolCard 5
     * @return un array di int pos:
     *                  pos[0]: numero del round scelto
     *                  pos[1]: posizione del dado all'interno del roundTrack
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    @Override
    public int[] askRoundTrackPos() throws EndTimerException, MoveStoppedException {
        return gamePlay.askRoundTrackPos();
    }

    /**
     * Metodo che richiede al controller della schermata di gioco (gamePlay) il numero che l'utente
     * vuole assegnare al dado, utilizzato dalla toolCard 11
     * @param c è il colore del nuovo dado estratto
     * @return 1-6: il numero scelto dall'utente
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    @Override
    public int askNumber(Colors c) throws EndTimerException, MoveStoppedException {
        return gamePlay.askNumber(c);
    }

    /**
     * Metodo che richiede al controller della schermata di gioco (gamePlay) di stampare il nuovo
     * dado estratto, utilizzato dalla toolCard 6
     * @param d è il nuovo dado estratto
     */
    @Override
    public void printDice(Dice d) {
        gamePlay.printDice(d);
    }

    /**
     * Metodo che richiede al controller della schermata di gioco (gamePlay) il numero degli spostamenti che
     * l'utente vuole fare, utilizzato dalla toolCard 12
     * @return true --> 2 spostamenti
     *         false --> 1 spostamento
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException  se l'utente decide di annullare la mossa
     */
    @Override
    public boolean askHowMany() throws EndTimerException, MoveStoppedException {
        return gamePlay.askHowMany();
    }

    /**
     * Metodo che comunica al controller della schermata di gioco (gamePlay) che il turno è finito
     */
    @Override
    public void printEndTurn() {
        gamePlay.printEndTurn();
    }

    /**
     * Metodo che comunica al client che l'utente ha terminato il suo turno
     * Utilizzato quando l'utente passa volontariamente
     */
    public void endTurn(){
        try {
            player.endTurn();
        } catch (IOException e) {
        }
    }

    /**
     * Notifica al controller dei pattern (timer durante la scelta dei pattern) o al controller della schermata di gioco
     * la scadenza del timer
     */
    @Override
    public void endTimer() {
        if(gamePlay==null)
            pattern.endTimer();
        else
            gamePlay.endTimer();
    }

    /**
     * Metodo richiamato a fine partita che carica schermata con pattern e vincitori
     * @param ranking lista delle posizioni dei vari giocatori in ordine di punteggio
     */
    @Override
    public void endGame(ArrayList<Integer> ranking) {
        Platform.runLater(() -> {
            FXMLLoader endGameLoader;
            endGameLoader = new FXMLLoader(getClass().getResource("/fxml/endingGame.fxml"));
            Parent pane;
            Scene scene = null;
            try {
                pane = endGameLoader.load();
                scene = new Scene(pane);
            } catch (IOException e) {
                System.out.println("Problemi nel caricamento del gioco.");
            }
            endGame = endGameLoader.getController();
            GUI.stage.setScene(scene);
            synchronized (monitor) {
                state = true;
                monitor.notifyAll();
            }
        });
        if(endGame == null)
            waitLoading();
        endGame.showResult(gamePlay.getPlayerInterfaces(), gamePlay.getWindowFrames(), ranking);
    }

    /**
     * Aggiornamento del draftPool modificato
     * @param draft è il draft aggiornato
     */
    @Override
    public void updateDraftPool(ArrayList<Dice> draft) {
        loadingGamePlayView();
        gamePlay.updateDraftPool(draft);
    }

    /**
     * Aggiornamento delle toolCard disponibile nella partita
     * @param tools è la lista delle carte strumento, dove ogni intero [1-12] rappresenta il numero di una carta
     * in base all'id
     */
    @Override
    public void updateTools(ArrayList<Integer> tools) {
        loadingGamePlayView();
        Platform.runLater(() -> gamePlay.updateTools(tools));
    }

    /**
     * Aggiornamento delle toolCard disponibile nella partita
     * @param cards è la lista delle carte pubbliche, dove ogni intero [1-10] rappresenta il numero di una carta
     * in base all'id
     */
    @Override
    public void updatePublicObjectiveCards(ArrayList<Integer> cards) {
        loadingGamePlayView();
        Platform.runLater(() -> gamePlay.updatePublicObjectiveCards(cards));
    }

    /**
     * Aggiornamento del giocatore corrente
     * @param currentPlayer rappresenta la posizione del giocatore corrente all'interno della lista di giocatori
     */
    @Override
    public void updateCurrentPlayer(int currentPlayer) {
        loadingGamePlayView();
        Platform.runLater(() -> gamePlay.updateCurrentPlayer(currentPlayer));
    }

    /**
     * Aggiornamento dei segnalini favore delle carte strumento
     * @param tokens è la lista dei segnalini aggiornati, ordinati come la lista delle carte strumenti
     */
    @Override
    public void updateTokens(ArrayList<Integer> tokens) {
        loadingGamePlayView();
        Platform.runLater(() -> gamePlay.updateTokens(tokens));
    }

    /**
     * Aggiornamento del turno del round
     * @param turn è il turno del round, può essere ZERO, FIRST, SECOND
     */
    @Override
    public void updateGameTurn(Turns turn) {
        loadingGamePlayView();
        Platform.runLater(() -> gamePlay.updateGameTurn(turn));
    }

    /**
     * Aggiornamento della windowframe del giocatore corrente
     * @param wf è la windowframe aggiornata
     */
    @Override
    public void updateWindowsFrame(WindowFrame wf) {
        loadingGamePlayView();
        Platform.runLater(() -> gamePlay.updateWindowsFrame(wf));
    }

    /**
     * Aggiornamento del roundTrack modificato
     * @param track è il roundTrack aggiornato
     */
    @Override
    public void updateRoundTrack(HashMap<Integer, ArrayList<Dice>> track) {
        loadingGamePlayView();
        Platform.runLater(() -> gamePlay.updateRoundTrack(track));
    }

    /**
     * Aggiornamento del round della partita
     * @param r [1-10]: è il round della partita
     */
    @Override
    public void updateCurrentRound(int r) {
        loadingGamePlayView();
        Platform.runLater(() -> gamePlay.updateCurrentRound(r));
    }

    /**
     * Salva il colore della carta privata se il giocatore è quello che lanciata il gioco (utilizzato poi nella schermata
     * della scelta dei pattern)
     * Aggiornamento dello stato di uno specifico giocatore
     * @param player è l'oggetto giocatore da aggiornare
     * @param pos è la posizione del giocatore all'interno della lista dei giocatori
     */
    @Override
    public void updatePlayerList(Player player, int pos) {
        if(player.getUsername().equals(this.username))
            privateCard = player.getPrivateObjectiveCard().getColor();
        if(gamePlay != null)
            Platform.runLater(() -> gamePlay.updatePlayer(player, pos));
    }

    /**
     * Aggiornamento dello stato della windowFrame di uno specifico giocatore
     * @param wf è l'oggetto windowFrame da aggiornare
     * @param pos è la posizione della windowFrame all'interno della lista di windowFrame
     */
    @Override
    public void updateWf(WindowFrame wf, int pos) {
        loadingGamePlayView();
        Platform.runLater(() -> gamePlay.updateWf(wf, pos));
    }

    /**
     * Notifica la disconnessione di un giocatore
     * @param name è il nome del giocatore che si è disconnesso
     */
    @Override
    public void alertDisconnectionOf(String name) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText(name + " si è disconnesso.");
            alert.initOwner(stage);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        });
    }

    /**
     * Notifica l'avvenuta connessione di un giocatore (sia nel caso di connessione iniziale ad inizio partita
     * che nel caso di riconnessione alla partita)
     * @param name è il nome del giocatore che si è connesso/riconnesso
     */
    @Override
    public void alertConnectionOf(String name) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText(name + " si è connesso alla partita.");
            alert.initOwner(stage);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        });
    }

    /**
     * Metodo utilizzato dagli update nel caso in cui il controller della schermata di gioco sia null, utilizzato
     * in particolare quando un utente si riconnette, e la sequenza degli update è casuale
     */
    private void loadingGamePlayView(){
        if(gamePlay == null) {
            Platform.runLater(() -> {
                FXMLLoader gameLoader;
                gameLoader = new FXMLLoader(getClass().getResource("/fxml/prova.fxml"));
                Parent pane = null;
                Scene scene = null;
                try {
                    pane = gameLoader.load();
                    scene = new Scene(pane);
                } catch (IOException e) {
                    showAlert("Problemi nel caricamento del gioco.");
                }
                gamePlay = gameLoader.getController();
                gamePlay.setGui(this);
                pane.setStyle("-fx-background-image: url('/img/sfondo1.png');" + "-fx-background-size: cover;");
                if(stopAlert != null)
                    stopAlert.fire();
                stage.setScene(scene);
                synchronized (monitor) {
                    state = true;
                    monitor.notifyAll();
                }
            });
            waitLoading();
            state = false;
        }
    }

    /**
     * Metodo utilizzato da diversi thread per mettersi in attesa del cambio di stato
     */
    private void waitLoading(){
        while(!isState()){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Metodo per mostrare un alert con uno specifico messaggio
     * @param text è il testo da mostrare all'utente
     */
    private void showAlert(String text){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText(text);
            alert.initOwner(stage);
            if(gamePlay == null){
                ButtonType cancel = new ButtonType("");
                alert.getButtonTypes().clear();
                alert.getButtonTypes().add(cancel);
                alert.initStyle(StageStyle.UNDECORATED);
                stopAlert = (Button) alert.getDialogPane().lookupButton(cancel);
                stopAlert.setVisible(false);
                Optional<ButtonType> option = alert.showAndWait();
                if(option.isPresent() && option.get() == cancel){
                }
            }
            else
                alert.showAndWait();
        });
    }

    /**
     * Metodo richiesto dal gamePlay controller per richiedere al server di utilizzare una specifica carta strumento
     * @param pos è la posizione della carta strumento scelta all'interno dell'arrayList di carte strumento di game
     * @throws DiceNotFoundException se non trova un dado
     * @throws RulesBreakException se vengono violate le regole del gioco
     * @throws CellNotEmptyException se la cella dove si vuole posizionare il dado non è vuota
     * @throws IOException se ci sono problemi con la connessione
     * @throws InvalidParamsException se sono violate delle regole durante l'utilizzo della carta strumento
     * @throws NotPlayingException se il giocatore non sta giocando
     * @throws DraftFullException se il draft è pieno
     * @throws NotValidCellException se la cella selezionata non è valida
     */
    public void useTool(int pos) throws DiceNotFoundException, RulesBreakException, CellNotEmptyException, IOException, InvalidParamsException, NotPlayingException, DraftFullException, NotValidCellException {
        player.useToolCard(pos);
    }

    /**
     * Metodo richiesto dal gamePlay controller per richiedere al server di posizionare un dado
     * @param draftPos è la posizione nel draft del dado selezionato
     * @param windowPos è la posizione all'interno della windowframe dove si vuole posizionare il dado
     * @throws IOException se ci sono problemi di connessione col server
     * @throws CellNotEmptyException se la cella selezionata non è valida
     * @throws NotPlayingException se il giocatore non st giocando
     * @throws DiceNotFoundException se non viene trovato il dado da posizionare
     * @throws RulesBreakException se vengolo violate le regole del gioco
     */
    public void placeDice(int draftPos, int windowPos) throws IOException, CellNotEmptyException, NotPlayingException, DiceNotFoundException, RulesBreakException {
        player.placeDice(draftPos, windowPos);
    }

    public static Object getMonitor() {
        return monitor;
    }

    private synchronized boolean isState() {
        return state;
    }

    public String getUsername() {
        return username;
    }

    public CommandInterface getPlayer() {
        return player;
    }

    public void setPlayer(CommandInterface player) {
        this.player = player;
    }
}
