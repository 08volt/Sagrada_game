package progettoIngSW;




import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import progettoIngSW.Controller.GameController;
import progettoIngSW.Controller.PlayerController;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;
import progettoIngSW.Network.Server.Server;


import java.io.IOException;
import java.util.ArrayList;


public class ControllerTest {

    private GameController gameController;
    private Game game;
    private PlayerController pc1,pc2,pc3,pc4;
    private Server server;

    @Before
    public void before() throws EndTimerException, MoveStoppedException, FullLobbyException, IOException {

        server = mock(Server.class);


        pc1 = new PlayerController("uno","1",server);
        pc2 = new PlayerController("due","2",server);
        pc3 = new PlayerController("tre","3",server);
        pc4 = new PlayerController("quattro","4",server);


        when(server.askWindowPos("uno",false)).thenReturn(0);
        when(server.askWindowPos("uno",true)).thenReturn(5);
        when(server.askWindowPos("due",false)).thenReturn(0);
        when(server.askWindowPos("due",true)).thenReturn(5);
        when(server.askWindowPos("tre",false)).thenReturn(0);
        when(server.askWindowPos("tre",true)).thenReturn(5);
        when(server.askWindowPos("quattro",false)).thenReturn(0);
        when(server.askWindowPos("quattro",true)).thenReturn(5);


        when(server.askDraftPos("uno")).thenReturn(0);
        when(server.askDraftPos("due")).thenReturn(1);
        when(server.askDraftPos("tre")).thenReturn(2);
        when(server.askDraftPos("quattro")).thenReturn(3);

        when(server.askHowMany("uno")).thenReturn(true);
        when(server.askHowMany("due")).thenReturn(true);
        when(server.askHowMany("tre")).thenReturn(true);
        when(server.askHowMany("quattro")).thenReturn(false);

        when(server.askIncrease("uno")).thenReturn(true);
        when(server.askIncrease("due")).thenReturn(true);
        when(server.askIncrease("tre")).thenReturn(true);
        when(server.askIncrease("quattro")).thenReturn(true);

        when(server.askNumber(Colors.YELLOW,"uno")).thenReturn(1);
        when(server.askNumber(Colors.BLUE,"uno")).thenReturn(2);
        when(server.askNumber(Colors.GREEN,"uno")).thenReturn(3);
        when(server.askNumber(Colors.RED,"uno")).thenReturn(4);
        when(server.askNumber(Colors.PURPLE,"uno")).thenReturn(1);



        int[] trackPos = new int[2];
        trackPos[0] = 1;
        trackPos[1] = 1;
        when(server.askRoundTrackPos("uno")).thenReturn(trackPos);
        when(server.askRoundTrackPos("due")).thenReturn(trackPos);
        when(server.askRoundTrackPos("tre")).thenReturn(trackPos);
        when(server.askRoundTrackPos("quattro")).thenReturn(trackPos);

        when(server.askWhichPattern(new Pattern[4],"uno")).thenReturn(0);
        when(server.askWhichPattern(new Pattern[4],"due")).thenReturn(0);
        when(server.askWhichPattern(new Pattern[4],"tre")).thenReturn(0);
        when(server.askWhichPattern(new Pattern[4],"quattro")).thenReturn(0);


        Game.resetGame();
        game = Game.getGame();
        game.setGameStarted(false);
        game.setPlayers(new ArrayList<>());
        GameController.resetGameController();
        gameController = GameController.getGameController();
        assertEquals("Game diverso",game,gameController.getGame());
        GameController.setPlayerControllers(new ArrayList<>());
        gameController.setTimers(0,0,0);
        gameController.registerPlayer(pc1);
        gameController.registerPlayer(pc2);
        gameController.registerPlayer(pc3);
        gameController.registerPlayer(pc4);
        assertEquals("Player non aggiunti",4,gameController.getPlayerControllers().size());
    }



    @Test
    public  void shouldResetGameController(){
        GameController gc =GameController.getGameController();
        GameController gc2 =GameController.getGameController();

        //TEST PRIMA DEL RESET
        assertEquals("Game Controller non e' singleton", gc, gc2);
        assertNotNull(gc.getPlayerControllers());
        assertEquals("Numero player controller sbagliato", 4, gc.getPlayerControllers().size());

        GameController.resetGameController();
        gc =GameController.getGameController();
        gc2 =GameController.getGameController();
        assertEquals("Dopo il reset, Game Controller non e' singleton", gc, gc2);
        assertNotNull(gc.getPlayerControllers());
        assertEquals("Numero player controller sbagliato", 0, gc.getPlayerControllers().size());

    }

