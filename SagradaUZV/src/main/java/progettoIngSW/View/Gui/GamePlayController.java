package progettoIngSW.View.Gui;

import progettoIngSW.Exceptions.*;
import progettoIngSW.GameInterface;
import progettoIngSW.Model.*;
import progettoIngSW.Model.Cell;
import progettoIngSW.PlayerInterface;
import progettoIngSW.WindowFrameInterface;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GamePlayController {

    //
    //GAME STATE
    //
    private static GameInterface game;
    private ArrayList<PlayerInterface> playerInterfaces;
    private ArrayList<WindowFrameInterface> windowFrames;
    private static HashMap<Integer, ArrayList<Dice>> track;
    private static PrivateObjectiveCard card;
    private HashMap<String, GridPane> playersWindow;
    private HashMap<String, Label> playersName;

    private static final Object monitor = new Object();

    private static boolean yourTurn;
    private static boolean endTimer = false;
    private static boolean moveStopped = false;
    private static Integer numRound;
    private static Button stopAlert;

    private GUI gui;

    @FXML
    GridPane roundTrack;
    @FXML
    GridPane draftPool;
    @FXML
    ImageView public1;
    @FXML
    ImageView public2;
    @FXML
    ImageView public3;
    @FXML
    ImageView tool1;
    @FXML
    ImageView tool2;
    @FXML
    ImageView tool3;
    @FXML
    Button placeDice;
    @FXML
    Button useTool;
    @FXML
    Button changeTurn;
    @FXML
    Button undoButton;
    @FXML
    Label tokens;
    @FXML
    Pane windowFrame;
    @FXML
    Pane player2;
    @FXML
    Pane player3;
    @FXML
    Pane player4;
    @FXML
    Label token1;
    @FXML
    Label token2;
    @FXML
    Label token3;
    @FXML
    Button privateCard;
    @FXML
    TextArea textBox;
    @FXML
    MenuButton roundMenu;
    @FXML
    GridPane toolCard;
    @FXML
    Label nRound;
    @FXML
    Label nTurn;
    @FXML
    Label username2;
    @FXML
    Label username3;
    @FXML
    Label username4;
    @FXML
    Label curPlayer;

    public GamePlayController(){
        game = Game.getGame();
        playerInterfaces = new ArrayList<>();
        windowFrames = new ArrayList<>();
        track = new HashMap<>();
        yourTurn = false;
        playersWindow = new HashMap<>();
        playersName = new HashMap<>();
    }

    @FXML
    public void initialize(){
        textBox.setBackground(Background.EMPTY);
        textBox.setStyle("-fx-background-color: transparent;");
        windowFrame.setStyle("-fx-border-color: black;");
        textBox.setWrapText(true);
        textBox.setEditable(false);
        synchronized (GUI.getMonitor()){
            GUI.getMonitor().notifyAll();
        }
    }

    /**
     * Notifica all'utente che il suo turno è iniziato, settando yourTurn a true, e endTimer e moveStopped a false,
     * variabili utilizzate per gestire le varie situazioni possibili durante il turno di gioco
     * Abilita tutti i bottoni della schermata di gioco
     * Si mette in attesa fino a quando l'utente decide di passare o scade il timer
     * @throws EndTimerException quando scade il timer
     */
    public void startTurn() throws EndTimerException {
        synchronized (monitor){
            yourTurn = true;
            endTimer = false;
            moveStopped = false;
        }
        Platform.runLater(() -> {
            textBox.clear();
            textBox.appendText("È il tuo turno!\n");
            useTool.setDisable(false);
            placeDice.setDisable(false);
            changeTurn.setDisable(false);
        });
        while(isYourTurn() && !endTimer){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if(endTimer){
            removeFilter();
            Platform.runLater(() -> {
                textBox.clear();
                textBox.appendText("Il tempo è scaduto!");
            });
            throw new EndTimerException();
        }
        removeFilter();
        gui.endTurn();
    }

    //
    //COMMAND METHODS
    //

    /**
     * Metodo richiamato dal pulsante "Passa", controlla che sia il turno del giocatore, in caso contrario
     * notifica al giocatore che non è il suo turno
     * Se è il turno del giocatore, setta yourTurn a false e risveglia i thread in attesa (thread in startTurn)
     */
    public void changeTurn(){
        if(!yourTurn){
            textBox.clear();
            textBox.appendText("Non è il tuo turno!");
        }
        else{
            synchronized (monitor) {
                yourTurn = false;
                monitor.notifyAll();
            }
            textBox.clear();
            textBox.appendText("Il tuo turno è finito. Aspetta il tuo prossimo turno per giocare.");
        }
    }

    /**
     * Metodo richiamato dal pulsante "Posiziona dado", controlla che sia il turno del giocatore, in caso contrario
     * notifica al giocatore che non è il suo turno
     * Se è il turno del giocatore, setta moveStopped a false, disattiva i bottoni "Passa" e "Usa ToolCard"
     * Richiama il metodo placeDice, dopo aver richiesto la posizione del draftPool e della windowFrame
     * Gestisce le varie eccezioni mostrando un messaggio di errore specifico per ogni situazione
     */
    public void placeDice(){
        if(!yourTurn){
            textBox.clear();
            textBox.appendText("Non è il tuo turno!");
        }
        else{
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call(){
                    try {
                        moveStopped = false;
                        changeTurn.setDisable(true);
                        useTool.setDisable(true);
                        gui.placeDice(askDraftPos(), askWindowPos(true));
                        textBox.clear();
                    } catch (IOException e) {
                        textBox.clear();
                        textBox.appendText("Problemi del gioco.");
                    } catch (CellNotEmptyException e) {
                        textBox.clear();
                        textBox.appendText("La cella che hai selezionato non è vuota.");
                    } catch (NotPlayingException e) {
                        textBox.clear();
                        textBox.appendText("Non stai giocando.");
                    } catch (DiceNotFoundException e) {
                        textBox.clear();
                        textBox.appendText("Dado non trovato.");
                    } catch (RulesBreakException e) {
                        showRulesBreak(e.getType());
                    } catch (EndTimerException e) {
                        textBox.clear();
                        textBox.appendText("Timer scaduto.");
                    } catch (MoveStoppedException ignored) {
                    }
                    changeTurn.setDisable(false);
                    useTool.setDisable(false);
                    moveStopped = false;
                    return null;
                }
            };
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Metodo richiamato dal pulsante "Usa ToolCard", controlla che sia il turno del giocatore, in caso contrario
     * notifica al giocatore che non è il suo turno
     * Se è il turno del giocatore, setta moveStopped a false, disattiva i bottoni "Passa" e "Posiziona dado"
     * Richiama il metodo placeDice, dopo aver richiesto la posizione del draftPool e della windowFrame
     * Gestisce le varie eccezioni mostrando un messaggio di errore specifico per ogni situazione
     */
    public void useTool() {
        if(!yourTurn){
            textBox.clear();
            textBox.appendText("Non è il tuo turno!");
        }
        else{
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        moveStopped=false;
                        placeDice.setDisable(true);
                        changeTurn.setDisable(true);
                        gui.useTool(askToolPos());
                        textBox.clear();
                    } catch (DiceNotFoundException e) {
                        textBox.clear();
                        textBox.appendText("Dado non trovato.");
                    } catch (RulesBreakException e) {
                        showRulesBreak(e.getType());
                    } catch (CellNotEmptyException e) {
                        textBox.clear();
                        textBox.appendText("Cella non vuota.");
                    } catch (IOException e) {
                        textBox.clear();
                        textBox.appendText("Problemi del gioco.");
                    } catch (InvalidParamsException e) {
                        textBox.clear();
                        textBox.appendText("Rispetta le regole della carta.");
                    } catch (NotPlayingException e) {
                        textBox.clear();
                        textBox.appendText("Non stai giocando.");
                    } catch (DraftFullException e) {
                        textBox.clear();
                        textBox.appendText("Il draft è pieno.");
                    } catch (NotValidCellException e) {
                        textBox.clear();
                        textBox.appendText("Cella non valida.");
                    } catch (EndTimerException e) {
                        textBox.clear();
                        textBox.appendText("Timer scaduto.");
                    } catch (MoveStoppedException ignored){
                    }
                    placeDice.setDisable(false);
                    changeTurn.setDisable(false);
                    moveStopped = false;
                    return null;
                }
            };
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Metodo richiamato dal pulsante "Annulla", controlla che sia il turno del giocatore, in caso contrario
     * notifica al giocatore che non è il suo turno
     * Se è il turno del giocatore setta moveStopped a true, e risveglia i thread in attesa (tutti gli ask method che
     * devono rimanere in attesa di una scelta dell'utente)
     */
    public void undoMoves(){
        if(!yourTurn){
            textBox.clear();
            textBox.appendText("Non è il tuo turno!");
        }else {
            if(!moveStopped) {
                synchronized (monitor) {
                    moveStopped = true;
                    monitor.notifyAll();
                }
            }
        }
    }

    /**
     * Metodo richiamato dal pulsante "Carta privata", mostra la carta privata del giocatore in un'altra finestra
     */
    public void showPublic(){
        StackPane pane = new StackPane();
        ImageView image = new ImageView();
        image.setImage(privateImg(card.getColor()));
        pane.getChildren().add(image);
        Scene scene = new Scene(pane, 240, 360);
        Stage  stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metodo richiamato dal pulsante 1 in "Round", mostra i dadi del primo round sul Rountrack
     */
    public void showRound1(){
        showTrack(1);
        numRound = 1;
    }

    /**
     * Metodo richiamato dal pulsante 2 in "Round", mostra i dadi del secondo round sul Rountrack
     */
    public void showRound2(){
        showTrack(2);
        numRound = 2;
    }

    /**
     * Metodo richiamato dal pulsante 3 in "Round", mostra i dadi del terzo round sul Rountrack
     */
    public void showRound3(){
        showTrack(3);
        numRound = 3;
    }

    /**
     * Metodo richiamato dal pulsante 4 in "Round", mostra i dadi del quarto round sul Rountrack
     */
    public void showRound4(){
        showTrack(4);
        numRound = 4;
    }

    /**
     * Metodo richiamato dal pulsante 5 in "Round", mostra i dadi del quinto round sul Rountrack
     */
    public void showRound5(){
        showTrack(5);
        numRound = 5;
    }

    /**
     * Metodo richiamato dal pulsante 6 in "Round", mostra i dadi del sesto round sul Rountrack
     */
    public void showRound6(){
        showTrack(6);
        numRound = 6;
    }

    /**
     * Metodo richiamato dal pulsante 7 in "Round", mostra i dadi del settimo round sul Rountrack
     */
    public void showRound7(){
        showTrack(7);
        numRound = 7;
    }

    /**
     * Metodo richiamato dal pulsante 8 in "Round", mostra i dadi del ottavo round sul Rountrack
     */
    public void showRound8(){
        showTrack(8);
        numRound = 8;
    }

    /**
     * Metodo richiamato dal pulsante 9 in "Round", mostra i dadi del nono round sul Rountrack
     */
    public void showRound9(){
        showTrack(9);
        numRound = 9;
    }

    /**
     * Metodo richiamato dal pulsante 10 in "Round", mostra i dadi del decimo round sul Rountrack
     */
    public void showRound10(){
        showTrack(10);
        numRound = 10;
    }

    /**
     * Mostra i dadi del roundTrack appartenenti al round selezionato
     * @param round è il numero del round selezionato
     */
    private void showTrack(int round) {
        for(int i = 0; i < track.get(round).size(); i++){
            pickUpDice(track.get(round).get(i), getCellFromGridPane(roundTrack, i, 0));
            Pane cell = getCellFromGridPane(roundTrack, i, 0);
            if(cell != null)
                cell.setVisible(true);
        }
        for(int i = track.get(round).size(); i < 9; i++){
            Pane cell = getCellFromGridPane(roundTrack, i, 0);
            if(cell != null)
                cell.setVisible(false);
        }
    }

    //
    //ASK METHODS
    //

    /**
     * Metodo che richiede all'utente che toolCard vuole utilizzare, aggiungendo un evento MOUSE CLICKED a ogni tool,
     * per sapere qual è stata selezionata
     * @return la posizione della tool all'interno dell'array list di toolCard
     * @throws EndTimerException se il timer del turno finisce
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    private int askToolPos() throws EndTimerException, MoveStoppedException {
        AtomicInteger toolChoosed = new AtomicInteger(-1);
        Platform.runLater(() -> {
            if(!textBox.getText().isEmpty())
                textBox.clear();
            textBox.appendText("Scegli la carta strumento che vuoi usare\n");
            for (Node element : toolCard.getChildren()) {
                element.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    synchronized (monitor){
                        toolChoosed.set(GridPane.getColumnIndex(element));
                        monitor.notifyAll();
                    }
                });
            }
        });
        while (toolChoosed.get() == -1 && !moveStopped && !endTimer){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if(endTimer)
            throw new EndTimerException();
        if(moveStopped) {
            printMoveStopped();
            throw new MoveStoppedException();
        }
        return toolChoosed.get();
    }

    /**
     * Metodo che richiede all'utente la posizione della cella, aggiungendo un evento MOUSE CLICKED a ogni cella,
     * per sapere qual è stata selezionata
     * @param placement true -> posizione dove l'utente vuole posizionare il dado
     *                  false -> posizione dove sta il dado che l'utente vuole spostare
     * @return la posizione scelta
     * @throws EndTimerException se il timer del turno finisce
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    public int askWindowPos(boolean placement) throws EndTimerException, MoveStoppedException {
        AtomicInteger windowPos = new AtomicInteger(-1);
        Platform.runLater(() -> {
            textBox.clear();
            String message = (placement) ? "Seleziona la cella dove vuoi posizionare il dado":
                    "Seleziona il dado che vuoi spostare";
            textBox.appendText(message);
            for (Node element : playersWindow.get(gui.getUsername()).getChildren()) {
                element.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    synchronized (monitor){
                        windowPos.set(5*GridPane.getRowIndex(element) + GridPane.getColumnIndex(element));
                        monitor.notifyAll();
                    }
                });
            }
        });
        while (windowPos.get() == -1 && !endTimer && !moveStopped){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if(endTimer)
            throw  new EndTimerException();
        if(moveStopped) {
            printMoveStopped();
            throw new MoveStoppedException();
        }
        return windowPos.get();
    }

    /**
     * Metodo che richiede all'utente la posizione del dado della riserva (draftPool), aggiungendo un evento MOUSE CLICKED
     * a ogni cella, per sapere qual è stata selezionata
     * @return 0-players.size()*2: la posizione del dado scelto dall'utente
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    public int askDraftPos() throws EndTimerException, MoveStoppedException {
        AtomicInteger draftPos = new AtomicInteger(-1);
        Platform.runLater(() -> {
            textBox.clear();
            textBox.appendText("Scegli un dado del draftpool\n");
            for (Node element : draftPool.getChildren()) {
                element.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    synchronized (monitor){
                        draftPos.set(GridPane.getColumnIndex(element));
                        monitor.notifyAll();
                    }
                });
            }
        });
        while (draftPos.get() == -1 && !endTimer && !moveStopped){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if(endTimer)
            throw new EndTimerException();
        if(moveStopped) {
            printMoveStopped();
            throw new MoveStoppedException();
        }
        return draftPos.get();
    }

    /**
     * Metodo che richiede all'utente la posizione del dado del roundTrack (utilizzato dalla toolCard 5), , aggiungendo
     * un evento MOUSE CLICKED a ogni cella, per sapere qual è stata selezionata
     * @return un array di int pos:
     *                  pos[0]: numero del round scelto
     *                  pos[1]: posizione del dado all'interno del roundTrack
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    public int[] askRoundTrackPos() throws EndTimerException, MoveStoppedException {
        AtomicInteger roundPos = new AtomicInteger(-1);
        Platform.runLater(() -> {
            textBox.clear();
            textBox.appendText("Scegli un dado del roundTrack\n");
            for (Node element : roundTrack.getChildren()) {
                element.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    synchronized (monitor){
                        roundPos.set(GridPane.getColumnIndex(element));
                        monitor.notifyAll();
                    }
                });
            }
        });
        while (roundPos.get() == -1 && !endTimer && !moveStopped){
            try {
                synchronized (monitor){
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if(endTimer)
            throw new EndTimerException();
        if(moveStopped) {
            printMoveStopped();
            throw new MoveStoppedException();
        }
        return new int[]{numRound, roundPos.get()};
    }

    /**
     * Metodo che richiede (tramite un alert a comparsa) all'utente se vuole aumentare o diminuire il dado scelto (toolCard1)
     * @return la scelta dell'utente:
     *          true --> l'utente vuole aumentare il valore del dado
     *          false --> l'utente vuole diminuire il valore del dado
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    public boolean askIncrease() throws EndTimerException, MoveStoppedException {
        AtomicInteger choice = new AtomicInteger(-1);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Vuoi aumentare o diminuire il numero del dado?");
            alert.getButtonTypes().remove(ButtonType.OK);
            ButtonType increase = new ButtonType("Aumentare");
            ButtonType decrease = new ButtonType("Diminuire");
            ButtonType undo = new ButtonType("Annulla");
            alert.getButtonTypes().addAll(increase, decrease, undo);
            stopAlert = (Button) alert.getDialogPane().lookupButton(undo);
            Optional<ButtonType> option = alert.showAndWait();
            if(option.isPresent() && option.get() == increase){
                choice.set(1);
            }
            else if(option.isPresent() && option.get() == decrease){
                choice.set(0);
            }
            else if(option.isPresent() && option.get() == undo){
                choice.set(2);
            }
            synchronized (monitor){
                monitor.notifyAll();
            }
        });
        while (choice.get() == -1 && !endTimer){
            synchronized (monitor){
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if(endTimer) {
            Platform.runLater(() -> stopAlert.fire());
            throw new EndTimerException();
        }
        if(choice.get() == 2) {
            printMoveStopped();
            throw new MoveStoppedException();
        }
        return choice.get() == 1;
    }

    /**
     * Metodo che richiede (tramite un alert a comparsa) all'utente il numero degli spostamenti
     * che l'utente vuole fare, utilizzato dalla toolCard 12
     * @return true --> 2 spostamenti
     *         false --> 1 spostamento
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException  se l'utente decide di annullare la mossa
     */
    public boolean askHowMany() throws EndTimerException, MoveStoppedException {
        AtomicInteger howMany = new AtomicInteger(-1);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Quanti spostamenti vuoi fare?");
            alert.getButtonTypes().clear();
            ButtonType one = new ButtonType("1");
            ButtonType two = new ButtonType("2");
            ButtonType undo = new ButtonType("Annulla");
            alert.getButtonTypes().addAll(one, two, undo);
            stopAlert = (Button) alert.getDialogPane().lookupButton(undo);
            Optional<ButtonType> option = alert.showAndWait();
            if(option.isPresent() && option.get() == one){
                synchronized (monitor){
                    howMany.set(0);
                    monitor.notifyAll();
                }
            }
            else if(option.isPresent() && option.get() == two){
                synchronized (monitor){
                    howMany.set(1);
                    monitor.notifyAll();
                }
            }
            else if(option.isPresent() && option.get() == undo){
                synchronized (monitor){
                    howMany.set(2);
                    monitor.notifyAll();
                }
            }
        });
        while (howMany.get() == -1 && !endTimer){
            synchronized (monitor){
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if(endTimer) {
            Platform.runLater(() -> stopAlert.fire());
            throw new EndTimerException();
        }
        if(howMany.get() == 2) {
            printMoveStopped();
            throw new MoveStoppedException();
        }
        return howMany.get() == 1;
    }

    /**
     * Metodo che stampa il nuovo dado estratto, utilizzato dalla toolCard 6
     * @param d è il nuovo dado estratto
     */
    public void printDice(Dice d) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText("Il nuovo dado che devi piazzare è:");
            alert.setGraphic(null);
            Pane dice = new Pane();
            dice.setPrefSize(50, 50);
            dice.setMaxSize(50, 50);
            Pane container = new Pane(dice);
            pickUpDice(d, dice);
            alert.getDialogPane().setContent(container);
            alert.showAndWait();
        });
    }

    /**
     * Metodo che richiede all'utente il numero che vuole assegnare al dado, utilizzato dalla toolCard 11
     * @param c è il colore del nuovo dado estratto
     * @return 1-6: il numero scelto dall'utente
     * @throws EndTimerException quando scade il timer del turno
     * @throws MoveStoppedException se l'utente decide di annullare la mossa
     */
    public int askNumber(Colors c) throws EndTimerException, MoveStoppedException {
        AtomicInteger choice = new AtomicInteger(-1);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(null);
            alert.setHeaderText(null);
            Dice d = new Dice(c);
            alert.setHeaderText("Scegli il numero del dado estratto");
            alert.setGraphic(null);
            Pane dice = new Pane();
            dice.setPrefSize(50, 50);
            dice.setMaxSize(50, 50);
            Pane container = new Pane(dice);
            pickUpDice(d, dice);
            alert.getDialogPane().setContent(container);
            alert.setContentText(null);
            ButtonType one = new ButtonType(""+1);
            ButtonType two = new ButtonType(""+2);
            ButtonType three = new ButtonType(""+3);
            ButtonType four = new ButtonType(""+4);
            ButtonType five = new ButtonType(""+5);
            ButtonType six = new ButtonType(""+6);
            ButtonType undo = new ButtonType("Interrompi");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(one, two, three, four, five, six, undo);
            stopAlert = (Button) alert.getDialogPane().lookupButton(undo);
            Optional<ButtonType> option = alert.showAndWait();
            if(option.isPresent() && option.get() == one){
                synchronized (monitor){
                    choice.set(1);
                    monitor.notifyAll();
                }
            }else if(option.isPresent() && option.get() == two){
                synchronized (monitor){
                    choice.set(2);
                    monitor.notifyAll();
                }
            }else if(option.isPresent() && option.get() == three){
                synchronized (monitor){
                    choice.set(3);
                    monitor.notifyAll();
                }
            }else if(option.isPresent() && option.get() == four){
                synchronized (monitor){
                    choice.set(4);
                    monitor.notifyAll();
                }
            }else if(option.isPresent() && option.get() == five){
                synchronized (monitor){
                    choice.set(5);
                    monitor.notifyAll();
                }
            }else if(option.isPresent() && option.get() == six){
                synchronized (monitor){
                    choice.set(6);
                    monitor.notifyAll();
                }
            }else if(option.isPresent() && option.get() == undo){
                synchronized (monitor){
                    choice.set(0);
                    monitor.notifyAll();
                }
            }
        });
        while (choice.get() == -1 && !endTimer){
            synchronized (monitor){
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if(choice.get() == 0) {
            printMoveStopped();
            throw new MoveStoppedException();
        }
        if(endTimer) {
            Platform.runLater(() -> stopAlert.fire());
            throw new EndTimerException();
        }
        return choice.get();
    }

    /**
     *Metodi di update, si occupato di aggiornare lo stato del gioco, salvandosi esclusivamente le informazioni utili
     * al gioco
     */
    //
    //UPDATE METHODS
    //
    public void updateTools(ArrayList<Integer> tools) {
        Platform.runLater(() -> {
            tool1.setImage(toolImg(tools.get(0)));
            tool2.setImage(toolImg(tools.get(1)));
            tool3.setImage(toolImg(tools.get(2)));
            createTooltip(tool1, toolImg(tools.get(0)));
            createTooltip(tool2, toolImg(tools.get(1)));
            createTooltip(tool3, toolImg(tools.get(2)));
        });
    }

    public void updatePublicObjectiveCards(ArrayList<Integer> cards) {
        Platform.runLater(() -> {
            public1.setImage(publicImg(cards.get(0)));
            public2.setImage(publicImg(cards.get(1)));
            public3.setImage(publicImg(cards.get(2)));
            createTooltip(public1, publicImg(cards.get(0)));
            createTooltip(public2, publicImg(cards.get(1)));
            createTooltip(public3, publicImg(cards.get(2)));
        });
    }

    public void updateCurrentPlayer(int currentPlayer) {
        Platform.runLater(() -> {
            curPlayer.setText("Sta giocando: "+playerInterfaces.get(currentPlayer).getUsername());
        });
        game.setCurrentPlayer(currentPlayer);
    }

    public void updateTokens(ArrayList<Integer> tokens) {
        Platform.runLater(() -> {
            token1.setText("Tok. "+tokens.get(0));
            token2.setText("Tok. "+tokens.get(1));
            token3.setText("Tok. "+tokens.get(2));
        });
    }

    public void updateGameTurn(Turns turn) {
        Platform.runLater(() -> {
            switch (turn){
                case ZERO: nTurn.setText("Turno: zero");
                    break;
                case FIRST: nTurn.setText("Turno: primo");
                    break;
                case SECOND: nTurn.setText("Turno: secondo");
                    break;
            }
        });
    }

    public void updateWindowsFrame(WindowFrame wf) {
        modifyWf(playersWindow.get(playerInterfaces.get(game.getCurrentPlayer()).getUsername()), wf);
    }

    public void updateDraftPool(ArrayList<Dice> draft) {
        Platform.runLater(() -> {
            for(int i = 0; i < draft.size(); i++){
                //getCellFromGridPane(draftPool, i, 0).setImage(pickUpDice(draft.get(i)));
                pickUpDice(draft.get(i), getCellFromGridPane(draftPool, i, 0));
                getCellFromGridPane(draftPool, i, 0).setVisible(true);
            }
            for(int i = draft.size(); i < 9; i++){
                getCellFromGridPane(draftPool, i, 0).setVisible(false);
                //getCellFromGridPane(draftPool, i, 0).setImage(null);
            }
        });
    }

    public void updateRoundTrack(HashMap<Integer,ArrayList<Dice>> track) {
        GamePlayController.track = track;
        if(numRound!= null) {
            Platform.runLater(() -> {
                for (int i = 0; i < track.get(numRound).size(); i++) {
                    //getCellFromGridPane(roundTrack, i, 0).setImage(pickUpDice(track.get(numRound).get(i)));
                    pickUpDice(track.get(numRound).get(i), getCellFromGridPane(roundTrack, i, 0));
                    getCellFromGridPane(roundTrack, i, 0).setVisible(true);
                }
                for (int i = track.get(numRound).size(); i < 9; i++) {
                    getCellFromGridPane(roundTrack, i, 0).setVisible(false);
                    //getCellFromGridPane(roundTrack, i, 0).setImage(null);
                }
            });
        }
    }

    public void updateCurrentRound(int r) {
        Platform.runLater(() -> {
            nRound.setText("Round: " + r);
        });
    }

    public void updatePlayer(Player player, int pos) {
        Platform.runLater(() -> {
            for(PlayerInterface p : playerInterfaces) {
                if (p.getUsername().equals(player.getUsername())) {
                    playerInterfaces.set(playerInterfaces.indexOf(p), player);
                    if(player.getUsername().equals(gui.getUsername()))
                        tokens.setText(""+player.getNumTokens());
                    else
                        playersName.get(player.getUsername()).setText(player.getUsername() + "\nnum Tok: " + player.getNumTokens());
                    return;
                }
            }
            playerInterfaces.add(player);

            if(player.getUsername().equals(gui.getUsername())){
                card = player.getPrivateObjectiveCard();
                tokens.setText("" + player.getNumTokens());
            }
            else{
                playersName.put(player.getUsername(), chooseName(pos));
            }
            if(!Objects.equals(player.getUsername(), gui.getUsername())) {
                playersName.get(player.getUsername()).setText(player.getUsername() + "\nnum Tok: " + player.getNumTokens());
            }
        });
    }

    public void updateWf(WindowFrame wf, int pos) {
        Platform.runLater(() -> {
            if(playersWindow.get(playerInterfaces.get(pos).getUsername()) == null){
                GridPane window = new GridPane();
                Pane wfplayer;
                if(playerInterfaces.get(pos).getUsername().equals(gui.getUsername()))
                    wfplayer = windowFrame;
                else
                    wfplayer = chooseGrid(pos);

                for(int i = 0; i<wf.getRow(); i++){
                    for(int j = 0; j<wf.getCol(); j++){
                        Pane cell = new Pane();
                        cell.setPrefWidth(wfplayer.getPrefWidth()/wf.getCol());
                        cell.setPrefHeight(wfplayer.getPrefHeight()/wf.getRow());
                        window.add(cell, j, i);
                    }
                }
                wfplayer.getChildren().add(window);
                wfplayer.setStyle(wfplayer.getStyle() + "-fx-border-color: black;");
                playersWindow.put(playerInterfaces.get(pos).getUsername(), window);
            }
            modifyWf(playersWindow.get(playerInterfaces.get(pos).getUsername()), wf);
        });
        if(this.windowFrames.size() == pos){
            windowFrames.add(wf);
        }else if(this.windowFrames.size()>pos){
            windowFrames.set(pos,wf);
        }
    }

    public void endTimer() {
        synchronized (monitor){
            yourTurn = false;
            endTimer = true;
            monitor.notifyAll();
        }
    }

    //
    //PRINTER METHODS
    //

    /**
     * Metodo richiamato per assegnare la windowframe giusta a tutti i player
     * @param pos del player all'interno della lista
     * @return la posizione del pannello scelta
     */
    private Pane chooseGrid(int pos) {
        Pane grid = null;
        switch (pos){
            case 0: grid = player2;
                    break;
            case 1: if(player2.isVisible())
                        grid = player3;
                    else
                        grid = player2;
                    break;
            case 2: grid = player3;
                    break;
            case 3: if(player3.isVisible())
                        grid = player4;
                    else
                        grid = player3;
                    break;
        }
        if(grid != null)
            grid.setVisible(true);
        return grid;
    }

    /**
     * Metodo richiamato per assegnare il label giusto a tutti i player
     * @param pos del player all'interno della lista
     * @return la posizione del label scelto
     */
    private Label chooseName(int pos) {
        Label name = null;
        switch (pos){
            case 0: name = username2;
                break;
            case 1: if(!username2.getText().isEmpty())
                name = username3;
            else
                name = username2;
                break;
            case 2: name = username3;
                break;
            case 3: if(!username3.getText().isEmpty())
                name = username4;
            else
                name = username3;
                break;
        }
        return name;
    }

    /**
     * Metodo che crea l'immagine del dado
     * @param dice è il dado da creare
     * @param diceImage è il pannello sul quale si deve disegnare il dado
     */
    private static void pickUpDice(Dice dice, Pane diceImage) {
        PatternController.setNumber(diceImage, dice.getNumber());
        PatternController.setColor(diceImage, dice.getColor());
        diceImage.setStyle(diceImage.getStyle() + "-fx-background-radius: "+diceImage.getHeight()/8+";");
        for(Node img : diceImage.getChildren()){
            if(img instanceof Group)
                for(Node circle : ((Group) img).getChildren())
                    ((Circle) circle).setFill(Paint.valueOf("#000000"));
        }
    }

    /**
     * Metodo che aggiorna tutte le celle della windowFrame selezionata
     * @param gridPane è la griglia da modificare
     * @param windowFrame è la windowFrame aggiornata
     */
    public static void modifyWf(GridPane gridPane, WindowFrameInterface windowFrame) {
        for(int x = 0; x < windowFrame.getRow(); x++)
            for(int y = 0; y < windowFrame.getCol(); y++){
                Cell cell = windowFrame.getCell(x, y);
                Pane cellWindow = getCellFromGridPane(gridPane, y, x);
                if(cellWindow != null){
                    cellWindow.getChildren().clear();
                    cellWindow.setStyle("");
                }
                if(cell.getDice() != null){
                    pickUpDice(cell.getDice(), getCellFromGridPane(gridPane, y, x));
                }else{
                    int numbRestr = cell.getNumberRestriction();
                    Colors colorRestr = cell.getColorRestriction();
                    if (numbRestr != 0)
                        PatternController.setNumber(getCellFromGridPane(gridPane, y, x), numbRestr);
                    else
                        PatternController.setColor(getCellFromGridPane(gridPane, y, x), colorRestr);
                }
            }
    }

    /**
     * Metodo che ritorna la cella (di tipo Pane) appartenente a una determinata GridPane
     * @param gridPane è la griglia selezionata
     * @param col indice della colonna della cella all'interno della griglia
     * @param row indice della riga della cella all'interno della griglia
     * @return il Pane richiesto
     */
    private static Pane getCellFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return (Pane) node;
            }
        }
        return null;
    }

    /**
     * Metodo che ritorna l'immagine appartenente alla carta pubblica richiesta
     * @param numPublic è l'id della carta pubblica
     * @return l'immagine della carta pubblica
     */
    private Image publicImg(Integer numPublic) {
        Image img = null;
        switch (numPublic){
            case 1: img = new Image("img/publicCard/Public01.png");
                break;
            case 2: img = new Image("img/publicCard/Public02.png");
                break;
            case 3: img = new Image("img/publicCard/Public03.png");
                break;
            case 4: img = new Image("img/publicCard/Public04.png");
                break;
            case 5: img = new Image("img/publicCard/Public05.png");
                break;
            case 6: img = new Image("img/publicCard/Public06.png");
                break;
            case 7: img = new Image("img/publicCard/Public07.png");
                break;
            case 8: img = new Image("img/publicCard/Public08.png");
                break;
            case 9: img = new Image("img/publicCard/Public09.png");
                break;
            case 10: img = new Image("img/publicCard/Public10.png");
                break;
        }
        return img;
    }

    /**
     * Metodo che ritorna l'immagine appartenente alla toolCard richiesta
     * @param numTool è l'id della toolCard
     * @return l'immagine della toolCard
     */
    private Image toolImg(Integer numTool) {
        Image img = null;
        switch (numTool){
            case 1: img = new Image("img/toolCard/ToolCard01.png");
                break;
            case 2: img = new Image("img/toolCard/ToolCard02.png");
                break;
            case 3: img = new Image("img/toolCard/ToolCard03.png");
                break;
            case 4: img = new Image("img/toolCard/ToolCard04.png");
                break;
            case 5: img = new Image("img/toolCard/ToolCard05.png");
                break;
            case 6: img = new Image("img/toolCard/ToolCard06.png");
                break;
            case 7: img = new Image("img/toolCard/ToolCard07.png");
                break;
            case 8: img = new Image("img/toolCard/ToolCard08.png");
                break;
            case 9: img = new Image("img/toolCard/ToolCard09.png");
                break;
            case 10: img = new Image("img/toolCard/ToolCard10.png");
                break;
            case 11: img = new Image("img/toolCard/ToolCard11.png");
                break;
            case 12: img = new Image("img/toolCard/ToolCard12.png");
                break;
        }
        return img;
    }

    /**
     * Metodo che ritorna l'immagine appartenente alla carta privata richiesta
     * @param color è il colore che individua univocamente la carta privata
     * @return l'immagine della carta privata
     */
    private Image privateImg(Colors color) {
        Image img = null;
        switch (color){
            case BLUE: img = new Image("img/privateCard/PrivateBlue.png");
                break;
            case GREEN: img = new Image("img/privateCard/PrivateGreen.png");
                break;
            case PURPLE: img = new Image("img/privateCard/PrivatePurple.png");
                break;
            case YELLOW: img = new Image("img/privateCard/PrivateYellow.png");
                break;
            case RED: img = new Image("img/privateCard/PrivateRed.png");
                break;
        }
        return img;
    }

    /**
     * Metodo che stampa un messaggio specifico per ogni regole non rispettata
     * @param type è il tipo di regola non rispettata
     */
    private void showRulesBreak(RulesBreakException.CODE type) {
        switch (type){
            case TOKEN: {
                textBox.clear();
                textBox.appendText("Non hai abbastanza segnalini per usare la tool.");
                break;
            }
            case COLOR: {
                textBox.clear();
                textBox.appendText("Il dado che hai selezionato non ha lo stesso colore della restrizione della cella dove vuoi posizionarlo.");
                break;
            }
            case ADJACENCY: {
                textBox.clear();
                textBox.appendText("Il dado deve essere posizionato adiacente ortogonalmente o diagonalmente ad un altro.");
                break;
            }
            case MOVE: {
                textBox.clear();
                textBox.appendText("Hai già fatto questa mossa, aspetta il tuo prossimo turno per rifarla.");
                break;
            }
            case NUMBER: {
                textBox.clear();
                textBox.appendText("Il dado che hai selezionato non ha lo stesso numero della restrizione della cella dove vuoi posizionarlo.");
                break;
            }
            case EMPTYWF: {
                textBox.clear();
                textBox.appendText("Non hai dadi a sufficienza per utilizzare questa toolcard.");
                break;
            }
            case EMPTYTRACK: {
                textBox.clear();
                textBox.appendText("Non ci sono dadi nel roundtrack, perciò non puoi utilizzare questa tool.");
                break;
            }
            case FIRSTDICE: {
                textBox.clear();
                textBox.appendText("Il primo dado deve essere posizionato sul bordo della tua windowframe.");
                break;
            }
            case FIRSTTURN: {
                textBox.clear();
                textBox.appendText("Questa tool card può essere utilizzata solo durante il tuo secondo turno.");
                break;
            }
            case NEEDPLACE: {
                textBox.clear();
                textBox.appendText("Prima di poter utilizzare questa tool card devi posizionare un dado.");
                break;
            }
            case SECONDTURN: {
                textBox.clear();
                textBox.appendText("Questa tool card può essere utilizzata solo durante il tuo primo turno.");
                break;
            }
            case COLADJACENCY: {
                textBox.clear();
                textBox.appendText("Il dado che vuoi posizionare ha lo stesso colore di un dado ortogonalmente adiacente alla posizione scelta.");
                break;
            }
            case VALADJACENCY: {
                textBox.clear();
                textBox.appendText("Il dado che vuoi posizionare ha lo stesso valore di un dado ortogonalmente adiacente alla posizione scelta.");
                break;
            }
        }
    }

    /**
     * Metodo che crea un toolTip di una immagine, utilizzato per le toolCard e per le carte pubbliche
     * @param card è l'imageView dove si vuole creare il toolTip
     * @param image è l'immagine da rappresentare all'interno del toolTip
     */
    private void createTooltip(ImageView card, Image image){
        Tooltip tooltip1 = new Tooltip();
        ImageView imgView = new ImageView(image);
        imgView.setFitHeight(360);
        imgView.setFitWidth(240);
        tooltip1.setGraphic(imgView);
        Tooltip.install(card, tooltip1);
    }

    /**
     * Metodo che rimuove l'evento di tipo mouseClicked a tutti gli elementi del draftPool, windowFrame e roundTrack
     */
    private void removeFilter() {
        for(Node node : draftPool.getChildren())
            node.removeEventHandler(MouseEvent.MOUSE_CLICKED, event -> {});
        for(Node node : windowFrame.getChildren())
            node.removeEventHandler(MouseEvent.MOUSE_CLICKED, event -> {});
        for(Node node : roundTrack.getChildren())
            node.removeEventHandler(MouseEvent.MOUSE_CLICKED, event -> {});
    }

    /**
     * Metodo che avvisa l'utente che il suo turno è finito
     */
    public void printEndTurn(){
        Platform.runLater(() -> {
            textBox.clear();
            textBox.appendText("Il tuo turno è finito");
        });
    }

    /**
     * Metodo che avvisa l'utente che la mossa è stata annullata
     */
    private void printMoveStopped() {
        Platform.runLater(() -> {
            textBox.clear();
            textBox.appendText("Mossa annullata.");
        });
    }

    //
    //SETTER METHODS
    //
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    private synchronized boolean isYourTurn() {
        return yourTurn;
    }

    public ArrayList<PlayerInterface> getPlayerInterfaces() {
        return playerInterfaces;
    }

    public ArrayList<WindowFrameInterface> getWindowFrames() {
        return windowFrames;
    }
}
