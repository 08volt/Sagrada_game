package progettoIngSW.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class RoundTrack extends Observable implements Serializable {

    //
    //ATTRIBUTES
    //
    private HashMap<Integer,ArrayList<Dice>> track;

    private int maxRound;

    //
    //CONSTRUCTOR
    //
    public RoundTrack() {
        this.maxRound = 10;
        this.track = new HashMap<>();
        for (int i = 1; i <= maxRound; i++) {
            track.put(i, new ArrayList<Dice>());
        }
    }

    //
    //METHODS
    //

    /**
     * Impila un dado sul Tracciato dei Round nella casella del Rouund scelto e notifica la modifica all'Observers
     * @param round round del RoundTrack in cui deve essere impilato il dado
     * @param d dado da piazzare
     */
    public void addDice(int round, Dice d){
        ArrayList<Dice> dicesInPos = track.get(round);
        dicesInPos.add(d);
        track.put(round,dicesInPos);
        setChanged();
        notifyObservers(track);
    }

    /**
     * Rimuove un dado sul Tracciato dei Round dalla casella del Rouund scelto e notifica la modifica all'Observers
     * @param round round del RoundTrack in cui deve essere rimosso il dado
     * @param index indice del dado che deve essere rimosso
     * @return dado rimosso
     *
     */
    public Dice removeDice(int round, int index){

        Dice d = getDice(round).remove(index);
        setChanged();
        notifyObservers(track);
        return d;
    }

    //
    //GET AND SET METHODS
    //
    public ArrayList<Dice> getDice(int round){
        return track.get(round);
    }

    public HashMap<Integer, ArrayList<Dice>> getTrack() {
        return track;
    }

    public int getMaxRound() {
        return maxRound;
    }
}
