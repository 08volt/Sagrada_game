package progettoIngSW.Model;

import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.RulesBreakException;

public class ToolCard02 implements ToolCard {

    //
    //ATTRIBUTES
    //
    private int i1,i2;
    private WindowFrame w;

    //
    //CONSTRUCTOR
    //
    /**
     * @param wf windowFrame da modificare
     * @param i1 posizione cella dado da spostare
     * @param i2 posizione in cui si vuole spostare il dado
     */
    public ToolCard02(WindowFrame wf, int i1, int i2) {
        super();
        this.i1 = i1;
        this.i2 = i2;
        this.w = wf;
    }

    //
    //METHOD
    //

    /**
     * Sposta il dado dalla cella in posizione i1 alla cella in posizione i2 senza considerare le restrizioni di colore
     * @throws DiceNotFoundException Se nella cella in i1 non e' presente alcun dado
     * @throws RulesBreakException  se lo spostamento non rispetta le restrizioni
     * @throws CellNotEmptyException se la cella i2 non e' vuota ma contiene gia' un dado
     */
    @Override
    public void useToolCard() throws DiceNotFoundException, RulesBreakException, CellNotEmptyException {

        Cell c = w.getCell(i1);


        w.getCell(i2).setColorFlag(false);

        try {
            w.moveDice(i1,i2);
        } catch (Exception e) {
            w.getCell(i2).setColorFlag(true);
            throw e;
        }



    }
}