package progettoIngSW.Model;

public class PublicObjectiveCard9 extends PublicObjectiveCard {

    //
    //CONSTRUCTOR
    //
    public PublicObjectiveCard9(){

        super.setVictoryPoint(0);
    }

    //
    //OVERRIDE METHODS
    //
    @Override
    public int scoringCalc(WindowFrame windowFrame){

        int num = 0;
        boolean find = false;
        for(int i = 0; i < windowFrame.getRow(); i++){
            for(int j = 0; j < windowFrame.getCol(); j++){

                if(windowFrame.getCell(i, j).getDice() != null){
                    if(i < 3 && j < 4 && windowFrame.getCell(i + 1, j + 1).getDice() != null &&
                            windowFrame.getCell( i, j).getDice().getColor() == windowFrame.getCell( i + 1, j + 1).getDice().getColor())
                        find = true;

                    if(i > 0 && j > 0 && windowFrame.getCell(i - 1, j - 1).getDice() != null &&
                            windowFrame.getCell( i, j).getDice().getColor() == windowFrame.getCell( i - 1, j - 1).getDice().getColor())
                        find = true;

                    if(i > 0 && j < 4 && windowFrame.getCell(i - 1, j + 1).getDice() != null &&
                            windowFrame.getCell( i, j).getDice().getColor() == windowFrame.getCell( i - 1, j + 1).getDice().getColor())
                        find = true;
                    if(i < 3 && j > 0 &&  windowFrame.getCell(i + 1, j - 1).getDice() != null &&
                            windowFrame.getCell( i, j).getDice().getColor() == windowFrame.getCell( i + 1, j - 1).getDice().getColor())
                        find = true;
                }


                if(find == true)
                    num++;
                find = false;
                }
            }

        return num;
    }
}
