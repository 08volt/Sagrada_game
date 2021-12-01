package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.InvalidParamsException;
import progettoIngSW.Model.Dice;
import progettoIngSW.Model.DraftPool;
import progettoIngSW.Model.ToolCard01;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ToolCard01Test {

    @Test
    public void shouldIncrementNumber() throws InvalidParamsException {
        Random poolPosGen = new Random();
        int poolPos = 0;
        for (int numP = 2; numP <= 4; numP++) {
            DraftPool dp = new DraftPool();
            dp.setNumPlayer(numP);
            poolPos = poolPosGen.nextInt((2 * numP)+1);
            dp.generateDraft();
            Dice d = dp.getDraft().get(poolPos);
            ToolCard01 tc1 = new ToolCard01(dp, poolPos, true);
            for (int numDice = 1; numDice < 6; numDice++) {
                d.setNumber(numDice);
                tc1.useToolCard();
                assertEquals("Incremento permesso non avvenuto", numDice + 1, d.getNumber());
            }
        }
    }

    @Test
    public void shouldNotIncrementNumber() {
        Random poolPosGen = new Random();
        int poolPos = 0;
        for(int numP = 2;numP<=4;numP++) {
            DraftPool dp = new DraftPool();
            dp.setNumPlayer(numP);
            poolPos = poolPosGen.nextInt((2 * numP)+1);
            dp.generateDraft();
            Dice d = dp.getDraft().get(poolPos);
            ToolCard01 tc1 = new ToolCard01(dp, poolPos, true);
            d.setNumber(6);
            try {
                tc1.useToolCard();
            } catch (InvalidParamsException e) {
                assertEquals("Incremento eseguto ma numero dado = 6", 6,d.getNumber());
            }
        }
    }

    @Test
    public void shouldDecrementNumber() throws InvalidParamsException {
        Random poolPosGen = new Random();
        int poolPos = 0;
        for (int numP = 2; numP <= 4; numP++) {
            DraftPool dp = new DraftPool();
            dp.setNumPlayer(numP);
            poolPos = poolPosGen.nextInt((2 * numP)+1);
            dp.generateDraft();
            Dice d = dp.getDraft().get(poolPos);
            ToolCard01 tc1 = new ToolCard01(dp, poolPos, false);
            for (int numDice = 2; numDice <=6; numDice++) {
                d.setNumber(numDice);
                tc1.useToolCard();
                assertEquals("Decrease not happened", numDice - 1, d.getNumber());
            }
        }
    }
    @Test
    public void shouldNotDecrementNumber() {
        Random poolPosGen = new Random();
        int poolPos = 0;
        for(int numP = 2;numP<=4;numP++) {
            DraftPool dp = new DraftPool();
            dp.setNumPlayer(numP);
            poolPos = poolPosGen.nextInt((2 * numP)+1);
            dp.generateDraft();
            Dice d = dp.getDraft().get(poolPos);
            ToolCard01 tc1 = new ToolCard01(dp, poolPos, false);
            d.setNumber(1);
            try {
                tc1.useToolCard();
            } catch (InvalidParamsException e) {
                assertEquals("Decrease happened but dice = 1", 1,d.getNumber());
            }
        }
    }
}

