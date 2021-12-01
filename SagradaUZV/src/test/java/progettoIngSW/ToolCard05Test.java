package progettoIngSW;

import static org.junit.Assert.*;
import org.junit.Test;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.DraftFullException;
import progettoIngSW.Model.*;


public class ToolCard05Test {

    @Test
    public void checkTool05() throws DraftFullException {
        RoundTrack track = new RoundTrack();
        DraftPool dp = new DraftPool();
        dp.setNumPlayer(4);


        Dice d1 = new Dice(Colors.YELLOW);
        Dice d2 = new Dice(Colors.BLUE);
        Dice d3 = new Dice(Colors.GREEN);



        track.addDice(1, d1);
        track.addDice(2, d2);
        track.addDice(3, d3);

        ToolCard05 tool05_02 = new ToolCard05(dp,track,3,1,0);    //Dado draftpool non presente
        try {
            tool05_02.useToolCard();
        } catch (DiceNotFoundException e) {
            assertEquals("Rimosso dado da draft vuota",e.getClass(),DiceNotFoundException.class);
        }


        dp.generateDraft();


        ToolCard05 tool05_03 = new ToolCard05(dp,track,3,5,2);    //Dado track non presente
        try {
            tool05_03.useToolCard();
        } catch (DiceNotFoundException e) {
            assertEquals("Rimosso dado da track vuota",e.getClass(),DiceNotFoundException.class);
        }


        Dice d5 = dp.getDraft().get(3);

        ToolCard05 tool05 = new ToolCard05(dp,track,3,2,0);

        int draftLength = dp.getDraft().size();
        int trackLength = track.getDice(2).size();

        //useTool corretta
        try {
            tool05.useToolCard();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals("dimensione draft cambiata", draftLength, dp.getDraft().size());
        assertEquals("dimensione track cambiata", trackLength, track.getDice(2).size());

        assertTrue("switch sbagliato", dp.getDraft().contains(d2));
        assertTrue("switch sbagliato", track.getDice(2).contains(d5));


    }
}
