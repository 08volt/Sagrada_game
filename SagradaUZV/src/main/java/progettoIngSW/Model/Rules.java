package progettoIngSW.Model;


import progettoIngSW.Exceptions.RulesBreakException;



//INCOMPLETA

public final class Rules {

    /**
     * Verifica se tutte la restrizioni relative ad un piazzamento di un dado in una cella sono verificate,
     * se per la cella scelta almeno una restrizione non e' rispettata, allora viene sollevata l'eccezione
     * @param wf windowFrame contenente la cella su cui devono essere verificate le restrizioni
     * @param dice dado che si vuole piazzare
     * @param wfPos posizione della cella all'interno della windowFrame su cui devono essere verificate le restrizioni
     * @throws RulesBreakException se almeno una delle restrizioni non e' rispettata (dado non piazzabile)
     */
    public static void checkCell(WindowFrame wf, Dice dice, int wfPos) throws RulesBreakException {
        firstDiceRestriction(wf,wfPos);
        adjacencyRestriction(wf,dice,wfPos);
        colorRestriction(wf,dice,wfPos);
        valueRestriction(wf,dice,wfPos);

    }


    /**
     * Verifica che il primo dado sia adiacente al margine
     * @param windowFrame windowFrame su cui verificare la restrizione
     * @param wfPos posizione della cella in cui si vuole piazzare il dado
     * @throws RulesBreakException se si vuole piazzare il primo dado in una cella che non e' sul margine(CODE.FIRSTDICE)
     */
    public static void firstDiceRestriction(WindowFrame windowFrame, int wfPos) throws RulesBreakException {
        int x = windowFrame.getX(wfPos);
        int y = windowFrame.getY(wfPos);

        if (windowFrame.numberOfDice() == 0)


            if(!( x == 0 || y == 0 || x == windowFrame.getRow()-1 || y == windowFrame.getCol()-1))
                throw new RulesBreakException(RulesBreakException.CODE.FIRSTDICE);

    }


    /**
     * Deve controllare che ci siano dadi vicini ortogonalmente o in diagonale
     * e che i dadi ortogonalmente vicini non siano dello stesso colore o stesso numero
     * @param windowFrame windowFrame su cui verificare la restrizione
     * @param wfPos posizione della cella in cui si vuole piazzare il dado
     * @param dice dado che si vuole piazzare
     * @throws RulesBreakException se la restrizione di dadi adiacenti non e' verificata
     *                  - CODE.ADJACENCY: tentativo di posizionare un dado non adiacente ad un altro
     *                  - CODE.VALADJACENCY: tentativo di posizionare dadi ortogonalmente vicini dello stesso valore
     *                  - CODE.COLADJACENCY: tentativo di posizionare dadi ortogonalmente vicini dello stesso colore
     */
    public static void adjacencyRestriction(WindowFrame windowFrame, Dice dice, int wfPos) throws RulesBreakException {
        int x = windowFrame.getX(wfPos);
        int y = windowFrame.getY(wfPos);


        if(windowFrame.numberOfDice() == 0)
            return;
        boolean check = false;
        int i = 0, j = 0, max_x = 0, max_y = 0;
        switch (x){
            case 0: i = 0; max_x = 2; break;
            case 3: i=-1; max_x = 1; break;
            default: i = -1; max_x = 2;
        }
        switch (y){
            case 0 : j = 0; max_y = 2; break;
            case 4: j=-1; max_y = 1; break;
            default: j = -1; max_y = 2;
        }
        int t = j;
        while(i < max_x && !check){
            j=t;
            while (j < max_y && !check) {
                //controllo tutte le posizioni del quadrato attorno a x y esclusi
                if ((windowFrame.getCell(windowFrame.getI(x + i, y + j)).getDice() != null)
                        && (i != 0 || j != 0)) {
                    check = true;

                }
                j++;
            }
            i++;
        }
        if(!check)
            throw new RulesBreakException(RulesBreakException.CODE.ADJACENCY);

        if (x > 0 && windowFrame.getCell(windowFrame.getI(x - 1, y)).getDice() != null
                && windowFrame.getCell(windowFrame.getI(x - 1, y)).getDice().getColor() == dice.getColor())
            check = false;
        if (y < 4 && windowFrame.getCell(windowFrame.getI(x, y + 1)).getDice() != null
                && windowFrame.getCell(windowFrame.getI(x, y + 1)).getDice().getColor() == dice.getColor())
            check = false;
        if (x < 3 && windowFrame.getCell(windowFrame.getI(x + 1, y)).getDice() != null
                && windowFrame.getCell(windowFrame.getI(x + 1, y)).getDice().getColor() == dice.getColor())
            check = false;
        if (y > 0 && windowFrame.getCell(windowFrame.getI(x, y - 1)).getDice() != null
                && windowFrame.getCell(windowFrame.getI(x, y - 1)).getDice().getColor() == dice.getColor())
            check = false;

        if(!check)
            throw new RulesBreakException(RulesBreakException.CODE.COLADJACENCY);

        if(x > 0 && windowFrame.getCell(windowFrame.getI(x - 1, y)).getDice() != null
                && windowFrame.getCell(windowFrame.getI(x - 1, y)).getDice().getNumber() == dice.getNumber())
            check = false;
        if(y < 4 && windowFrame.getCell(windowFrame.getI(x, y + 1)).getDice() != null
                && windowFrame.getCell(windowFrame.getI(x, y + 1)).getDice().getNumber() == dice.getNumber())
            check = false;
        if(x < 3 && windowFrame.getCell(windowFrame.getI(x + 1, y)).getDice() != null
                && windowFrame.getCell(windowFrame.getI(x + 1, y)).getDice().getNumber() == dice.getNumber())
            check = false;
        if(y > 0 && windowFrame.getCell(windowFrame.getI(x, y - 1)).getDice() != null
                && windowFrame.getCell(windowFrame.getI(x, y - 1)).getDice().getNumber() == dice.getNumber())
            check = false;

        if(!check)
            throw new RulesBreakException(RulesBreakException.CODE.VALADJACENCY);

    }

    /**
     * Restrizione colore non rispettata (colore dado diverso da colore resrtrizione cella)
     * @param windowFrame windowFrame su cui verificare la restrizione
     * @param wfPos posizione della cella in cui si vuole piazzare il dado
     * @param dice dado che si vuole piazzare
     * @throws RulesBreakException
     */
    public static void colorRestriction(WindowFrame windowFrame, Dice dice, int wfPos) throws RulesBreakException {
        Cell c = windowFrame.getCell(wfPos);
        if (c.isColorFlag() && c.getColorRestriction() != Colors.WHITE && c.getColorRestriction() != dice.getColor())
            throw new RulesBreakException(RulesBreakException.CODE.COLOR);
    }

    /**
     * Restrizione valore non rispettata (valore dado diverso da valore resrtrizione cella)
     * @param windowFrame windowFrame su cui verificare la restrizione
     * @param wfPos posizione della cella in cui si vuole piazzare il dado
     * @param dice dado che si vuole piazzare
     * @throws RulesBreakException
     */
    public static void valueRestriction(WindowFrame windowFrame, Dice dice, int wfPos) throws RulesBreakException {
        Cell c = windowFrame.getCell(wfPos);
        if (c.isNumberFlag() && c.getNumberRestriction() != 0 && c.getNumberRestriction() != dice.getNumber())
            throw new RulesBreakException(RulesBreakException.CODE.NUMBER);

    }

}
