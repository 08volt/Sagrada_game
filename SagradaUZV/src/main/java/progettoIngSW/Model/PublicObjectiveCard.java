package progettoIngSW.Model;

public abstract class PublicObjectiveCard {

    //
    //ATTRIBUTES
    //
    private int victoryPoint;

    /**
     * Calcolo punteggio dato dalle varie ObjectiveCard (ogni carta ha un calcolo punteggio deifferente - Override)
     * @param windowFrame windowFrame sulla quale applicare il calcolo del punteggio
     * @return punteggio dato dalla carta applicata alla wf
     */
    //
    //METHODS
    //
    public abstract int scoringCalc(WindowFrame windowFrame);

    //
    //GET AND SET METHODS
    //
    public void setVictoryPoint(int victoryPoint) {
        this.victoryPoint = victoryPoint;
    }
    public int getVictoryPoint() {
        return victoryPoint;
    }
}
