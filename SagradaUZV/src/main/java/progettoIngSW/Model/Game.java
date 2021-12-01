package progettoIngSW.Model;

import progettoIngSW.Exceptions.*;
import progettoIngSW.GameInterface;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Game extends Observable implements GameInterface, Serializable, Observer {

    //
    //ATTRIBUTES
    //
    private RoundTrack track;
    private ArrayList<Player> players;
    private static transient ArrayList<Pattern> patterns;
    private DraftPool draft;
    private Turns currentTurn;
    private int currentRound;
    private int currentPlayer; //posizione nella lista players
    private ArrayList<Integer> publicObjectiveCards;
    private ArrayList<Integer> toolCards;
    private ArrayList<Integer> toolTokens;
    private boolean gameStarted;
    private static transient ArrayList<Integer> ranking = new ArrayList<>();


    //
    //CONSTANT
    //
    final int maxRound = 10;
    final int maxNumCard = 3;

    //
    //CONSTRUCTOR
    //
    private static Game g = null;

    /**
     * Loads the 24 standard patterns from file + other custom putterns
     */
    private Game() {


        gameStarted = false;
        players = new ArrayList<>();
        toolTokens = new ArrayList<>();
        publicObjectiveCards = new ArrayList<>();
        toolCards = new ArrayList<>();

        draft = new DraftPool();
        track = new RoundTrack();
        patterns = new ArrayList<>();
        final int MAX = 24;

        try{
            int i = 0;
            while(i<MAX){

                this.patterns.add(new Pattern(i++));
            }

            final File folder = new File("CustomPatterns");
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isFile()) {
                    patterns.add(new Pattern(fileEntry.getName()));
                }
            }

        }catch (NullPointerException | IOException ignored){

        }

    }

    public static Game getGame(){
        if(g == null)
            g = new Game();

        return g;
    }

    /**
     * Reset the game removing the pointer
     */
    public static void resetGame(){
        g = null;
    }

    //FIXME SISTEMARE TUTTI I NOMI DEI METODI DI TUTTO IL CODICE AFFINCHE' SIA SUBITO EVIDENTE COSA FANNO
    //
    //METHODS
    //

    /**
     * Game setup:
     * - set current round  = 1
     * - Set current turn = 1
     * - Set number of player and generate the draft accordingly
     * - Adds 3 random toolcards from the 12 on the ArrayList
     * - Set tokens on the 3 toolcards = 0
     * - Adds 3 random public cards from the 10 on the ArrayList publicObjectiveCards
     * - set the index of the current player
     * @see Server notify the server that the match has been updated
     */
    public void gameSetup(){


        currentRound = 1;
        currentTurn = Turns.FIRST;

        Random r = new Random();

        draft.setNumPlayer(players.size());

        draft.generateDraft();




        //3 TOOL CARDS
        while(toolCards.size()<maxNumCard)
        {
            int t = (r.nextInt(22271) % 12) + 1;
            while( toolCards.contains(t)){
                t = (r.nextInt(22271) % 12) + 1;
            }
            toolCards.add(t);
            toolTokens.add(0);
        }


        //3 OBJECTIVE CARD
        while(publicObjectiveCards.size()<maxNumCard)
        {
            int t = (r.nextInt(22271)%10)+1;
            while( publicObjectiveCards.contains(t)){
                t = (r.nextInt(22271)%10)+1;
            }
            publicObjectiveCards.add(t);
        }

        currentPlayer = 0;


        setChanged();
        notifyObservers(this);

    }

    /**
     * Adds the private Cards to the ArrayList all'Array list
     * It Shuffle them and it assign a random one to each player
     */
    public void extractPrivateCards(){
        ArrayList<PrivateObjectiveCard> privateCards = new ArrayList<>();
        for (Colors c:Colors.values()) {
            if(c != Colors.WHITE)
                privateCards.add(new PrivateObjectiveCard(c));
        }
        Collections.shuffle(privateCards);
        for(int p = 0; p<players.size(); p++)
        {
            //CARTE PRIVATE
            players.get(p).setPrivateObjectiveCard((privateCards.get(p)));

        }

    }

    /**
     * Shuffle the players
     */
    public void shufflePlayers(){
        Collections.shuffle(players);
    }

    /**
     * Removes 4 patterns from the ArrayList of patterns
     * @return the 4 patterns removed
     */
    public Pattern[] extractPattern(){
        Random r = new Random();
        Pattern[] p = new Pattern[4];

        for(int i = 0; i<4; i++) {
            int randomPattern = r.nextInt(22271) % patterns.size();
            p[i] = patterns.remove(randomPattern);
        }

        return p;
    }

    /**
     * Metodo utilizzato per effettuare il cambio del round.
     * Ad ogni cambio del round tutti i dadi del draft vengono trasferiti nell'ArryList relativo al tracciato dei Round.
     * Ad ogni cambio del round viene impostato il nuovo player corrente.
     * Quando currentRound = 10 e viene chiamato changeRound, gameStarted viene settato a false,
     * vengono calcolati e settati i punteggi dei vari giocatori e decretato il vincitore
     * @return false se e' finita la partita (changeRound richiamto al round 10), true altrimenti
     * @see Server notifica all'observer (server) l'avvenuta modifica dello stato del game
     * @see Scoring classe statica utilizzata per il calcolo punteggio
     */
    public boolean changeRound(){
        boolean gameEnd;

        for (Dice dice : draft.getDraft()){
            track.addDice(currentRound,dice);
        }
        currentRound++;

        if(currentRound <= maxRound) {

            draft.generateDraft();

            this.currentTurn = Turns.FIRST;

            for (Player p : players) {
                p.resetTurn();
            }

            //impostazione nuovo current player
            this.currentPlayer = (currentRound - 1) % players.size();


            gameEnd = false;
        }
        else
        {
            setGameStarted(false);
            for (Player p: players) {
                p.setScore(Scoring.calc(p));
            }
            decreeWinner();
            gameEnd = true;            //FINISCI PARTITA E CALCOLA PUNTEGGI
        }

        setChanged();
        notifyObservers(this);


        return !gameEnd;
    }

    /**
     * Metodo utilizzato per effettuare il cambio del turno all'interno dello stesso round,
     * impostando il player corrente
     * @return :
     *          - true se il cambio turno viene eseguito correttamete
     *          - changeRound() se il metodo viene chiamato alla fine del secondo giro
     *          - changeTurn() se e' stata utilizzata una carta che altera la sequenza di gioco
     * alla fine del secondo giro
     */
    public boolean changeTurn(){

        int playerStart = (currentRound - 1) % players.size();

        if(currentTurn == Turns.FIRST){//SE SIAMO AL PRIMO GIRO
            if(this.getPlayers().get(currentPlayer).getCurrentTurn() == Turns.ZERO){
                this.getPlayers().get(currentPlayer).setCurrentTurn(Turns.FIRST);
                this.getPlayers().get(currentPlayer).setMoves(Moves.NONE);
            }

            currentPlayer++;
            if(currentPlayer == players.size())
                currentPlayer = 0;

            if(currentPlayer == playerStart)  //SE IL PRIMO GIRO È FINITO
            {
                currentPlayer--;                   //INIZIA AL CONTRARIO
                if(currentPlayer < 0) currentPlayer = players.size() - 1;

                currentTurn = Turns.SECOND;

                if(players.get(currentPlayer).getCurrentTurn() == Turns.SECOND) //SE IL PLAYER HA GIA FATTO IL SECONDO TURNO (AD ESEMPIO TRAMITE TOOL CARD)
                {
                    return changeTurn(); //CAMBIO ANCORA TURNO
                }
            }

        }else if(currentTurn == Turns.SECOND) {    //SE SIAMO AL SECONDO GIRO
            players.get(currentPlayer).setCurrentTurn(Turns.SECOND);
            players.get(currentPlayer).setMoves(Moves.NONE);

            if (currentPlayer == playerStart)  //SE IL SECONDO GIRO È FINITO
            {
                return changeRound();  //CAMBIA ROUND
            }

            currentPlayer--;
            if(currentPlayer < 0) currentPlayer = players.size() - 1;

            if(players.get(currentPlayer).getCurrentTurn() == Turns.SECOND) //SE IL PLAYER HA GIA FATTO IL SECONDO TURNO (AD ESEMPIO TRAMITE TOOL CARD)
            {
                return changeTurn(); //CAMBIO ANCORA TURNO
            }

        }
        setChanged();
        notifyObservers(currentPlayer);
        setChanged();
        notifyObservers(currentTurn);
        return true;
    }


    /**
     * Aggiunge un nuovo player al Game, se il numero di giocatori e' minore del numero massimo (max 4 giocatori)
     * @param player nuovo giocatore da aggiungere
     * @throws FullLobbyException se si tenta di aggiungere un nuovo giocatore quando
     * la lista dei player e' al completo (aggiunti gia' 4 player)
     */
    public void addPlayer(Player player) throws FullLobbyException {

        if(players.size()<4) {
            this.players.add(player);
            player.addObserver(this);
        }else
        {
            throw new FullLobbyException();
        }
    }

    /**
     * Verifica che il numero di tokens del player sia sufficiente per utilizzare la tool scelta
     * @see ToolCard viene richiamato useToolCard() se la condizione del numero di tokens e' rispettata
     * @param t toolCard da utilizzare
     * @param player giocatore che vuole utilizzare la tool
     * @param posToolTokens posizione all'interno dell'array dei Tokens relativa alla tool che si vuole utilizzare
     * @see ToolCard use toolCard per le singole eccezioni e le possibili casistiche
     * @throws NotValidCellException
     * @throws RulesBreakException
     * @throws CellNotEmptyException
     * @throws DiceNotFoundException
     * @throws InvalidParamsException
     * @throws DraftFullException
     */
    public void useToolCard(ToolCard t, Player player, int posToolTokens) throws NotValidCellException, RulesBreakException, CellNotEmptyException, DiceNotFoundException, InvalidParamsException, DraftFullException {
        
        if((toolTokens.get(posToolTokens) == 0 && player.getNumTokens() >= 1)||(toolTokens.get(posToolTokens) > 0 && player.getNumTokens() > 1)) {
                t.useToolCard();
        }
        else {
            throw new RulesBreakException(RulesBreakException.CODE.TOKEN);
        }

    }

    /**
     * Rimuove un dado dalla draft e lo piazza all'interno della windowFrame del player
     * @param player giocatore a cui appartiene la windowFrame in cui si vuole piazzare il dado
     * @param posDraft posizione del dado scelto all'interno del draft
     * @param wfPos posizione della cella nella windowFrame in cui si vuole piazzare il dado
     * @throws CellNotEmptyException se nella cella scelta (wfPos) e' gia' presente un dado
     * @throws RulesBreakException se non viene rispettata almeno una tra le restrizioni per il posizionamento in wfPos
     * @throws DiceNotFoundException se il draft non contiene alcun dado nalla posizione scelta (posDraft)
     */
    public void placeDice(Player player, int posDraft, int wfPos) throws CellNotEmptyException, RulesBreakException, DiceNotFoundException {
        Dice dice = g.getDraft().getDraft().get(posDraft);
        player.getWindowFrame().placeDice(dice,wfPos);
        g.getDraft().removeDice(posDraft);
    }

    //FIXME JAVADOC
    /**
     * Notifica al Observer (Server) l'avvenuta modifica di una classe della quale Game e' l'Observer
     * @param o classe relativa all'oggetto che notifica l'update al game
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    /**
     * Classe che implementa Comparator ed esegue un compare su un copia dell'ArrayList di players (players.clone)
     * La nuova lista dei player viene ordinata in base alla classifica, ovvero in posizione 0 il vincitore,
     * mentre in coda alla lista l'ultimo classificato, secondo le seguenti restrizioni:
     *
     * - Il giocatore con il punteggio piu' alto e' il vincitore,
     * - in caso di parita vince il giocatore col maggior numero di punti
     *   dati dall’Obiettivo Personale;
     * - se c’è ancora parita' vince chi ha più Segnalini Favore;
     * - se ancora non c’è un vincitore, vince il giocatore che
     *   occupa la posizione più bassa dell’ordine dell’ultimo round
     */
    private class ComparePlayer implements Comparator<Integer> {
        ArrayList<Player> players;

        public ComparePlayer(ArrayList<Player> players){
            this.players = (ArrayList<Player>) players.clone();
        }

        /**
         * Metodo che esegue il confronto tra due player
         * @param posPlayer2 posizione player2 all'interno dell'arrayList players
         * @param posPlayer1 posizione player1 all'interno dell'arrayList players
         * @return 1 se player2 deve occupare una posizione piu' alta in classifica, -1 altrimenti
         */
        @Override
        public int compare(Integer posPlayer2, Integer posPlayer1) {
            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending

            int ret = 0;
            ret = (players.get(posPlayer1).getScore() > players.get(posPlayer2).getScore() ? 1 : (players.get(posPlayer1).getScore() < players.get(posPlayer2).getScore()) ? -1 : 0);
            if(ret==0)
                ret = (Scoring.privateObjectiveScore(players.get(posPlayer1)) > Scoring.privateObjectiveScore(players.get(posPlayer2)) ? 1
                        : (Scoring.privateObjectiveScore(players.get(posPlayer1)) < Scoring.privateObjectiveScore(players.get(posPlayer2))) ? -1 : 0);
            if(ret==0)
                ret = (players.get(posPlayer1).getNumTokens() > players.get(posPlayer2).getNumTokens() ? 1
                        : players.get(posPlayer1).getNumTokens() < players.get(posPlayer2).getNumTokens() ? -1 : 0);
            if(ret == 0){
                int i1 = 0, i2= 0;
                for (int i = 0; i<ranking.size();i++) {
                    if(ranking.get(i).equals(posPlayer1))
                    {
                        i1 = i - 9%ranking.size() < 0 ? i - 9 % ranking.size() : i-9%ranking.size()+ranking.size();
                    }else if(ranking.get(i).equals(posPlayer2))
                    {
                        i2 = i - 9%ranking.size() < 0 ? i - 9 % ranking.size() : i-9%ranking.size()+ranking.size();
                    }
                }
                ret = (i1 > i2) ? 1 : (i1 < i2) ? -1 : 0;
            }
            return  ret;
        }
    }

    /**
     * Esegue l'ordinamento dell'arrayList ranking (copia di players)
     * secondo le regole descritte nella classe ComparePlayer
     * @see ComparePlayer
     */
    private void decreeWinner(){

        for (int i = 0; i< players.size(); i++)
            ranking.add(i);
        Collections.sort(ranking, new ComparePlayer(players));
    }


    /**
     * Aggiorna il numero di segnalini e le mosse di un player che utilizza una toolCard
     * Aggiorna inoltre il numero di toolTokens relativi ad una toolCard
     * @param p player che ha utilizzato la toolCard
     * @param toolPos posizione della tool utilizzata
     * @throws RulesBreakException se il numero di segnalini necessari ad utilizzare la tool scelta non e' sufficiente
     */
    public void setToolUSED(Player p, int toolPos) throws RulesBreakException {

        if(toolTokens.get(toolPos) == 0 && p.getNumTokens() >= 1) {

                p.setNumTokens(p.getNumTokens() -1);
                toolTokens.set(toolPos,1);
                setChanged();
                notifyObservers(toolTokens);

        }else if(toolTokens.get(toolPos) > 0 && p.getNumTokens() > 1) {


                p.setNumTokens(p.getNumTokens() -2);
                toolTokens.set(toolPos,toolTokens.get(toolPos) + 2);
                setChanged();
                notifyObservers(toolTokens);

        }else {
            throw new RulesBreakException(RulesBreakException.CODE.TOKEN);
        }

        if(p.getMoves() == Moves.NONE)
            p.setMoves(Moves.TOOLUSED);
        else if(p.getMoves() == Moves.DICEPLACED)
            p.setMoves(Moves.BOTH);
    }

    /**
     * Aggiorna le mosse di un giocatore che piazza un dado
     * @param p giocatore che ha piazzato il dado
     */
    public void setDicePLACED(Player p) {
        if(p.getMoves() == Moves.NONE)
            p.setMoves(Moves.DICEPLACED);
        else if(p.getMoves() == Moves.TOOLUSED)
            p.setMoves(Moves.BOTH);
    }

    //
    //GET AND SET METHODS
    //
    public ArrayList<Integer> getToolCards() {
        return toolCards;
    }
    public void setToolCards(ArrayList<Integer> toolCards) {
        this.toolCards = toolCards;
    }

    public RoundTrack getTrack() {
        return track;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public DraftPool getDraft() {
        return draft;
    }
    public void setDraft(DraftPool draft) {
        this.draft = draft;
    }

    public Turns getCurrentTurn() {
        return currentTurn;
    }
    public void setCurrentTurn(Turns currentTurn){
        this.currentTurn = currentTurn;
    }

    public int getCurrentRound() {
        return currentRound;
    }
    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public ArrayList<Integer> getPublicObjectiveCards() {
        return publicObjectiveCards;
    }
    public void setPublicObjectiveCards(ArrayList<Integer> publicObjectiveCards) {
        this.publicObjectiveCards = publicObjectiveCards;
    }

    public synchronized boolean isGameStarted() {
        return gameStarted;
    }
    public synchronized void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
        setChanged();
        notifyObservers(gameStarted);
    }

    public ArrayList<Integer> getToolTokens() {
        return toolTokens;
    }
    public void setToolTokens(ArrayList<Integer> toolTokens) {
        this.toolTokens = toolTokens;
    }

    public static ArrayList<Pattern> getPatterns() {
        return patterns;
    }


    public ArrayList<Integer> getRanking() {
        return ranking;
    }

    public void setRanking(ArrayList<Integer> ranking) {
        this.ranking = ranking;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }



}
