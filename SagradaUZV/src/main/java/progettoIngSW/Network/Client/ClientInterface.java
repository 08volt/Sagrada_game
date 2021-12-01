package progettoIngSW.Network.Client;

import progettoIngSW.Exceptions.EndTimerException;
import progettoIngSW.Exceptions.MoveStoppedException;
import progettoIngSW.Model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Interfaccia del ClientRMI per l'invocazione remota dei metodi tramite RMI
 */

public interface ClientInterface extends Remote
{

    String getUsername() throws RemoteException;

    int askDraftPos() throws RemoteException, EndTimerException, MoveStoppedException;

    boolean askIncrease() throws RemoteException, EndTimerException, MoveStoppedException;

    int askWindowPos(boolean placement) throws RemoteException, EndTimerException, MoveStoppedException;

    int[] askRoundTrackPos() throws RemoteException, EndTimerException, MoveStoppedException;

    int askNumber(Colors c) throws RemoteException, EndTimerException, MoveStoppedException;

    boolean askHowMany() throws RemoteException, EndTimerException, MoveStoppedException;

    void startTurn() throws RemoteException;

    void printDice(Dice d) throws RemoteException;

    int askWhichPattern(Pattern[] patterns) throws RemoteException;

    void updateCurrentPlayer(int currentPlayer)throws RemoteException;

    void updateTokens(ArrayList<Integer> tokens)throws RemoteException;

    void updateGameTurn(Turns turn)throws RemoteException;

    void updatePlayerList(Player player, int pos)throws RemoteException;

    void updateWindowsFrame(WindowFrame cells)throws RemoteException;

    void updateDraftPool(ArrayList<Dice> draft)throws RemoteException;

    void updateRoundTrack(HashMap<Integer,ArrayList<Dice>> track)throws RemoteException;

    void endGame(ArrayList<Integer> ranking) throws RemoteException;

    void endTimer() throws RemoteException;

    String getPassword() throws RemoteException;

    void updatePublicObj(ArrayList<Integer> poc)throws RemoteException;

    void updateCurrentRound(int currentRound)throws RemoteException;

    void updateTools(ArrayList<Integer> toolCards)throws RemoteException;

    void updateAllWf(WindowFrame wf, int pos) throws RemoteException;

    void sendDisconnectionOf(String username)throws RemoteException;

    void sendConnectionOf(String username)throws RemoteException;

    void ping()throws RemoteException;
}
