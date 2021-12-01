package progettoIngSW.Network.Server;

import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.Pattern;
import progettoIngSW.Network.Client.ClientInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 *  Interfaccia del ServerRMI per l'invocazione remota dei metodi tramite RMI
 */

public interface ServerInterface extends Remote {

    void login(ClientInterface c) throws RemoteException, FullLobbyException, NameNotAvailableException, WrongPasswordException;

    void placeDice(int posDraft, int i, ClientInterface c) throws RemoteException, CellNotEmptyException, NotPlayingException, DiceNotFoundException, RulesBreakException;

    void useToolCard(int toolPos, ClientInterface c) throws RemoteException, DiceNotFoundException, RulesBreakException, CellNotEmptyException, NotPlayingException, InvalidParamsException, NotValidCellException, DraftFullException, EndTimerException;

    void endTurn (ClientInterface c) throws RemoteException;

    void uploadPattern(Pattern pattern) throws RemoteException;
}
