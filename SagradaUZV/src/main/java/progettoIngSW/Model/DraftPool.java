package progettoIngSW.Model;

import javafx.collections.SetChangeListener;
import progettoIngSW.Exceptions.DiceNotFoundException;
import progettoIngSW.Exceptions.DraftFullException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DraftPool extends Observable implements Serializable{

    //
    //ATTRIBUTES
    //
    private DiceBag diceBag;
    private ArrayList<Dice> draft;
    private int numPlayer;


    //
    //CONSTRUCTOR
    //
    public DraftPool() {
        diceBag = new DiceBag();
        draft = new ArrayList<>();
    }

    //
    //METHODS
    //

    /**
     * removes all the dice from the draft and it adds new ones from the bag
     * new number of dice extracted = 2* number of players + 1)
     * @see DiceBag source of dices to add to the draft
     * @see Server notify the server of the draft update
     */
    public void generateDraft(){
        draft.clear();
        for(int i = 0; i < 2*numPlayer + 1; i++){
            draft.add(diceBag.extractDice());
        }
        setChanged();
        notifyObservers(draft);
    }


    /**
     * Remove a dice from the draft
     * @param pos position of the dice to remove on the draft
     * @return removed dice if there was one on the selected position
     * @throws DiceNotFoundException if there wasn't any dice on the selected position
     * @see DiceNotFoundException the class get notified of that there wasn't any dice
     * and the position on the draft in which the dice wasn't found
     * @see Server if a dice was removed, the server will be notified
     */
    public Dice removeDice(int pos) throws DiceNotFoundException {
        try {
            Dice d = draft.remove(pos);
            setChanged();
            notifyObservers(draft);
            return d;

        }catch (Exception e){
            DiceNotFoundException de = new DiceNotFoundException();
            de.setPos(pos);
            de.setWhere(this);
            throw de;
        }
    }

    /**
     * Adds a dice to the draft
     * @param d dice to add
     * @throws DraftFullException if the draft is already full (dice on draft = 2 * number of players + 1)
     * @see Server if a dice has been added the Server will be notified
     */
    public void addDice(Dice d) throws DraftFullException {
        if(draft.size()>=(2*this.numPlayer+1))
            throw new DraftFullException(d);

        draft.add(d);
        setChanged();
        notifyObservers(draft);
    }

    /**
     * Change the number of a dice inside the draft
     * @param d dice on which we want to change the number
     * @param n new number to set on the dice
     * @see Server the Server will be notified
     */
    public void changeValueDice(Dice d, int n){
        d.setNumber(n);
        setChanged();
        notifyObservers(draft);
    }

    /**
     * Chenge the value of all the dices on the draft
     * @see Server the Server will be notified
     */
    public void regenerateDraft(){
        for(Dice d : draft)
            d.generateNumber();

        setChanged();
        notifyObservers(draft);
    }

    /**
     * Randomize a value on a dice (roll a dice)
     * @param position of the dice to roll on the draft
     * @see Server the Server will be notified
     */
    public void rollDice(int pos){
        draft.get(pos).generateNumber();
        setChanged();
        notifyObservers(draft);
    }

    //
    //GET AND SET METHODS
    //
    public ArrayList<Dice> getDraft() {
        return draft;   //non so se è meglio ritornare un array
    }

    public DiceBag getDiceBag() {
        return diceBag;
    }

    public void setNumPlayer(int numPlayer) {
        this.numPlayer = numPlayer;
    }

}
