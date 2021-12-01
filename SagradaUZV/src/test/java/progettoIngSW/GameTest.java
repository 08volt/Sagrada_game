package progettoIngSW;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;

import java.util.ArrayList;

public class GameTest {

    @Before
    public void init() throws FullLobbyException {
        Game.resetGame();
        Game g = Game.getGame();
        g.setPlayers(new ArrayList<>());
        Player p1 = new Player("uno", "d");
        Player p2 = new Player("due", "e");
        Player p3 = new Player("tre", "f");
        Player p4 = new Player("quattro", "g");
        g.addPlayer(p1);
        g.addPlayer(p2);
        g.addPlayer(p3);
        g.addPlayer(p4);
    }


    @Test
    public void shouldSet_And_ResetGame(){

        //TEST PRIMA DEL RESET
        Game g = Game.getGame();
        Game g1 = Game.getGame();
        assertEquals("Game non e' singleton", g, g1);

        assertNotNull(g.getPlayers());
        assertEquals("Numero player non corretto", 4, g.getPlayers().size());
        g.gameSetup();
        assertNotNull(g.getToolCards());
        assertEquals("Numero toolcard non corretto", 3, g.getToolCards().size());

        assertNotNull(g.getPublicObjectiveCards());
        assertEquals("Numero publicObject non corretto", 3, g.getPublicObjectiveCards().size());

        assertEquals("Numero round non corretto", 1,g.getCurrentRound());

        assertEquals("Dimensione track non corretta",10,g.getTrack().getTrack().size());
        assertEquals("Dimensione draft non corretta",9,g.getDraft().getDraft().size());
        assertEquals("Dadi diceBag non esratti per la riserva",81,g.getDraft().getDiceBag().getDiceBag().size());

        Game.resetGame();
        g = Game.getGame();
        g1 = Game.getGame();
        assertEquals("Dopo il reset, Game non e' singleton", g, g1);

        assertEquals("Problemi reset: numero player non azzerato", 0, g.getPlayers().size());
        assertEquals("Problemi reset: numero toolcard non azzerato", 0, g.getToolCards().size());

        assertEquals("Problemi reset: numero publicObjCard non azzerato", 0, g.getPublicObjectiveCards().size());

        assertEquals("Problemi reset: round non azzerato", 0,g.getCurrentRound());
    }

    @Test
    public void shouldGenerateGame(){
        assertNotNull("Game is null", Game.getGame());
    }

    @Test
    public void shouldNotAddPlayer(){
        Game g = Game.getGame();
        Player p5 = new Player("cinque","h");
        try {
            g.addPlayer(p5);
        } catch (FullLobbyException e) {
            assertEquals("Giocatore aggiunto con lobby piena",e.getMessage(),"lobby is full");
        }
    }

    @Test
    public void checkExtractPrivateCards(){
        Game g = Game.getGame();
        for(Player p : g.getPlayers())
            assertNull("Assegnamento carte private non richiesto",p.getPrivateObjectiveCard());
        g.extractPrivateCards();
        for(Player p : g.getPlayers())
            assertNotNull("Carte private non assegnate",p.getPrivateObjectiveCard());
    }

    @Test
    public void checkShuffle() throws Exception {
        Game g = Game.getGame();
        int numPlayers = g.getPlayers().size();
        ArrayList<Player> players = new ArrayList<>();
        for(Player p : g.getPlayers())
            players.add(p);
        g.shufflePlayers();
        assertEquals("Lunghezza player cambiata dopo shuffle",numPlayers,g.getPlayers().size());
        for(Player p : players) {
            if(!g.getPlayers().contains(p))
                throw new Exception();
        }
    }

    @Test
    public void checkExtractPattern(){
        Game g = Game.getGame();
        assertTrue("Pattern non ancora caricati", g.getPatterns().size()>=24);
        int patternSize = g.getPatterns().size();
        g.extractPattern();
        assertEquals("Pattern estratti non rimossi",(patternSize-4),g.getPatterns().size());
    }

