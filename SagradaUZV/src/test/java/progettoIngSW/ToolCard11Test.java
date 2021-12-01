package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.DraftFullException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.Model.*;

import java.io.IOException;

import static org.junit.Assert.*;

public class ToolCard11Test {

    @Test
    public void checkTool_11() throws IOException {

        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        Dice d1 = new Dice(Colors.BLUE);
        Dice d2 = new Dice(Colors.PURPLE);
        Dice d3 = new Dice(Colors.GREEN);
        d1.setNumber(2);
        d2.setNumber(4);
        d3.setNumber(5);
        try {
            wf.placeDice(d2, 16);
            wf.placeDice(d1, 15);
            wf.placeDice(d3, 11);
        } catch (RulesBreakException e) {
           e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        Dice draftDice = new Dice(Colors.RED);
        draftDice.setNumber(6);
        DraftPool draft = new DraftPool();
        try {
            draft.addDice(draftDice);
        } catch (DraftFullException e) {
            e.printStackTrace();
        }


        //test funzionamento sbagliato

        //restrizioni non rispettate
        ToolCard11 tc11_KO1 = new ToolCard11(wf,draft,7);
        //cella non vuota
        ToolCard11 tc11_KO2 = new ToolCard11(wf,draft,16);
        try {
            tc11_KO1.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizioni non controllate",e.getClass(),RulesBreakException.class);
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        }
        assertNull("Dado piazzato ma restrizioni non verificate",wf.getCell(7).getDice());


        assertEquals("Dado non piazzato ma rimosso dalla draft",draftDice,draft.getDraft().get(0));

        try {
            tc11_KO2.useToolCard();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            assertEquals("Verifica cella vuota non eseguita",e.getMessage(),"Cell already with dice");
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals("Dado piazzato su cella occupata",wf.getCell(16).getDice(),d2);

        assertEquals("Dado non piazzato ma rimosso dalla draft",draftDice,draft.getDraft().get(0));

        //test funzionamento corretto
        ToolCard11 tc11_OK = new ToolCard11(wf,draft,17);
        try {
            tc11_OK.useToolCard();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        }
    }

}
