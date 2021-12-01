package progettoIngSW.Controller;

import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;
import progettoIngSW.Network.Server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * Class Controller of each single player of the match.
 * Handle the turns and the move that the player wants to do
 */
public class PlayerController {

    private Server server;
    private Player player;
    private Game g;
    private boolean isPlaying;
    private final Object monitor = new Object();

    private boolean connected;


    public PlayerController(String username, String password, Server s) {
        server = s;
        player = new Player(username, password);
        this.connected = true;
    }


    /**
     * Asks the player which pattern he wants to use
     *
     * @param patterns array of 4 patterns to choose from
     */
    public void choosePattern(Pattern[] patterns) {

        int choice = server.askWhichPattern(patterns, player.getUsername());
        if (choice == 4) {
            Random r = new Random();
            choice = r.nextInt(4);
        }
        player.patternChoosen(patterns[choice]);
    }


    /**
     * concatenate two array of integers
     * @param first first array to concatenate
     * @param second second array to concatenate
     * @return array composed of the first array concatenated with the second one
     */
    private int[] concatInt(int[] first, int[] second) {
        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * method called by the user to place a dice on his Windowsframe
     * @param posDraft dice position on the draft
     * @param windowPos position on the windowsframe where the user wants to put the dice
     * @throws RulesBreakException if the required placement doesn't respect the game rules
     * @throws CellNotEmptyException if there already is a dice on that position on the windows frame
     * @throws NotPlayingException if it isn't the turn of the player
     * @throws DiceNotFoundException if the position on the draft is not valid
     */
    public void placeDice(int posDraft, int windowPos) throws RulesBreakException, CellNotEmptyException, NotPlayingException, DiceNotFoundException {
        if (!isPlaying) throw new NotPlayingException(player);
        if (player.getMoves() == Moves.BOTH || player.getMoves() == Moves.DICEPLACED)
            throw new RulesBreakException(RulesBreakException.CODE.MOVE);
        g.placeDice(player, posDraft, windowPos);
        g.setDicePLACED(player);

    }

    /**
     * Method that handle the request of the player to use a Tool Card
     * @param posToolCard Position of the tool card in the array
     * @throws NotPlayingException if it isn't the turn of the player
     * @throws RulesBreakException if the game rules are not respected
     * @throws CellNotEmptyException if there already is a dice on that position on the windows frame
     * @throws DiceNotFoundException if the position on the draft is not valid
     * @throws InvalidParamsException if the parameters to use the tool card are not valid
     * @throws NotValidCellException If there is a request on a not valid cell
     * @throws DraftFullException if it is not possible to add dices to the draft
     * @throws EndTimerException if the timer for the parameters of the tool card ends
     */
    public void useToolCard(int posToolCard) throws NotPlayingException, RulesBreakException, CellNotEmptyException, DiceNotFoundException, InvalidParamsException, NotValidCellException, DraftFullException, EndTimerException {
        if (!isPlaying) throw new NotPlayingException(player);
        if (player.getMoves() == Moves.BOTH || player.getMoves() == Moves.TOOLUSED)
            throw new RulesBreakException(RulesBreakException.CODE.MOVE);

        try {
            ToolCard t;
            switch (g.getToolCards().get(posToolCard)) {
                case 1: {
                    int dpos = server.askDraftPos(player.getUsername());
                    boolean increase = server.askIncrease(player.getUsername());
                    t = new ToolCard01(g.getDraft(), dpos, increase);
                    g.useToolCard(t, player, posToolCard);
                    g.setToolUSED(player,posToolCard);
                    break;
                }

                case 2: {
                    if (player.getWindowFrame().numberOfDice() < 1)
                        throw new RulesBreakException(RulesBreakException.CODE.EMPTYWF);
                    int pos1 = server.askWindowPos(player.getUsername(), false);
                    int pos2 = server.askWindowPos(player.getUsername(), true);
                    t = new ToolCard02(player.getWindowFrame(), pos1, pos2);
                    g.useToolCard(t, player, posToolCard);
                    g.setToolUSED(player,posToolCard);
                    break;
                }

                case 3: {
                    if (player.getWindowFrame().numberOfDice() < 1)
                        throw new RulesBreakException(RulesBreakException.CODE.EMPTYWF);
                    int pos1 = server.askWindowPos(player.getUsername(), false);
                    int pos2 = server.askWindowPos(player.getUsername(), true);
                    t = new ToolCard03(player.getWindowFrame(), pos1, pos2);
                    g.useToolCard(t, player, posToolCard);
                    g.setToolUSED(player,posToolCard);
                    break;
                }

                case 4: {
                    if (player.getWindowFrame().numberOfDice() <= 1)  //per tutti gli spostamenti controllerei se si possono fare o meno
                        throw new RulesBreakException(RulesBreakException.CODE.EMPTYWF);
                    int[] pos = new int[4];
                    pos[0] = server.askWindowPos(player.getUsername(), false);
                    pos[1] = server.askWindowPos(player.getUsername(), true);
                    pos[2] = server.askWindowPos(player.getUsername(), false);
                    pos[3] = server.askWindowPos(player.getUsername(), true);
                    t = new ToolCard04(player.getWindowFrame(), pos);
                    g.useToolCard(t, player, posToolCard);
                    g.setToolUSED(player,posToolCard);
                    break;
                }
                case 5: {
                    if (g.getCurrentRound() == 1)
                        throw new RulesBreakException(RulesBreakException.CODE.EMPTYTRACK);
                    int dpos = server.askDraftPos(player.getUsername());
                    int[] rpos = server.askRoundTrackPos( player.getUsername());
                    t = new ToolCard05(g.getDraft(), g.getTrack(), dpos, rpos[0], rpos[1]);
                    g.useToolCard(t, player, posToolCard);
                    g.setToolUSED(player,posToolCard);
                    break;
                }
                case 6: {
                    if (player.getMoves() != Moves.DICEPLACED) {
                        int dpos = 0;
                        dpos = server.askDraftPos(player.getUsername());

                        try {
                            t = new ToolCard06(player.getWindowFrame(), dpos, g.getDraft());
                            g.useToolCard(t, player, posToolCard); //se il dado con il nuovo numero è piazzabile
                            g.setToolUSED(player,posToolCard);

                        } catch (NotValidCellException e) {
                            g.setToolUSED(player,posToolCard);
                            throw e;
                        }

                        boolean placed = false;

                        while (!placed) {  //one position is available so the user has to place the dice.
                            try {
                                server.printDice(g.getDraft().getDraft().get(dpos), player.getUsername());
                                int pos1 = server.askWindowPos(player.getUsername(), true);
                                placeDice(dpos, pos1);
                                player.setMoves(Moves.BOTH);
                                placed = true;
                            } catch (RulesBreakException | CellNotEmptyException ignored) {
                            }
                        }
                    } else throw new RulesBreakException(RulesBreakException.CODE.MOVE);
                    break;
                }
                case 7: {
                    //Se è il secondo turno e il giocatore non ha ancora piazzato il dado puo usarla
                    if (g.getCurrentTurn() == Turns.SECOND && player.getMoves() != Moves.DICEPLACED) {
                        t = new ToolCard07(g.getDraft());
                        g.useToolCard(t, player, posToolCard);
                        g.setToolUSED(player,posToolCard);
                    } else throw new RulesBreakException(RulesBreakException.CODE.FIRSTTURN);

                    break;
                }
                case 8: {
                    //se è il primo turno del player
                    if (player.getCurrentTurn() == Turns.ZERO) {
                        if (player.getMoves() != Moves.DICEPLACED)
                            throw new RulesBreakException(RulesBreakException.CODE.NEEDPLACE);
                        int dpos = server.askDraftPos(player.getUsername());
                        int pos1 = server.askWindowPos(player.getUsername(), true);
                        t = new ToolCard08(player.getWindowFrame(), g.getDraft(), dpos, pos1);
                        g.useToolCard(t, player, posToolCard); //il player salta il secondo turno
                        player.setCurrentTurn(Turns.SECOND);
                        g.setToolUSED(player,posToolCard);
                    } else throw new RulesBreakException(RulesBreakException.CODE.SECONDTURN);
                    break;
                }
                case 9: {
                    if (player.getMoves() != Moves.DICEPLACED) {
                        int dpos = server.askDraftPos(player.getUsername());
                        int pos1 = server.askWindowPos(player.getUsername(), true);
                        t = new ToolCard09(g.getDraft(), dpos, player.getWindowFrame(), pos1);
                        g.useToolCard(t, player, posToolCard);
                        g.setToolUSED(player,posToolCard);
                        player.setMoves(Moves.BOTH);
                    } else throw new RulesBreakException(RulesBreakException.CODE.MOVE);

                    break;
                }
                case 10: {
                    int dpos = server.askDraftPos(player.getUsername());
                    //t = new ToolCard10(g.getDraft().getDraft().get(dpos));
                    t = new ToolCard10(g.getDraft(), dpos);
                    g.useToolCard(t, player, posToolCard);
                    g.setToolUSED(player,posToolCard);
                    break;
                }
                case 11: {

                    if (player.getMoves() != Moves.DICEPLACED) {
                        int dpos = server.askDraftPos(player.getUsername());

                        if(!((g.getToolTokens().get(posToolCard) == 0 && player.getNumTokens() >= 1)||(g.getToolTokens().get(posToolCard) > 0 && player.getNumTokens() > 1))) {
                            throw new RulesBreakException(RulesBreakException.CODE.TOKEN);
                        }

                        Dice diceFromDraft = g.getDraft().removeDice(dpos);
                        g.getDraft().getDiceBag().putDice(diceFromDraft);

                        Dice diceFromBag = g.getDraft().getDiceBag().extractDice();

                        try {
                            diceFromBag.setNumber(server.askNumber(diceFromBag.getColor(), player.getUsername()));
                            int pos1 = server.askWindowPos(player.getUsername(), true);
                            g.getDraft().addDice(diceFromBag);
                            t = new ToolCard11(player.getWindowFrame(), g.getDraft(), pos1);
                            g.useToolCard(t, player, posToolCard);
                            g.setToolUSED(player,posToolCard);
                            player.setMoves(Moves.BOTH);

                        } catch (EndTimerException | MoveStoppedException | RulesBreakException e) {
                            if(!g.getDraft().getDraft().contains(diceFromBag))
                                g.getDraft().addDice(diceFromBag);
                            g.setToolUSED(player,posToolCard);
                            throw e;
                        }

                    } else throw new RulesBreakException(RulesBreakException.CODE.MOVE);
                    break;
                }

                case 12: {
                    //how many = true -> 2 moves
                    //how many = false -> 1 move
                    if (g.getCurrentRound() == 1)
                        throw new RulesBreakException(RulesBreakException.CODE.EMPTYTRACK);
                    boolean howMany = server.askHowMany(player.getUsername());
                    int[] pos = new int[2];
                    pos[0] = server.askWindowPos(player.getUsername(), false);
                    pos[1] = server.askWindowPos(player.getUsername(), true);
                    if (howMany) {
                        int[] addingpos = new int[2];
                        addingpos[0] = server.askWindowPos(player.getUsername(), false);
                        addingpos[1] = server.askWindowPos(player.getUsername(), true);
                        pos = concatInt(pos, addingpos);
                    }
                    t = new ToolCard12(player.getWindowFrame(), g.getTrack(), pos);
                    g.useToolCard(t, player, posToolCard);
                    g.setToolUSED(player,posToolCard);
                    break;

                }
            }
        }catch(MoveStoppedException ignored){
        }
    }

    /**
     * notify the user that the time for his turn has terminated
     */
    public void endTimer(){
        server.endTimer(player.getUsername());
        endTurn();
    }


    /**
     * Notify the user that his turn has begun
     */
    public void play(){

        this.isPlaying = true;
        server.startTurn(player.getUsername());

    }


    /**
     * It terminate the turn of the user
     */
    public void endTurn(){

        synchronized (monitor) {
            this.isPlaying = false;
            monitor.notifyAll();
        }
    }

    /**
     * It terminate the match and send to the user the final rank
     * @param ranking rank of the players
     */
    public void endGame(ArrayList<Integer> ranking) {
        server.endGame(player.getUsername(),ranking);
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public Player getPlayer() {
        return player;
    }

    public Server getServer() {
        return server;
    }

    public void setG(Game g) {
        this.g = g;
    }

    public synchronized Object getMonitor() {
        return monitor;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * compare player controllers w.r.t the username
     * @param o playercontroller to compare
     * @return True if the username is the same, False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if(o == null || o.getClass() != this.getClass())
            return false;
        PlayerController pc = (PlayerController) o;
        return this.player.getUsername().equals(pc.getPlayer().getUsername());
    }

    @Override
    public int hashCode(){
        return Objects.hash(player.getUsername());
    }
}