    @Test
    public void checkChangeRound(){
        Game g = Game.getGame();
        Game.resetGame();
        g.gameSetup();
        g.getDraft().generateDraft();
        assertTrue(g.changeRound());
        assertTrue("Wrong setting", g.getCurrentRound() == 2);
        assertEquals("Wrong loading draft", 9, g.getTrack().getDice(g.getCurrentRound() - 1).size());


        //CONTROLLO ULTIMO ROUND
        g.setCurrentRound(10);
        assertFalse("Partita finita in anticipo",g.isGameStarted());
        assertFalse("Partita non finita",g.changeRound());
    }

    @Test
    public void checkChangeTurn(){
        Game g = Game.getGame();
        Game.resetGame();
        g.gameSetup();
        DraftPool draftPool = g.getDraft();
        draftPool.setNumPlayer(4);
        draftPool.generateDraft();
        Player p1 = g.getPlayers().get(g.getCurrentPlayer());
        assertTrue(g.changeTurn());
        assertEquals("Problemi inizializzazione turno", Turns.FIRST, p1.getCurrentTurn());
        assertNotEquals("Cambio turno fallito", p1, g.getPlayers().get(g.getCurrentPlayer()));

        Player curP = g.getPlayers().get(g.getCurrentPlayer());
        assertEquals("Problemi inizializzazione turno", Turns.ZERO, curP.getCurrentTurn());

        for(int i = 0; i < 5; i++)
            g.changeTurn();
        assertEquals("Errore in change turn", 1 ,g.getCurrentRound());
        assertEquals("Errore impostazione primo giocatore", 1, g.getCurrentPlayer());
        assertEquals("Ordine gioco cambiato", curP, g.getPlayers().get(g.getCurrentPlayer()));


        //VERIFICA PLAYER SALTA SECONDO TURNO (ES. HA USATO UNA TOOL)
        g.setCurrentTurn(Turns.SECOND);
        curP = g.getPlayers().get(g.getCurrentPlayer());
        curP.setCurrentTurn(Turns.SECOND);
        g.setCurrentPlayer(g.getCurrentPlayer()+1);
        assertTrue(g.changeTurn());
        assertEquals("Errore in change turn", 1 ,g.getCurrentRound());
        assertEquals("Errore impostazione primo giocatore", 0, g.getCurrentPlayer());
        assertNotEquals("Ordine gioco cambiato", curP, g.getPlayers().get(g.getCurrentPlayer()));
    }

