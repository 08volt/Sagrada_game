package progettoIngSW.Model;

import progettoIngSW.Exceptions.InvalidParamsException;

public class ToolCard01 implements ToolCard {


    //
    //ATTRIBUTES
    //
    private int poolPos;
    private boolean increase;
    private DraftPool dp;

    //
    //CONSTRUCTOR
    //

    /**
     * @param dp draftPool in cui e' presente il dado che si vuole cambiare
     * @param poolPos posizione del dado all'interno del draft
     * @param increase true se si vuole incrementare, false altrimenti
     */
    public ToolCard01(DraftPool dp, int poolPos, boolean increase) {
        super();
        this.poolPos = poolPos;
        this.increase = increase;
        this.dp = dp;
    }

    //
    //METHOD
    //

    /**
     * Aumenta o diminuisce di 1 un dado nella riserva
     * @throws InvalidParamsException il valore del dado scelto e':
     *                                                          - 1 e si vuole decrementare
     *                                                          - 6 e si vuole incrementare
     */
    @Override
    public void useToolCard() throws InvalidParamsException {
        Dice d = dp.getDraft().get(poolPos);
        if(increase && d.getNumber() < 6){
            dp.changeValueDice(d,d.getNumber() + 1);
            return;
        }
        else if(!increase && d.getNumber() > 1){
            dp.changeValueDice(d,d.getNumber() - 1);
            return;
        }
        else
            throw new InvalidParamsException("number");

    }
}
