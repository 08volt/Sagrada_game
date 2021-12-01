package progettoIngSW;

import progettoIngSW.Exceptions.EndTimerException;
import progettoIngSW.Exceptions.MoveStoppedException;
import progettoIngSW.Model.*;
import progettoIngSW.Network.Client.ConnectionType;

import java.util.ArrayList;
import java.util.HashMap;

public interface ViewInterface {

    int askDraftPos() throws  EndTimerException, MoveStoppedException; //CHIEDE UNA POSIZIONE NEL DRAFT

    boolean askIncrease() throws  EndTimerException, MoveStoppedException; //CHIEDE SE VUOLE AUMENTARE(TRUE) O DIMINUIRE

    //utente inserisce x e y
    int askWindowPos(boolean placement) throws  EndTimerException, MoveStoppedException; //CHIEDE UNA POSIZIONE NELLA WINDOWSFRAME

    int[] askRoundTrackPos() throws   EndTimerException, MoveStoppedException; //CHIEDE POSIZIONE SUL ROUND TRACK

    int askNumber(Colors c) throws  EndTimerException, MoveStoppedException; //CHIEDE UN NUMERO DA 1 A 6 PER IL DADO D

    boolean askHowMany() throws  EndTimerException, MoveStoppedException; //chiede se vuole spostare 1 o 2 dadi

    void startTurn() throws   EndTimerException; //INIZIA IL TUO TURNO, GIOCA

    int askWhichPattern(Pattern[] patterns);

    void printEndTurn(); //COSE DA FARE A FINE DEL TURNO

    void endGame(ArrayList<Integer> ranking);

    ConnectionType askConnectionType();

    String askServerAddress();

    void setLogged(boolean b);

    String askName();  //chiede username

    void printDice(Dice d);

    void updateTools(ArrayList<Integer> tools);

    void updatePublicObjectiveCards(ArrayList<Integer> cards);
    
    void updateCurrentPlayer(int currentPlayer);

    void updateTokens(ArrayList<Integer> tokens);

    void updateGameTurn(Turns turn);

    void updateWindowsFrame(WindowFrame wf);

    void updateDraftPool(ArrayList<Dice> draft);

    void updateRoundTrack(HashMap<Integer,ArrayList<Dice>> track);

    void endTimer();

    void updateCurrentRound(int r);

    String askPassword();

    void updatePlayerList(Player player, int pos);

    void updateWf(WindowFrame wf, int pos);

    void alertDisconnectionOf(String name);

    void alertConnectionOf(String name);

    void fullLobby();

    void connectionError();

    void nameNotAvailable();

    void wrongPassword();

    boolean askToPlay();

    Pattern writeNewPattern();

    void setPlayer(CommandInterface commandInterface);
}
