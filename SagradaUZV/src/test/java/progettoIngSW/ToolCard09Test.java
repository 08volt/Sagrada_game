package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class ToolCard09Test {

    @Test
    public void checkColorRestriction() throws DiceNotFoundException, DraftFullException, CellNotEmptyException, IOException {

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
            wf.placeDice(d2, 16);
            wf.placeDice(d1, 15);
            wf.placeDice(d3, 11);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }

        Dice draftDice = new Dice(Colors.BLUE);
        draftDice.setNumber(3);
        draft.addDice(draftDice);
        ToolCard09 tool09_KO1 = new ToolCard09(draft, 0, wf, 0);  //test uso NON corretto tool
        ToolCard09 tool09_OK = new ToolCard09(draft, 0, wf, 18);  //test uso corretto tool
        try{
            tool09_KO1.useToolCard();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione colore non verificata",e.getMessage(),"COLOR Restriction Not Matched");
        }

        try{
            tool09_OK.useToolCard();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Posizionamento consentito non avvenuto", wf.getCell(18).getDice(),draftDice);
        assertEquals("Dado draft non rimosso",draft.getDraft().size(),0);
    }

    @Test
    public void checkValueRestriction() throws DiceNotFoundException, DraftFullException, CellNotEmptyException, IOException {

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
            wf.placeDice(d3, 11);
            wf.placeDice(d2, 16);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }


        Dice draftDice = new Dice(Colors.BLUE);
        draftDice.setNumber(1);
        draft.addDice(draftDice);
        ToolCard09 tool09_KO = new ToolCard09(draft, 0, wf, 9);  //test uso NON corretto tool
        ToolCard09 tool09_OK = new ToolCard09(draft, 0, wf, 4);  //test uso corretto tool
        try{
            tool09_KO.useToolCard();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione valore non verificata",e.getMessage(),"NUMBER Restriction Not Matched");
        }

        try{
            tool09_OK.useToolCard();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Posizionamento consentito non avvenuto", wf.getCell(4).getDice(),draftDice);
        assertEquals("Dado draft non rimosso",draft.getDraft().size(),0);
    }

    @Test
    public void checkAdjacencyRestriction() throws  DraftFullException, IOException {

        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        DraftPool draft = new DraftPool();
        Dice d1 = new Dice(Colors.PURPLE);
        d1.setNumber(4);
        try {
            wf.placeDice(d1, 16);
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }


        Dice draftDice = new Dice(Colors.RED);
        draftDice.setNumber(3);
        draft.addDice(draftDice);

        //test uso NON corretto tool (adiacenza sopra sx)
        ToolCard09 tool09_KO1 = new ToolCard09(draft, 0, wf, 10);
        try{
            tool09_KO1.useToolCard();
        } catch (NotValidCellException e) {
            assertEquals("Posizionamento avvenuto con dado adiacente (sopra sx)",NotValidCellException.class,e.getClass());
        } catch (RulesBreakException | DiceNotFoundException | CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertTrue("Dado rimosso dal draft con uso tool non corretto",draft.getDraft().contains(draftDice));
        assertNull("Dado piazzato vicino ad un altro dado",wf.getCell(10).getDice());
        assertEquals("Numero dadi su wf maggiore dopo uso non corretto tool",1,wf.numberOfDice());


        //test uso NON corretto tool (adicenza sopra)
        ToolCard09 tool09_KO2 = new ToolCard09(draft, 0, wf, 11);
        try{
            tool09_KO2.useToolCard();
        } catch (NotValidCellException e) {
            assertEquals("Posizionamento avvenuto con dado adiacente (sopra)",NotValidCellException.class,e.getClass());
        } catch (RulesBreakException | DiceNotFoundException | CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertTrue("Dado rimosso dal draft con uso tool non corretto",draft.getDraft().contains(draftDice));
        assertNull("Dado piazzato vicino ad un altro dado",wf.getCell(11).getDice());
        assertEquals("Numero dadi su wf maggiore dopo uso non corretto tool",1,wf.numberOfDice());


        //test uso NON corretto tool (adicenza sopra dx)
        ToolCard09 tool09_KO3 = new ToolCard09(draft, 0, wf, 12);
        try{
            tool09_KO3.useToolCard();
        } catch (NotValidCellException e) {
            assertEquals("Posizionamento avvenuto con dado adiacente (sopra dx)",NotValidCellException.class,e.getClass());
        } catch (RulesBreakException | DiceNotFoundException | CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertTrue("Dado rimosso dal draft con uso tool non corretto",draft.getDraft().contains(draftDice));
        assertNull("Dado piazzato vicino ad un altro dado",wf.getCell(12).getDice());
        assertEquals("Numero dadi su wf maggiore dopo uso non corretto tool",1,wf.numberOfDice());


        draft.getDraft().get(0).setNumber(2);
        draftDice = draft.getDraft().get(0);

        //test uso NON corretto tool (adicenza sx)
        ToolCard09 tool09_KO4 = new ToolCard09(draft, 0, wf, 15);
        try{
            tool09_KO4.useToolCard();
        } catch (NotValidCellException e) {
            assertEquals("Posizionamento avvenuto con dado adiacente (sx)",NotValidCellException.class,e.getClass());
        } catch (RulesBreakException | DiceNotFoundException | CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertTrue("Dado rimosso dal draft con uso tool non corretto",draft.getDraft().contains(draftDice));
        assertNull("Dado piazzato vicino ad un altro dado",wf.getCell(15).getDice());
        assertEquals("Numero dadi su wf maggiore dopo uso non corretto tool",1,wf.numberOfDice());


        //test uso NON corretto tool (adicenza dx)
        ToolCard09 tool09_KO5 = new ToolCard09(draft, 0, wf, 17);
        try{
            tool09_KO5.useToolCard();
        } catch (NotValidCellException e) {
            assertEquals("Posizionamento avvenuto con dado adiacente (dx)",NotValidCellException.class,e.getClass());
        } catch (RulesBreakException | DiceNotFoundException | CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertTrue("Dado rimosso dal draft con uso tool non corretto",draft.getDraft().contains(draftDice));
        assertNull("Dado piazzato vicino ad un altro dado",wf.getCell(17).getDice());
        assertEquals("Numero dadi su wf maggiore dopo uso non corretto tool",1,wf.numberOfDice());


        try {
            wf.removeDice(16);
            wf.placeDice(d1,2);
        } catch (DiceNotFoundException | CellNotEmptyException | RulesBreakException e) {
            e.printStackTrace();
        }
        draft.getDraft().get(0).setNumber(5);
        draftDice = draft.getDraft().get(0);


        //test uso NON corretto tool (adicenza sotto sx)
        ToolCard09 tool09_KO6 = new ToolCard09(draft, 0, wf, 6);
        try{
            tool09_KO6.useToolCard();
        } catch (NotValidCellException e) {
            assertEquals("Posizionamento avvenuto con dado adiacente (sotto sx)",NotValidCellException.class,e.getClass());
        } catch (RulesBreakException | DiceNotFoundException | CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertTrue("Dado rimosso dal draft con uso tool non corretto",draft.getDraft().contains(draftDice));
        assertNull("Dado piazzato vicino ad un altro dado",wf.getCell(6).getDice());
        assertEquals("Numero dadi su wf maggiore dopo uso non corretto tool",1,wf.numberOfDice());


        //test uso NON corretto tool (adicenza sotto)
        ToolCard09 tool09_KO7 = new ToolCard09(draft, 0, wf, 7);
        try{
            tool09_KO7.useToolCard();
        } catch (NotValidCellException e) {
            assertEquals("Posizionamento avvenuto con dado adiacente (sotto)",NotValidCellException.class,e.getClass());
        } catch (RulesBreakException | DiceNotFoundException | CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertTrue("Dado rimosso dal draft con uso tool non corretto",draft.getDraft().contains(draftDice));
        assertNull("Dado piazzato vicino ad un altro dado",wf.getCell(7).getDice());
        assertEquals("Numero dadi su wf maggiore dopo uso non corretto tool",1,wf.numberOfDice());


        //test uso NON corretto tool (adicenza sotto dx)
        ToolCard09 tool09_KO8 = new ToolCard09(draft, 0, wf, 8);
        try{
            tool09_KO8.useToolCard();
        } catch (NotValidCellException e) {
            assertEquals("Posizionamento avvenuto con dado adiacente (sotto dx)",NotValidCellException.class,e.getClass());
        } catch (RulesBreakException | DiceNotFoundException | CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertTrue("Dado rimosso dal draft con uso tool non corretto",draft.getDraft().contains(draftDice));
        assertNull("Dado piazzato vicino ad un altro dado",wf.getCell(8).getDice());
        assertEquals("Numero dadi su wf maggiore dopo uso non corretto tool",1,wf.numberOfDice());
    }

}
