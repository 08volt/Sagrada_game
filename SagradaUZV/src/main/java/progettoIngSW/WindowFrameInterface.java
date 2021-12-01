package progettoIngSW;

import progettoIngSW.Model.Cell;

public interface WindowFrameInterface {

    Cell getCell(int i);

    Cell getCell(int x,int y);

    int getCol();
    int getRow();
}
