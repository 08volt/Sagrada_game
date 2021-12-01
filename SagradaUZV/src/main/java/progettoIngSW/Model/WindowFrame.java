package progettoIngSW.Model;



//
//  [0,0]     [0,1]     [0,2]     [0,3]     [0,4]
//
//  [1,0]     [1,1]     [1,2]     [1,3]     [1,4]
//
//  [2,0]     [2,1]     [2,2]     [2,3]     [2,4]
//
//  [3,0]     [3,1]     [3,2]     [3,3]     [3,4]
//
//
// [x][y]  -->   0<=x<4      0<=y<5
//
// altezza = 4
// lunghezza = 5
//
//

import progettoIngSW.Exceptions.CellNotEmptyException;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.RulesBreakException;
import progettoIngSW.WindowFrameInterface;

import java.io.Serializable;
import java.util.Observable;

public class WindowFrame extends Observable implements Serializable, WindowFrameInterface {

    //
    //ATTRIBUTES
    //
    private Cell[] cells;

    private int col;
    private int row;

    //
    //CONSTRUCTOR
    //
    public WindowFrame(Pattern pattern) {
        this.col = pattern.getCol();
        this.row = pattern.getRow();
        this.cells = new Cell[(pattern.getCol()*pattern.getRow())];
        initPattern(pattern);
    }

    public WindowFrame(Cell[] cells) {
        this.cells = cells;
    }

    //
    //METHODS
    //
    private boolean initPattern(Pattern p){

        for (int i = 0; i < (this.col*this.row); i++)
                this.cells[i] = new Cell(p.getColors()[i], p.getNumbers()[i]);
        return true;
    }

    /**
     * Calcola il numero di dadi presenti nella windowFrame
     * @return il numero di dadi presenti
     */
    public int numberOfDice(){
        int n = 0;
        for(int i = 0; i< cells.length; i++)
        {
            if(cells[i].getDice() != null)
                n ++;

        }
        return n;
    }

    /**
     * Posiziona il dado
     * @param d è il dado selezionato
     * @param i è la posizione all'interno della windowFrame
     * @throws CellNotEmptyException se la cella dove si vuole posizionare il dado non è vuoto
     * @throws RulesBreakException se non vengono rispettate le restrizioni
     */
    public void placeDice(Dice d, int i) throws CellNotEmptyException, RulesBreakException {
        Rules.checkCell(this,d,i);
        cells[i].setDice(d);
        setChanged();
        notifyObservers(this);
    }

    /**
     * Posiziona il dado ignorando le regole di piazzamento
     * @param d è il dado da posizionare
     * @param i è la posizione dove si deve posizionare il dado
     * @throws CellNotEmptyException se la cella non è vuota
     */
    public void setBruteDice(Dice d, int i) throws CellNotEmptyException {
        cells[i].setDice(d);
       setChanged();
       notifyObservers(this);
    }

    /**
     * Rimuove un dado dalla windowFrame
     * @param i è la posizione all'interno della WindowFrame
     * @return il dado rimosso
     * @throws DiceNotFoundException se non è stato trovato alcun dado
     */
    public Dice removeDice(int i) throws DiceNotFoundException {
        Dice d = null;
        d = cells[i].removeDice();
        setChanged();
        notifyObservers(this);
        return d;
    }

    //NOTA: aggiunto controllo restrizione come per placeDice, da decidere dove chiamare le restrizioni

    /**
     * Effettua uno spostamento di un dado
     * @param i1 posizione iniziale dello spostamento
     * @param i2 posizione finale dello spostamento
     * @throws DiceNotFoundException se non è stato trovato il dado
     * @throws CellNotEmptyException se la cella dove si vuole posizionare il dado non è vuota
     * @throws RulesBreakException se non sono rispettate le regole
     */
    public void moveDice(int i1,int i2) throws DiceNotFoundException, CellNotEmptyException, RulesBreakException {
        if(cells[i1].getDice() == null) throw new DiceNotFoundException();
        if(cells[i2].getDice() != null ) throw new CellNotEmptyException();
        Dice d1 = cells[i1].removeDice();
        try {
            Rules.checkCell(this, d1, i2);
            placeDice(d1,i2);
        } catch (RulesBreakException e) {
            setBruteDice(d1,i1);
            throw e;
        }
        cells[i1].setNumberFlag(true);
        cells[i1].setColorFlag(true);
        setChanged();
        notifyObservers(this);
    }

    public int getRow(){
        return this.row;
    }

    public int getCol(){
        return this.col;
    }

    //
    //GET AND SET METHODS
    //
    public Cell getCell(int i){
        return cells[i];
    }

    public Cell getCell(int x,int y){
        return cells[getI(x,y)];
    }

    public Cell[] getCells() {
        return cells;
    }


    public int getI(int x,int y){
        return x*this.col+y;
    }
    public int getX(int i){
        return i/this.col;
    }
    public int getY(int i){
        return i%this.col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }


}
