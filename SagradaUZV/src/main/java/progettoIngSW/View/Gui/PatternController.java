package progettoIngSW.View.Gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Pattern;
import java.util.ArrayList;

public class PatternController {

    @FXML
    private Pane pattern1;
    @FXML
    private Pane pattern2;
    @FXML
    private Pane pattern3;
    @FXML
    private Pane pattern4;
    @FXML
    private Label name1;
    @FXML
    private Label name2;
    @FXML
    private Label name3;
    @FXML
    private Label name4;
    @FXML
    private Label difficult1;
    @FXML
    private Label difficult2;
    @FXML
    private Label difficult3;
    @FXML
    private Label difficult4;
    @FXML
    private Pane privateCard;

    private int choice = -1;

    @FXML
    public void initialize(){
        synchronized (GUI.getMonitor()){
            GUI.getMonitor().notifyAll();
        }
    }

    /**
     * Metodo attivato cliccando sul primo pattern, notifica al thread in attesa nella gui la scelta
     */
    @FXML
    public void button1Clicked(){
        choice = 0;
        synchronized (GUI.getMonitor()){
            GUI.getMonitor().notifyAll();
        }
    }

    /**
     * Metodo attivato cliccando sul secondo pattern, notifica al thread in attesa nella gui la scelta
     */
    @FXML
    public void button2Clicked(){
        choice = 1;
        synchronized (GUI.getMonitor()){
            GUI.getMonitor().notifyAll();
        }
    }

    /**
     * Metodo attivato cliccando sul terzo pattern, notifica al thread in attesa nella gui la scelta
     */
    @FXML
    public void button3Clicked(){
        choice = 2;
        synchronized (GUI.getMonitor()){
            GUI.getMonitor().notifyAll();
        }
    }

    /**
     * Metodo attivato cliccando sul quarto pattern, notifica al thread in attesa nella gui la scelta
     */
    @FXML
    public void button4Clicked(){
        choice = 3;
        synchronized (GUI.getMonitor()){
            GUI.getMonitor().notifyAll();
        }
    }

    /**
     * Metodo che inizializza i vari pattern (restrizioni, nome, difficoltà) tra cui l'utente deve scegliere
     * @param patterns è l'array di pattern tra i cui quali l'utente deve scegliere
     * @param color è il colore della carta privata dell'utente
     */
    public void askPattern(Pattern[] patterns, Colors color){
        Platform.runLater(() -> {
            ArrayList<Pane> pattern = new ArrayList<>();
            pattern.add(pattern1);
            pattern.add(pattern2);
            pattern.add(pattern3);
            pattern.add(pattern4);
            name1.setText(patterns[0].getName());
            name2.setText(patterns[1].getName());
            name3.setText(patterns[2].getName());
            name4.setText(patterns[3].getName());
            difficult1.setText(difficult1.getText() + patterns[0].getDifficult());
            difficult2.setText(difficult2.getText() + patterns[1].getDifficult());
            difficult3.setText(difficult3.getText() + patterns[2].getDifficult());
            difficult4.setText(difficult4.getText() + patterns[3].getDifficult());
            setColor(privateCard, color);
            for(int i = 0; i < patterns.length; i++) {
                GridPane patt = new GridPane();
                for (int x = 0; x < patterns[i].getRow(); x++) {
                    for (int y = 0; y < patterns[i].getCol(); y++) {
                        int numbRestr = patterns[i].getNumbers()[patterns[i].getCol() * x + y];
                        Colors cellRestr = patterns[i].getColors()[patterns[i].getCol() * x + y];
                        Pane cell = new Pane();
                        cell.setPrefWidth(pattern.get(i).getPrefWidth()/patterns[i].getCol());
                        cell.setPrefHeight(pattern.get(i).getPrefHeight()/patterns[i].getRow());
                        if (numbRestr != 0) {
                            setNumber(cell, numbRestr);
                        }
                        else
                            setColor(cell, cellRestr);
                        patt.add(cell, y, x);
                        patt.setStyle(patt.getStyle() + "-fx-border-color: black;");
                    }
                }
                pattern.get(i).getChildren().add(patt);
            }
        });

    }

    /**
     * Metodo richiamato quando scade il timer, setta la scelta a 4, numero deciso per notificare al server di effettuare
     * una scelta casuale
     */
    public void endTimer() {
        choice = 4;
        synchronized (GUI.getMonitor()){
            GUI.getMonitor().notifyAll();
        }
    }

