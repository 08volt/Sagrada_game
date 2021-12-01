package progettoIngSW.Network.Server;

import progettoIngSW.Exceptions.EndTimerException;
import progettoIngSW.Exceptions.MoveStoppedException;
import progettoIngSW.Exceptions.UserNotFoundException;
import progettoIngSW.Model.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Thread che attende connessioni di tipo Socket e crea un nuovo Thread per ogniuna di esse.
 * Invia a tutti i client connessi gli update e a solo quello con uno specifico username le richieste individuali.
 */

public class ServerSocketImpl extends Thread{
    static final int  PORT =   8000;
    ServerSocket serverSocket = null;

    private Server server;
    ArrayList<ServerSocketHandler> handlers = new ArrayList<>();


    public ServerSocketImpl(Server s){
        server = s;

    }


    public void run(){

        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println(serverSocket.toString() + " " + serverSocket.getInetAddress());
            while (true) {

                ServerSocketHandler socketHandler = new ServerSocketHandler(serverSocket.accept(),server);
                handlers.add(socketHandler);
                socketHandler.start();
            }

        } catch (IOException ignored) {

        } finally {
            try {
                serverSocket.close();
            } catch (IOException ignored) {

            }
        }



    }



    public boolean askIncrease(String user) throws UserNotFoundException, EndTimerException, MoveStoppedException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(user)){
                return h.askIncrease();
            }
        }
        throw new UserNotFoundException();

    }


    public int askWindowPos(String user, boolean placement) throws UserNotFoundException, EndTimerException, MoveStoppedException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(user)){
                return h.askWindowPos(placement);
            }
        }
        throw new UserNotFoundException();
    }


    public int[] askRoundTrackPos(String user) throws UserNotFoundException, EndTimerException, MoveStoppedException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(user)){
                return h.askRoundTrackPos();
            }
        }
        throw new UserNotFoundException();
    }


    public int askNumber(Colors col, String user) throws UserNotFoundException, EndTimerException, MoveStoppedException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(user)){
                return h.askNumber(col);
            }
        }
        throw new UserNotFoundException();
    }


    public boolean askHowMany(String user) throws UserNotFoundException, EndTimerException, MoveStoppedException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(user)){
                return h.askHowMany();
            }
        }
        throw new UserNotFoundException();
    }


    public int askWhichPattern(Pattern[] patterns, String user) throws  UserNotFoundException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(user)){
                return h.askWhichPattern(patterns);
            }
        }
        throw new UserNotFoundException();
    }


    public int askDraftPos(String user) throws UserNotFoundException, EndTimerException, MoveStoppedException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(user)){
                return h.askDraftPos();
            }
        }
        throw new UserNotFoundException();
    }


    public void startTurn(String user) throws  UserNotFoundException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(user)){
                h.startTurn();
                return;
            }
        }
        throw new UserNotFoundException();
    }


    public void updateCurrentPlayer(int currentPlayer) {
        for (ServerSocketHandler h:handlers) {
            if(!h.getUsername().isEmpty())
                h.updateCurrentPlayer(currentPlayer);

        }
    }


    public void updateTokens(ArrayList<Integer> tokens)  {
        for (ServerSocketHandler h:handlers){
            if(!h.getUsername().isEmpty())
                h.updateTokens(tokens);

        }
    }


    public void updateGameTurn(Turns turn)  {
        for (ServerSocketHandler h:handlers) {
            if(!h.getUsername().isEmpty())
                h.updateGameTurn(turn);

        }
    }


    public void updatePlayerState(Player player, int pos)  {
        for (ServerSocketHandler h:handlers) {
            if(!h.getUsername().isEmpty())
                h.updatePlayerState(player, pos);

        }
    }


    public void updateWindowsFrame(WindowFrame wf)  {
        for (ServerSocketHandler h:handlers) {
            if(!h.getUsername().isEmpty())
                h.updateWindowsFrame( wf);

        }
    }


    public void updateDraftPool(ArrayList<Dice> draft) {
        for (ServerSocketHandler h:handlers) {
            if(!h.getUsername().isEmpty())
                h.updateDraftPool(draft);

        }

    }


    public void updateRoundTrack(HashMap<Integer, ArrayList<Dice>> track) {
        for (ServerSocketHandler h : handlers) {
            if(!h.getUsername().isEmpty())
                h.updateRoundTrack(track);

        }
    }


    public void updateGame(Game arg) {
        for (ServerSocketHandler h : handlers) {
            if(!h.getUsername().isEmpty())
                h.updateGame(arg);

        }
    }

    public void endGame(String user, ArrayList<Integer> ranking) throws UserNotFoundException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(user)){
                h.endGame(ranking);
                return;
            }
        }
        throw new UserNotFoundException();

    }

    public void endTimer(String username) throws UserNotFoundException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(username)){
                h.endTimer();
                return;
            }
        }
        throw new UserNotFoundException();
    }

    public void printDice(Dice dice, String username) throws UserNotFoundException {
        for (ServerSocketHandler h:handlers) {
            if(h.getUsername().equals(username)){
                h.printDice(dice);
                return;
            }
        }
        throw new UserNotFoundException();
    }

    public void sendDisconnectionOf(String username) {
        for(int i = 0; i< handlers.size();i++)
            if(handlers.get(i).getUsername().equals(username))
            {
                handlers.get(i).interrupt();
                handlers.remove(i);
                break;
            }


        for (ServerSocketHandler h : handlers) {
            h.sendDisconnectionOf(username);
        }
    }

    public void sendConnectionOf(String username) {
        for (ServerSocketHandler h : handlers) {
            if(!h.getUsername().isEmpty())
                h.sendConnectionOf(username);
        }
    }


    public void close() throws IOException {
        serverSocket.close();
    }
}
