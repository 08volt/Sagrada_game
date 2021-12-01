package progettoIngSW;

import static org.junit.Assert.*;
import org.junit.Test;
import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.Model.*;

import java.io.IOException;

public class PublicObjectiveCard5Test {

    @Test
    public void checkPublicCard5() throws  IOException {

        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        Dice d1 = new Dice(Colors.YELLOW);
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
        Dice d15 = new Dice(Colors.GREEN);
        Dice d16 = new Dice(Colors.BLUE);
        Dice d17 = new Dice(Colors.PURPLE);
        Dice d18 = new Dice(Colors.GREEN);
        Dice d19 = new Dice(Colors.BLUE);
        Dice d20 = new Dice(Colors.YELLOW);
        d1.setNumber(1);
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
        d15.setNumber(6);
        d16.setNumber(2);
        d17.setNumber(6);
        d18.setNumber(5);
        d19.setNumber(2);
        d20.setNumber(3);
        try {
            wf.placeDice(d1, 0);
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
            wf.placeDice(d15, 14);
            wf.placeDice(d14, 13);
            wf.placeDice(d16, 15);
            wf.placeDice(d17, 16);
            wf.placeDice(d18, 17);
            wf.placeDice(d19, 18);
            wf.placeDice(d20, 19);
        }catch(RulesBreakException | CellNotEmptyException e){
            e.printStackTrace();
        }

        PublicObjectiveCard5 card5 = new PublicObjectiveCard5();
        assertEquals("Calcolo punteggio sbagliato", 8, card5.scoringCalc(wf));
    }
}