    @Test
    public void checkUnregister_Player() {

        gameController.unregisterPlayer(pc4);

        assertEquals("Numero player controller non modificato", 3,gameController.getPlayerControllers().size());
        assertEquals("Numero player non modificato", 3,game.getPlayers().size());
        for(PlayerController pc : gameController.getPlayerControllers())
            assertNotEquals("Utente non eliminato dalla lista dei controllers",pc4,pc);
        for(Player p : game.getPlayers())
            assertNotEquals("Utente non eliminato dalla lista di player",pc4.getPlayer(),p);
    }

    @Test
    public void checkAssignPattern(){
        gameController = GameController.getGameController();
        int remainingPattern = (24-gameController.getPlayerControllers().size()*4);

        for(PlayerController pc : gameController.getPlayerControllers())
            assertNull("Pattern gia' assegnati",pc.getPlayer().getWindowFrame());
        gameController.assignPatterns();
        for(PlayerController pc : gameController.getPlayerControllers())
            assertNotNull("Pattern non assegnati correttamente",pc.getPlayer().getWindowFrame());
        assertTrue("Numero pattern assegnati non corretto",Game.getPatterns().size()>=remainingPattern);

    }

    @Test
    public void checkRun(){
        game.setGameStarted(true);
        assertEquals("Numero player non corretto", 4, game.getPlayers().size());
        assertNotNull(game.getToolCards());
        assertEquals("Toolcard gia' inizializzate", 0, game.getToolCards().size());

        assertNotNull(game.getPublicObjectiveCards());
        assertEquals("Numero publicObject gia' inizializzate", 0, game.getPublicObjectiveCards().size());

        assertEquals("Numero round gia' inizializzate", 0,game.getCurrentRound());


        gameController.play();
        assertEquals("Numero player non corretto", 4, game.getPlayers().size());
        assertNotNull(game.getToolCards());
        assertEquals("Numero toolcard non corretto", 3, game.getToolCards().size());

        assertNotNull(game.getPublicObjectiveCards());
        assertEquals("Numero publicObject non corretto", 3, game.getPublicObjectiveCards().size());

        assertEquals("Numero round non corretto (partita non terminata)", 11,game.getCurrentRound());


        for(int i=0; i<game.getPlayers().size()-1;i++)
            assertTrue("Ranking non corretto",game.getPlayers().get(game.getRanking().get(i)).getScore()>=game.getPlayers().get(game.getRanking().get(i+1)).getScore());
    }

    @Test
    public void checkOnlyOnePlayer(){

        assertEquals("Numero player non corretto", 4, game.getPlayers().size());
        assertEquals("Numero player non corretto", 4, gameController.getPlayerControllers().size());


        gameController.unregisterPlayer(pc4);
        assertTrue("Partita terminata in anticipo", game.isGameStarted());
        gameController.unregisterPlayer(pc3);
        gameController.unregisterPlayer(pc2);

        assertEquals("Numero player non corretto", 1, game.getPlayers().size());
        assertEquals("Numero player non corretto", 1, gameController.getPlayerControllers().size());
        assertTrue("Partita terminata senza decretare il vincitore", game.isGameStarted());

        gameController.endGameWithWinner(0);
        assertEquals("Rimasto un solo giocatore ma non ha vinto",gameController.getPlayerControllers().get(game.getRanking().get(0)),pc1);
        assertFalse("La partita sarebbe dovuta terminare", game.isGameStarted());

        gameController.unregisterPlayer(pc1);
        assertEquals("Numero player non corretto", 0, game.getPlayers().size());
        assertEquals("Numero player non corretto", 0, gameController.getPlayerControllers().size());

    }

