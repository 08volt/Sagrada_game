package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.InvalidParamsException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.Model.*;


import java.io.IOException;

import static org.junit.Assert.*;


public class ToolCard02Test {

    @Test
    public void shouldIgnoreColorRestriction() throws IOException {
        Pattern p = new Pattern(0);
        WindowFrame w = new WindowFrame(p);
        Dice d1 = new Dice(Colors.BLUE);
        Dice d2 = new Dice(Colors.PURPLE);
        Dice d3 = new Dice(Colors.GREEN);
        d1.setNumber(2);
        d2.setNumber(6);
        d3.setNumber(5);
        try {
            w.placeDice(d1, 15);
            w.placeDice(d2, 16);
            w.placeDice(d3, 17);
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }

        ToolCard02 t = new ToolCard02(w, 16, 11);
        try {
            t.useToolCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("Mossa consentita ma non eseguita",w.getCell(11).getDice(),d2);
        assertNull("Mossa consentita ma non eseguita",w.getCell(16).getDice());

    }



    @Test
    public void checkValueRestriction() throws IOException {
        Pattern p = new Pattern(0);
        WindowFrame w = new WindowFrame(p);
        Dice d1 = new Dice(Colors.BLUE);
        d1.setNumber(2);
        try {
            w.placeDice(d1, 15);
        }  catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }


        ToolCard02 t = new ToolCard02(w, 15, 10);


        try {
            t.useToolCard();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Dado spostato con restrizione valore non rispettata",e.getMessage(),"NUMBER Restriction Not Matched");
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertEquals("Restrizione colore non rispettata ma spostamento eseguito",w.getCell(15).getDice(),d1);
        assertNull("Restrizione colore non rispettata ma spostamento eseguito",w.getCell(10).getDice());

    }


    @Test
    public void checkFirstDiceRestriction() throws IOException {
        Pattern p = new Pattern(0);
        WindowFrame w = new WindowFrame(p);
        Dice d1 = new Dice(Colors.YELLOW);
        d1.setNumber(3);
        try {
            w.placeDice(d1, 0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        ToolCard02 t = new ToolCard02(w, 0, 13);
        try {
            t.useToolCard();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Problemi con restrizione primo dado",e.getMessage(),"FIRSTDICE Restriction Not Matched");
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        assertEquals("Restrizione primo dado non rispettata ma spostamento eseguito",w.getCell(0).getDice(),d1);
        assertNull("Restrizione colore non rispettata ma spostamento eseguito",w.getCell(13).getDice());
    }

    @Test
    public void checkAdjacencyDiceRestriction() throws IOException {
        Pattern p = new Pattern(0);
        WindowFrame w = new WindowFrame(p);
        Dice d1 = new Dice(Colors.BLUE);
        Dice d2 = new Dice(Colors.RED);
        d1.setNumber(2);
        d2.setNumber(6);
        try {
            w.placeDice(d2, 16);
            w.placeDice(d1, 15);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        //verifica dadi non adiacenti
        ToolCard02 t1 = new ToolCard02(w, 16, 1);
        try {
            t1.useToolCard();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione dadi adiacenti non rispettata",e.getMessage(),"ADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertEquals("Restrizione dadi adiacenti non rispettata ma spostamento eseguito",w.getCell(16).getDice(),d2);
        assertNull("Restrizione dadi adiacenti non rispettata ma spostamento eseguito",w.getCell(1).getDice());




        Dice d3 = new Dice(Colors.GREEN);
        d3.setNumber(2);
        try {
            w.placeDice(d3, 11);
        }catch (RulesBreakException e){
            e.printStackTrace();
        }
        catch (CellNotEmptyException e){
            e.printStackTrace();
        }


        //verifica dadi adiacenti con stesso valore
        ToolCard02 t2 = new ToolCard02(w, 15, 12);
        try {
            t2.useToolCard();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Adiacenza per valore non rispettata",e.getMessage(),"VALADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertEquals("Adiacenza per valore non rispettata ma spostamento eseguito",w.getCell(15).getDice(),d1);
        assertNull("Adiacenza per valore non rispettata ma spostamento eseguito",w.getCell(12).getDice());



        Dice d4 = new Dice(Colors.GREEN);
        d4.setNumber(5);
        try {
            w.placeDice(d4, 5);
        }catch (RulesBreakException e){
            e.printStackTrace();
        }
        catch (CellNotEmptyException e){
            e.printStackTrace();
        }

        //verifica dadi adiacenti con stesso colore
        ToolCard02 t3 = new ToolCard02(w, 5, 12);
        try {
            t3.useToolCard();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Adiacenza per colore non rispettata",e.getMessage(),"COLADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertEquals("Adiacenza per colore non rispettata ma spostamento eseguito",w.getCell(5).getDice(),d4);
        assertNull("Adiacenza per colore non rispettata ma spostamento eseguito",w.getCell(12).getDice());
    }

    @Test
    public void checkCellNotEmpty() throws IOException {
        Pattern p = new Pattern(0);
        WindowFrame w = new WindowFrame(p);
        Dice d1 = new Dice(Colors.BLUE);
        Dice d2 = new Dice(Colors.RED);
        d1.setNumber(2);
        d2.setNumber(3);
        try {
            w.placeDice(d1, 15);
            w.placeDice(d2, 10);
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }

        //verifica cella non vuota
        ToolCard02 t = new ToolCard02(w, 15, 10);
        try {
            t.useToolCard();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            assertEquals("Verifica cella vuota non effettuata",e.getMessage(),"Cell already with dice");
        }
        assertEquals("Cella occupata ma spostamento eseguito",w.getCell(15).getDice(),d1);
        assertEquals("Cella occupata ma spostamento eseguito",w.getCell(10).getDice(),d2);
    }

    @Test
    public void checkWrongInput() throws IOException {
        Pattern p = new Pattern(0);
        WindowFrame w = new WindowFrame(p);
        Dice d1 = new Dice(Colors.BLUE);
        Dice d2 = new Dice(Colors.RED);
        d1.setNumber(2);
        d2.setNumber(3);
        try {
            w.placeDice(d1, 15);
            w.placeDice(d2, 10);
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }

        //verifica cella selezionata senza dado
        ToolCard02 t = new ToolCard02(w, 0, 12);
        try {
            t.useToolCard();
        } catch (DiceNotFoundException e) {
            assertEquals("Scelta cella senza dado",e.getMessage(),"DICE NOT FOUND");
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }
        assertNull("Spostato un dado non presente",w.getCell(0).getDice());
        assertNull("Spostato un dado non presente",w.getCell(12).getDice());
    }

}
