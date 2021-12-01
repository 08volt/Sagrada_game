package progettoIngSW;

import static org.junit.Assert.*;

import org.junit.Test;
import progettoIngSW.Model.*;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerTest {

    @Test
    public void shouldReturnPlayer(){
        Player p = new Player("tizio","b");
        assertNotNull(p);
        assertEquals("Wrong setting turn", Turns.ZERO, p.getCurrentTurn());

        assertNull(p.getPrivateObjectiveCard());
    }

    @Test
    public void checkPattern() throws IOException {
        Player p = new Player("tizio","c");
        assertNull("Wrong window frame",p.getWindowFrame());
        Pattern pattern = new Pattern(1);
        p.patternChoosen(pattern);
        assertNotNull("Window frame is null", p.getWindowFrame());
    }

    @Test
    public void shouldSetWf() throws IOException {
        WindowFrame wf =new WindowFrame(new Pattern(0));
        Player p = new Player("paperino","777");
        p.setWindowFrame(wf);
        assertEquals("Set windowFrame non avvenuto", wf,p.getWindowFrame());
    }

    @Test
    public void shouldSetLoginCredential(){
        Player p = new Player("paperino","777");
        String newUsername = "topolino";
        p.setUsername(newUsername);
        String newPassword = "dpg";
        p.setPassword(newPassword);
        assertEquals("Set Username non avvenuto", newUsername,p.getUsername());
        assertEquals("Set Password non avvenuto", newPassword,p.getPassword());
    }

    @Test
    public void shouldSetNumTokens(){
        Player p = new Player("topolino","123");
        p.setNumTokens(5);
        assertEquals("Set numTokens non avvenuto",p.getNumTokens(),5);
    }

    @Test
    public void shouldSetScore(){
        Player p = new Player("topolino","123");
        p.setScore(30);
        assertEquals("Set Score non avvenuto",p.getScore(),30);
    }


    @Test
    public void shouldSetMoves(){
        Player p = new Player("topolino","123");
        p.setMoves(Moves.BOTH);
        assertEquals("Set Score non avvenuto",p.getMoves(),Moves.BOTH);
        p.setMoves(Moves.DICEPLACED);
        assertEquals("Set Score non avvenuto",p.getMoves(),Moves.DICEPLACED);
        p.setMoves(Moves.TOOLUSED);
        assertEquals("Set Score non avvenuto",p.getMoves(),Moves.TOOLUSED);
    }

    @Test
    public void shouldNotBeEquals(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("uno","1"));
        players.add(new Player("due","2"));
        players.add(new Player("tre","3"));
        Object o = null;
        for(Player pc : players){
            assertFalse(pc.equals(o));
        }
        o = new Object();
        for(Player pc : players){
            assertFalse(pc.equals(o));
        }
    }

}
