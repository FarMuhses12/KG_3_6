package ru.vsu.cs.legostaev.drawers.linedrawer;



import ru.vsu.cs.legostaev.drawers.pixeldrawer.IPixelDrawer;


public class DDALineDrawer implements ILineDrawer {

    @Override
    public void drawLine(IPixelDrawer pixelDrawer, double x1, double y1, double x2, double y2) { // DDA line algorithm
        double k = (y2 - y1) / (x2 - x1); // tangents of angle between line and x-axis
        if (Double.isNaN(k) || Math.abs(k) > 1) { // vertical
            for (double row = Math.min(y1, y2); row <= Math.max(y1, y2); row++) {
                double col = (row - y1) / k + x1;
                pixelDrawer.putPixel(col, row);
            }
        } else { // horizontal
            for (double col = Math.min(x1, x2); col <= Math.max(x1, x2); col++) {
                double row = (col - x1) * k + y1;
                pixelDrawer.putPixel(col, row);
            }
        }

    }

}
