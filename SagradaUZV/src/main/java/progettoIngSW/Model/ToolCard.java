package progettoIngSW.Model;

import progettoIngSW.Exceptions.*;

public interface ToolCard {


    /**
     * Interfaccia per le varie tool, le quali implementano il COMMAND PATTERN
     * Per i dettagli delle eccezioni sollevate, vedere specifiche tool
     * @throws InvalidParamsException
     * @throws DiceNotFoundException
     * @throws RulesBreakException
     * @throws CellNotEmptyException
     * @throws NotValidCellException
     * @throws DraftFullException
     */

    void useToolCard() throws InvalidParamsException, DiceNotFoundException, RulesBreakException,
            CellNotEmptyException, NotValidCellException, DraftFullException;

}
