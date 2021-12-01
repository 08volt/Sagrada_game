package progettoIngSW.Model;

public class PublicObjectiveCard8 extends PublicObjectiveCard{

    //
    //CONSTRUCTOR
    //
    public PublicObjectiveCard8(){

        super.setVictoryPoint(5);
    }

    //
    //OVERRIDE METHODS
    //
    @Override
    public int scoringCalc(WindowFrame windowFrame){

        int numOf1 = 0;
        int numOf2 = 0;
        int numOf3 = 0;
        int numOf4 = 0;
        int numOf5 = 0;
        int numOf6 = 0;
        for(int i = 0; i < windowFrame.getRow(); i++){
            for(int j = 0; j < windowFrame.getCol(); j++){
                if(windowFrame.getCell(i, j).getDice() != null){
                    if(windowFrame.getCell(i , j).getDice().getNumber() == 1)
                        numOf1++;
                    if(windowFrame.getCell(i, j).getDice().getNumber() == 2)
                        numOf2++;
                    if(windowFrame.getCell(i , j).getDice().getNumber() == 3)
                        numOf3++;
                    if(windowFrame.getCell(i, j).getDice().getNumber() == 4)
                        numOf4++;
                    if(windowFrame.getCell(i , j).getDice().getNumber() == 5)
                        numOf5++;
                    if(windowFrame.getCell(i, j).getDice().getNumber() == 6)
                        numOf6++;
                }
            }
        }
        int temp1 = Math.min(numOf1, numOf2);
        int temp2 = Math.min(numOf3, numOf4);
        temp1 = Math.min(temp1, temp2);
        temp2 = Math.min(numOf5, numOf6);
        temp1 = Math.min(temp1, temp2);

        return temp1*getVictoryPoint();


    }
}
