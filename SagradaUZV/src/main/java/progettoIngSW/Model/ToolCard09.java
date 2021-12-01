package progettoIngSW.Model;

import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.NotValidCellException;
import progettoIngSW.Exceptions.RulesBreakException;


public class ToolCard09 implements ToolCard{


    //
    //ATTRIBUTES
    //
    WindowFrame wf;
    DraftPool dp;
    int poolPos;
    int x1;
    int y1;



    //
    //CONSTRUCTOR
    //

    /**
     * @param dp è il draftPool da dove si vuole prelevare il dado
     * @param poolPos è la posizione del dado all'interno del draftPool
     * @param windowFrame è la windowFrame del giocatore che utilizza la toolCard
     * @param i è la posizione del dado all'interno della windowFrame dove si vuole posizionare il dado
     */
    public ToolCard09(DraftPool dp, int poolPos, WindowFrame windowFrame, int i) {
        super();
        this.dp = dp;
        this.poolPos = poolPos;
        this.wf = windowFrame;
        this.x1 = windowFrame.getX(i);
        this.y1 = windowFrame.getY(i);
    }

    //
    // piazza il dado dove non ci sono altri dadi adiacenti
    //
    //
    //METHOD
    //

    /**
     * Permette di posizionare il dado dove non ci sono altri dadi adiacenti
     * @throws CellNotEmptyException se la cella dove si vuole posizionare il dado non è vuoto
     * @throws DiceNotFoundException se non trova il dado nella posizione indicata dal giocatore
     * @throws NotValidCellException se è stata selezionata una cella non valida
     * @throws RulesBreakException se lo spostamento non rispetta le restrizioni
     */
    @Override
    public void useToolCard() throws CellNotEmptyException, DiceNotFoundException, NotValidCellException, RulesBreakException {


        if ((x1 > 0 && wf.getCell(x1 - 1, y1).getDice() != null) //sotto
                || (x1 < wf.getRow() - 1 && wf.getCell(x1 + 1, y1).getDice() != null) //sopra
                || (y1 > 0 && wf.getCell(x1, y1 - 1).getDice() != null) //sinistra
                || (y1 < wf.getCol() - 1 && wf.getCell(x1, y1 + 1).getDice() != null) //destra

                || (x1 > 0 && y1 > 0 && wf.getCell(x1 - 1, y1 - 1).getDice() != null) //sotto sx
                || (x1 > 0 && y1 < wf.getCol() - 1 && wf.getCell(x1 - 1, y1 + 1).getDice() != null) //sotto dx
                || (x1 < wf.getRow() - 1 && y1 > 0 && wf.getCell(x1 + 1, y1 - 1).getDice() != null) //sopra sx
                || (x1 < wf.getRow() - 1 && y1 < wf.getCol() - 1 && wf.getCell(x1 + 1, y1 + 1).getDice() != null)) //sopra dx


        {
            throw new NotValidCellException();
        }
        Dice d = dp.getDraft().get(poolPos);
        Rules.firstDiceRestriction(wf,wf.getI(x1, y1));
        Rules.colorRestriction(wf, d, wf.getI(x1, y1));
        Rules.valueRestriction(wf, d, wf.getI(x1, y1));
        wf.setBruteDice(d,wf.getI(x1, y1));
        dp.removeDice(poolPos);
    }
}
