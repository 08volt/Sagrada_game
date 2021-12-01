package progettoIngSW.Network.Client;

import progettoIngSW.CommandInterface;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;
import progettoIngSW.Network.Server.ServerInterface;
import progettoIngSW.ViewInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * classe di comunicazione lato client con il server tramite una connessione di tipo RMI.
 * implementa ClientInterface dove tutti i metodi riportano semplicemente la chiamata all'oggetto Client.
 * implementa ClientToServer per invocare sul server le funzionalità di base: login e caricamento di un nuovo pattern.
 * implementa CommandInterface per inviare al server i comandi dell'utente
 */

public class ClientRMI extends UnicastRemoteObject implements ClientInterface,ClientToServer, CommandInterface {

    private ViewInterface viewInterface;
    private ServerInterface server;
    private Client client;


    protected ClientRMI(ViewInterface view,Client client, String address) throws RemoteException, NotBoundException {

        this.client = client;
        this.viewInterface = view;
        Registry registry = LocateRegistry.getRegistry(address);
        this.server = (ServerInterface)registry.lookup("server");

    }

    @Override
    public void login() throws FullLobbyException, RemoteException, NameNotAvailableException, WrongPasswordException {
        server.login(this);
        client.setLogged(true);
    }

    @Override
    public void startTurn() throws RemoteException {
        client.startTurn();
    }

    @Override
    public String getPassword() throws RemoteException {
        return client.getPassword();
    }

    @Override
    public String getUsername() {
        return client.getName();
    }

    @Override
    public int askDraftPos() throws RemoteException, EndTimerException, MoveStoppedException {
        return viewInterface.askDraftPos();
    }

    @Override
    public boolean askIncrease() throws RemoteException, EndTimerException, MoveStoppedException {
        return viewInterface.askIncrease();
    }

    @Override
    public int askWindowPos(boolean placement) throws RemoteException, EndTimerException, MoveStoppedException {
        return viewInterface.askWindowPos(placement);
    }

    @Override
    public int[] askRoundTrackPos() throws RemoteException, EndTimerException, MoveStoppedException {
        return viewInterface.askRoundTrackPos();
    }

    @Override
    public int askNumber(Colors c) throws RemoteException, EndTimerException, MoveStoppedException {
        return viewInterface.askNumber(c);
    }

    @Override
    public boolean askHowMany() throws RemoteException, EndTimerException, MoveStoppedException {
        return viewInterface.askHowMany();
    }

    @Override
    public void printDice(Dice d) throws RemoteException {
       viewInterface.printDice(d);
    }

    @Override
    public int askWhichPattern(Pattern[] patterns) throws RemoteException {
        return viewInterface.askWhichPattern(patterns);
    }

    @Override
    public void updateCurrentPlayer(int currentPlayer) throws RemoteException {
        viewInterface.updateCurrentPlayer(currentPlayer);
    }

    @Override
    public void updateTokens(ArrayList<Integer> tokens) throws RemoteException {
        viewInterface.updateTokens(tokens);
    }

    @Override
    public void updateGameTurn(Turns turn) throws RemoteException {
        viewInterface.updateGameTurn(turn);
    }


    @Override
    public void updatePlayerList(Player player, int pos) throws RemoteException {
        viewInterface.updatePlayerList(player, pos);
    }

    @Override
    public void updateWindowsFrame(WindowFrame wf) throws RemoteException {
        viewInterface.updateWindowsFrame(wf);
    }

    @Override
    public void updateAllWf(WindowFrame wf, int pos) throws RemoteException {
        viewInterface.updateWf(wf,pos);
    }

    @Override
    public void sendDisconnectionOf(String username) {
        viewInterface.alertDisconnectionOf(username);
    }

    @Override
    public void sendConnectionOf(String username) throws RemoteException {
        if(!username.equals(getName()))
            viewInterface.alertConnectionOf(username);
    }


    @Override
    public void ping() throws RemoteException {
    }

    @Override
    public void updateDraftPool(ArrayList<Dice> draft) throws RemoteException {
        viewInterface.updateDraftPool(draft);
    }

    @Override
    public void updateRoundTrack(HashMap<Integer, ArrayList<Dice>> track) throws RemoteException {
        viewInterface.updateRoundTrack(track);
    }

    @Override
    public void endGame(ArrayList<Integer> ranking) throws RemoteException {
        viewInterface.endGame(ranking);
    }

    @Override
    public void endTimer() throws RemoteException {
        viewInterface.endTimer();
    }


    @Override
    public void updatePublicObj(ArrayList<Integer> poc) {
        viewInterface.updatePublicObjectiveCards(poc);
    }

    @Override
    public void updateCurrentRound(int currentRound) {
        viewInterface.updateCurrentRound(currentRound);
    }

    @Override
    public void updateTools(ArrayList<Integer> toolCards) {
        viewInterface.updateTools(toolCards);
    }


    @Override
    public void endTurn() throws RemoteException {
        client.endTurn();
        server.endTurn(this);
    }

    @Override
    public String getName() {
        return client.getName();
    }

    @Override
    public void uploadPattern(Pattern pattern) throws RemoteException {
        server.uploadPattern(pattern);
    }

    @Override
    public void placeDice(int posDraft, int posWindow) throws RemoteException, CellNotEmptyException, NotPlayingException, DiceNotFoundException, RulesBreakException {
        server.placeDice(posDraft,posWindow, this);
    }

    @Override
    public void useToolCard(int toolNumber) throws RemoteException, DiceNotFoundException, CellNotEmptyException, RulesBreakException, InvalidParamsException, NotPlayingException, NotValidCellException, DraftFullException {
        try {
            server.useToolCard(toolNumber, this);
        } catch (EndTimerException ignored) {

        }
    }
}