    @Test
    public void checkChangeMoves(){
        Game g = Game.getGame();
        g.gameSetup();
        Player p1 = g.getPlayers().get(0);
        Player p2 = g.getPlayers().get(1);
        assertEquals("Player1 aveva gia effettuato una mossa",Moves.NONE,p1.getMoves());
        assertEquals("Player2 aveva gia effettuato una mossa",Moves.NONE,p2.getMoves());

        //
        //TEST PLAYER USA PRIMA UNA TOOL E POI PIAZZA IL DADO
        //
        //TEST UTILIZZO TOOL PER LA PRIMA VOLTA (toolTokens = 0)
        g.setCurrentPlayer(0);
        g.getToolTokens().set(0,0);
        int tokensP1 = 4;
        p1.setNumTokens(tokensP1);
        try {
            g.setToolUSED(p1,0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Player1 ha usato una carta ma mossa non aggiornata",Moves.TOOLUSED,p1.getMoves());
        assertEquals("Numero segnalini p1 non aggiornato",(tokensP1-1),p1.getNumTokens());
        assertTrue("Numero segnalini toolCard1 non aggiornato", g.getToolTokens().get(0) == 1);

        g.setDicePLACED(p1);
        assertEquals("Player1 ha piazzato un dado dopo aver usato una carta ma mossa non aggiornata",Moves.BOTH,p1.getMoves());

        //TEST UTILIZZO TOOL PER LA SECONDA VOLTA (toolTokens > 0)
        p1.setMoves(Moves.NONE);
        g.setCurrentPlayer(0);
        try {
            g.setToolUSED(p1,0);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Player1 ha usato una carta ma mossa non aggiornata",Moves.TOOLUSED,p1.getMoves());
        assertEquals("Numero segnalini p1 non aggiornato, stessa carta utilizzata due volte",(tokensP1-3),p1.getNumTokens());
        assertTrue("Numero segnalini toolCard1 non aggiornato, stessa carta utilizzata due volte", g.getToolTokens().get(0) == 3);
        g.setDicePLACED(p1);
        assertEquals("Player1 ha piazzato un dado dopo aver usato una carta ma mossa non aggiornata",Moves.BOTH,p1.getMoves());



        //
        //TEST PLAYER PRIMA PIAZZA IL DADO E POI USA UNA TOOL
        //
        //TEST UTILIZZO TOOL PER LA PRIMA VOLTA (toolTokens = 0)
        g.setCurrentPlayer(1);
        g.getToolTokens().set(1,0);
        int tokensP2 = 3;
        p2.setNumTokens(tokensP2);
        g.setDicePLACED(p2);
        assertEquals("Player2 ha piazzato un dado ma mossa non aggiornata",Moves.DICEPLACED,p2.getMoves());
        try {
            g.setToolUSED(p2,1);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Player2 ha usato una carta (mai usata) dopo aver piazzato un dado ma mossa non aggiornata",Moves.BOTH,p2.getMoves());
        assertEquals("Numero segnalini p2 non aggiornato",(tokensP2-1),p2.getNumTokens());
        assertTrue("Numero segnalini toolCard2 non aggiornato", g.getToolTokens().get(1) == 1);

        //TEST UTILIZZO TOOL PER LA SECONDA VOLTA (toolTokens >0)
        p2.setMoves(Moves.NONE);
        g.setCurrentPlayer(1);
        g.setDicePLACED(p2);
        assertEquals("Player2 ha piazzato un dado ma mossa non aggiornata",Moves.DICEPLACED,p2.getMoves());
        try {
            g.setToolUSED(p2,1);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Player2 ha usato una carta (secondo utilizzo) dopo aver piazzato un dado ma mossa non aggiornata",Moves.BOTH,p2.getMoves());
        assertEquals("Numero segnalini p2 non aggiornato",(tokensP2-3),p2.getNumTokens());
        assertTrue("Numero segnalini toolCard2 non aggiornato", g.getToolTokens().get(1) == 3);

        //TEST NUMERO SEGNALINI NON SUFFICIENTI
        p1.setMoves(Moves.NONE);
        p2.setMoves(Moves.NONE);
        g.setCurrentPlayer(1);
        try {
            g.setToolUSED(p2,2);
        } catch (RulesBreakException e) {
            assertEquals("Carta utilizzata da p2 ma numero segnalini non sufficienti",e.getMessage(),"TOKEN Restriction Not Matched");
        }
        g.setCurrentPlayer(0);
        try {
            g.setToolUSED(p1,1);
        } catch (RulesBreakException e) {
            assertEquals("Carta utilizzata da p1 ma numero segnalini non sufficienti",e.getMessage(),"TOKEN Restriction Not Matched");
        }
        assertEquals("Numero segnalini cambiato dopo eccezione",tokensP1-3,p1.getNumTokens());

        try {
            g.setToolUSED(p1,2);
        } catch (RulesBreakException e) {
            e.printStackTrace();
        }
        assertEquals("Carta utilizzata da p1 ma mossa non aggiornata", Moves.TOOLUSED,p1.getMoves());
        assertEquals("Numero segnalini p1 non aggiornato",(tokensP1-4),p1.getNumTokens());
        assertTrue("Numero segnalini toolCard3 non aggiornato", g.getToolTokens().get(2) == 1);
    }

    @Test
    public void checkSetterMethods(){
        Game g = Game.getGame();

        //SET DRAFT
        DraftPool draft = new DraftPool();
        Dice d = new Dice(Colors.BLUE);
        try {
            draft.addDice(d);
        } catch (DraftFullException e) {
            e.printStackTrace();
        }

        g.setDraft(draft);
        assertEquals("Problemi con set draft",draft,g.getDraft());

        //SET CURRENT TURN
        g.setCurrentTurn(Turns.SECOND);
        assertEquals("Problemi con set Current Turn",Turns.SECOND,g.getCurrentTurn());

        //SET CURRENT PLAYER
        g.setCurrentPlayer(3);
        assertEquals("Problemi con set Current Player",3,g.getCurrentPlayer());




    }

}
