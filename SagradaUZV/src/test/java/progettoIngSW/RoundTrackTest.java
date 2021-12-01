package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Dice;
import progettoIngSW.Model.RoundTrack;

import static org.junit.Assert.*;

public class RoundTrackTest {

    @Test
    public void checkAddDice(){
         RoundTrack roundTrack = new RoundTrack();
         Dice d = new Dice(Colors.GREEN);
         roundTrack.addDice(1, d);
         assertTrue("Insertion failed", roundTrack.getDice(1).contains(d));
    }

    @Test
    public void checkRemoveDice(){
        RoundTrack roundTrack = new RoundTrack();
        Dice d = new Dice(Colors.GREEN);
        roundTrack.addDice(1, d);
        Dice d2 = roundTrack.removeDice(1, 0);
        assertEquals("Different objects", d, d2);
    }
}
