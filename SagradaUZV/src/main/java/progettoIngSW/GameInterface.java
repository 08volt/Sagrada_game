package progettoIngSW;

import progettoIngSW.Model.Turns;

import java.util.ArrayList;

public interface GameInterface {

    Turns getCurrentTurn();
    void setCurrentTurn(Turns currentTurn);

    int getCurrentRound();
    //void setCurrentRound(int currentRound);

    int getCurrentPlayer(); //posizione nella lista players
    void setCurrentPlayer(int currentPlayer);

    ArrayList<Integer> getPublicObjectiveCards();
    void setPublicObjectiveCards(ArrayList<Integer> poc);
    ArrayList<Integer> getToolCards();
    ArrayList<Integer> getToolTokens();
    void setToolTokens(ArrayList<Integer> toolTokens);

    boolean isGameStarted();

    void setToolCards(ArrayList<Integer> tools);

    void setCurrentRound(int r);
}
