package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.FullLobbyException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.Model.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ScoringTest {

    @Test
    public void checkScoring() throws RulesBreakException, CellNotEmptyException, IOException {

        Player player = new Player("caio","a");
        Pattern p = new Pattern(0);
        player.patternChoosen(p);
        PrivateObjectiveCard cardPrivate = new PrivateObjectiveCard(Colors.RED);
        player.setPrivateObjectiveCard(cardPrivate);
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
        player.getWindowFrame().placeDice(d1, 0);
        player.getWindowFrame().placeDice(d2, 1);
        player.getWindowFrame().placeDice(d3, 2);
        player.getWindowFrame().placeDice(d4, 3);
        player.getWindowFrame().placeDice(d5, 4);
        player.getWindowFrame().placeDice(d6, 5);
        player.getWindowFrame().placeDice(d7, 6);
        player.getWindowFrame().placeDice(d8, 7);
        player.getWindowFrame().placeDice(d9, 8);
        player.getWindowFrame().placeDice(d10, 9);
        player.getWindowFrame().placeDice(d11, 10);
        player.getWindowFrame().placeDice(d12, 11);
        player.getWindowFrame().placeDice(d13, 12);
        player.getWindowFrame().placeDice(d14, 13);
        player.getWindowFrame().placeDice(d15, 14);
        player.getWindowFrame().placeDice(d16, 15);
        player.getWindowFrame().placeDice(d17, 16);
        player.getWindowFrame().placeDice(d18, 17);
        player.getWindowFrame().placeDice(d19, 18);
        ArrayList<Integer> card = new ArrayList<>();
        card.add(1);
        card.add(2);
        card.add(3);
        Game.getGame().setPublicObjectiveCards(card);

        assertEquals("Wrong count1", 13, Scoring.calc(player));

        card.clear();
        //testing the results with other public objective cards
        card.add(4);
        card.add(5);
        card.add(6);
        assertEquals("Wrong count2", 30, Scoring.calc(player));

        card.clear();
        card.add(7);
        card.add(8);
        card.add(9);
        assertEquals("Wrong count3", 27, Scoring.calc(player));

        card.clear();
        card.add(1);
        card.add(10);
        card.add(5);
        assertEquals("Wrong count3", 24, Scoring.calc(player));
    }

    @Test
    public void checkScorePlayerWithoutWf(){
        Player p = new Player("topolino","password");
        Game.resetGame();
        Game g = Game.getGame();
        g.gameSetup();
        try {
            g.addPlayer(p);
        } catch (FullLobbyException e) {
            e.printStackTrace();
        }
        p.setWindowFrame(null);
        int score = Scoring.privateObjectiveScore(p);
        assertEquals("Player senza wf ha un punteggio diverso da 0",score,0);
        score = Scoring.calc(p);
        assertEquals("Player senza wf ha un punteggio diverso da 0",score,0);

    }

}
