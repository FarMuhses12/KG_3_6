package ru.vsu.cs.legostaev.drawers.linedrawer;



import ru.vsu.cs.legostaev.drawers.pixeldrawer.IPixelDrawer;

public interface ILineDrawer {

    void drawLine(IPixelDrawer pixelDrawer, double x1, double y1, double x2, double y2);

}
