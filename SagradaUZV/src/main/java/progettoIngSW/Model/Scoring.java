package progettoIngSW.Model;

import java.util.ArrayList;

/**
 * Classe final utilizzata per il calcolo punteggio
 */
public final class Scoring {


    /**
     * Calcolo punteggio dato dalla carta privata (somma del valore dei dadi dello stesso colore della carta privata)
     * @param p player per il quale si vuole calcolare il punteggio
     * @return punteggio dato dalla carta privata
     */
    public static int privateObjectiveScore(Player p)
    {
        try {
            WindowFrame w = p.getWindowFrame();
            int score = 0;
            for (int i = 0; i < w.getRow(); i++) {
                for (int j = 0; j < w.getCol(); j++) {
                    if (w.getCell(i, j).getDice() == null) {
                        score--;
                    } else if (w.getCell(i, j).getDice().getColor() == p.getPrivateObjectiveCard().getColor()) {
                        score += w.getCell(i, j).getDice().getNumber();
                    }
                }
            }
            return score;
        }catch (NullPointerException e){
            return 0;
        }
    }

    /**
     * Calcolo punteggio player dato dalla carte pubbliche e dal numero dei dadi nella windowFrame
     * @param p giocatore per il quale si vuole calcolare il punteggio
     * @return punteggio giocatore. Punti:
     *                                  (+1 per ogni dado presente, -1 per ogni cella vuota) + punti carte pubbliche
     */
    public static int calc(Player p){

        try {

            int score = 0;

            //CARTE PUBBLICHE
            ArrayList<Integer> pocIndex = Game.getGame().getPublicObjectiveCards(); //INDICI CARTE OBBIETTIVO IN GAME
            for (int i = 0; i < pocIndex.size(); i++) {
                PublicObjectiveCard poc = null;

                //FIXME partial test (switch)
                switch (pocIndex.get(i)) {
                    case 1:
                        poc = new PublicObjectiveCard1();
                        break;
                    case 2:
                        poc = new PublicObjectiveCard2();
                        break;
                    case 3:
                        poc = new PublicObjectiveCard3();
                        break;
                    case 4:
                        poc = new PublicObjectiveCard4();
                        break;
                    case 5:
                        poc = new PublicObjectiveCard5();
                        break;
                    case 6:
                        poc = new PublicObjectiveCard6();
                        break;
                    case 7:
                        poc = new PublicObjectiveCard7();
                        break;
                    case 8:
                        poc = new PublicObjectiveCard8();
                        break;
                    case 9:
                        poc = new PublicObjectiveCard9();
                        break;
                    case 10:
                        poc = new PublicObjectiveCard10();
                        break;
                    default: break; //QUESTA CONDIZIONE NON SI VERIFICA MAI
                }
                if(poc != null)
                    score += poc.scoringCalc(p.getWindowFrame());
            }
            score += Scoring.privateObjectiveScore(p);


            //SEGNALINI FAVORE AVANZATI
            score += p.getNumTokens();

            return score;
        }catch(NullPointerException e){
            return 0;
        }
    }


}
