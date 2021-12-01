package progettoIngSW;


import org.junit.Test;
import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Model.Cell;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Dice;

import java.util.Random;

import static org.junit.Assert.*;

public class CellTest {


    //Testing setDice() and removeDice() with ColorRestriction
    @Test
    public void checkColorRestriction() throws Exception {
        for (int col = 0; col < Colors.values().length; col++) {
            Cell cell = new Cell(Colors.values()[col], 0);
            Dice dice = new Dice(Colors.values()[col]);
            try {
                cell.setDice(dice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            assertEquals("Color restriction non conincidono", cell.getColorRestriction(), cell.getDice().getColor());
            cell.removeDice();
            assertNull("Dado non rimosso", cell.getDice());
            try {
                cell.removeDice();
            } catch (Exception e) {
                assertEquals("Rimosso un dado non esistente",DiceNotFoundException.class,e.getClass());
            }
        }
    }


    @Test
    public void testCellWithDice() {
        Cell cell = new Cell(Colors.WHITE, 0);
        Dice d1 = new Dice(Colors.GREEN);
        Dice d2 = new Dice(Colors.BLUE);
        try {
            cell.setDice(d1);
            cell.setDice(d2);
        } catch (CellNotEmptyException e) {
            assertEquals("Assegnato un dado ad una cella gia' occupata",e.getMessage(),"Cell already with dice");
        }
    }

    @Test
    public void checkNumberRestriction() throws Exception {
        Random colGen = new Random();
        for (int numR = 1; numR <= 6; ++numR) {
            Cell c = new Cell(Colors.WHITE, numR);
            Dice d = new Dice(Colors.values()[colGen.nextInt(5)+1]);
            d.setNumber(numR);
            try {
                c.setDice(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
            assertEquals("Value restriction non coincidono", c.getNumberRestriction(), c.getDice().getNumber());
            c.removeDice();
            assertNull("Dado non rimosso", c.getDice());
            try {
                c.removeDice();
            } catch (Exception e) {
                assertEquals("Rimosso un dado non esistente",DiceNotFoundException.class,e.getClass());
            }

        }
    }



}
