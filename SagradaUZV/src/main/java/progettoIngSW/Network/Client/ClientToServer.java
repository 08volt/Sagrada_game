package progettoIngSW.Network.Client;

import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.Pattern;

import java.io.IOException;
import java.rmi.RemoteException;

public interface ClientToServer {
    /**
     * invia al server le credenziali per loggarsi ovvero il nome utente e la password, se il metodo finisce senza eccezzioni
     * vuol dire che il login è andato a buon fine e l'utente si è connesso alla partita
     * @throws FullLobbyException quando la lobby ha gia raggiunto il numero massimo di giocatori o la partita è gia iniziata
     * @throws IOException quando vi sono problemi di connessione
     * @throws NameNotAvailableException quando il nome utente che deve essere unico durante la partita è gia usato da un altro giocatore
     * @throws WrongPasswordException quando si inserisce il nome utente di un giocatore che precedentemente si era sconnesso dalla partita ma si sbaglia password
     *
     */
    void login() throws FullLobbyException, IOException, NameNotAvailableException, WrongPasswordException;


    /**
     * viene inviato il nuovo pattern al server
     * @param pattern pattern da inviare
     * @throws RemoteException problemi di connessione
     */
    void uploadPattern(Pattern pattern) throws RemoteException;
}
