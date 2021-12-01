package progettoIngSW;

import static org.junit.Assert.*;

import org.junit.Test;
import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.Model.*;

import java.io.IOException;

public class ToolCard04Test {

    @Test
    public void checkToolCard4() throws IOException{
        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        Dice d1 = new Dice(Colors.YELLOW);
        Dice d2 = new Dice(Colors.GREEN);
        Dice d3 = new Dice(Colors.BLUE);
        Dice d4 = new Dice(Colors.RED);
        d1.setNumber(5);
        d2.setNumber(2);
        d3.setNumber(4);
        d4.setNumber(5);
        try {
            wf.placeDice(d1, 0);
            wf.placeDice(d2, 5);
            wf.placeDice(d3, 1);
            wf.placeDice(d4, 6);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        int[] pos = {1,2,6,8};

       //spostament sbagliati
        int[] pos2 = {1,2,5,7};         //restrizione valore non rispettata
        int[] pos3 = {1,3,6,8};         //restrizione dadi adicenti non rispettata
        int[] pos4 = {5,11,0,12};       //restrizione colore non rispettata
        int[] pos5 = {0,7,5,8};         //restrizione valori adiacenti uguali non rispettata
        int[] pos6 = {0,1,5,2};         //restrizione cella primo spostamento non vuota
        int[] pos7 = {15,16,17,18};     //Dado non presente
        int[] pos8 = {1,2,5,0};         //restrizione cella secondo spostamento non vuota

        ToolCard04 tool04 = new ToolCard04(wf, pos);
        ToolCard04 tool04_2 = new ToolCard04(wf, pos2);
        ToolCard04 tool04_3 = new ToolCard04(wf, pos3);
        ToolCard04 tool04_4 = new ToolCard04(wf, pos4);
        ToolCard04 tool04_5 = new ToolCard04(wf, pos5);
        ToolCard04 tool04_6 = new ToolCard04(wf, pos6);
        ToolCard04 tool04_7 = new ToolCard04(wf, pos7);
        ToolCard04 tool04_8 = new ToolCard04(wf, pos8);

        try{
            tool04_2.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione valore non verificata",e.getMessage(),"NUMBER Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        }

        try{
            tool04_3.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione dadi adicenti non verificata",e.getMessage(),"ADJACENCY Restriction Not Matched");
        }catch (CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        }

        try{
            tool04_4.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione colore non verificata",e.getMessage(),"COLOR Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        }

        try{
            tool04_5.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione valori adiacenti uguali non verificata",e.getMessage(),"VALADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        }


        try{
            tool04_6.useToolCard();
        } catch (RulesBreakException | DiceNotFoundException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            assertEquals("Restrizione cella non vuota",e.getMessage(),"Cell already with dice");
        }

        try{
            tool04_7.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            assertEquals("Problemi con DiceNotFound Exception",e.getMessage(),"DICE NOT FOUND");
        }

        try{
            tool04_8.useToolCard();
        } catch (RulesBreakException | DiceNotFoundException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            assertEquals("Restrizione cella non vuota",e.getMessage(),"Cell already with dice");
        }

        Dice d5 = new Dice(Colors.RED);
        d5.setNumber(3);
        try {
            wf.placeDice(d5,10);
        } catch (CellNotEmptyException | RulesBreakException e) {
            e.printStackTrace();
        }


        int[] pos9 = {0,2,6,11};         //restrizione colori adiacenti uguali non rispettata
        ToolCard04 tool04_9 = new ToolCard04(wf, pos9);
        try{
            tool04_9.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione colori adiacenti uguali non verificata",e.getMessage(),"COLADJACENCY Restriction Not Matched");
        }  catch (CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        }

        try{
            tool04.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals("Spostamento consentito non avvenuto",wf.getCell(2).getDice(),d3);
        assertEquals("Spostamento consentito non avvenuto",wf.getCell(8).getDice(),d4);
    }


}
