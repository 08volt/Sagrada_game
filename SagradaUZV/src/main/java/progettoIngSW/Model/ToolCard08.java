package progettoIngSW.Model;

import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.RulesBreakException;

public class ToolCard08 implements ToolCard{


    //
    //ATTRIBUTES
    //
    WindowFrame windowFrame;
    DraftPool dp;
    int draftPos;
    int i1;

    //
    //CONSTRUCTOR
    //

    /**
     * @param windowFrame è la windowFrame del giocatore che vuole utilizzare la toolCard
     * @param dp è l'oggetto draftPool dal quale si vuole prendere il secondo dado
     * @param draftPos è la posizione del dado scelto all'interno del draftPool
     * @param i1 è la posizione scelta dove si vuole posizionare il dado
     */
    public ToolCard08(WindowFrame windowFrame, DraftPool dp, int draftPos, int i1) {
        super();

        this.windowFrame = windowFrame;
        this.dp = dp;
        this.draftPos = draftPos;
        this.i1 = i1;
    }


    //
    //METHOD
    //
    /**
     * Permette al giocatore di posizionare subito un secondo dado dopo aver posizionato il primo
     * @throws RulesBreakException se lo spostamento non rispetta le restrizioni
     * @throws DiceNotFoundException se nella posizione scelta non è presente alcun dado
     * @throws CellNotEmptyException se la cella dove si vuole spostare il dado è occupata da un altro dado
     */
    @Override
    public void useToolCard() throws RulesBreakException, DiceNotFoundException, CellNotEmptyException {

        try {
            windowFrame.placeDice(dp.getDraft().get(draftPos), i1);
            dp.removeDice(draftPos);
        }
        catch (IndexOutOfBoundsException e){
            throw new DiceNotFoundException();
        }
    }
}
