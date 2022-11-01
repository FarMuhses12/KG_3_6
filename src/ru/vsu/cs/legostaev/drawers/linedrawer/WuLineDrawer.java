package ru.vsu.cs.legostaev.drawers.linedrawer;

import ru.vsu.cs.legostaev.drawers.pixeldrawer.IPixelDrawer;


public class WuLineDrawer implements ILineDrawer {

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
         
        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);
        
        if (steep) {
            drawLineInverted(pixelDrawer,  y1,  x1,  y2,  x2, true);
        } else {
            drawLineInverted(pixelDrawer,  x1,  y1,  x2,  y2, false);
        }
    }
    
    private void drawLineInverted(IPixelDrawer pixelDrawer, double x1, double y1, double x2, double y2, boolean inverted) {
        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        double dx =  x2 -  x1;
        double dy =  y2 -  y1;
        double gradient = dy / dx;

        // handle first endpoint
        double xend = x1;
        double yend = y1;
        double xgap = rfpart(x1 + 0.5);
        double xpxl1 = xend; // this will be used in the main loop
        double ypxl1 = Math.floor(yend);

        if (steep ^ inverted) {
            plot(pixelDrawer, ypxl1, xpxl1, rfpart(yend) * xgap);
            plot(pixelDrawer, ypxl1 + 1, xpxl1, fpart(yend) * xgap);
        } else {
            plot(pixelDrawer, xpxl1, ypxl1, rfpart(yend) * xgap);
            plot(pixelDrawer, xpxl1, ypxl1 + 1, fpart(yend) * xgap);
        }

        // first y-intersection for the main loop
        double intery = yend + gradient;

        // handle second endpoint
        xend = Math.round( x2);
        yend =  y2 + gradient * (xend -  x2);
        xgap = fpart( x2 + 0.5);
        double xpxl2 = xend; // this will be used in the main loop
        double ypxl2 = Math.floor(yend);

        if (steep ^ inverted) {
            plot(pixelDrawer, ypxl2, xpxl2, rfpart(yend) * xgap);
            plot(pixelDrawer, ypxl2 + 1, xpxl2, fpart(yend) * xgap);
        } else {
            plot(pixelDrawer, xpxl2, ypxl2, rfpart(yend) * xgap);
            plot(pixelDrawer, xpxl2, ypxl2 + 1, fpart(yend) * xgap);
        }

        // main loop
        if (xpxl1 < xpxl2) {
            for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
                if (steep ^ inverted) {
                    plot(pixelDrawer, Math.floor(intery), x, rfpart(intery));
                    plot(pixelDrawer, Math.floor(intery) + 1, x, fpart(intery));
                } else {
                    plot(pixelDrawer, x, Math.floor(intery), rfpart(intery));
                    plot(pixelDrawer, x, Math.floor(intery) + 1, fpart(intery));
                }
                intery = intery + gradient; 
            }
        } else {
            for (double x = xpxl1 - 1; x >= xpxl2 + 1; x--) {
                if (steep ^ inverted) {
                    plot(pixelDrawer, Math.floor(intery), x, rfpart(intery));
                    plot(pixelDrawer, Math.floor(intery) + 1, x, fpart(intery));
                } else {
                    plot(pixelDrawer, x, Math.floor(intery), rfpart(intery));
                    plot(pixelDrawer, x, Math.floor(intery) + 1, fpart(intery));
                }
                intery = intery - gradient; 
            }
        }
        
    }
    
    private static double fpart(double x) {
        return x - Math.floor(x);
    }
    
    private static double rfpart(double x) {
        return 1 - fpart(x);
    }
    
    private void plot(IPixelDrawer pixelDrawer, double x, double y, double c) {

        java.awt.Color color = pixelDrawer.getColor();
        pixelDrawer.setColor(new java.awt.Color(
                color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, (float) c));
        pixelDrawer.putPixel(x, y);
    }
    
  
}
