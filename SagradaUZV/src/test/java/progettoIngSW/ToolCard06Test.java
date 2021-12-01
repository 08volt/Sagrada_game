package progettoIngSW;


import org.junit.Test;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ToolCard06Test {

    @Test
    public void checkTool06() throws IOException, DraftFullException, CellNotEmptyException{
        WindowFrame wf = new WindowFrame(new Pattern(0));
        DraftPool dp = new DraftPool();
        dp.addDice(new Dice(Colors.BLUE));
        ToolCard06 tc6 = new ToolCard06(wf,0,dp);
        try {
            tc6.useToolCard();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        }
        Dice d = dp.getDraft().get(0);
        assertEquals("Modificato colore dado",dp.getDraft().get(0).getColor(),Colors.BLUE);
        try {
            wf.placeDice(dp.getDraft().get(0),1);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Posizionamento consentito non avvenuto",wf.getCell(1).getDice(),d);
    }

    @Test
    public void shouldNotPlaceDice() throws IOException, DraftFullException {
        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        ArrayList<Dice> dices = new ArrayList<>();
        dices.add(new Dice(Colors.YELLOW));
        dices.add(new Dice(Colors.BLUE));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.PURPLE));
        dices.add(new Dice(Colors.YELLOW));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.PURPLE));
        dices.add(new Dice(Colors.BLUE));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.BLUE));
        dices.add(new Dice(Colors.RED));
        dices.add(new Dice(Colors.YELLOW));
        dices.add(new Dice(Colors.RED));
        dices.add(new Dice(Colors.PURPLE));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.BLUE));
        dices.add(new Dice(Colors.PURPLE));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.BLUE));
        dices.get(0).setNumber(1);
        dices.get(1).setNumber(2);
        dices.get(2).setNumber(3);
        dices.get(3).setNumber(4);
        dices.get(4).setNumber(1);
        dices.get(5).setNumber(2);
        dices.get(6).setNumber(4);
        dices.get(7).setNumber(5);
        dices.get(8).setNumber(1);
        dices.get(9).setNumber(4);
        dices.get(10).setNumber(3);
        dices.get(11).setNumber(1);
        dices.get(12).setNumber(2);
        dices.get(13).setNumber(3);
        dices.get(14).setNumber(6);
        dices.get(15).setNumber(2);
        dices.get(16).setNumber(6);
        dices.get(17).setNumber(5);
        dices.get(18).setNumber(2);
        for(int i=0;i<dices.size();i++) {
            try {
                wf.placeDice(dices.get(i), i);
            } catch (CellNotEmptyException e) {
                e.printStackTrace();
            } catch (RulesBreakException e) {
                e.printStackTrace();
            }
        }
        DraftPool dp = new DraftPool();
        dp.addDice(new Dice(Colors.BLUE));
        Dice d =dp.getDraft().get(0);
        ToolCard06 tc6 = new ToolCard06(wf,0,dp);
        try {
            tc6.useToolCard();
        } catch (NotValidCellException e) {
            assertNull("Posizionamento non consentito ma avvenuto",e.getMessage());
        }
        try {
            wf.placeDice(d,19);
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Posizionamento non consentito avvenuto",e.getClass(),RulesBreakException.class);
        }
    }
}
