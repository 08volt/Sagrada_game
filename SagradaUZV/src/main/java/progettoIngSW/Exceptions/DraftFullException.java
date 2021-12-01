package progettoIngSW.Exceptions;

import progettoIngSW.Model.Dice;

public class DraftFullException extends Exception{


    //WHEN YOU CANT ADD A DICE TO THE DRAFT BECAUSE IT REACHED HIS LIMIT
    public DraftFullException(Dice d) {
        super("DRAFT FULL CANT ADD THIS DICE "+ d.getNumber() +" "+d.getColor().name());

    }
}
