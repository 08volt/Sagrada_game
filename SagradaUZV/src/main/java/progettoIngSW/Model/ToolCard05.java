package progettoIngSW.Model;

import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.DraftFullException;

public class ToolCard05 implements ToolCard {


    //
    //ATTRIBUTES
    //
    private DraftPool draft;
    private RoundTrack roundTrack;
    private int draftPos, trackRound, trackPos;

    //
    //
    //draftPos = posizione all'interno del draftpool
    //trackRound = numero round
    //trackPos = numero dado impilato all'interno del round scelto

    //
    //CONSTRUCTOR
    //

    /**
     * @param draft è da dove deve prendere il dado
     * @param roundTrack è l'oggetto che contiene il dado che vuole scambiare
     * @param draftPos è la posizione del dado scelto all'interno del draftPool
     * @param trackRound è il numero del round che contiene il dado scelto
     * @param trackPos è la posizione del dado scelto all'interno del roundTrack
     */
    public ToolCard05(DraftPool draft, RoundTrack roundTrack, int draftPos, int trackRound, int trackPos) {
        super();
        this.draft = draft;
        this.roundTrack = roundTrack;
        this.draftPos = draftPos;
        this.trackRound = trackRound;
        this.trackPos = trackPos;
    }

    //
    //METHOD
    //

    /**
     * Fa uno switch del dado del draftPool col dado del roundTrack
     * @throws DiceNotFoundException se nella posizione scelta non è presente nessun dado
     * @throws DraftFullException se il draftPool è pieno e si vuole aggiungere un dado
     */
    @Override
    public void useToolCard() throws DiceNotFoundException,DraftFullException {

        try {
            draft.getDraft().get(draftPos);
            roundTrack.getDice(trackRound).get(trackPos);

        }catch (IndexOutOfBoundsException e){
           throw new DiceNotFoundException();
        }

        roundTrack.addDice(trackRound, draft.removeDice(draftPos));
        draft.addDice(roundTrack.removeDice(trackRound, trackPos));

    }
}
