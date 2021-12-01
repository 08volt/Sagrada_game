package progettoIngSW.Exceptions;

//WHEN SOME RULES ARE NOT MATCHED
//COLOR  (cella)
//NUMBER (cella)
//PLACEMENT --> restrizione di piazzamento
//MOVE  --> mossa gia fatta

public class RulesBreakException extends Exception {

    public enum CODE { COLOR, NUMBER,
        ADJACENCY, COLADJACENCY, VALADJACENCY,
        FIRSTDICE, FIRSTTURN, SECONDTURN,
        MOVE, TOKEN, NEEDPLACE,
        EMPTYWF, EMPTYTRACK }

    CODE Type;

    public RulesBreakException(CODE Type) {
        super(Type.name()+ " Restriction Not Matched");
        this.Type = Type;

    }

    public CODE getType() {
        return Type;
    }
}
