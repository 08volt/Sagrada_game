package progettoIngSW;

import static org.junit.Assert.*;

import org.junit.Test;
import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.Model.*;

import java.io.IOException;
import java.util.ArrayList;

public class WindowFrameTest {

    @Test
    public void checkInit() throws IOException {
        for(int numPattern=0;numPattern<24;numPattern++) {
            Pattern p = new Pattern(numPattern);
            WindowFrame wf = new WindowFrame(p);
            assertNotNull("inizializzazione sbagliata", wf);
            assertEquals("altezza sbagliata", 4, wf.getRow());
            assertEquals("lunghezza sbagliata", 5, wf.getCol() );
        }

    }

    @Test
    public void checkFirstDice() throws IOException{

        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        Dice d = new Dice(Colors.YELLOW);
        try {
            wf.placeDice(d,0);
            assertEquals("Cella scelta sul bordo ma dado non piazzato",d,wf.getCell(0).getDice());
            assertEquals("Cella scelta sul bordo ma dado non piazzato",1, wf.numberOfDice());
            wf.removeDice(0);

            wf.placeDice(d,2);
            assertEquals("Cella scelta sul bordo ma dado non piazzato",d,wf.getCell(2).getDice());
            assertEquals("Cella scelta sul bordo ma dado non piazzato",1, wf.numberOfDice());
            wf.removeDice(2);

            d.setNumber(1);
            wf.placeDice(d,4);
            assertEquals("Cella scelta sul bordo ma dado non piazzato",d,wf.getCell(4).getDice());
            assertEquals("Cella scelta sul bordo ma dado non piazzato",1, wf.numberOfDice());
            wf.removeDice(4);

            d.setNumber(4);
            wf.placeDice(d,9);
            assertEquals("Cella scelta sul bordo ma dado non piazzato",d,wf.getCell(9).getDice());
            assertEquals("Cella scelta sul bordo ma dado non piazzato",1, wf.numberOfDice());
            wf.removeDice(9);

            d.setNumber(3);
            wf.placeDice(d,10);
            assertEquals("Cella scelta sul bordo ma dado non piazzato",d,wf.getCell(10).getDice());
            assertEquals("Cella scelta sul bordo ma dado non piazzato",1, wf.numberOfDice());
            wf.removeDice(10);

            d.setNumber(2);
            wf.placeDice(d,15);
            assertEquals("Cella scelta sul bordo ma dado non piazzato",d,wf.getCell(15).getDice());
            assertEquals("Cella scelta sul bordo ma dado non piazzato",1, wf.numberOfDice());
            wf.removeDice(15);

            wf.placeDice(d,17);
            assertEquals("Cella scelta sul bordo ma dado non piazzato",d,wf.getCell(17).getDice());
            assertEquals("Cella scelta sul bordo ma dado non piazzato",1, wf.numberOfDice());
            wf.removeDice(17);

            wf.placeDice(d,19);
            assertEquals("Cella scelta sul bordo ma dado non piazzato",d,wf.getCell(19).getDice());
            assertEquals("Cella scelta sul bordo ma dado non piazzato",1, wf.numberOfDice());
            wf.removeDice(19);
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        }



        try {
            wf.placeDice(d,13);
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Restrzione firstDice non controllata",e.getMessage(),"FIRSTDICE Restriction Not Matched");
        }
        assertNull("Il primo dado deve essere piazzato sul bordo",wf.getCell(13).getDice());
        assertEquals("Il primo dado deve essere piazzato sul bordo",0, wf.numberOfDice());






    }

    @Test
    public void checkNumberOfDice() throws IOException {

        for(int numPattern=0;numPattern<24;numPattern++) {
            Pattern p = new Pattern(numPattern);
            WindowFrame wf = new WindowFrame(p);
            assertEquals("errore", 0, wf.numberOfDice());
        }

    }

    @Test
    public void checkPlaceDice() throws IOException, RulesBreakException, CellNotEmptyException {//, CellNotEmptyException, RulesBreakException {

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
        dices.add(new Dice(Colors.YELLOW));
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
        dices.get(19).setNumber(3);
        for(int i=0;i<20;i++) {
            if(i>0 && i<18)
                Rules.checkCell(wf,dices.get(i),i);
            wf.placeDice(dices.get(i), i);
            assertEquals("Posizionamento sbagliato dado"+ (i + 1), dices.get(i),wf.getCell(i).getDice());
        }
        assertEquals("Numero dadi non corretto",20,wf.numberOfDice());
    }