    /**
     * Metodo per colorare la carta privata del giocatore
     * @param cell è il pannello della schermata che si vuole modificare, può essere la carta privata visualizzata durante
     *             la scelta dei pattern o la cella della windowFrame
     * @param color è il colore che si vuole assegnare alla cella
     */
    public  static void setColor(Pane cell, Colors color){
        switch (color) {
            case BLUE:
                cell.setStyle("-fx-background-color: #35B1B1;");
                break;
            case GREEN:
                cell.setStyle("-fx-background-color: #27825F;");
                break;
            case YELLOW:
                cell.setStyle("-fx-background-color: #FFE84D;");
                break;
            case RED:
                cell.setStyle("-fx-background-color: #C23A3A;");
                break;
            case PURPLE:{
                cell.setStyle("-fx-background-color: #994599;");
                break;
            }
            default: {
                cell.setStyle("");
                break;
            }
        }
        cell.setStyle(cell.getStyle() + "-fx-border-color: black;");
    }

    /**
     * Metodo per assegnare un numero (restrizione di colore) al Pane selezionato
     * @param cell è la cella alla quale si vuole assegnare il numero
     * @param restr è il numero della restrizione (numero da assegnare al Pane)
     */
    public static void setNumber(Pane cell, int restr){
        cell.getChildren().clear();
        Group number = new Group();
        cell.getChildren().add(number);
        Double centerX = cell.getPrefWidth()/2;
        Double centerY = cell.getPrefHeight()/2;
        Double radius = cell.getPrefHeight()/12;
        Double posX = centerX - 3*radius;
        Double posY = centerY - 3*radius;
        number.setTranslateX(centerX);
        number.setTranslateY(centerY);
        switch (restr){
            case 1: {
                number.getChildren().add(new Circle(0, 0, radius));
                for(Node circle : number.getChildren()){
                    ((Circle) circle).setFill(Paint.valueOf("#2F2F2F"));
                }
                cell.setStyle("-fx-background-color: #E1E1E1;");
                break;
            }
            case 2: {
                number.getChildren().addAll(new Circle(-posX, -posY, radius),
                        new Circle(posX, posY, radius));
                for(Node circle : number.getChildren()){
                    ((Circle) circle).setFill(Paint.valueOf("#4F4F4F"));
                }
                cell.setStyle("-fx-background-color: #C0C0C0;");
                break;
            }
            case 3: {
                number.getChildren().addAll(new Circle(-posX, posY, radius), new Circle(posX, -posY, radius),
                        new Circle(0, 0, radius));
                for(Node circle : number.getChildren()){
                    ((Circle) circle).setFill(Paint.valueOf("#5F5F5F"));
                }
                cell.setStyle("-fx-background-color: #A2A2A2;");
                break;
            }
            case 4: {
                number.getChildren().addAll(new Circle(posX, posY, radius), new Circle(posX, -posY, radius),
                        new Circle(-posX, posY, radius), new Circle(-posX, -posY, radius));
                for(Node circle : number.getChildren()){
                        ((Circle) circle).setFill(Paint.valueOf("#C0C0C0"));
                }
                cell.setStyle("-fx-background-color: #808080;");
                break;
            }
            case 5: {
                number.getChildren().addAll(new Circle(posX, posY, radius), new Circle(posX, -posY, radius),
                        new Circle(-posX, posY, radius), new Circle(-posX, -posY, radius),
                        new Circle(0, 0, radius));
                for(Node circle : number.getChildren()){
                    ((Circle) circle).setFill(Paint.valueOf("#D2D2D2"));
                }
                cell.setStyle("-fx-background-color: #5F5F5F;");
                break;
            }
            case 6: {
                number.getChildren().addAll(new Circle(posX, posY, radius), new Circle(posX, -posY, radius),
                        new Circle(-posX, posY, radius), new Circle(-posX, -posY, radius),
                        new Circle(posX, 0, radius), new Circle(-posX, 0, radius));
                for(Node circle : number.getChildren()){
                    ((Circle) circle).setFill(Paint.valueOf("#EFEFEF"));
                }
                cell.setStyle("-fx-background-color: #4F4F4F;");
                break;
            }
        }
        cell.setStyle(cell.getStyle() + "-fx-border-color: black;");
    }

    public int getChoice() {
        return choice;
    }
}
