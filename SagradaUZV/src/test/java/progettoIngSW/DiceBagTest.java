package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Dice;
import progettoIngSW.Model.DiceBag;

import static org.junit.Assert.*;

public class DiceBagTest {

    @Test
    public void shouldExtractDice() {

        DiceBag diceBag = new DiceBag();
        for(int i = diceBag.getDiceBag().size(); i > 0; i--){

            diceBag.extractDice();
            assertEquals("Problemi con estrazione dadi", i-1, diceBag.getDiceBag().size());
        }

    }

    @Test
    public void shouldAddDice(){
        DiceBag db = new DiceBag();
        int sizeDB = db.getDiceBag().size();
        db.putDice(new Dice(Colors.BLUE));
        assertEquals("Dado non aggiunto",(sizeDB+1),db.getDiceBag().size());
    }

    @Test
    public void shouldNotExtractDice() {
        DiceBag bag = new DiceBag();
        while (!bag.getDiceBag().isEmpty())
            bag.getDiceBag().remove(0);

        assertNull("Dado estratto da sacchetto vuoto",bag.extractDice());
    }
}
