package progettoIngSW.Model;

/**
 * Enumerazione relativa alle mosse effettuate dai giocatori
 * NONE = nessuna mossa effettuata
 * DICEPLACED = il giocatore ha solo piazzato il dado
 * TOOLUSED = il giocatore ha solo utilizzato una tool
 * BOTH = il giocatore ha effettuato entrambe le mosse nello stesso turno (piazzato un dado e usato una tool)
 */
public enum Moves {
    NONE,DICEPLACED,TOOLUSED,BOTH
}
