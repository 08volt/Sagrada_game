package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.DraftFullException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.Model.*;

import java.io.IOException;

import static org.junit.Assert.*;

public class ToolCard08Test{

    @Test
    public void checkTool08() throws IOException {

        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        DraftPool draft = new DraftPool();

        Dice d1 = new Dice(Colors.BLUE);
        Dice d2 = new Dice(Colors.PURPLE);
        Dice d3 = new Dice(Colors.GREEN);
        d1.setNumber(2);
        d2.setNumber(4);
        d3.setNumber(5);
        try {
            wf.placeDice(d1, 15);
            wf.placeDice(d2, 16);
            wf.placeDice(d3, 11);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        Dice d4 = new Dice(Colors.RED);
        d4.setNumber(6);

        try {
            draft.addDice(d4);
        } catch (DraftFullException e) {
            e.printStackTrace();
        }

        //uso tool non corretto
        ToolCard08 tool08_RulesException = new ToolCard08(wf, draft, 0, 10);
        ToolCard08 tool08_CellNotEmpty = new ToolCard08(wf, draft, 0, 16);
        ToolCard08 tool08_NoDiceDraft = new ToolCard08(wf, draft, 1, 17);
        try {
            tool08_CellNotEmpty.useToolCard();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            assertEquals("Controllo cella vuota non eseguito",e.getMessage(),"Cell already with dice");
        }

        try {
            tool08_NoDiceDraft.useToolCard();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            assertEquals("Controllo presenza dado non effettuato",e.getMessage(),"DICE NOT FOUND");
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        try {
            tool08_RulesException.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Controllo restrizioni non eseguito",e.getClass(),RulesBreakException.class);
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }


        //uso tool corretto
        ToolCard08 tool08_OK = new ToolCard08(wf, draft, 0, 17);

        try {
            tool08_OK.useToolCard();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertEquals("Dado non rimosso dal draft", 0, draft.getDraft().size());
        assertEquals("Posizionamento non eseguito", d4, wf.getCell(17).getDice());

    }
}
