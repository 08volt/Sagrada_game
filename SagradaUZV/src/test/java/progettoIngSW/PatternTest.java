package progettoIngSW;

import org.junit.Test;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Pattern;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatternTest {


    //FIXME lettura da path non corretto non gestita
    // Pattern compresi tra 0 e 23
    @Test
    public void shouldLoadPatternFromFile(){
      ArrayList<Pattern> patterns = new ArrayList<>();
        for(int numPattern=0;numPattern<28;numPattern++) {
            try {
                patterns.add(new Pattern(numPattern));
            } catch (Exception e) {
                assertEquals("Pattern non esistenti aggiunti",NullPointerException.class,e.getClass());
            }
        }
        assertEquals("Numero pattern non corretto",24,patterns.size());
    }

    @Test
    public void checkSetterANDGetter(){

        Pattern pattern = new Pattern();

        String namePattern = "patternTest";
        pattern.setName(namePattern);
        assertEquals("SetName pattern non corretto",namePattern,pattern.getName());

        int difficult = 5;
        pattern.setDifficult(difficult);
        assertEquals("SetDifficult pattern non corretto",difficult,pattern.getDifficult());

        Colors[] colorsRestr = new Colors[30];
        pattern.setColors(colorsRestr);
        int[] numRestr = new int[30];
        pattern.setNumbers(numRestr);
        assertTrue("SetColors non corretto", Arrays.equals(pattern.getColors(), colorsRestr));
        assertEquals("SetColors non corretto",30,pattern.getColors().length);
        assertEquals("SetNumbers non corretto",numRestr,pattern.getNumbers());
        assertEquals("SetNumbers non corretto",30,pattern.getNumbers().length);

    }

}
