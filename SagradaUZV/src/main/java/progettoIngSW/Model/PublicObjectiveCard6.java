package progettoIngSW.Model;

public class PublicObjectiveCard6 extends PublicObjectiveCard{

    //
    //CONSTRUCTOR
    //
    public PublicObjectiveCard6(){

        super.setVictoryPoint(2);
    }

    //
    //OVERRIDE METHODS
    //
    @Override
    public int scoringCalc(WindowFrame windowFrame){

        int numOf3 = 0;
        int numOf4 = 0;
        for(int i = 0; i < windowFrame.getRow(); i++){
            for(int j = 0; j < windowFrame.getCol(); j++){
                if(windowFrame.getCell(i, j).getDice() != null){
                    if(windowFrame.getCell(i , j).getDice().getNumber() == 3)
                        numOf3++;
                    if(windowFrame.getCell(i, j).getDice().getNumber() == 4)
                        numOf4++;
                }
            }
        }
        return Math.min(numOf3, numOf4)*getVictoryPoint();
    }
}
