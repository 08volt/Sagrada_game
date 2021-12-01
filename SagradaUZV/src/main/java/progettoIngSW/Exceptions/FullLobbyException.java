package progettoIngSW.Exceptions;

import progettoIngSW.Model.Player;


//WHEN THE PLAYER CANT ENTER THE GAME BECOUSE THE LOBBY IS FULL

public class FullLobbyException extends Exception {



    public FullLobbyException() {
        super("lobby is full");
    }
}
