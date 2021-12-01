package progettoIngSW.View.Gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import progettoIngSW.Network.Client.ConnectionType;

public class ConnectionController {

    @FXML
    private TextField address;

    private String serverAddress;
    private ConnectionType connectionChosen = ConnectionType.RMI;

    /**
     * Metodo attivato dal pulsante "Conferma", salva l'indirizzo del server che l'utente ha inserito, controllando
     * che il campo non sia vuoto, altrimenti mostra un alert per notificare all'utente di completare il campo
     * Se l'indirizzo è stato inserito notifica al thread in attesa nella gui che il server address è stato inserito
     */
    public void submit(){
        serverAddress = address.getText();
        if(serverAddress.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Inserisci l'indirizzo del server");
            alert.showAndWait();
        }else {
            synchronized (GUI.getMonitor()){
                GUI.getMonitor().notifyAll();
            }
        }
    }

    /**
     * Metodo attivato cliccando su rmi nella scelta della connessione, imposta il tipo di connessione scelto su RMI
     */
    public void rmiConn(){
        connectionChosen = ConnectionType.RMI;
    }

    /**
     * Metodo attivato cliccando su socket nella scelta della connessione, imposta il tipo di connessione scelto
     * su Socket
     */
    public void socketConn(){
        connectionChosen = ConnectionType.SOCKET;
    }

    /**
     * Metodo lanciato quando l'indirizzo inserito dall'utente non è valido, richiede all'utente di inserire
     * un indirizzo corretto
     */
    public void ioException() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Server irraggiungibile");
            alert.showAndWait();
        });
        serverAddress = null;
    }

    public ConnectionType getConnection() {
        return connectionChosen;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
