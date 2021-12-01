package progettoIngSW.View.Gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import progettoIngSW.PlayerInterface;
import progettoIngSW.WindowFrameInterface;

public class EndGameController {

    @FXML
    Pane player1;
    @FXML
    Pane player2;
    @FXML
    Pane player3;
    @FXML
    Pane player4;
    @FXML
    Label username1;
    @FXML
    Label username2;
    @FXML
    Label username3;
    @FXML
    Label username4;
    @FXML
    Label winner;

    /**
     * Metodo richiamato finita la partita per mostrare le schermata finali dei vari giocatori, con relativo punteggio
     * @param playerInterfaces è la lista dei giocatori della partita, ciascuno col punteggio finale assegnato
     * @param playersWindow è la lista delle windowFrames dei giocatori, ordinati in modo uguale alla lista di giocatori
     * @param ranking è la lista degli id dei giocatori ordinati secondo il punteggio
     */
    public void showResult(ArrayList<PlayerInterface> playerInterfaces, ArrayList<WindowFrameInterface> playersWindow, ArrayList<Integer> ranking) {
        Platform.runLater(() -> {
            winner.setText("Il vincitore è: " + playerInterfaces.get(ranking.get(0)).getUsername());
            username1.setText(playerInterfaces.get(ranking.get(0)).getUsername() + " punteggio: " + playerInterfaces.get(ranking.get(0)).getScore());
            GridPane wf1 = createWindowframe(playersWindow.get(ranking.get(0)), player1);
            GamePlayController.modifyWf(wf1, playersWindow.get(ranking.get(0)));
            player1.setVisible(true);
            username2.setText(playerInterfaces.get(ranking.get(1)).getUsername() + " punteggio: " + playerInterfaces.get(ranking.get(1)).getScore());
            GridPane wf2 = createWindowframe(playersWindow.get(ranking.get(1)), player2);
            GamePlayController.modifyWf(wf2, playersWindow.get(ranking.get(1)));
            player2.setVisible(true);
            if(playerInterfaces.size() > 2) {
                username3.setText(playerInterfaces.get(ranking.get(2)).getUsername() + " punteggio: " + playerInterfaces.get(ranking.get(2)).getScore());
                GridPane wf3 = createWindowframe(playersWindow.get(ranking.get(2)), player3);
                GamePlayController.modifyWf(wf3, playersWindow.get(ranking.get(2)));
                player3.setVisible(true);
            }
            if(playerInterfaces.size() > 3) {
                username4.setText(playerInterfaces.get(ranking.get(3)).getUsername() + " punteggio: " + playerInterfaces.get(ranking.get(3)).getScore());
                GridPane wf3 = createWindowframe(playersWindow.get(ranking.get(3)), player4);
                GamePlayController.modifyWf(wf3, playersWindow.get(ranking.get(3)));
                player4.setVisible(true);
            }
        });
    }

    private GridPane createWindowframe(WindowFrameInterface windowFrameInterface, Pane player) {
        GridPane wf = new GridPane();
        for(int i = 0; i<windowFrameInterface.getRow(); i++){
            for(int j = 0; j<windowFrameInterface.getCol(); j++){
                Pane cell = new Pane();
                cell.setPrefHeight(player.getPrefHeight()/windowFrameInterface.getRow());
                cell.setPrefWidth(player.getPrefWidth()/windowFrameInterface.getCol());
                wf.add(cell, j, i);
            }
        }
        player.getChildren().add(wf);
        return wf;
    }

}
