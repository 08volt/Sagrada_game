package progettoIngSW.View.Cli;

import progettoIngSW.*;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;
import progettoIngSW.Network.Client.ConnectionType;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;

import static progettoIngSW.Network.Client.ClientStart.isInteger;

public final class CLI implements ViewInterface {

    private static CommandInterface player;


    //
    //GAME STATE
    //
    private static GameInterface game;
    private static ArrayList<PlayerInterface> playerInterfaces;
    private static HashMap<Integer, ArrayList<Dice>> track;
    private static ArrayList<Dice> draft;
    private static ArrayList<WindowFrameInterface> windowFrames;
    private PrintWriter writer;


    private static boolean endTurn = false;

    public static List<String> commands = Collections.synchronizedList(new ArrayList<>());





    //
    //CONSTRUCTOR
    //
    public CLI() {

        writer = new PrintWriter(System.out);
        game = Game.getGame();
        playerInterfaces = new ArrayList<>();
        windowFrames = new ArrayList<>();
        track = new HashMap<>();
        ThreadConsoleReader tr = new ThreadConsoleReader();
        tr.start();
    }


    //
    //PRINTER METHODS
    //

    private void printAllCommands() {
        writer.println("\t\t\t\t-- COMANDI --");
        writer.println();
        writer.println("1. Posiziona dado");
        writer.println("2. Usa ToolCard");
        writer.println("3. Mostra carta obiettivo privato");
        writer.println("0. Cambia turno");
        writer.flush();
    }

    private String colorToLetter(Colors c) {
        String r = "/";
        switch (c) {
            case BLUE:
                r = "B";
                break;
            case GREEN:
                r = "G";
                break;
            case YELLOW:
                r = "Y";
                break;
            case RED:
                r = "R";
                break;
            case PURPLE:
                r = "P";
                break;
        }
        return r;
    }

    private Colors letterToColor(char c) {
        Colors color = null;
        switch (c) {
            case 'B':
                color = Colors.BLUE;
                break;
            case 'G':
                color = Colors.GREEN;
                break;
            case 'Y':
                color = Colors.YELLOW;
                break;
            case 'R':
                color = Colors.RED;
                break;
            case 'P':
                color = Colors.PURPLE;
                break;
        }
        return color;
    }


    private void printToolCard() {
        writer.println("\t\t\t\t-- CARTE STRUMENTO --");
        writer.println();
        for (int i = 0; i <  game.getToolCards().size(); i++) {
            writer.print(i + 1 + " { tokens: "+game.getToolTokens().get(i)+" }  : ");
            switch (game.getToolCards().get(i)) {
                case 1:
                    writer.print("Dopo aver scelto un dado, aumenta o dominuisci il valore del dado scelto di 1. " +
                            "Non puoi cambiare un 6 in 1 o un 1 in 6");
                    break;
                case 2:
                    writer.print("Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore. " +
                            "Devi rispettare tutte le altre restrizioni di piazzamento");
                    break;
                case 3:
                    writer.print("Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore. "+
                            "Devi rispettare tutte le altre restrizioni di piazzamento");
                    break;
                case 4:
                    writer.print("Muovi esattamente due dadi, rispettando tutte le restrizioni di piazzamento");
                    break;
                case 5:
                    writer.print("Dopo aver scelto un dado, scambia quel dado con un dado sul Tracciato dei Round");
                    break;
                case 6:
                    writer.print("Dopo aver scelto un dado, tira nuovamente quel dado. " +
                            "Se non puoi piazzarlo, riponilo nella Riserva");
                    break;
                case 7:
                    writer.print("Tira nuovamente tutti i dadi della Riserva. Questa carta può essera usata " +
                            "solo durante il tuo secondo turno, prima di scegliere il secondo dado");
                    break;
                case 8:
                    writer.print("Dopo il tuo primo turno scegli immediatamente un altro dado. " +
                            "Salta il tuo secondo turno in questo round");
                    break;
                case 9:
                    writer.print("Dopo aver scelto un dado, piazzalo in una casella che non " +
                            "sia adiacente a un altro dado. Devi rispettare tutte le restrizioni di piazzamento");
                    break;
                case 10:
                    writer.print("Dopo aver scelto un dado, giralo sulla faccia opposta");
                    break;
                case 11:
                    writer.print("Dopo aver scelto un dado, riponilo nel Sacchetto, poi pescane uno dal Sacchetto. " +
                            "Scegli il valore del nuovo dado e piazzalo, rispettando tutte le restrizioni di piazzamento");
                    break;
                case 12:
                    writer.print("Muovi fino a due dadi dello stesso colore di un solo dado sul Tracciato dei Round. " +
                            "Devi rispettare tutte le restrizioni di piazzamento");
                    break;
            }
            writer.println();
        }
        writer.println();
        writer.flush();
    }


