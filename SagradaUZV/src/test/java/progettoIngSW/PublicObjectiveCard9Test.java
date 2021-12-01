package progettoIngSW;

import static org.junit.Assert.*;
import org.junit.Test;
import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.Model.*;

import java.io.IOException;

public class PublicObjectiveCard9Test {

    @Test
    public void checkPublicCard9() throws RulesBreakException, IOException {

        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);

        //Calcolo punteggio con wf vuota
        PublicObjectiveCard9 card9_zero = new PublicObjectiveCard9();
        assertEquals("Calcolo punteggio sbagliato", 0, card9_zero.scoringCalc(wf));


        Dice d2 = new Dice(Colors.BLUE);
        Dice d3 = new Dice(Colors.GREEN);
        Dice d4 = new Dice(Colors.PURPLE);
        Dice d5 = new Dice(Colors.YELLOW);
        Dice d6 = new Dice(Colors.GREEN);
        Dice d7 = new Dice(Colors.PURPLE);
        Dice d8 = new Dice(Colors.BLUE);
        Dice d9 = new Dice(Colors.GREEN);
        Dice d10 = new Dice(Colors.BLUE);
        Dice d11 = new Dice(Colors.RED);
        Dice d12 = new Dice(Colors.YELLOW);
        Dice d13 = new Dice(Colors.RED);
        Dice d14 = new Dice(Colors.PURPLE);

        Dice d16 = new Dice(Colors.BLUE);
        Dice d17 = new Dice(Colors.RED);
        Dice d18 = new Dice(Colors.GREEN);
        Dice d19 = new Dice(Colors.BLUE);
        Dice d20 = new Dice(Colors.YELLOW);

        d2.setNumber(2);
        d3.setNumber(3);
        d4.setNumber(4);
        d5.setNumber(1);
        d6.setNumber(2);
        d7.setNumber(4);
        d8.setNumber(5);
        d9.setNumber(1);
        d10.setNumber(4);
        d11.setNumber(3);
        d12.setNumber(1);
        d13.setNumber(2);
        d14.setNumber(3);

        d16.setNumber(2);
        d17.setNumber(6);
        d18.setNumber(5);
        d19.setNumber(2);
        d20.setNumber(3);
        try {

            wf.placeDice(d2, 1);
            wf.placeDice(d3, 2);
            wf.placeDice(d4, 3);
            wf.placeDice(d5, 4);
            wf.placeDice(d6, 5);
            wf.placeDice(d7, 6);
            wf.placeDice(d8, 7);
            wf.placeDice(d9, 8);
            wf.placeDice(d10, 9);
            wf.placeDice(d11, 10);
            wf.placeDice(d12, 11);
            wf.placeDice(d13, 12);
            wf.placeDice(d14, 13);

            wf.placeDice(d16, 15);
            wf.placeDice(d17, 16);
            wf.placeDice(d18, 17);
            wf.placeDice(d20, 19);
            wf.placeDice(d19, 18);
        }catch(RulesBreakException | CellNotEmptyException e){
           e.printStackTrace();
        }

        PublicObjectiveCard9 card9 = new PublicObjectiveCard9();
        assertEquals("Calcolo punteggio sbagliato", 7, card9.scoringCalc(wf));

    }
}
