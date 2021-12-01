package progettoIngSW.Model;

import progettoIngSW.Exceptions.*;

import java.awt.*;

public class ToolCard12 implements ToolCard{


    //
    //ATTRIBUTES
    //
    private WindowFrame wf;
    private RoundTrack rt;
    private int[] pos;


    // pos.lenght = 2 --> muovo 1 dado
    // pos.lenght = 4 --> muovo 2 dadi
    //
    //
    //[0] o1
    //[1] d1
    //[2] o2
    //[3] d2
    //
    //
    //CONSTRUCTOR
    //

    /**
     * @param wf è la windowFrame del giocatore che vuole utilizzare la toolCard
     * @param rt è il roundTrack
     * @param pos contiene gli spostamenti decisi dal giocatore
     */
    public ToolCard12(WindowFrame wf, RoundTrack rt, int[] pos) {
        super();
        this.rt = rt;
        this.wf = wf;
        this.pos = pos;
    }

    //
    //METHOD
    //

    /**
     * Permette di fare fino a 2 spostamenti di dadi dello stesso colore di un dado presente nel roundTrack
     * @throws DiceNotFoundException se non trova il dado nella posizione indicata dal giocatore
     * @throws CellNotEmptyException se la cella dove si vuole posizionare il dado non è vuoto
     * @throws RulesBreakException se lo spostamento non rispetta le restrizioni
     * @throws InvalidParamsException se non è presente il colore nel roundTrack
     */
    @Override
    public void useToolCard() throws DiceNotFoundException, CellNotEmptyException, RulesBreakException, InvalidParamsException {
        if (pos.length == 2) {
            Dice d = wf.getCell(pos[0]).getDice();
            if(d == null)
                throw new DiceNotFoundException();

            if (ColorInRoundTrack(d, rt)) {
                try {
                    wf.moveDice(pos[0], pos[1]);
                } catch (CellNotEmptyException e) {
                    throw e;
                } catch (RulesBreakException e) {
                    throw e;
                }

            }else{
                throw new InvalidParamsException("color");
            }

        }


        else if(pos.length == 4) {

            Dice d1 = wf.getCell(pos[0]).getDice();
            Dice d2 = wf.getCell(pos[2]).getDice();


            if (d1 != null && d2 != null) {
                if (!ColorInRoundTrack(d1, rt) || d2.getColor() != d1.getColor())
                    throw new InvalidParamsException("color");

                wf.moveDice(pos[0], pos[1]);

                try {
                    wf.moveDice(pos[2], pos[3]);
                }catch (RulesBreakException e) {
                    wf.removeDice(pos[1]);
                    wf.setBruteDice(d1, pos[0]);
                    throw e;
                }catch (CellNotEmptyException e) {
                    wf.removeDice(pos[1]);
                    wf.setBruteDice(d1, pos[0]);
                    throw e;
                }
            }
            else
                throw new DiceNotFoundException();
        }
        else
            throw new InvalidParamsException("howMany");
    }



    private boolean ColorInRoundTrack(Dice dice, RoundTrack track){
        for (int r = 1; r <= rt.getMaxRound(); r++) {
            for (Dice d:  track.getDice(r)) {
                if( d.getColor() == dice.getColor()) return true;
            }

        }
        return false;
    }
}