    //STAMPA DIVERSE WINDOW FRAME IN LINEA
    //indexBeginLine INDICA DA QUALE INDICE PLAYER INIZIARE A STAMPARE PER UNA SINGOLA LINEA
    //indexEndLine INDICA QUALE è L'INDICE PLAYER DELL'ULTIMA WF SULLA LINEA
    private void printWfInLine(int row, int col, int indexBeginLine, int indexEndLine) {
        for (int x = 0; x < row; x++) {
            for (int i = indexBeginLine; i < indexEndLine; i++) {
                for (int y = 0; y < col; y++) {
                    if (y == 0)
                        writer.print(x + "|");
                    Cell c = windowFrames.get(i).getCell(col * x + y);
                    String cellRestr = colorToLetter(c.getColorRestriction());
                    if (c.getNumberRestriction() != 0)
                        cellRestr = "" + c.getNumberRestriction();

                    String dice = "";

                    if (c.getDice() != null) {
                        dice = colorToLetter(c.getDice().getColor());
                        dice += c.getDice().getNumber();
                    } else {
                        dice = "//";
                    }

                    writer.print("(" + cellRestr + ")[" + dice + "] ");

                }
                writer.print("\t\t");
            }
            writer.println();
        }
        writer.flush();
    }

    private void printAllWf() {
        writer.println("\t\t\t\t-- WINDOWFRAMES --");
        writer.println();

        String windowPlayerString = "Window Frame Giocatore %-28s";

        if (playerInterfaces.size() == 4) {
            for (int j = 0; j < 2; j++)
                writer.printf(windowPlayerString, playerInterfaces.get(j).getUsername());
            writer.println();
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < windowFrames.get(0).getCol(); k++)
                    writer.print("\t\t" + k);
                writer.print("\t\t");
            }
            writer.println();
            printWfInLine(windowFrames.get(0).getRow(), windowFrames.get(0).getCol(), 0, 2);

