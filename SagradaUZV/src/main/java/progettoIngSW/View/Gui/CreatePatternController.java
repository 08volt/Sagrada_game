package progettoIngSW.View.Gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Pattern;
import java.util.Optional;

public class CreatePatternController {

    private Pattern p;
    private Colors[] colors;
    private int[] numbers;

    public CreatePatternController(){
        p = new Pattern();
    }

    @FXML
    GridPane pattern;
    @FXML
    TextField namePattern;
    @FXML
    TextField difficultPattern;

    /**
     * A ogni cella viene assegnata un evento, per permettere all'utente di scegliere la restrizione desiderata
     */
    @FXML
    public void initialize(){
        colors = new Colors[20];
        numbers = new int[20];
        for (Node element : pattern.getChildren()) {
            element.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                ChoiceDialog<String> restriction = new ChoiceDialog<>("Restrizione",""+0, ""+1,""+2,""+3,""+4,""+5,
                        ""+6,"rosso", "verde", "giallo", "viola", "blu");
                restriction.setTitle("Crea pattern");
                restriction.setHeaderText("Scegli il tipo di restrizione");
                restriction.setGraphic(null);
                Optional<String> choice = restriction.showAndWait();
                if(choice.isPresent() && choice.get().length() == 1){
                    modifyPatternNumber((Pane) element , Integer.parseInt(choice.get()));
                }
                else choice.ifPresent(s -> modifyPatternColor((Pane) element, s));
            });
        }
    }

    /**
     * Metodo attivato cliccando sul pulsante salva, controlla che l'utente abbia inserito il nome e la
     * difficoltà, nel caso contrario stampa un messaggio di errore all'utente
     * Notifica al thread in attesa nella gui che è stato caricato il pattern
     */
    public void savePattern(){
        if (namePattern.getText().isEmpty() || difficultPattern.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Inserisci il nome e la difficoltà del pattern");
            alert.showAndWait();
        } else {
            for(int i = 0; i < colors.length; i++){
                if(colors[i] == null)
                    colors[i] = Colors.WHITE;
            }
            p.setName(namePattern.getText());
            p.setDifficult(Integer.parseInt(difficultPattern.getText()));
            p.setColors(colors);
            p.setNumbers(numbers);
            synchronized (GUI.getMonitor()){
                GUI.getMonitor().notifyAll();
            }
        }
    }

    /**
     * Metodo che assegna alla cella selezionata dall'utente la restrizione di colore desiderata
     * Assegna all'array di colori la restrizione scelta, nella posizione della cella all'interno della griglia
     * @param cell è la cella selezionata
     * @param restriction è il tipo di restrizione selezionata
     */
    private void modifyPatternColor(Pane cell, String restriction) {
        Colors restr = Colors.WHITE;
        switch (restriction){
            case "rosso": restr = Colors.RED;
                break;
            case "verde": restr = Colors.GREEN;
                break;
            case "giallo": restr = Colors.YELLOW;
                break;
            case "viola": restr = Colors.PURPLE;
                break;
            case "blu": restr = Colors.BLUE;
                break;
        }
        cell.getChildren().clear();
        PatternController.setColor(cell, restr);
        colors[5 * GridPane.getRowIndex(cell) + GridPane.getColumnIndex(cell)] = restr;
    }

    /**
     * Metodo che assegna alla cella selezionata dall'utente la restrizione di numero desiderata
     * @param cell è la cella selezionata
     * @param choice è il tipo di restrizione selezionata
     */
    private void modifyPatternNumber(Pane cell,Integer choice) {
        if(choice == 0){
            cell.getChildren().clear();
            cell.setStyle("");
        }else {
            PatternController.setNumber(cell, choice);
        }
        numbers[5 * GridPane.getRowIndex(cell) + GridPane.getColumnIndex(cell)] = choice;
    }

    public Pattern getPattern() {
        return p;
    }
}
