package progettoIngSW.Model;

import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.RulesBreakException;

public class ToolCard11 implements ToolCard {



    //
    // piazza il dado pescato dal sacchetto con il numero scelto
    // (((dopo averne tolto una dalla riserva))) ->  controllore
    //
    //


    //
    //ATTRIBUTES
    //
    private DraftPool draft;
    private int wfPos;
    private WindowFrame wf;

    //
    //CONSTRUCTOR
    //

    /**
     * @param wf è la windowFrame del giocatore che vuole utilizzare la toolCard
     * @param draft è il draft che contiene il dado selezionato
     * @param wfPos è la posizione del dado
     */
    public ToolCard11(WindowFrame wf, DraftPool draft, int wfPos){
        super();
        this.wf = wf;
        this.wfPos = wfPos;
        this.draft = draft;
    }

    //
    //METHOD
    //

    /**
     * Rimette il dado selezionato nel sacchetto e ne estrae uno nuovo
     * @throws CellNotEmptyException se la cella dove si vuole posizionare il dado non è vuoto
     * @throws RulesBreakException se lo spostamento non rispetta le restrizioni
     * @throws DiceNotFoundException se non trova il dado nella posizione indicata dal giocatore
     */
    @Override
    public void useToolCard() throws CellNotEmptyException,RulesBreakException,DiceNotFoundException {

        Dice d  = draft.getDraft().get(draft.getDraft().size()-1);
        wf.placeDice(d,wfPos);
        draft.removeDice(draft.getDraft().size()-1);

    }
    
}
