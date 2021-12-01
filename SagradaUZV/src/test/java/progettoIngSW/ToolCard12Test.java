package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;

import java.io.IOException;

import static org.junit.Assert.*;

public class ToolCard12Test {

    @Test
    public void shouldNotMoveTwoDices() throws IOException {
        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        Dice d1 = new Dice(Colors.YELLOW);
        Dice d2 = new Dice(Colors.GREEN);
        Dice d3 = new Dice(Colors.BLUE);
        Dice d4 = new Dice(Colors.RED);
        Dice d5 = new Dice(Colors.BLUE);
        Dice d6 = new Dice(Colors.YELLOW);
        d1.setNumber(5);
        d2.setNumber(2);
        d3.setNumber(4);
        d4.setNumber(5);
        d5.setNumber(6);
        d6.setNumber(1);
        try {
            wf.placeDice(d1, 0);
            wf.placeDice(d2, 5);
            wf.placeDice(d3, 1);
            wf.placeDice(d4, 6);
            wf.placeDice(d5, 11);
            wf.placeDice(d6, 16);
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }

        RoundTrack track = new RoundTrack();
        track.addDice(1, new Dice(Colors.RED));
        int[] pos = {1, 2, 6, 3};         //primo dado colore non presente nel track
        int[] pos2 = {6, 7, 1, 11};         //secondo dado colore non presente nel track
        ToolCard12 tool12 = new ToolCard12(wf, track, pos);
        ToolCard12 tool12_2 = new ToolCard12(wf, track, pos2);

        try {
            tool12.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidParamsException e) {
            assertEquals("Restrizione dado colore roundtrack non presente non verificata", e.getMessage(), "color");
        }

        try {
            tool12_2.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidParamsException e) {
            assertEquals("Restrizione dado colore roundtrack non presente non verificata", e.getMessage(), "color");
        }

        track.addDice(1, new Dice(Colors.BLUE));
        track.addDice(1, new Dice(Colors.GREEN));
        track.addDice(1, new Dice(Colors.YELLOW));

        //spostament sbagliati
        int[] pos3 = {1, 3, 11, 8};         //restrizione dadi adicenti non rispettata
        int[] pos4 = {16, 12, 0, 2};       //restrizione colore non rispettata
        int[] pos5 = {0, 7, 16, 0};         //restrizione valori adiacenti uguali non rispettata
        int[] pos6 = {0, 1, 16, 2};         //restrizione cella primo spostamento non vuota
        int[] pos7 = {13, 3, 0, 8};         //Dado1 non presente
        int[] pos8 = {1, 2, 11, 0};         //restrizione cella secondo spostamento non vuota
        int[] pos9 = {1, 7, 11, 10};         //restrizione valore non rispettata
        int[] pos10 = {8, 13, 3, 1};           //La wf non contiene dadi nelle posizioni scelte
        int[] pos12 = {16,2,3,13};            //Dado2 non presente

        ToolCard12 tool12_3 = new ToolCard12(wf, track, pos3);
        ToolCard12 tool12_4 = new ToolCard12(wf, track, pos4);
        ToolCard12 tool12_5 = new ToolCard12(wf, track, pos5);
        ToolCard12 tool12_6 = new ToolCard12(wf, track, pos6);
        ToolCard12 tool12_7 = new ToolCard12(wf, track, pos7);
        ToolCard12 tool12_8 = new ToolCard12(wf, track, pos8);
        ToolCard12 tool12_9 = new ToolCard12(wf, track, pos9);
        ToolCard12 tool12_10 = new ToolCard12(wf,track,pos10);
        ToolCard12 tool12_12 = new ToolCard12(wf,track,pos12);


        try {
            tool12_3.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione dadi adicenti non verificata", e.getMessage(), "ADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }

        try {
            tool12_4.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione colore non verificata", e.getMessage(), "COLOR Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }

        try {
            tool12_5.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione valori adiacenti uguali non verificata", e.getMessage(), "VALADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }


        try {
            tool12_6.useToolCard();
        } catch (RulesBreakException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            assertEquals("Restrizione cella non vuota", e.getMessage(), "Cell already with dice");
        }

        try {
            tool12_7.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | InvalidParamsException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            assertEquals("Problemi con DiceNotFound Exception", e.getMessage(), "DICE NOT FOUND");
        }

        try {
            tool12_8.useToolCard();
        }  catch (RulesBreakException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            assertEquals("Restrizione cella non vuota", e.getMessage(), "Cell already with dice");
        }

        Dice d7 = new Dice(Colors.RED);
        d7.setNumber(3);
        try {
            wf.placeDice(d7, 10);
        } catch (CellNotEmptyException | RulesBreakException e) {
            e.printStackTrace();
        }


        try {
            tool12_9.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione valore non verificata", e.getMessage(), "NUMBER Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }

        try {
            tool12_10.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | InvalidParamsException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            assertEquals("Spostato un dado non presente", e.getMessage(), "DICE NOT FOUND");
        }

        try {
            tool12_12.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | InvalidParamsException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            assertEquals("Spostato un dado non presente", e.getMessage(), "DICE NOT FOUND");
        }

        int[] pos11 = {0, 2, 16, 3};         //restrizione colori adiacenti uguali non rispettata
        ToolCard12 tool12_11 = new ToolCard12(wf, track, pos11);
        try {
            tool12_11.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione colori adiacenti uguali non verificata", e.getMessage(), "COLADJACENCY Restriction Not Matched");
        }  catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldNotMoveDice() throws IOException {
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
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }

        RoundTrack track = new RoundTrack();
        track.addDice(1, new Dice(Colors.RED));
        int[] pos = {1, 2};         //primo dado colore non presente nel track
        ToolCard12 tool12 = new ToolCard12(wf, track, pos);

        try {
            tool12.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidParamsException e) {
            assertEquals("Restrizione dado colore roundtrack non presente non verificata", e.getMessage(), "color");
        }

        track.addDice(1, new Dice(Colors.BLUE));
        track.addDice(1, new Dice(Colors.GREEN));
        track.addDice(1, new Dice(Colors.YELLOW));

        //spostament sbagliati
        int[] pos3 = {1, 3};         //restrizione dadi adicenti non rispettata
        int[] pos4 = {0, 12};       //restrizione colore non rispettata
        int[] pos5 = {0, 7};         //restrizione valori adiacenti uguali non rispettata
        int[] pos6 = {0, 1};         //restrizione cella spostamento non vuota
        int[] pos7 = {13, 3};        //Dado non presente
        int[] pos8 = {1, 10};         //restrizione valore non rispettata

        ToolCard12 tool12_3 = new ToolCard12(wf, track, pos3);
        ToolCard12 tool12_4 = new ToolCard12(wf, track, pos4);
        ToolCard12 tool12_5 = new ToolCard12(wf, track, pos5);
        ToolCard12 tool12_6 = new ToolCard12(wf, track, pos6);
        ToolCard12 tool12_7 = new ToolCard12(wf, track, pos7);
        ToolCard12 tool12_8 = new ToolCard12(wf, track, pos8);


        try {
            tool12_3.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione dadi adicenti non verificata", e.getMessage(), "ADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }


        try {
            tool12_4.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione colore non verificata", e.getMessage(), "COLOR Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }

        try {
            tool12_5.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione valori adiacenti uguali non verificata", e.getMessage(), "VALADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }


        try {
            tool12_6.useToolCard();
        } catch (RulesBreakException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            assertEquals("Restrizione cella non vuota", e.getMessage(), "Cell already with dice");
        }

        try {
            tool12_7.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | InvalidParamsException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            assertEquals("Problemi con DiceNotFound Exception", e.getMessage(), "DICE NOT FOUND");
        }


        try {
            tool12_8.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione valore non verificata", e.getMessage(), "NUMBER Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }


        Dice d7 = new Dice(Colors.RED);
        d7.setNumber(3);
        try {
            wf.placeDice(d7, 10);
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }


        int[] pos10 = {6, 11};         //restrizione colori adiacenti uguali non rispettata
        ToolCard12 tool12_10 = new ToolCard12(wf, track, pos10);
        try {
            tool12_10.useToolCard();
        } catch (RulesBreakException e) {
            assertEquals("Restrizione colori adiacenti uguali non verificata", e.getMessage(), "COLADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException | DiceNotFoundException | InvalidParamsException e) {
            e.printStackTrace();
        }


    }


    @Test
    public void checkTwoDices() throws DiceNotFoundException, IOException {

        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        Dice d1 = new Dice(Colors.YELLOW);
        Dice d2 = new Dice(Colors.GREEN);
        Dice d3 = new Dice(Colors.BLUE);
        Dice d4 = new Dice(Colors.RED);
        Dice d5 = new Dice(Colors.BLUE);
        Dice d6 = new Dice(Colors.YELLOW);
        d1.setNumber(5);
        d2.setNumber(2);
        d3.setNumber(4);
        d4.setNumber(5);
        d5.setNumber(6);
        d6.setNumber(1);
        try {
            wf.placeDice(d6, 16);
            wf.placeDice(d5, 11);
            wf.placeDice(d4, 6);
            wf.placeDice(d3, 1);
            wf.placeDice(d2, 5);
            wf.placeDice(d1, 0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        RoundTrack track = new RoundTrack();
        track.addDice(1, new Dice(Colors.YELLOW));
        int[] pos = {16, 2, 0, 8};
        ToolCard12 tool12 = new ToolCard12(wf, track, pos);
        try {
            tool12.useToolCard();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        } catch (InvalidParamsException e) {
            e.printStackTrace();
        }

        assertEquals("Mossa consentita non avvenuta", wf.getCell(2).getDice(), d6);
        assertEquals("Mossa consentita non avvenuta", wf.getCell(8).getDice(), d1);


    }

    @Test
    public void checkOneDice() throws DiceNotFoundException, IOException {

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
            wf.placeDice(d4, 6);
            wf.placeDice(d3, 1);
            wf.placeDice(d2, 5);
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }

        RoundTrack track = new RoundTrack();
        track.addDice(1, new Dice(Colors.YELLOW));
        int[] pos = {0, 2};
        ToolCard12 tool12 = new ToolCard12(wf, track, pos);
        try {
            tool12.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | InvalidParamsException e) {
            e.printStackTrace();
        }

        assertEquals("Mossa consentita non avvenuta", wf.getCell(2).getDice(), d1);

    }

    @Test
    public void checkWrongHowMany() throws IOException {
        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        Dice d1 = new Dice(Colors.YELLOW);
        Dice d2 = new Dice(Colors.GREEN);
        Dice d3 = new Dice(Colors.BLUE);
        Dice d4 = new Dice(Colors.RED);
        d1.setNumber(4);
        d2.setNumber(1);
        d3.setNumber(5);
        d4.setNumber(3);
        try {
            wf.placeDice(d1, 0);
            wf.placeDice(d4, 6);
            wf.placeDice(d2, 5);
            wf.placeDice(d3, 1);
        } catch (RulesBreakException | CellNotEmptyException e) {
            e.printStackTrace();
        }

        RoundTrack track = new RoundTrack();
        track.addDice(1, new Dice(Colors.YELLOW));
        int[] pos = {0, 2, 1,3,5,4};
        ToolCard12 tool12 = new ToolCard12(wf, track, pos);
        try {
            tool12.useToolCard();
        } catch (RulesBreakException | CellNotEmptyException | DiceNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidParamsException e) {
            assertEquals("Lunghezza array posizioni non consentita ma tool utilizzata","howMany",e.getMessage());
        }

        assertEquals("Mossa non consentita avvenuta", wf.getCell(0).getDice(), d1);
        assertEquals("Mossa non consentita avvenuta", wf.getCell(5).getDice(), d2);
        assertEquals("Mossa non consentita avvenuta", wf.getCell(1).getDice(), d3);
        assertEquals("Mossa non consentita avvenuta", wf.getCell(6).getDice(), d4);
        assertEquals("Numero dadi cambiato dopo utilizzo non consentito",4,wf.numberOfDice());
    }

}