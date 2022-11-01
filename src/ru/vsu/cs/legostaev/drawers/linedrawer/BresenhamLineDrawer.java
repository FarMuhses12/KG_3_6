package ru.vsu.cs.legostaev.drawers.linedrawer;

import ru.vsu.cs.legostaev.drawers.pixeldrawer.IPixelDrawer;

public class BresenhamLineDrawer implements ILineDrawer {

    @Override
    public void drawLine(IPixelDrawer pixelDrawer, double x1, double y1, double x2, double y2) {
        if (x1 > x2) { // x1 should be left
            double t = x2;
            x2 = x1;
            x1 = t;
            t = y2;
            y2 = y1;
            y1 = t;
        }

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);
        double k = dy / dx; // tangents of angle between line and x-axis
        double col = x1;
        double row = y1;
        double d = 2 * dy - dx;
        while (col <= x2) {
            pixelDrawer.putPixel(col, row);
            if (d >= 0) {
                row += ((int) Math.signum(y2 - y1));
                col++;
                if (Double.isNaN(k) || Math.abs(k) > 1) {
                    d += 2 * dx - 2 * dy;
                } else {
                    d += 2 * dy - 2 * dx;
                }
            } else {
                if (Double.isNaN(k) || Math.abs(k) > 1) {
                    row += ((int) Math.signum(y2 - y1));
                    d += 2 * dx;
                } else {
                    col++;
                    d += 2 * dy;
                }
            }
        }
    }
  
}
