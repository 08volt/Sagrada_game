package progettoIngSW.Model;

import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.RulesBreakException;

public class Cell implements java.io.Serializable{
    //
    //ATTRIBUTES
    //
    private Colors colorRestriction;
    private int numberRestriction;
    private boolean numberFlag;
    private boolean colorFlag;
    private Dice dice;

    //
    //CONSTRUCTOR
    //

    /**
     * Class constructor that set the color and number restriction of the cell
     * @param colorRestriction color restriction
     * @param numberRestriction number restriction
     */
    public Cell(Colors colorRestriction, int numberRestriction) {
        this.colorRestriction = colorRestriction;
        this.numberRestriction = numberRestriction;
        this.numberFlag = true;
        this.colorFlag = true;
        this.dice = null;
    }

    //
    //METHODS
    //
    /**
     * Method that removes the dice from the cell
     * @throws DiceNotFoundException if there isn't a dice on the cell
     * @return dice removed from the cell
     */
    public Dice removeDice() throws DiceNotFoundException {
        if (this.dice == null)
            throw new DiceNotFoundException();

        Dice d = this.dice;
        this.dice = null;
        return d;
    }

    //
    //GET AND SET METHODS
    //
    public Dice getDice() {
        return dice;
    }
    public void setDice(Dice dice) throws CellNotEmptyException {
        if (this.dice == null){
            this.dice = dice;
        }
        else
            throw new CellNotEmptyException();
    }

    public boolean isNumberFlag() {
        return numberFlag;
    }
    public void setNumberFlag(boolean numberFlag) {
        this.numberFlag = numberFlag;
    }

    public boolean isColorFlag() {
        return colorFlag;
    }
    public void setColorFlag(boolean colorFlag) {
        this.colorFlag = colorFlag;
    }

    public Colors getColorRestriction() {
        return colorRestriction;
    }

    public int getNumberRestriction() {
        return numberRestriction;
    }


}
