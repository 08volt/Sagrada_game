package progettoIngSW.Network.Client;


import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;
import progettoIngSW.View.Cli.ThreadConsoleReader;
import progettoIngSW.ViewInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Client è la classe che mette in comunicazione la view interface e la connessione con il server.
 * Implementa l'interfaccia Runnable e chiede all'utente le credenziali per configurare il login
 * o le informazioni per caricare un nuovo pattern. Gestisce inoltre l'inizio e la fine di ogni turno.
 *
 */

public class Client implements Runnable {

    static boolean logged = false;

    private String name;
    private String password;
    private String serverAddress;
    private ViewInterface view;
    private ClientToServer client; //INVIA RICHIESTE AL SERVER
    private boolean endGame = false;
    private static boolean yourTurn = false;
    private final Object monitor = new Object();
    private ThreadConsoleReader tr;


    /**
     * Costruttore della classe
     * @param viewInterface l'oggetto visivo da utilizzare per comunicare con l'utente.
     */
    public Client(ViewInterface viewInterface) {
        this.view = viewInterface;
    }


    /**
     * gestisce il pre-partita chiedendo le credenziali, il caricamento di nuovi pattern e il cambio turno durante la partita
     */
    @Override
    public void run() {

        while(client == null) {
            serverAddress = askServerAddress();
            try {
                client = askConnectionType();
            } catch (IOException | NotBoundException e) {
                view.connectionError();
            }
        }


        while(!view.askToPlay()){
            Pattern p  = view.writeNewPattern();
            try {
                client.uploadPattern(p);
            } catch (RemoteException ignored) {

            }
        }

        while(!logged){
            name = askName();
            password = askPassword();

            try {
                client.login();
            } catch (FullLobbyException e) {
                view.fullLobby();
            } catch (IOException e) {
                view.connectionError();
            } catch (NameNotAvailableException e) {
                view.nameNotAvailable();
            } catch (WrongPasswordException e) {
                view.wrongPassword();
            }
        }



        while(!endGame) {
            while(!Client.yourTurn){
                try {
                    synchronized (monitor) {

                        monitor.wait();
                    }
                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            try {
                view.startTurn();

            } catch (EndTimerException e) {
                view.printEndTurn();
                synchronized (monitor){
                    Client.yourTurn = false;
                }

            }
        }


    }


    /**
     * chiede all'utente, tramite la view, l'indirizzo del server
     * @return l'indirizzo del server
     */

    private String askServerAddress() {
        return view.askServerAddress();
    }


    /**
     * chiede all'utente, tramite la view, che tipo di connessione utilizzare
     * @return l'interfaccia della connessione da usare per comunicare con il server
     * @throws IOException se non riesce a connettersi sia con RMI che con Socket
     * @throws NotBoundException se non riesce a connettersi con RMI
     */
    private ClientToServer askConnectionType() throws IOException, NotBoundException {

        switch (view.askConnectionType()) {
            case RMI:
            {
                ClientRMI cLientRMI = new ClientRMI(view,this,serverAddress);
                view.setPlayer(cLientRMI);
                return cLientRMI;
            }
            case SOCKET: {
                ClientSocket clientSocket = new ClientSocket(serverAddress,view,this);
                Thread t = new Thread(clientSocket, "clientSocket");
                t.start();
                view.setPlayer(clientSocket);
                return clientSocket;
            }
        }
        return null;
    }

    /**
     * tramite la view chiede l'username all'utente
     * @return l'username scelto
     */
    private String askName() {
        return view.askName();
    }

    /**
     * tramite la view chiede la password all'utente
     * @return la password scelta
     */
    private String askPassword() {
        return view.askPassword();
    }

    /**
     * finisce il turno avvisando il thread di mettersi in attesa.
     * setta yourTurn a false in modo sincronizzato.
     */
    public void endTurn() {
        synchronized (monitor){
            yourTurn = false;
            monitor.notifyAll();
        }
//        client.endTurn();
    }

    /**
     * se la connessione alla partita va a buon fine view settato il campo logged a true
     * e viene avvisato l'utente, tramite la view, della connessione riuscita.
     * @param log true se si è connesso alla partita, false altrimetni
     */

    public void setLogged(boolean log) {
        logged = log;
        view.setLogged(log);
    }

    /**
     * avvia il turno avvisando il thread di terminare l'attesa
     */
    public void startTurn() {
        System.out.println("START TURN");
        synchronized (monitor) {
            yourTurn = true;
            monitor.notifyAll();
        }
    }


    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }


}
