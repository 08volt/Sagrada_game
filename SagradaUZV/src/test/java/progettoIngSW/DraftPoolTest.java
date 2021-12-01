package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.DraftFullException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Dice;
import progettoIngSW.Model.DraftPool;

import java.util.Random;

import static org.junit.Assert.*;


public class DraftPoolTest {

    @Test
    public void checkGenerateDraftPool() {
        for(int numP=2;numP<=4;numP++) {
            DraftPool draftPool = new DraftPool();
            draftPool.setNumPlayer(numP);
            draftPool.generateDraft();
            assertEquals("Lunghezza draft non corretta",(2*numP + 1), draftPool.getDraft().size());
            for (int i = 0; i < draftPool.getDraft().size(); i++) {
                assertTrue("Numero dadi nel draft non corretto", draftPool.getDraft().get(i).getNumber() <= 6 &&
                        draftPool.getDraft().get(i).getNumber() >= 1);
            }
        }
    }


    @Test
    public void shouldNotRemoveDice()  {
        for(int numP=2;numP<=4;numP++) {
            DraftPool draftPool = new DraftPool();
            draftPool.setNumPlayer(numP);
            try {
                draftPool.removeDice(0);
            } catch (Exception e) {
                assertEquals("Hai rimosso un dado non presente", 0, draftPool.getDraft().size());
                assertEquals("Rimosso un dado con draft vuota ",DiceNotFoundException.class,e.getClass());
            }
        }
    }

    @Test
    public void shouldRemoveDice() throws Exception {
        for (int numP = 2; numP <= 4; numP++) {
            DraftPool draftPool = new DraftPool();
            draftPool.setNumPlayer(numP);
            draftPool.generateDraft();
            Dice d = draftPool.removeDice(0);          //rimozione casuale in testa
            for(int i=0;i<draftPool.getDraft().size();i++)
                assertNotEquals("Dado non eliminato correttamente", d, draftPool.getDraft().get(i));
        }
    }



    @Test
    public void shouldNotAddDice() {
        Random colGen = new Random();
        for(int numP=2;numP<=4;numP++) {
            DraftPool draftPool = new DraftPool();
            draftPool.setNumPlayer(numP);
            Dice d = new Dice(Colors.values()[colGen.nextInt(5)+1]);
            draftPool.generateDraft();
            try {
                draftPool.addDice(d);
            }catch (Exception e){
                assertEquals("Aggiunto dadi con draft piena",(2*numP+1),draftPool.getDraft().size());
                assertEquals("Draft piena ma dado aggiunto",DraftFullException.class,e.getClass());
            }
        }

    }

    @Test
    public void shouldAddDice() throws Exception {
        Random colGen = new Random();
        Random remGen = new Random();
        int numRem=0;
        for(int numP=2;numP<=4;numP++) {
            numRem=0;
            DraftPool draftPool = new DraftPool();
            draftPool.setNumPlayer(numP);
            draftPool.generateDraft();
            while(numRem<(remGen.nextInt(numP)+1)) {
                draftPool.removeDice(0);        //rimozione casuale in testa
                numRem++;                           //draft.size() diminuita di numRem
            }
            assertEquals("Dado non rimosso",(2*numP+1-numRem),draftPool.getDraft().size());
            Dice d = new Dice(Colors.values()[colGen.nextInt(5) + 1]);
            draftPool.addDice(d);         //aggiunta casuale in testa
            assertEquals("Dado non aggiunto",(2*numP+2-numRem),draftPool.getDraft().size());
        }

    }

}
