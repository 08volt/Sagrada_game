package progettoIngSW.Model;


public class PrivateObjectiveCard implements java.io.Serializable {

    //
    //ATTRIBUTES
    //
    private Colors color;

    //
    //CONSTRUCTOR
    //
    public PrivateObjectiveCard(Colors color) {
        this.color = color;
    }

    //
    //GET AND SET METHODS
    //
    public Colors getColor() {
        return color;
    }

}
