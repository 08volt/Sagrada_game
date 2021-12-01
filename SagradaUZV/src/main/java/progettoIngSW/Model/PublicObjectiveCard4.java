package progettoIngSW.Model;

public class PublicObjectiveCard4 extends PublicObjectiveCard{

    //
    //CONSTRUCTOR
    //
    public PublicObjectiveCard4(){

        super.setVictoryPoint(4);
    }

    //
    //OVERRIDE METHODS
    //
    @Override
    public int scoringCalc(WindowFrame windowFrame){

        int num = 0;
        boolean equal = false;
        for(int j = 0; j < windowFrame.getCol(); j++){
            for(int i = 0; i < windowFrame.getRow() - 1 && !equal; i++){
                for(int k = i + 1; k < windowFrame.getRow() && !equal; k++){
                    if(windowFrame.getCell(k, j).getDice() == null || windowFrame.getCell(i, j).getDice() == null ||
                    windowFrame.getCell(i, j).getDice().getNumber() == windowFrame.getCell(k, j).getDice().getNumber()){
                        equal = true;
                    }
                }
            }

            if(!equal)
                num++;
            equal = false;
        }
        return num*getVictoryPoint();
    }
}
