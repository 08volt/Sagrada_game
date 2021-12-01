package progettoIngSW.Model;

import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.RulesBreakException;

public class ToolCard04 implements ToolCard{

    //
    //ATTRIBUTES
    //
    private int[] pos;
    private WindowFrame w;

    //
    //CONSTRUCTOR
    //

    /**
     * @param wf windowFrame da modificare
     * @param pos array di posizioni contenente gli spostamenti decisi dall'utente
     */
    public ToolCard04(WindowFrame wf, int[] pos) {
        super();
        this.w = wf;
        this.pos = pos;
    }

    //pos[0] i1  0 1
    //
    //pos[1] 12   2 3
    //
    // i1 --> i2  spostamento del primo dado
    //pos[2] i3   4 5
    //
    //pos[3] i4   6 7
    //
    // i3 --> 44   spostamento del secondo dado

    //
    //METHOD
    //

    /**
     * Sposta esattamente due dadi, rispettando tutte le restrizioni di piazzamento
     * @throws DiceNotFoundException Se nella cella iniziale dello spostamento non e' presente alcun dado
     * @throws CellNotEmptyException se la cella finale dello spostamento non e' vuota ma contiene gia' un dado
     * @throws RulesBreakException se gli spostamenti non rispettano le restrizioni
     */
    @Override
    public void useToolCard() throws DiceNotFoundException, CellNotEmptyException, RulesBreakException {


        w.moveDice(pos[0],pos[1]);

        try {
            w.moveDice(pos[2], pos[3]);
        }catch (RulesBreakException | CellNotEmptyException | DiceNotFoundException e){
            Dice d1 = w.removeDice(pos[1]);
            w.setBruteDice(d1,pos[0]);
            throw e;
        }
    }
}
