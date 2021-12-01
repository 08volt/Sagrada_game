package progettoIngSW.Model;

import progettoIngSW.PlayerInterface;

import java.io.Serializable;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class Player extends Observable implements Serializable, Observer, PlayerInterface {

    //
    //ATTRIBUTES
    //
    private String username;
    private String password;
    private int numTokens;
    private Turns currentTurn;
    private Moves moves;
    private int score;
    private transient WindowFrame windowFrame;
    private PrivateObjectiveCard privateObjectiveCard;

    //
    //CONSTRUCTOR
    //
    public Player(String username, String password) {
        this.username = username;
        numTokens = 0;
        currentTurn = Turns.ZERO;
        moves = Moves.NONE;
        score = 0;
        windowFrame = null;
        this.password = password;

    }

    //
    //METHODS
    //

    /**
     * Inizializza la windowFrame del giocatore con il pattern scelto
     * e assegna il numero di segnalini in base alla difficolta' dello schema scelto
     * @param p pattern da assegnare
     * @return true
     */
    public boolean patternChoosen(Pattern p){
        this.windowFrame = new WindowFrame(p);
        windowFrame.addObserver(this);
        this.numTokens = p.getDifficult();
        return true;
    }

    /**
     * Resetta il turno del giocatore e azzera le mosse effettuate
     * @see Server notifica all' Observer (server) l'avvenuta modifica
     */
    public void resetTurn() {
        this.moves = Moves.NONE;
        this.currentTurn = Turns.ZERO;
        setChanged();
        notifyObservers(this);
    }

    //FIXME
    /**
     * Notifica l'avvenuta modifica
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);

    }

    /**
     * Confronto tra due player in base al nome (due player sono uguali se hanno lo stesso username)
     * @param o oggetto da confrontare con il player (confrontabile solo se o di tipo Player)
     * @return :
     * - true se i due player sono uguali (hanno lo stesso username)
     * - false se o == null oppure se o non e' un oggetto di tipo player
     */
    @Override
    public boolean equals(Object o) {
        if(o == null || o.getClass() != this.getClass())
            return false;
        Player p = (Player) o;
        return this.getUsername().equals(p.getUsername());
    }

    @Override
    public int hashCode(){
        return Objects.hash(username);
    }

    //
    //GET AND SET METHODS
    //
    public PrivateObjectiveCard getPrivateObjectiveCard() {
        return privateObjectiveCard;
    }
    public void setPrivateObjectiveCard(PrivateObjectiveCard privateObjectiveCard) {
        this.privateObjectiveCard = privateObjectiveCard;
        setChanged();
        notifyObservers(this);
    }

    public WindowFrame getWindowFrame() {
        return windowFrame;
    }

    public void setWindowFrame(WindowFrame windowFrame) {
        this.windowFrame = windowFrame;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumTokens() {
        return numTokens;
    }
    public void setNumTokens(int numTokens) {
        this.numTokens = numTokens;
        setChanged();
        notifyObservers(this);
    }

    public Turns getCurrentTurn() {
        return currentTurn;
    }
    public void setCurrentTurn(Turns currentTurn) {
        this.currentTurn = currentTurn;
        setChanged();
        notifyObservers(this);
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;

        setChanged();
        notifyObservers(this);

    }

    public Moves getMoves() {
        return moves;
    }
    public void setMoves(Moves moves) {
        this.moves = moves;
        setChanged();
        notifyObservers(this.moves);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




}
