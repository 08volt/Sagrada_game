package progettoIngSW.Exceptions;

import progettoIngSW.Model.Player;

//WHEN ONE PLAYER TRY TO DO SOMETHING BUT IT ISN'T HIS TURN

public class NotPlayingException extends Exception {
    public NotPlayingException(Player player) {
        super("Player " + player.getUsername() + " is not your turn");
    }
}
