package progettoIngSW.Exceptions;

import progettoIngSW.Model.Dice;

//WHEN SOMEONE TRY TO TAKE A DICE FROM A PLACE WHERE THERE ISN'T

public class DiceNotFoundException extends Exception {


    int pos = 0;
    Object where = null;

    public DiceNotFoundException() {
        super("DICE NOT FOUND");
    }

    public void setPos(int pos){
        this.pos = pos;
    }

    public void setWhere(Object where){
        this.where = where;
    }


    @Override
    public String getMessage() {
        String m = super.getMessage();
        if (pos != 0)
            m += " in position "+ pos + " ";
        if(where != null)
            m+= where.toString();
        return m;
    }
}
