package progettoIngSW.Model;

public class ToolCard07 implements ToolCard{

    //
    //ATTRIBUTES
    //
    private DraftPool draft;

    //
    //CONSTRUCTOR
    //

    /**
     * @param draftPool è il draftPool utilizzato dalla carta
     */
    public ToolCard07(DraftPool draftPool) {
        super();
        this.draft = draftPool;
    }

    //
    //METHOD
    //

    /**
     * Ritira tutti i dadi del draftPool
     */
    @Override
    public void useToolCard(){

        draft.regenerateDraft();
    }
}
