package progettoIngSW;

import static org.junit.Assert.*;

import org.junit.Test;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Dice;


public class DiceTest
{

    @Test
    public void shouldGenerateDice(){

        Dice dice = new Dice(Colors.BLUE);
        assertTrue("Dice not valid", dice.getNumber() >= 1 && dice.getNumber() <= 6);
    }

    @Test
    public void shouldGenerateCorrectNumber(){

        Dice dice = new Dice(Colors.BLUE);
        dice.generateNumber();
        assertTrue("Wrong number", dice.getNumber() >= 1 && dice.getNumber() <= 6);
    }
}
