package ru.vsu.cs.legostaev.drawers;

import ru.vsu.cs.legostaev.drawers.drawUtils.DrawUtils;
import ru.vsu.cs.legostaev.drawers.linedrawer.ILineDrawer;
import ru.vsu.cs.legostaev.drawers.pixeldrawer.IPixelDrawer;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class BufferedImagePlaneDrawer {

    private BufferedImage bi;
    private ILineDrawer lineDrawer;
    private IPixelDrawer pixelDrawer;

    public BufferedImagePlaneDrawer(BufferedImage bi, ILineDrawer lineDrawer, IPixelDrawer pixelDrawer) {
        this.bi = bi;
        this.pixelDrawer = pixelDrawer;
        this.lineDrawer = lineDrawer;
    }

    public IPixelDrawer getPixelDrawer() {
        return pixelDrawer;
    }

    public BufferedImage getBi() {
        return bi;
    }

    public void drawLine(Rectangle2D visibleRect,
                         double x1, double y1, double x2, double y2) {
        Point2D.Double p1 = new Point2D.Double(x1, y1);
        Point2D.Double p2 = new Point2D.Double(x2, y2);

        Point2D.Double p1OnImage = DrawUtils.fromPlaneToImage(p1, visibleRect, bi.getWidth(), bi.getHeight());
        Point2D.Double p2OnImage = DrawUtils.fromPlaneToImage(p2, visibleRect, bi.getWidth(), bi.getHeight());

        lineDrawer.drawLine(pixelDrawer, p1OnImage.x, p1OnImage.y, p2OnImage.x, p2OnImage.y);
    }

    public void drawFunction(Rectangle2D visibleRect,
                             Function<Double, Double> f, double startX, double endX) {

        double step = (endX - startX) / bi.getWidth();

        double prevX = Math.floor(startX);
        double prevY = f.apply(prevX);

        for (double currX = prevX + step; currX <= Math.ceil(Math.max(startX, endX)); currX += step) {
            double currY = f.apply(currX);

            drawLine(visibleRect, prevX, prevY, currX, currY);

            prevX = currX;
            prevY = currY;
        }
    }

}
