package progettoIngSW.Model;

public class PublicObjectiveCard7 extends  PublicObjectiveCard{
    //
    //CONSTRUCTOR
    //
    public PublicObjectiveCard7(){

        super.setVictoryPoint(2);
    }

    //
    //OVERRIDE METHODS
    //
    @Override
    public int scoringCalc(WindowFrame windowFrame){

        int numOf5 = 0;
        int numOf6 = 0;
        for(int i = 0; i < windowFrame.getRow(); i++){
            for(int j = 0; j < windowFrame.getCol(); j++){
                if(windowFrame.getCell(i, j).getDice() != null){
                    if(windowFrame.getCell(i , j).getDice().getNumber() == 5)
                        numOf5++;
                    if(windowFrame.getCell(i, j).getDice().getNumber() == 6)
                        numOf6++;
                }
            }
        }
        return Math.min(numOf5, numOf6)*getVictoryPoint();
    }
}
