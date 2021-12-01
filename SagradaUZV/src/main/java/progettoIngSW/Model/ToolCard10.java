package progettoIngSW.Model;

public class ToolCard10 implements ToolCard{

    //
    //ATTRIBUTES
    //
    private DraftPool dp;
    private int poolPos;

    //
    // gira un dado
    //
    //
    //CONSTRUCTOR
    //

    /**
     * @param dp è il draftPool contenente il dado selezionato
     * @param poolPos è la posizione del dado all'interno del draftPool
     */
    public ToolCard10(DraftPool dp, int poolPos) {
        super();
        this.dp = dp;
        this.poolPos = poolPos;

    }


    //
    //METHOD
    //

    /**
     * Gira il dado selezionata sulla faccia opposta
     */
    @Override
    public void useToolCard() {

        Dice d = dp.getDraft().get(poolPos);
        dp.changeValueDice(d,7 - d.getNumber());
        return;

    }
}