    @Test
    public void checkPlaceDice() throws EndTimerException, MoveStoppedException, DiceNotFoundException, CellNotEmptyException {
        gameController.play();

        try {
            pc1.placeDice(0,0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (NotPlayingException e) {
            assertEquals("Non e' il turno del giocatore ma piazza il dado",e.getMessage(),"Player uno is not your turn");
        }

        Player curPlayer = gameController.getCurrentPlayerController().getPlayer();
        gameController.getCurrentPlayerController().play();

        int draftPos = server.askDraftPos(curPlayer.getUsername());
        for(int i = 0; i< (curPlayer.getWindowFrame().getCol()*curPlayer.getWindowFrame().getRow()); i++) {
            try {
                Rules.checkCell(curPlayer.getWindowFrame(),game.getDraft().getDraft().get(draftPos),i);
                if (curPlayer.getWindowFrame().getCell(i).getDice() == null) {
                    gameController.getCurrentPlayerController().placeDice(draftPos,i);
                    assertNotNull("Dado non posizionato",curPlayer.getWindowFrame().getCell(i));
                    assertTrue("Numero dadi non corretto",curPlayer.getWindowFrame().numberOfDice()==1);
                    break;
                }
            } catch (RulesBreakException e) {
                continue;
            } catch (NotPlayingException e) {
                e.printStackTrace();
            }
        }
        assertEquals("Mossa non aggiornata",Moves.DICEPLACED,curPlayer.getMoves());


        try {
            gameController.getCurrentPlayerController().placeDice(0,0);
        } catch (RulesBreakException e) {
            assertEquals("Player ha gia' posizionato il dado",RulesBreakException.CODE.MOVE,e.getType());
        } catch (NotPlayingException e) {
            e.printStackTrace();
        }

        curPlayer.setMoves(Moves.BOTH);
        try {
            gameController.getCurrentPlayerController().placeDice(0,0);
        } catch (RulesBreakException e) {
            assertEquals("Player ha gia' posizionato il dado",RulesBreakException.CODE.MOVE,e.getType());
        } catch (NotPlayingException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void shouldNotUseToolCard() throws DiceNotFoundException, CellNotEmptyException, EndTimerException {

        gameController.play();

        try {
            try {
                pc1.useToolCard(0);
            } catch (EndTimerException e) {
                e.printStackTrace();
            }
        } catch (NotPlayingException e) {
            assertEquals("Non e' il turno del giocatore ma tool utilizzata",e.getMessage(),"Player uno is not your turn");
        } catch (DraftFullException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (InvalidParamsException e) {
            e.printStackTrace();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        }


        Player curPlayer = gameController.getCurrentPlayerController().getPlayer();
        gameController.getCurrentPlayerController().play();

        curPlayer.setMoves(Moves.TOOLUSED);
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (RulesBreakException e) {
            assertEquals("Player ha gia' utilizzato una tool in questo turno",RulesBreakException.CODE.MOVE,e.getType());
        } catch (NotPlayingException e) {
            e.printStackTrace();
        } catch (DraftFullException e) {
            e.printStackTrace();
        } catch (InvalidParamsException e) {
            e.printStackTrace();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        }
        curPlayer.setMoves(Moves.BOTH);
        try {
            gameController.getCurrentPlayerController().useToolCard(0);
        } catch (RulesBreakException e) {
            assertEquals("Player ha gia' effettuato entrambe le mosse e riutilizza una tool",RulesBreakException.CODE.MOVE,e.getType());
        } catch (NotPlayingException e) {
            e.printStackTrace();
        } catch (DraftFullException e) {
            e.printStackTrace();
        } catch (InvalidParamsException e) {
            e.printStackTrace();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        }

        curPlayer.setMoves(Moves.NONE);
        ArrayList<Integer> toolTokens = new ArrayList<>();
        toolTokens.add(2);
        toolTokens.add(2);
        toolTokens.add(2);
        game.getToolCards().set(1,1);
        game.setToolTokens(toolTokens);
        curPlayer.setNumTokens(1);
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (NotPlayingException e) {
            e.printStackTrace();
        } catch (RulesBreakException e) {
            assertEquals("Numero tokens non sufficienti ma carta utilizzata",RulesBreakException.CODE.TOKEN,e.getType());
        } catch (InvalidParamsException e) {
            e.printStackTrace();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        } catch (DraftFullException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void checkUseTool_123() throws IOException, DiceNotFoundException, CellNotEmptyException, NotPlayingException, InvalidParamsException, DraftFullException, NotValidCellException, EndTimerException, MoveStoppedException {
        gameController.play();

        ArrayList<Integer> toolToBeTested = new ArrayList<>();
        toolToBeTested.add(1);
        toolToBeTested.add(2);
        toolToBeTested.add(3);
        game.setToolCards(toolToBeTested);
        game.setCurrentPlayer(0);
        Player curPlayer = gameController.getCurrentPlayerController().getPlayer();
        curPlayer.setMoves(Moves.NONE);
        gameController.getCurrentPlayerController().play();
        curPlayer.setWindowFrame(new WindowFrame(new Pattern(0)));

        //TOOL1
        int draftPos = server.askDraftPos(curPlayer.getUsername());
        game.getDraft().getDraft().get(draftPos).setNumber(4);
        try {
            gameController.getCurrentPlayerController().useToolCard(0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Valore dado non modificato",5,game.getDraft().getDraft().get(draftPos).getNumber());
        assertEquals("Mossa non aggiornata",Moves.TOOLUSED,curPlayer.getMoves());

        //
        curPlayer.setMoves(Moves.NONE);

        //TOOL2
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (RulesBreakException e) {
            assertEquals("Uso tool card 2 non consntio ma effettuato",RulesBreakException.CODE.EMPTYWF,e.getType());
        }
        Dice d2 = new Dice(Colors.YELLOW);
        try {
            curPlayer.getWindowFrame().placeDice(d2,0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        curPlayer.setMoves(Moves.DICEPLACED);
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Dopo lo spostamento il numero di dadi è cambiato",1,curPlayer.getWindowFrame().numberOfDice());
        assertEquals("Dado non spostato",d2,curPlayer.getWindowFrame().getCell(5).getDice());
        assertNull("Dado non spostato",curPlayer.getWindowFrame().getCell(0).getDice());
        assertEquals("Mossa non aggiornata",Moves.BOTH,curPlayer.getMoves());


        //
        curPlayer.setMoves(Moves.NONE);
        gameController.getCurrentPlayerController().endTurn();
        game.setCurrentPlayer(1);
        curPlayer = gameController.getCurrentPlayerController().getPlayer();
        gameController.getCurrentPlayerController().play();
        curPlayer.setWindowFrame(new WindowFrame(new Pattern(1)));
        curPlayer.setMoves(Moves.NONE);

        //TOOL3
        Dice d3 = new Dice(Colors.PURPLE);
        d3.setNumber(2);
        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        } catch (RulesBreakException e) {
            assertEquals("Uso tool card 3 non consntio ma effettuato",RulesBreakException.CODE.EMPTYWF,e.getType());
        }

        try {
            curPlayer.getWindowFrame().placeDice(d3,0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        curPlayer.setMoves(Moves.DICEPLACED);
        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Dopo lo spostamento il numero di dadi è cambiato",1,curPlayer.getWindowFrame().numberOfDice());
        assertEquals("Dado non spostato",d3,curPlayer.getWindowFrame().getCell(5).getDice());
        assertNull("Dado non spostato",curPlayer.getWindowFrame().getCell(0).getDice());
        assertEquals("Mossa non aggiornata",Moves.BOTH,curPlayer.getMoves());
    }

    @Test
    public void checkUseTool_456() throws IOException, CellNotEmptyException, NotPlayingException, InvalidParamsException, DraftFullException, EndTimerException, MoveStoppedException {
        gameController.play();


        ArrayList<Integer> toolToBeTested = new ArrayList<>();
        toolToBeTested.add(4);
        toolToBeTested.add(5);
        toolToBeTested.add(6);
        game.setToolCards(toolToBeTested);
        game.setCurrentPlayer(0);
        Player curPlayer = gameController.getCurrentPlayerController().getPlayer();
        curPlayer.setMoves(Moves.NONE);
        gameController.getCurrentPlayerController().play();
        curPlayer.setWindowFrame(new WindowFrame(new Pattern(3)));

        //TOOL4
        Dice d4_1 = new Dice(Colors.PURPLE);
        d4_1.setNumber(5);
        Dice d4_2 = new Dice(Colors.BLUE);
        d4_2.setNumber(6);
        try {
            gameController.getCurrentPlayerController().useToolCard(0);
        } catch (RulesBreakException e) {
            assertEquals("Uso tool card 4 non consntio ma effettuato",RulesBreakException.CODE.EMPTYWF,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        try {
            curPlayer.getWindowFrame().placeDice(d4_1,0);
            curPlayer.getWindowFrame().placeDice(d4_2,1);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        curPlayer.setMoves(Moves.DICEPLACED);
        try {
            gameController.getCurrentPlayerController().useToolCard(0);
        } catch (RulesBreakException | NotValidCellException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException  e) {
            assertEquals("Tool card utilizzata nonostante parametri non consentiti","DICE NOT FOUND",e.getMessage());
        }
        assertEquals("Mossa aggiornata nonostante l'uso della tool non sia terminato",Moves.DICEPLACED,curPlayer.getMoves());
        assertEquals("Numero dadi cambiato dopo l'utilizzo della tool 4",2,curPlayer.getWindowFrame().numberOfDice());
        assertNull("Utilizzo tool 4 interrotto ma dado spostato",curPlayer.getWindowFrame().getCell(5).getDice());
        assertEquals("Utilizzo tool 4 interrotto ma dado in (0,0) spostato",d4_1,curPlayer.getWindowFrame().getCell(0).getDice());
        assertEquals("Utilizzo tool 4 interrotto ma dado in (0,1) spostato",d4_2,curPlayer.getWindowFrame().getCell(1).getDice());

        //TOOL5
        game.setCurrentRound(1);
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (RulesBreakException e) {
            assertEquals("La tool 5 e' utilizzata con il track vuoto",RulesBreakException.CODE.EMPTYTRACK,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        game.setCurrentRound(2);


        int trackLength = game.getTrack().getDice(1).size();
        Dice d5Track = game.getTrack().getDice(1).get(1);
        Dice d5Draft = game.getDraft().getDraft().get(server.askDraftPos(curPlayer.getUsername()));
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (RulesBreakException | DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Dado track non aggiunto alla draft",d5Track,game.getDraft().getDraft().get(game.getDraft().getDraft().size()-1));
        assertEquals("Dado draft non aggiunto alla tack",d5Draft,game.getTrack().getDice(1).get(game.getDraft().getDraft().size()-1));
        assertEquals("Lunghezza track cambiata",trackLength,game.getTrack().getDice(1).size());
        assertEquals("Lunghezza draft cambiata",9,game.getDraft().getDraft().size());
        for(Dice d : game.getDraft().getDraft())
            assertNotEquals("Dado non rimosso dalla draft",d,d5Draft);
        for(Dice d : game.getTrack().getDice(1))
            assertNotEquals("Dado non rimosso dalla track",d,d5Track);


        //TOOL6
        curPlayer.setMoves(Moves.DICEPLACED);
        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        } catch (RulesBreakException e) {
            assertEquals("La tool 6 e' utilizzata nonostante il giocatore avessia gia' piazzato un dado",RulesBreakException.CODE.MOVE,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }

        curPlayer.setMoves(Moves.NONE);
        try {
            curPlayer.getWindowFrame().removeDice(0);
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        }
        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        } catch (RulesBreakException | DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Lunghezza draft non aggiornata",8,game.getDraft().getDraft().size());
        assertNotNull("Utilizzo tool 6 corretto ma dado non posizionato",curPlayer.getWindowFrame().getCell(5).getDice());
        assertEquals("Numero dadi wf non corretto dopo l'utilizzo della tool 6",2,curPlayer.getWindowFrame().numberOfDice());
        assertEquals("Mossa non aggiornata",Moves.BOTH,curPlayer.getMoves());
    }


    @Test
    public void shouldNotPlaceDice_Tool6() throws IOException {
        gameController.play();


        ArrayList<Integer> toolToBeTested = new ArrayList<>();
        toolToBeTested.add(4);
        toolToBeTested.add(5);
        toolToBeTested.add(6);
        game.setToolCards(toolToBeTested);
        game.setCurrentPlayer(0);
        Player curPlayer = gameController.getCurrentPlayerController().getPlayer();
        curPlayer.setMoves(Moves.NONE);
        gameController.getCurrentPlayerController().play();
        curPlayer.setWindowFrame(new WindowFrame(new Pattern(0)));

        ArrayList<Dice> dices = new ArrayList<>();
        dices.add(new Dice(Colors.YELLOW));
        dices.add(new Dice(Colors.BLUE));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.PURPLE));
        dices.add(new Dice(Colors.YELLOW));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.PURPLE));
        dices.add(new Dice(Colors.BLUE));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.BLUE));
        dices.add(new Dice(Colors.RED));
        dices.add(new Dice(Colors.YELLOW));
        dices.add(new Dice(Colors.RED));
        dices.add(new Dice(Colors.PURPLE));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.BLUE));
        dices.add(new Dice(Colors.PURPLE));
        dices.add(new Dice(Colors.GREEN));
        dices.add(new Dice(Colors.BLUE));
        dices.add(new Dice(Colors.YELLOW));
        dices.get(0).setNumber(1);
        dices.get(1).setNumber(2);
        dices.get(2).setNumber(3);
        dices.get(3).setNumber(4);
        dices.get(4).setNumber(1);
        dices.get(5).setNumber(2);
        dices.get(6).setNumber(4);
        dices.get(7).setNumber(5);
        dices.get(8).setNumber(1);
        dices.get(9).setNumber(4);
        dices.get(10).setNumber(3);
        dices.get(11).setNumber(1);
        dices.get(12).setNumber(2);
        dices.get(13).setNumber(3);
        dices.get(14).setNumber(6);
        dices.get(15).setNumber(2);
        dices.get(16).setNumber(6);
        dices.get(17).setNumber(5);
        dices.get(18).setNumber(2);
        dices.get(19).setNumber(3);
        for(int i=0;i<dices.size();i++) {
                try {
                    curPlayer.getWindowFrame().placeDice(dices.get(i), i);
                } catch (CellNotEmptyException | RulesBreakException e) {
                    e.printStackTrace();
                }
        }
        int draftLength = game.getDraft().getDraft().size();
        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        } catch (NotPlayingException | RulesBreakException | CellNotEmptyException  e) {
            e.printStackTrace();
        } catch (InvalidParamsException | DraftFullException | DiceNotFoundException e) {
            e.printStackTrace();
        } catch (NotValidCellException e) {
            assertEquals("Dado non puo' essere piazzato",NotValidCellException.class,e.getClass());
        } catch (EndTimerException e) {
            e.printStackTrace();
        }
        assertEquals("Mossa non aggiornata",Moves.TOOLUSED,curPlayer.getMoves());
        assertEquals("Dado non piazzato ma lunghezza draft variata",draftLength,game.getDraft().getDraft().size());
    }

    @Test
    public void checkUseTool_789() throws IOException, CellNotEmptyException, NotPlayingException, InvalidParamsException, DraftFullException, EndTimerException, MoveStoppedException {
        gameController.play();


        ArrayList<Integer> toolToBeTested = new ArrayList<>();
        toolToBeTested.add(7);
        toolToBeTested.add(8);
        toolToBeTested.add(9);
        game.setToolCards(toolToBeTested);
        game.setCurrentPlayer(0);
        Player curPlayer = gameController.getCurrentPlayerController().getPlayer();
        curPlayer.setMoves(Moves.NONE);
        gameController.getCurrentPlayerController().play();
        curPlayer.setWindowFrame(new WindowFrame(new Pattern(3)));

        //TOOL7
        //test funzionamento non corretto
        game.setCurrentTurn(Turns.FIRST);
        curPlayer.setMoves(Moves.NONE);
        try {
            gameController.getCurrentPlayerController().useToolCard(0);
        } catch (RulesBreakException e) {
            assertEquals("Carta utilizzata ma turno corrente = FIRST",RulesBreakException.CODE.FIRSTTURN,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        game.setCurrentTurn(Turns.SECOND);
        curPlayer.setMoves(Moves.DICEPLACED);
        try {
            gameController.getCurrentPlayerController().useToolCard(0);
        } catch (RulesBreakException e) {
            assertEquals("Carta utilizzata ma turno corrente = FIRST",RulesBreakException.CODE.FIRSTTURN,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        //TOOL7
        //test funzionamento  corretto
        int draftLength = game.getDraft().getDraft().size();
        game.setCurrentTurn(Turns.SECOND);
        curPlayer.setMoves(Moves.NONE);
        try {
            gameController.getCurrentPlayerController().useToolCard(0);
        } catch (RulesBreakException | DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Lunghezza draft variata",draftLength,game.getDraft().getDraft().size());
        assertEquals("Mossa non aggiornata",Moves.TOOLUSED,curPlayer.getMoves());

        //TOOL8
        //test funzionamento non corretto
        curPlayer.setCurrentTurn(Turns.SECOND);
        curPlayer.setMoves(Moves.NONE);
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (RulesBreakException e) {
            assertEquals("Carta utilizzata ma turno corrente = ZERO",RulesBreakException.CODE.SECONDTURN,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        curPlayer.setCurrentTurn(Turns.ZERO);
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (RulesBreakException e) {
            assertEquals("Carta utilizzata senza aver piazzato un dado",RulesBreakException.CODE.NEEDPLACE,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }

        //TOOL8
        //test funzionamento corretto
        try {
            gameController.getCurrentPlayerController().placeDice(0,1);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        }
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (RulesBreakException | DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Turno giocatore non aggiornato",Turns.SECOND,curPlayer.getCurrentTurn());
        assertEquals("Mossa giocatore non aggiornata",Moves.BOTH,curPlayer.getMoves());

        //TOOL9
        //test funzionamento non corretto
        curPlayer.setMoves(Moves.DICEPLACED);
        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        }catch (RulesBreakException e) {
            assertEquals("Carta utilizzata dopo aver piazzato un dado",RulesBreakException.CODE.MOVE,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }

        //TOOL9
        //test funzionamento corretto
        curPlayer.setWindowFrame(new WindowFrame(new Pattern(3)));
        try {
            curPlayer.getWindowFrame().placeDice(new Dice(Colors.GREEN),2);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        curPlayer.setMoves(Moves.NONE);
        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Numero dadi wf non aggiornato",2,curPlayer.getWindowFrame().numberOfDice());
        assertNotNull("Dado non piazzato (tool 9)",curPlayer.getWindowFrame().getCell(5).getDice());
        assertEquals("Mosse non aggiornate dopo uso tool 9",Moves.BOTH,curPlayer.getMoves());
    }


    @Test
    public void checkUseTool_10_11_12() throws IOException, CellNotEmptyException, NotPlayingException, InvalidParamsException, DraftFullException, EndTimerException, MoveStoppedException {
        gameController.play();

        ArrayList<Integer> toolToBeTested = new ArrayList<>();
        toolToBeTested.add(10);
        toolToBeTested.add(11);
        toolToBeTested.add(12);
        game.setToolCards(toolToBeTested);
        game.setCurrentPlayer(0);
        Player curPlayer = gameController.getCurrentPlayerController().getPlayer();
        curPlayer.setMoves(Moves.NONE);
        gameController.getCurrentPlayerController().play();
        game.setCurrentRound(1);
        curPlayer.setWindowFrame(new WindowFrame(new Pattern(3)));

        //TOOL10
        int numDraftDice = game.getDraft().getDraft().get(server.askDraftPos(curPlayer.getUsername())).getNumber();
        try {
            gameController.getCurrentPlayerController().useToolCard(0);
        } catch (RulesBreakException | DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Dado non girato",(7-numDraftDice),game.getDraft().getDraft().get(server.askDraftPos(curPlayer.getUsername())).getNumber());
        assertEquals("Mossa non aggiornata dopo uso tool 10",Moves.TOOLUSED,curPlayer.getMoves());


        //TOOL11
        Dice diceFromBag = new Dice(Colors.GREEN);
        game.getDraft().getDiceBag().putDice(diceFromBag);
        int draftLength = game.getDraft().getDraft().size();
        int diceBagLength = game.getDraft().getDiceBag().getDiceBag().size();
        //test funzionamento non corretto
        curPlayer.setMoves(Moves.DICEPLACED);
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        }catch (RulesBreakException e) {
            assertEquals("Carta utilizzata senza aver piazzato un dado",RulesBreakException.CODE.MOVE,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Lunghezza draft variata",draftLength,game.getDraft().getDraft().size());
        assertEquals("Lunghezza dicebag variata",diceBagLength,game.getDraft().getDiceBag().getDiceBag().size());

        //TOOL11
        //test funzionamento corretto
        curPlayer.setMoves(Moves.NONE);
        Dice draftDice = game.getDraft().getDraft().get(server.askDraftPos(curPlayer.getUsername()));
        draftLength = game.getDraft().getDraft().size();
        diceBagLength = game.getDraft().getDiceBag().getDiceBag().size();
        try {
            gameController.getCurrentPlayerController().useToolCard(1);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        } catch (DiceNotFoundException e) {
            e.printStackTrace();
        } catch (NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Mossa non aggiornata dopo uso tool 11",Moves.BOTH,curPlayer.getMoves());
        assertFalse("Dado draft ancora presente nel draft",game.getDraft().getDraft().contains(draftDice));
        assertEquals("Lunghezza draft non aggiornata",draftLength-1,game.getDraft().getDraft().size());
        assertEquals("Lunghezza dicebag variata",diceBagLength,game.getDraft().getDiceBag().getDiceBag().size());

        //TOOL12
        //test funzionamento non corretto
        game.setCurrentRound(1);
        gameController.getCurrentPlayerController().endTurn();
        game.setCurrentPlayer(game.getPlayers().indexOf(pc1.getPlayer()));
        curPlayer = game.getPlayers().get(game.getCurrentPlayer());
        curPlayer.setMoves(Moves.NONE);
        gameController.getCurrentPlayerController().play();
        curPlayer.setWindowFrame(new WindowFrame(new Pattern(0)));
        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        } catch (RulesBreakException e) {
            assertEquals("Tool 12 non puo' essere utilizzata al primo round",RulesBreakException.CODE.EMPTYTRACK,e.getType());
        } catch (DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Mossa cambiata dopo tool 12 (uso non riuscito)",Moves.NONE,curPlayer.getMoves());
        game.setCurrentRound(2);
        Dice d12_KO = new Dice(Colors.YELLOW);
        try {
            curPlayer.getWindowFrame().placeDice(d12_KO,0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Posizionamento non avvenuto per testare TOOL12",1,curPlayer.getWindowFrame().numberOfDice());
        assertEquals("Posizionamento non avvenuto per testare TOOL12",d12_KO,curPlayer.getWindowFrame().getCell(0).getDice());
        curPlayer.setMoves(Moves.NONE);
        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        } catch (RulesBreakException | NotValidCellException | DiceNotFoundException e) {
            assertEquals("Mossa non consentita ma effettuata(tool 12)", RulesBreakException.class, e.getClass());
        }
        assertEquals("Uso non corretto Tool 12 ma mosse aggiornate",Moves.NONE,curPlayer.getMoves());
        assertEquals("Spostamento tool 12 non consentito ma avvenuto",d12_KO,curPlayer.getWindowFrame().getCell(0).getDice());
        assertNull("Spostamento tool 12 non consentito ma avvenuto",curPlayer.getWindowFrame().getCell(5).getDice());

        //TOOL12
        //test funzionamento corretto
        game.setCurrentRound(3);
        gameController.getCurrentPlayerController().endTurn();
        game.setCurrentPlayer(game.getPlayers().indexOf(pc4.getPlayer()));
        curPlayer = game.getPlayers().get(game.getCurrentPlayer());
        gameController.getCurrentPlayerController().play();
        curPlayer.setWindowFrame(new WindowFrame(new Pattern(3)));
        Colors tmpColor = game.getTrack().getDice(3).get(1).getColor();
        Dice d12 = new Dice(tmpColor);
        try {
            curPlayer.getWindowFrame().placeDice(d12,0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Posizionamento non avvenuto per TOOL12",1,curPlayer.getWindowFrame().numberOfDice());
        assertEquals("Posizionamento non avvenuto per TOOL12",d12,curPlayer.getWindowFrame().getCell(0).getDice());
        curPlayer.setMoves(Moves.NONE);


        try {
            gameController.getCurrentPlayerController().useToolCard(2);
        } catch (RulesBreakException | DiceNotFoundException | NotValidCellException e) {
            e.printStackTrace();
        }
        assertEquals("Numero dadi su wf cambiato dopo uso tool 12",1,curPlayer.getWindowFrame().numberOfDice());
        assertEquals("Spostamento tool 12 consentito ma non avvenuto",d12,curPlayer.getWindowFrame().getCell(5).getDice());
        assertNull("Spostamento tool 12 consentito ma non avvenuto",curPlayer.getWindowFrame().getCell(0).getDice());
        assertEquals("Uso corretto Tool 12 ma mosse non aggiornate",Moves.TOOLUSED,curPlayer.getMoves());

    }

    @Test
    public void shouldNotBeEquals(){
        Object o = null;
        for(PlayerController pc : gameController.getPlayerControllers()){
            assertFalse(pc.equals(o));
        }
        o = new Object();
        for(PlayerController pc : gameController.getPlayerControllers()){
            assertFalse(pc.equals(o));
        }
    }
}