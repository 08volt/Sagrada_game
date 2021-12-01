package progettoIngSW.Model;

public class PublicObjectiveCard5 extends PublicObjectiveCard {

    //
    //CONSTRUCTOR
    //
    public PublicObjectiveCard5(){

        super.setVictoryPoint(2);
    }

    //
    //OVERRIDE METHODS
    //
    @Override
    public int scoringCalc(WindowFrame windowFrame){

        int numOf1 = 0;
        int numOf2 = 0;
        for(int i = 0; i < windowFrame.getRow(); i++){
            for(int j = 0; j < windowFrame.getCol(); j++){
                if(windowFrame.getCell(i, j).getDice() != null){
                    if(windowFrame.getCell(i , j).getDice().getNumber() == 1)
                        numOf1++;
                    if(windowFrame.getCell(i, j).getDice().getNumber() == 2)
                        numOf2++;
                }
            }
        }
        return Math.min(numOf1, numOf2)*getVictoryPoint();
    }
}
