package progettoIngSW;

import static org.junit.Assert.*;
import org.junit.Test;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.DraftFullException;
import progettoIngSW.Model.*;

public class ToolCard10Test {

    @Test
    public void checkTool10() throws DraftFullException, DiceNotFoundException {

        DraftPool draft = new DraftPool();
        draft.setNumPlayer(4);
        for (int i = 1; i <= 6; i++) {
            Dice d = new Dice(Colors.BLUE);
            d.setNumber(i);
            draft.addDice(d);
        }


        while (draft.getDraft().size() > 0) {
            ToolCard10 tool10 = new ToolCard10(draft, 0);
            int diceNum = draft.getDraft().get(0).getNumber();
            tool10.useToolCard();
            switch (diceNum) {
                case 1: {
                    assertEquals("funzionamento sbagliato", draft.getDraft().get(0).getNumber() ,6);
                    break;
                }
                case 2: {
                    assertEquals("funzionamento sbagliato", draft.getDraft().get(0).getNumber() , 5);
                    break;
                }
                case 3: {
                    assertEquals("funzionamento sbagliato", draft.getDraft().get(0).getNumber() , 4);
                    break;
                }
                case 4: {
                    assertEquals("funzionamento sbagliato", draft.getDraft().get(0).getNumber() , 3);
                    break;
                }
                case 5: {
                    assertEquals("funzionamento sbagliato", draft.getDraft().get(0).getNumber() , 2);
                    break;
                }
                case 6: {
                    assertEquals("funzionamento sbagliato", draft.getDraft().get(0).getNumber() , 1);
                    break;
                }
                default:
                    break;
            }
            draft.removeDice(0);
        }
    }

}
