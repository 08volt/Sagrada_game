package progettoIngSW.Network.Server;

import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;
import progettoIngSW.Network.Client.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe che si occupa della comunicazione con i client che usano RMI come tipo di connessione
 * Invia a tutti i client connessi gli update e a solo quello con uno specifico username le richieste individuali.
 */
public class ServerRMI extends UnicastRemoteObject implements ServerInterface{


    private Server server;
    private ConcurrentHashMap<String,ClientInterface> clientsMap;


    public ServerRMI(Server s) throws RemoteException {
        server = s;
        clientsMap = new ConcurrentHashMap<>();
        ServerRmiPingThread sr = new ServerRmiPingThread(this,s);

        sr.start();
    }

    public boolean askIncrease(String user) throws RemoteException, UserNotFoundException, EndTimerException, MoveStoppedException {

        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        return c.askIncrease();

    }

    public int askWindowPos(String user, boolean placement) throws RemoteException, UserNotFoundException, EndTimerException, MoveStoppedException {

        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        return c.askWindowPos(placement);
    }

    public int[] askRoundTrackPos(String user) throws RemoteException, UserNotFoundException, EndTimerException, MoveStoppedException {

        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        return c.askRoundTrackPos();
    }

    public int askNumber(Colors col, String user) throws RemoteException, UserNotFoundException, EndTimerException, MoveStoppedException {
        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        return c.askNumber(col);
    }

    public boolean askHowMany(String user) throws RemoteException, UserNotFoundException, EndTimerException, MoveStoppedException {
        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        return c.askHowMany();
    }


    public int askWhichPattern(Pattern[] patterns, String user) throws RemoteException, UserNotFoundException {

        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        return c.askWhichPattern(patterns);



    }

    public int askDraftPos(String user) throws RemoteException, UserNotFoundException, EndTimerException, MoveStoppedException {
        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        return c.askDraftPos();
    }

    public void startTurn(String user) throws RemoteException, UserNotFoundException {

        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        c.startTurn();

    }


    public void endGame(String user,ArrayList<Integer> ranking) throws RemoteException, UserNotFoundException {

        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        c.endGame(ranking);
    }

    @Override
    public void login(ClientInterface c) throws RemoteException, FullLobbyException, NameNotAvailableException, WrongPasswordException {


        boolean wpassword = server.login(c.getUsername(), c.getPassword());

        this.clientsMap.put(c.getUsername(),c);

        if(wpassword)
            server.askForUpdate();

    }

    @Override
    public void placeDice(int posDraft, int i, ClientInterface c) throws RemoteException, CellNotEmptyException, NotPlayingException, DiceNotFoundException, RulesBreakException {

        if(clientsMap.containsValue(c)){
            server.placeDice(posDraft,i,c.getUsername());
        }
    }

    @Override
    public void useToolCard(int toolNumber, ClientInterface c) throws RemoteException, DiceNotFoundException, RulesBreakException, CellNotEmptyException, NotPlayingException, InvalidParamsException, NotValidCellException, DraftFullException, EndTimerException {
        if(clientsMap.containsValue(c)){
            server.useToolCard(toolNumber,c.getUsername());
        }

    }

    @Override
    public void endTurn(ClientInterface c) throws RemoteException {
        if(clientsMap.containsValue(c)){
            server.endTurn(c.getUsername());
        }
    }

    @Override
    public void uploadPattern(Pattern pattern) {
        server.uploadPattern(pattern);

    }



    public void updateGame(Game game) {
        updateTools(game.getToolCards());
        updateRoundTrack(game.getTrack().getTrack());
        updateCurrentRound(game.getCurrentRound());
        updateGameTurn(game.getCurrentTurn());
        updatePublicObj(game.getPublicObjectiveCards());
        updatePlayerList(game.getPlayers());
        updateTokens( game.getToolTokens());
        updateDraftPool(game.getDraft().getDraft());
        updateCurrentPlayer(game.getCurrentPlayer());


    }

    public void updatePlayerList(ArrayList<Player> players) {
        for (ClientInterface c:clientsMap.values()) {
            for(int i = 0; i<players.size();i++) {
                try {
                    c.updatePlayerList(players.get(i), i);
                    c.updateAllWf(players.get(i).getWindowFrame(), i);
                }catch (RemoteException e) {
                    server.clientDisconnection(usernameFromHashmap(c));
                }
            }

        }
    }

    private void updatePublicObj(ArrayList<Integer> poc){
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updatePublicObj(poc);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }
        }
    }

    public String usernameFromHashmap(ClientInterface c){
        Iterator<Map.Entry<String, ClientInterface>> i = clientsMap.entrySet().iterator();

        while(i.hasNext()){
            Map.Entry<String, ClientInterface> m = i.next();
            if(m.getValue().equals(c))
                return m.getKey();

        }
        return null;

    }


    private void updateCurrentRound(int currentRound)  {
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updateCurrentRound(currentRound);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }
        }
    }

    private void updateTools(ArrayList<Integer> toolCards)  {
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updateTools(toolCards);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }
        }
    }

    public void updateCurrentPlayer(int currentPlayer) {
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updateCurrentPlayer( currentPlayer);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }
        }
    }


    public void updateTokens(ArrayList<Integer> tokens)  {
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updateTokens(tokens);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }

        }
    }


    public void updateGameTurn(Turns turn) {
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updateGameTurn(turn);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }

        }
    }



    public void updatePlayerState(Player player, int i) {
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updatePlayerList(player,i);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }

        }
    }


    public void updateWindowsFrame(WindowFrame wf) {
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updateWindowsFrame(wf);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }

        }
    }


    public void updateDraftPool(ArrayList<Dice> draft) {
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updateDraftPool(draft);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }

        }
    }

    public void updateRoundTrack(HashMap<Integer,ArrayList<Dice>> track) {
        for (ClientInterface c:clientsMap.values()) {
            try {
                c.updateRoundTrack(track);
            } catch (RemoteException e) {
                server.clientDisconnection(usernameFromHashmap(c));
            }

        }
    }


    public void printDice(Dice dice,String user) throws RemoteException, UserNotFoundException {
        ClientInterface c = clientsMap.get(user);
        if(c == null) throw new UserNotFoundException();
        c.printDice(dice);

    }

    public void endTimer(String username) throws RemoteException, UserNotFoundException {
        ClientInterface c = clientsMap.get(username);
        if(c == null) throw new UserNotFoundException();
         c.endTimer();

    }

    public void sendDisconnectionOf(String username) throws RemoteException {

        clientsMap.remove(username);

        for (ClientInterface c:clientsMap.values()) {
            c.sendDisconnectionOf(username);
        }

    }

    public void sendConnectionOf(String username) throws RemoteException {
        for (ClientInterface c:clientsMap.values()) {
            c.sendConnectionOf(username);
        }
    }


    public ConcurrentHashMap<String, ClientInterface> getClientsMap() {
        return clientsMap;
    }

}