    @Test
    public void shouldNotPlaceDice() throws IOException{
        Pattern p = new Pattern(1);
        WindowFrame wf = new WindowFrame(p);
        Dice d1 = new Dice(Colors.PURPLE);
        Dice d2 = new Dice(Colors.BLUE);
        d1.setNumber(6);
        d2.setNumber(6);
        try {
            wf.placeDice(d1,1);
            wf.placeDice(d2,0);
        } catch (RulesBreakException e) {
            assertEquals("Eccezione dadi adiacenti con stesso valore non verificata",e.getMessage(),"VALADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        Dice d3 = new Dice(Colors.PURPLE);
        d3.setNumber(3);
        try {
            wf.placeDice(d3,0);
        } catch (RulesBreakException e) {
            assertEquals("Eccezione dadi adiacenti con stesso colore non verificata",e.getMessage(),"COLADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        d2.setNumber(3);
        Dice d4 = new Dice(Colors.YELLOW);
        d4.setNumber(3);
        try {
            wf.placeDice(d2,7);
            wf.placeDice(d4,2);
        } catch (RulesBreakException e) {
            assertEquals("Eccezione dadi adiacenti con stesso valore non verificata",e.getMessage(),"VALADJACENCY Restriction Not Matched");
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void checkRemoveDice() throws RulesBreakException, IOException {

        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        Dice d1 = new Dice(Colors.YELLOW);
        Dice d2 = new Dice(Colors.BLUE);
        d1.setNumber(1);
        d2.setNumber(2);
        try {
            wf.placeDice(d1, 0);
            wf.placeDice(d2, 1);
        } catch (CellNotEmptyException e) {
            fail(e.getMessage());
        }
        assertEquals("numero dadi sbagliato", 2, wf.numberOfDice());
        try{
            wf.removeDice(1);

        }catch (Exception e){
            fail("errore rimozione dado");
        }
        assertEquals("numero dadi dopo rimozione sbagliato", 1, wf.numberOfDice());
    }

    @Test
    public void checkMoveDice() throws IOException {
        Pattern p = new Pattern(0);
        WindowFrame wf = new WindowFrame(p);
        Dice d1 = new Dice(Colors.YELLOW);
        Dice d2 = new Dice(Colors.BLUE);
        d1.setNumber(4);
        d2.setNumber(4);
        try{
            wf.placeDice(d1, 3);
            wf.placeDice(d2, 9);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        }

        //test spostamento non consentito
        try {
            wf.moveDice(9,8);
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Restrizioni non verificate",e.getMessage(),"VALADJACENCY Restriction Not Matched");
        }
        assertEquals("Numero dadi cambiato",2,wf.numberOfDice());
        assertEquals("Dado spostato ma mossa non consentita",d2,wf.getCell(9).getDice());
        assertEquals("Spostato dado non selezionato",d1,wf.getCell(3).getDice());
        assertNull("Dado spostato ma mossa non consentita",wf.getCell(8).getDice());


        //verifica spostamento consentito
        try {
            wf.moveDice(3,13);
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (CellNotEmptyException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Numero dadi cambiato",2,wf.numberOfDice());
        assertEquals("Dado non spostato ma mossa consentita",d1,wf.getCell(13).getDice());
        assertEquals("Spostato dado non selezionato",d2,wf.getCell(9).getDice());
        assertNull("Dado non spostato ma mossa consentita",wf.getCell(3).getDice());
    }

    @Test
    public void shouldSetWfSize(){
        WindowFrame windowFrame = new WindowFrame(new Cell[20]);
        windowFrame.setRow(5);
        windowFrame.setCol(4);
        assertEquals("inizializzazione restrizioni celle errata",windowFrame.getCells().length,20);
        assertEquals("setRow errato",windowFrame.getRow(),5);
        assertEquals("setCol errato",windowFrame.getCol(),4);

    }
}