            for (PlayerInterface p: playerInterfaces)
                writer.printf(windowPlayerString, p.getUsername());
            writer.println();
            for (int j = 2; j < playerInterfaces.size(); j++) {
                for (int k = 0; k < windowFrames.get(0).getCol(); k++)
                    writer.print("    \t" + k);
                writer.print("\t\t");
            }
            writer.println();
            printWfInLine(windowFrames.get(0).getRow(), windowFrames.get(0).getCol(), 2, playerInterfaces.size());
        } else {
            for (PlayerInterface p: playerInterfaces)
                writer.printf(windowPlayerString, p.getUsername());
            writer.println();
            for (int j = 0; j < playerInterfaces.size(); j++) {
                for (int k = 0; k < windowFrames.get(0).getCol(); k++)
                    writer.print("    \t" + k);
                writer.print("\t\t");
            }
            writer.println();
            printWfInLine(windowFrames.get(0).getRow(), windowFrames.get(0).getCol(), 0, playerInterfaces.size());
        }
        writer.println();
        writer.flush();
    }

    private void printDraftPool() {
        writer.println("\t\t\t\t-- RISERVA --");
        writer.println();
        for (int i = 0; i < draft.size(); i++) {
            Dice d = draft.get(i);
            writer.print((i + 1) + ".[" + colorToLetter(d.getColor()) + d.getNumber() + "]");
            writer.print("\t\t");
        }
        writer.println();
        writer.println();
        writer.flush();
    }

    private void printRoundTrack() {
        writer.println("\t\t\t\t-- ROUNDTRACK --");
        writer.println();
        ArrayList<Dice> dices;
        for (int i = 1; i < 11; i++) {
            dices = track.get(i);
            writer.print("[");
            if (dices.size() > 1) {
                for (int j = 0; j < dices.size() - 1; j++) {
                    writer.print(dices.get(j).getColor() + " " + dices.get(j).getNumber() + " , ");
                }
            }
            if (dices.isEmpty())
                writer.print("//]");
            else
                writer.print(dices.get(dices.size() - 1).getColor() + " " + dices.get(dices.size() - 1).getNumber() + "]");
        }
        writer.println();
        writer.flush();
    }

    private void printPublicObjectiveCard() {
        writer.println("\t\t\t\t-- CARTE OBIETTIVO PUBBLICHE --");
        writer.println();
        for (int i = 0; i < game.getPublicObjectiveCards().size(); i++) {
            writer.print(i + 1 + ": ");
            switch (game.getPublicObjectiveCards().get(i)) {
                case 1:
                    writer.print("Colori diversi - Riga: Righe senza colori ripetuti.");
                    break;
                case 2:
                    writer.print("Colori diversi - Colonna: Colonne senza colori ripetuti.");
                    break;
                case 3:
                    writer.print("Sfumature diverse - Riga: Righe senza sfumature ripetute.");
                    break;
                case 4:
                    writer.print("Sfumature diverse - Colonna: Colonne senza sfumature ripetute.");
                    break;
                case 5:
                    writer.print("Sfumature Chiare: Set di 1 & 2 ovunque.");
                    break;
                case 6:
                    writer.print("Sfumature Medie: Set di 3 & 4 ovunque.");
                    break;
                case 7:
                    writer.print("Sfumature Scure: Sfumature Scure.");
                    break;
                case 8:
                    writer.print("Sfumature Diverse: Set di dadi di ogni valore ovunque.");
                    break;
                case 9:
                    writer.print("Diagonali Colorate: Numero di dadi dello stesso colore diagonalmente adiacenti.");
                    break;
                case 10:
                    writer.print("Varietà di Colore: Set di dadi di ogni colore ovunque.");
                    break;
            }
            writer.println();
        }
        writer.println();
        writer.flush();
    }

    private void printOwnPrivateObjectiveCard(PrivateObjectiveCard privateObjectiveCard) {
        writer.println("\t\t\t\t-- LA TUA CARTA OBIETTIVO PRIVATA --");
        writer.println();
        writer.println("Somma di tutti i dadi di colore " + privateObjectiveCard.getColor());
        writer.println();
        writer.flush();
    }

    private void printPlaceDice() throws EndTimerException {

        try {
            player.placeDice(askDraftPos(), askWindowPos(true));
        } catch (RulesBreakException e) {
            printRulesException(e);
        } catch (CellNotEmptyException e) {
            writer.println("CELLA GIÀ OCCUPATA");
        } catch (NotPlayingException e) {
            writer.println("NON STAI GIOCANDO ORA");
        } catch (DiceNotFoundException e) {
            writer.println("INSERISCI UNA POSIZIONE DELLA RISERVA VALIDA");
        } catch (IOException e) {
            writer.println("ERRORE DI CONNESSIONE");
        }
        writer.flush();
    }

    private void printRulesException(RulesBreakException e) {
        if (e.getType() == RulesBreakException.CODE.MOVE)
            writer.println("HAI GIÀ FATTO QUESTA MOSSA! ASPETTA IL TUO PROSSIMO TURNO");
        else if (e.getType() == RulesBreakException.CODE.NEEDPLACE)
            writer.println("DEVI PRIMA POSIZIONARE UN DADO");
        else if (e.getType() == RulesBreakException.CODE.ADJACENCY)
            writer.println("IL DADO CHE VUOI POSIZIONARE DEVE ESSERE POSIZIONATO ADIACENTE AD UN ALTRO DADO");
        else if (e.getType() == RulesBreakException.CODE.COLOR)
            writer.println("LA CELLA CHE TU HAI SELEZIONATO HA UNA RESTRIZIONE DI COLORE DIVERSA " +
                    "DAL COLORE DEL DADO CHE VUOI POSIZIONARE");
        else if (e.getType() == RulesBreakException.CODE.NUMBER)
            writer.println("LA CELLA CHE TU HAI SELEZIONATO HA UNA RESTRIZIONE DI NUMERO DIVERSA " +
                    "DAL VALORE DEL DADO CHE VUOI POSIZIONARE");
        else if (e.getType() == RulesBreakException.CODE.FIRSTDICE)
            writer.println("IL TUO PRIMO DADO DEVE ESSERE POSIZIONATO SUL BORDO");
        else if (e.getType() == RulesBreakException.CODE.TOKEN)
            writer.println("NON HAI ABBASTANZA SEGNALINI PER POTER UTILIZZARE LA CARTA STRUMENTO");
        else if (e.getType() == RulesBreakException.CODE.COLADJACENCY)
            writer.println("IL DADO CHE VUOI POSIZIONARE È ORTOGONALMENTE ADIACENTE AD UN DADO CHE HA LO STESSO COLORE");
        else if (e.getType() == RulesBreakException.CODE.VALADJACENCY)
            writer.println("IL DADO CHE VUOI POSIZIONARE È ORTOGONALMENTE ADIACENTE AD UN DADO CHE HA LO STESSO VALORE");
        else if (e.getType() == RulesBreakException.CODE.EMPTYTRACK)
            writer.println("IL TRACCIATO DEL ROUND È VUOTO");
        else if (e.getType() == RulesBreakException.CODE.FIRSTTURN)
            writer.println("QUESTO E IL TUO PRIMO TURNO");
        else if (e.getType() == RulesBreakException.CODE.SECONDTURN)
            writer.println("QUESTO È IL TUO SECONDO TURNO");
        else if (e.getType() == RulesBreakException.CODE.EMPTYWF)
            writer.println("NON HAI ABBASTANZA DADI DA MUOVERE");
        writer.flush();

    }

    private void printUseToolCard() throws EndTimerException {
        int numToolChosen;

        String message = "Seleziona un carta strumento: [ 1 , 2 , 3 ]";

        numToolChosen = checkInput(1, 3, message);

        try {
            player.useToolCard(numToolChosen - 1);
        } catch (RulesBreakException e) {
            printRulesException(e);
        } catch (CellNotEmptyException e) {
            writer.println("CELLA GIÀ OCCUPATA");
        } catch (NotPlayingException e) {
            writer.println("NON STAI GIOCANDO, ASPETTA IL TUO TURNO");
        } catch (DiceNotFoundException e) {
            writer.println("INSERISCI UNA POSIZIONE CORRETTA");
        } catch (RemoteException e) {
            writer.println("ERRORE DI CONNESSIONE");
        } catch (NotValidCellException e) {
            writer.println("CELLA NON VALIDA");
        } catch (InvalidParamsException e) {
            writer.println(e.getMessage().toUpperCase() + " NOT VALID");
        } catch (IOException e) {
            writer.println("ERRORE DI CONNESSIONE");
        } catch (DraftFullException e) {
            writer.println("LA RISERVA È PIENA");
        }

        writer.flush();

    }


    private Integer checkInput(int min,int max,String message) throws EndTimerException {
        String command = "";

        while(!isInteger(command) || Integer.parseInt(command) < min || Integer.parseInt(command) > max ) {
            if(command != "") {
                writer.println("Inserimento non corretto, inserisci un valore corretto: ");
                writer.flush();
            }

            writer.println(message);
            writer.flush();
            while (commands.isEmpty()) {
            }
            command = commands.remove(0);

            if(command.equals("TIMER"))
                throw new EndTimerException();
        }

        return Integer.parseInt(command);
    }

    private  Integer nextCommand() throws EndTimerException {
        String message = ">>> Prossimo comando: ";
        return checkInput(0,3,message);
    }

    //
    //UPDATE METHODS
    //


    @Override
    public void updatePlayerList(Player player, int pos) {
        for(PlayerInterface p : playerInterfaces) {
            if (p.getUsername().equals(player.getUsername())) {
                playerInterfaces.set(playerInterfaces.indexOf(p), player);
                return;
            }
        }
        playerInterfaces.add(player);


    }

    @Override
    public void updateWf(WindowFrame wf, int pos) {
        if(windowFrames.size() == pos){
            windowFrames.add(wf);
        }else if(windowFrames.size()>pos){
            windowFrames.set(pos,wf);
        }

    }

    @Override
    public void alertDisconnectionOf(String name) {
        writer.println(name + " SI È SCONNESSO");
        writer.flush();
    }

    @Override
    public void alertConnectionOf(String name) {
        writer.println(name + " SI È CONNESSO");
        writer.flush();

    }

    @Override
    public void fullLobby() {
        writer.println("LOBBY PIENA, ASPETTA LA PROSSIMA PARTITA");
        writer.flush();
    }

    @Override
    public void connectionError() {
        writer.println("SERVER IRRAGGIUNGIBILE");
        writer.flush();
    }

    @Override
    public void nameNotAvailable() {
        writer.println("NOME NON DISPONIBILE");
        writer.flush();
    }

    @Override
    public void wrongPassword() {
        writer.println("PASSWORD SBAGLIATA");
        writer.flush();
    }

    @Override
    public boolean askToPlay() {
        int choice = 1;
        String message = "Cosa vuoi fare?\n1: Giocare!!\n2: Caricare un nuovo schema!";
        try {
            choice = checkInput(1,2,message);
        } catch (EndTimerException e) {

        }
        return choice == 1;
    }

    @Override
    public Pattern writeNewPattern() {
        Pattern p=new Pattern();

        String input;
        writer.println("Inserisci il nome del nuovo pattern:");
        writer.flush();
        while(commands.isEmpty()){}

        input = commands.remove(0);

        p.setName(input);


        try {
            p.setDifficult(checkInput(3,6,"Inserisci la difficoltà del nuovo pattern (da 3 a 6) :"));
        } catch (EndTimerException ignored) {

        }


        char[] restrictions = new char[] {'0','1','2','3','4','5','6','P','G','Y','B','R'};
        writer.println("Le restrizioni possibili sono:");
        for(char r : restrictions){
            writer.print(r+" ");
        }

        writer.println();
        writer.flush();

        Colors[] colors = new Colors[20];
        int[] numbers = new int[20];

        for(int i = 0; i<20; i++)
        {

            do {
                writer.println("cella numero " + i + "\tx=" + i / 5 + " y=" + i % 5);
                writer.println("Che restrizione vuoi mettere?");
                writer.flush();
                while(commands.isEmpty()){}
                input = commands.remove(0);
            }while(input.length()!=1 || !charArrayContains(restrictions,input.charAt(0)));

            Colors c;
            int n;
            if(letterToColor(input.charAt(0)) == null) {
                c = Colors.WHITE;
                n = Integer.parseInt(input.substring(0, 1));
            }
            else {
                c = letterToColor(input.charAt(0));
                n = 0;
            }
            colors[i] = c;
            numbers[i] = n;

        }
        p.setColors(colors);
        p.setNumbers(numbers);


        return p;
    }

    private boolean charArrayContains(char [] array, char c){
        for(char c1 : array)
            if(c1 == c)
                return true;

        return false;
    }


    @Override
    public void updateTools(ArrayList<Integer> tools) {
        game.setToolCards(tools);
    }

    @Override
    public void updatePublicObjectiveCards(ArrayList<Integer> cards) {
        game.setPublicObjectiveCards(cards);
    }


    @Override
    public void updateCurrentPlayer(int currentPlayer) {
        game.setCurrentPlayer(currentPlayer);
    }

    @Override
    public void updateTokens(ArrayList<Integer> tokens) {
        game.setToolTokens(tokens);
    }

    @Override
    public void updateGameTurn(Turns turn) {
        game.setCurrentTurn(turn);
    }


    @Override
    public void updateWindowsFrame(WindowFrame wf) {
        windowFrames.set(game.getCurrentPlayer(),wf);

        if(!playerInterfaces.isEmpty()) {
            PlayerInterface curP = playerInterfaces.get(game.getCurrentPlayer());
            if (!curP.getUsername().equals(player.getName()))
                printAllWf();
        }

    }

    @Override
    public void updateDraftPool(ArrayList<Dice> draftpool) {
        draft = draftpool;

        if(!playerInterfaces.isEmpty()) {
            PlayerInterface curP = playerInterfaces.get(game.getCurrentPlayer());
            if (!curP.getUsername().equals(player.getName()))
                printDraftPool();
        }
    }

    @Override
    public void updateRoundTrack(HashMap<Integer,ArrayList<Dice>> rtrack) {
        track = rtrack;
    }

    @Override
    public void endTimer() {
        commands.add("TIMER");
        writer.println("TIMER SCADUTO");
        writer.flush();

    }

    @Override
    public void updateCurrentRound(int r) {
        game.setCurrentRound(r);
    }


    //
    //ASK METHODS
    //
    @Override
    public String askName() {
        String username = "";
        while (username.isEmpty()) {
            writer.println("Inserisci il tuo username: ");
            writer.flush();
            while (commands.isEmpty()) {
            }
            username = commands.remove(0);
        }
        return username;
    }

    @Override
    public String askPassword() {
        String pass = "";
        while (pass.isEmpty()) {
            writer.println("Inserisci la tua password: ");
            writer.flush();
            while (commands.isEmpty()) {
            }
            pass = commands.remove(0);
        }
        return pass;
    }

    @Override
    public void printDice(Dice d) {
        writer.println("Selected dice: " + colorToLetter(d.getColor()) + d.getNumber());
        writer.println();
        writer.flush();
    }

    @Override
    public ConnectionType askConnectionType() {
        String message = "[0] RMI \n[1] Socket \n 0 o 1?";
        String choice = "";

        while(!isInteger(choice) || (Integer.parseInt(choice) != 0 && Integer.parseInt(choice) != 1)) {

            writer.println(message);
            writer.flush();
            while (commands.isEmpty()) {
            }
            choice = commands.remove(0);
        }
        if(Integer.parseInt(choice) == 0)
            return ConnectionType.RMI;
        else
            return ConnectionType.SOCKET;
    }

    @Override
    public String askServerAddress() {
        writer.println("Inserisci l'indirizzo del server: ");
        writer.flush();
        while (commands.isEmpty()) {
        }
        String choice = commands.remove(0);
        return choice;
    }

   @Override
    public int askDraftPos() throws EndTimerException {
        String message = "Seleziona la posizione del dado nella riserva: ";
        return checkInput(1,draft.size(),message)-1;
    }

    @Override
    public boolean askIncrease() throws EndTimerException {
        boolean t = false;
        int choice;
        String message = "Vuoi aumentare o diminuire\n1:Aumentare\n2:Diminuire";
        choice = checkInput(1,2,message);
        if(choice == 1) {
            t = true;
        }
        return t;

    }

    //quando richiamato deve essere mostrato un messaggio di errore nel caso di valore sbagliato inserito
    @Override
    public int askWindowPos(boolean placement) throws EndTimerException {

        String message = (placement) ? "Dove vuoi posizionare il dado?":"Seleziona il dado che vuoi spostare";
        writer.println(message);
        writer.flush();
        message = "Seleziona la cordinata x della tua WindowFrame: ";
        int x = checkInput(0,3,message);
        message = "Seleziona la cordinate y della tua WindowFrame: ";
        int y = checkInput(0,4,message);
        return (5*x+y);
    }

    //quando richiamato deve essere mostrato un messaggio di errore nel caso di valori sbagliato inserito
    @Override
    public int[] askRoundTrackPos() throws EndTimerException {
        int[] pos = new int[2];
        String message = "Seleziona una posizione sul Tracciato del Round: ";
        pos[0] = checkInput(1,game.getCurrentRound()-1,message);
        message = "Seleziona la posizione del dado: ";
        pos[1] = checkInput(1,track.get(pos[0]).size(),message)-1;
        return pos;
    }


    //quando richiamato deve essere mostrato un messaggio di errore nel caso di valore sbagliato inserito (<1 || >6)
    @Override
    public int askNumber(Colors c) throws EndTimerException {
        String message = ("Select a number for the " + c + " extracted dice : ");
        return checkInput(1,6,message);
    }


    //PERCHE BOOLEAN E LE POSIZIONI PERCHE QUI
    @Override
    public boolean askHowMany() throws EndTimerException {
        String message = "Quanti dadi vuoi muovere? 1 o 2?";
        Integer num = checkInput(1,2,message);
        if(num == 2)
            return true;
        else
            return false;
    }

    //print gameS
    public void printGameStatus() {

        writer.println("Round corrente: " + game.getCurrentRound());
        writer.println("Turno corrente " + game.getCurrentTurn());
        writer.println("Giocatore corrente: " + playerInterfaces.get(game.getCurrentPlayer()).getUsername());
        writer.println("Segnalini giocatore corrente: " + playerInterfaces.get(game.getCurrentPlayer()).getNumTokens());
        for (PlayerInterface p : playerInterfaces)
            writer.println(p.getUsername() + " turno: " + p.getCurrentTurn());
        writer.println();
        writer.flush();
        printRoundTrack();
        printPublicObjectiveCard();
        printToolCard();
        printDraftPool();
        printAllWf();
    }

    @Override
    public void startTurn() throws EndTimerException {
        commands.clear();

        do {
            printGameStatus();
            printAllCommands();

            int c = nextCommand();

            switch (c) {
                case 1: {
                    printPlaceDice();
                    break;
                }
                case 2: {
                    printUseToolCard();
                    break;
                }
                case 3: {
                    printOwnPrivateObjectiveCard(playerInterfaces.get(game.getCurrentPlayer()).getPrivateObjectiveCard());
                    break;
                }
                default: {
                    printEndTurn();
                    try {
                        player.endTurn();
                    } catch (IOException ignored) {
                    }
                }
            }
        } while (!endTurn);
        writer.println("ASPETTA IL TUO PROSSIMO TURNO!");
        writer.flush();
    }

    @Override
    public int askWhichPattern(Pattern[] patterns){

        for (PlayerInterface p:playerInterfaces) {
            if(p.getUsername().equals(player.getName()))
                printOwnPrivateObjectiveCard(p.getPrivateObjectiveCard());
        }

        for(int j=0;j<patterns.length;j++) {
            writer.print((j+1) + ") Difficoltà: " + patterns[j].getDifficult() + "\t\t\t\t");
        }

        writer.println();
        for (int x = 0; x < patterns[0].getRow(); x++) {
            for(int i=0;i<patterns.length;i++) {
                for (int y = 0; y < patterns[0].getCol(); y++) {
                    int numbRestr = patterns[i].getNumbers()[(patterns[0].getCol()*x+y)];
                    String cellRestr = colorToLetter(patterns[i].getColors()[(patterns[0].getCol()*x+y)]);

                    if (numbRestr != 0)
                        cellRestr = "" + numbRestr;

                    writer.print("(" + cellRestr + ")\t");
                }
                writer.print("\t\t");
            }
            writer.println();
        }

        writer.println();

        for(int j=0;j<patterns.length;j++) {
            writer.println((j+1) + ". " + patterns[j].getName());
        }
        writer.flush();

        String message = "Seleziona un pattern(1/2/3/4): ";

        try {
            return checkInput(1,4,message)-1;
        } catch (EndTimerException e) {
            writer.println("Timer scaduto\nÈ stato scelto un pattern casuale");
            writer.flush();
            return 4;

        }
    }

    @Override
    public void endGame(ArrayList<Integer> ranking) {
        writer.println("\t\t\t\t-- PUNTEGGIO --");
        writer.println();
        for (Integer i : ranking) {
            PlayerInterface p = playerInterfaces.get(i);
            writer.println(p.getUsername() + " punteggio: " + p.getScore());
        }
        writer.println();
        writer.println("\t\t\t\t-- VINCITORE --");
        writer.println();
        writer.println(playerInterfaces.get(ranking.get(0)).getUsername() + "VINCE");
        writer.println();
        writer.flush();
    }

    @Override
    public void printEndTurn() {
        endTurn = true;
        writer.println("IL TUO TURNO È FINITO");
        writer.flush();
    }

    public void setPlayer(CommandInterface commandInterface) {
        player = commandInterface;
    }

    @Override
    public void setLogged(boolean b){
        if(b){
            writer.println("CONNESSIONE ESEGUITA");
            writer.flush();
        }else
        {
            writer.println("CONNESSIONE NON RIUSCITA");
            writer.flush();
        }
    }
}
