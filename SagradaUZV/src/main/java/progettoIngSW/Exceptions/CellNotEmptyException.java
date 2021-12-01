package progettoIngSW.Exceptions;

public class CellNotEmptyException extends Exception {

    //WHEN SOMEONE TRY TO PLACE A DICE ON A CELL ALREADY OCCUPIED
    public CellNotEmptyException() {
        super("Cell already with dice");
    }
}
