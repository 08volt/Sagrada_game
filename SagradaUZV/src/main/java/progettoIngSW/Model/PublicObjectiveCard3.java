package progettoIngSW.Model;

public class PublicObjectiveCard3 extends PublicObjectiveCard{

    //
    //CONSTRUCTOR
    //
    public PublicObjectiveCard3(){

        super.setVictoryPoint(5);
    }

    //
    //OVERRIDE METHODS
    //
    @Override
    public int scoringCalc(WindowFrame windowFrame){

        int num = 0;
        boolean equal = false;
        for(int i = 0; i < windowFrame.getRow(); i++){
            for(int j = 0; j < windowFrame.getCol() - 1 && !equal; j++){
                for(int k = j + 1; k < windowFrame.getCol() && !equal; k++){
                    if(windowFrame.getCell(i, k).getDice() == null || windowFrame.getCell(i, j).getDice() == null ||
                            windowFrame.getCell(i, j).getDice().getNumber() == windowFrame.getCell(i, k).getDice().getNumber()){
                        equal = true;
                    }
                }
            }
            if(!equal)
                num++;
            equal = false;
        }
        return (num*getVictoryPoint());

    }
}
