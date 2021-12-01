package progettoIngSW;


import progettoIngSW.Model.*;

public interface PlayerInterface {
    String getUsername();
    int getNumTokens();
    Turns getCurrentTurn();
    Moves getMoves();
    void setMoves(Moves moves);
    int getScore();
    PrivateObjectiveCard getPrivateObjectiveCard();
}
