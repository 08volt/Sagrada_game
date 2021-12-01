package progettoIngSW.Model;

public class PublicObjectiveCard10 extends PublicObjectiveCard{

    //
    //CONSTRUCTOR
    //
    public PublicObjectiveCard10(){

        super.setVictoryPoint(4);
    }

    //
    //OVERRIDE METHODS
    //
    @Override
    public int scoringCalc(WindowFrame windowFrame){

        int numOfYellow = 0;
        int numOfBlue = 0;
        int numOfRed = 0;
        int numOfGreen = 0;
        int numOfPurple = 0;
        for(int i = 0; i < windowFrame.getRow(); i++){
            for(int j = 0; j < windowFrame.getCol(); j++){
                if(windowFrame.getCell(i, j).getDice() != null){
                    if(windowFrame.getCell(i , j).getDice().getColor() == Colors.YELLOW)
                        numOfYellow++;
                    if(windowFrame.getCell(i, j).getDice().getColor() == Colors.BLUE)
                        numOfBlue++;
                    if(windowFrame.getCell(i , j).getDice().getColor() == Colors.RED)
                        numOfRed++;
                    if(windowFrame.getCell(i, j).getDice().getColor() == Colors.GREEN)
                        numOfGreen++;
                    if(windowFrame.getCell(i , j).getDice().getColor() == Colors.PURPLE)
                        numOfPurple++;
                }

            }
        }
        int temp1 = Math.min(numOfYellow, numOfBlue);
        int temp2 = Math.min(numOfRed, numOfGreen);
        temp1 = Math.min(temp1, temp2);
        temp1 = Math.min(temp1, numOfPurple);

        return temp1*getVictoryPoint();


    }

}

