package progettoIngSW.Model;

import progettoIngSW.Exceptions.NotValidCellException;
import progettoIngSW.Exceptions.RulesBreakException;

public class ToolCard06 implements ToolCard {

    //
    //ATTRIBUTES
    //
    private DraftPool draft;
    private WindowFrame wf;
    private int pos;



    //Dopo aver scelto un dado, tira nuovamente quel dado
    //Se non puoi piazzarlo, riponilo nella riserva
    //
    //
    //CONSTRUCTOR
    //

    /**
     * @param wf è la windowFrame del giocatore che utilizza la carta
     * @param pos è la posizione del dado da rilanciare all'interno del draftPool
     * @param draft contiene il dado che si vuole prelevare
     */
    public ToolCard06(WindowFrame wf, int pos, DraftPool draft) {
        super();
        this.draft=draft;
        this.wf = wf;
        this.pos = pos;
    }

    //
    //METHOD
    //

    /**
     * Ritira il dado selezionato e controlla che ci sia almeno una posizione disponibile per l'utente dove
     * piazzare il dado
     * @throws NotValidCellException se non ci sono celle disponibili per il piazzamento
     */
    @Override
    public void useToolCard() throws NotValidCellException {

        this.draft.rollDice(pos);
        for(int i = 0; i< (wf.getCol()*wf.getRow()); i++) {
            try {
                Rules.checkCell(wf, draft.getDraft().get(pos), i);
                if (wf.getCell(i).getDice() == null)
                    return;
            }
            catch (RulesBreakException e) {
                continue;
            }

        }
        throw new NotValidCellException();
    }

}
