package progettoIngSW.Model;

import progettoIngSW.Exceptions.DiceNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiceBag implements java.io.Serializable{
    //
    //ATTRIBUTES
    //
    private ArrayList<Dice> bag;

    final int numDice = 18; //constant for number of Dice of a single color

    //
    //CONSTRUCTOR
    //

    /**
     * Adds 90 dices to the bag (18 dices for each color)
     * and shuffle it
     */
    public DiceBag() {
        this.bag = new ArrayList<>();
        for(int i =0; i<numDice;i++){
            bag.add(new Dice(Colors.RED));
        }
        for(int i =0; i<numDice;i++){
            bag.add(new Dice(Colors.YELLOW));
        }
        for(int i =0; i<numDice;i++){
            bag.add(new Dice(Colors.BLUE));
        }
        for(int i =0; i<numDice;i++){
            bag.add(new Dice(Colors.GREEN));
        }
        for(int i =0; i<numDice;i++){
            bag.add(new Dice(Colors.PURPLE));
        }
        Collections.shuffle(bag);
    }

    //
    //METHODS
    //

    /**
     * shuffle the bag and remove a dice from it
     * @return null if there isn't any dice, otherwise the removed dice
     */
    public Dice extractDice(){
        if(!bag.isEmpty()) {
            Collections.shuffle(bag);
            return bag.remove(0);
        }else
        {
            return null;
        }

    }

    /**
     * Adds a dice to the bag
     * @param d dice to put in the bag
     */
    public void putDice(Dice d){
        bag.add(d);
    }

    //
    //GET AND SET METHODS
    //
    public List<Dice> getDiceBag() {
        return bag;
    }
}
