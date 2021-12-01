package progettoIngSW.View.Gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class HomeController{


    @FXML
    private TextField name;
    @FXML
    private TextField password;
    @FXML
    private Button createPattern;

    //
    //USER SETTINGS
    //
    private String username;
    private String pass;

    private int choice;

    public HomeController(){
        choice = -1;
    }

    /**
     * Metodo attivato dal pulsante "Gioca", salva il nome e la password scelti dall'utente, controllando
     * che i campi non siano vuoti, altrimenti mostra un alert per notificare all'utente di completare i campi mancanti
     * Se i campi sono stati inseriti correttamento, notifica al thread in attesa nella gui che sono pronti username e password
     */
    public void submit(){
        if(createPattern.isArmed()){
            synchronized (GUI.getMonitor()){
                choice = 0;
                GUI.getMonitor().notifyAll();
            }
        }
        else {
            username = name.getText();
            pass = password.getText();
            if (username.isEmpty() || pass.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.setContentText("Inserisci tutti i campi");
                alert.showAndWait();
            } else {
                synchronized (GUI.getMonitor()) {
                    choice = 1;
                    GUI.getMonitor().notifyAll();
                }
            }
        }
    }

    /**
     * Metodo richiamato quando un utente cerca di iniziare una partita e la partita è già iniziata
     * resetta i campi inseriti precedentemente
     */
    public void fullLobby() {
        showAlertWrongCredentials("La partita è già iniziata, aspetta che finisca per giocare.");
    }

    /**
     * Metodo richiamato quando un utente cerca di connettersi ad una partita con uno username già utilizzato
     * da un altro utente, resetta i campi inseriti precedentemente
     */
    public void nameNotAvailable() {
        showAlertWrongCredentials("Lo username scelto non è disponibile, accedi con un altro username.");
    }

    /**
     * Metodo richiamato quando un utente cerca di riconnettersi ad una partita con una password sbagliata
     * Resetta i campi inseriti precedentemente
     */
    public void wrongPassword() {
        showAlertWrongCredentials("Lo username inserito è giusto, ma la password è sbagliata.");
    }

    /**
     * Metodo che mostra un alert di tipo Errore per segnalare all'utente cosa è stato sbagliato durante l'inserimento
     * dei parametri
     * @param text è il testo da mostrare all'utente
     */
    private void showAlertWrongCredentials(String text){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText(text);
            alert.showAndWait();
        });
        username = null;
        pass = null;
    }
    public String getUsername() {
        return username;
    }

    public String getPass() {
        return pass;
    }

    public int getChoice() {
        return choice;
    }
}
