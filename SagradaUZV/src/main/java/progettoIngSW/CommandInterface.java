package progettoIngSW;

import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.Moves;
import progettoIngSW.Model.PrivateObjectiveCard;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface CommandInterface {

    /**
     *
     * comando che l'utente usa per chiedere al server di piazzare un dado
     *
     * @param posDraft posizione nel draft del dado da piazzare
     * @param posWindow posizione nella windowsframe dove l'utente desidera piazzare il dado
     * @throws DiceNotFoundException se la posizione nel draft non è valida
     * @throws RulesBreakException se il piazzamento del dado non rispetta regole di gioco
     * @throws CellNotEmptyException se la cella dove si vuola piazzare il dado risulta gia occupata
     * @throws NotPlayingException se non è il turno del giocatore che ha fatto la richiesta
     * @throws IOException se vi sono problemi di connessione
     */
    void placeDice(int posDraft, int posWindow) throws DiceNotFoundException, RulesBreakException, CellNotEmptyException, NotPlayingException, IOException;

    /**
     *
     * comando che l'utente usa per chiedere al server di usare una tool card
     *
     * @param toolPos posizione della toolcard che si vuole usare nell'array delle toolcard di game
     * @throws IOException se vi sono problemi di connessione
     * @throws DiceNotFoundException se la posizione nel draft non è valida
     * @throws CellNotEmptyException se la cella dove si vuola piazzare il dado risulta gia occupata
     * @throws RulesBreakException se il piazzamento del dado non rispetta regole di gioco
     * @throws InvalidParamsException se i parametri con i quali si vuole usare la tool non sono validi
     * @throws NotPlayingException se non è il turno del giocatore che ha fatto la richiesta
     * @throws NotValidCellException se la cella indicata non è utilizzabile per l'obbiettivo della toolcard
     * @throws DraftFullException se il draft è pieno e non è possibile aggiungere dadi
     */
    void useToolCard(int toolPos) throws IOException, DiceNotFoundException, CellNotEmptyException, RulesBreakException, InvalidParamsException, NotPlayingException, NotValidCellException, DraftFullException;

    /**
     *
     * comando che l'utente usa per chiedere al server di terminare il turno
     *
     * @throws IOException se vi sono problemi di connessione
     */
    void endTurn() throws IOException;

    /**
     * @return il nome con cui l'utente si è registrato
     */
    String getName();

}
