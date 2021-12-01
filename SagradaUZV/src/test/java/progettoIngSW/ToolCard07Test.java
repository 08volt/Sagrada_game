package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Dice;
import progettoIngSW.Model.DraftPool;
import progettoIngSW.Model.ToolCard07;

import static org.junit.Assert.*;


public class ToolCard07Test {

    @Test
    public void checkTool07() throws DiceNotFoundException {

        DraftPool dp = new DraftPool();
        dp.setNumPlayer(2);
        dp.generateDraft();
        int length = dp.getDraft().size();


        ToolCard07 tool7 = new ToolCard07(dp);
        while (dp.getDraft().size() > 0) {
            Colors c = dp.getDraft().get(0).getColor();
            tool7.useToolCard();
            assertEquals("lunghezza sbagliata", length, dp.getDraft().size());
            assertEquals("Colore dado cambiato", c, dp.getDraft().get(0).getColor());
            dp.removeDice(0);
            length = dp.getDraft().size();
        }

    }
}
