package progettoIngSW.Model;

import java.util.Observable;
import java.util.Random;


public class Dice implements java.io.Serializable{
    //
    //ATTRIBUTES
    //
    private Random r;
    private Colors c;
    private int n;

    //
    //CONSTRUCTOR
    //

    /**
     * Constructor that set a random number un numero casuale and the color that he get as a parameter
     * @param color dice color
     */
    public Dice(Colors color) {
        this.c = color;
        this.r = new Random();
        this.n = generateNumber();
    }


    //
    //METHODS
    //

    /**
     * Generate a random number from 1 to 6
     * @return the random number generated
     */
    public int generateNumber(){
        this.n = (r.nextInt(22271)%6) + 1;
        return this.n;
    }


    //
    //GET AND SET METHODS
    //
    public int getNumber() {
        return n;
    }
    public void setNumber(int n) {
        this.n = n;
    }

    public Colors getColor() {
        return c;
    }
}
