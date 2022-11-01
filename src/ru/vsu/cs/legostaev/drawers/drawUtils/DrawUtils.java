package ru.vsu.cs.legostaev.drawers.drawUtils;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class DrawUtils {

    private DrawUtils() {}

    public static Point2D.Double fromPlaneToVisibleRect(Point2D.Double planeCoordinates, Rectangle2D visibleRect) {
        double xOnRect = planeCoordinates.x - visibleRect.getX();
        double yOnRect = visibleRect.getY() - planeCoordinates.y;

        return new Point2D.Double(xOnRect, yOnRect);
    }

    public static Point2D.Double fromVisibleRectToImage(Point2D.Double pOnRect, Rectangle2D visibleRect, int width, int height) {

        double xOnImg = pOnRect.x * width / visibleRect.getWidth();
        double yOnImg = pOnRect.y * height / visibleRect.getHeight();

        return new Point2D.Double(xOnImg, yOnImg);
    }

    public static Point2D.Double fromPlaneToImage(Point2D.Double planeCoordinates, Rectangle2D visibleRect, int width, int height) {

        Point2D.Double pOnRect = fromPlaneToVisibleRect(planeCoordinates, visibleRect);

        return fromVisibleRectToImage(pOnRect, visibleRect, width, height);

    }

    public static Point2D.Double fromImageToPlane(Point2D.Double imageCoordinates, int width, int height, Rectangle2D visibleRect) {

        Point2D.Double pOnRect = fromImageToVisibleRect(imageCoordinates, width, height, visibleRect);

        return fromVisibleRectToPlane(pOnRect, visibleRect);

    }

    public static Point2D.Double fromImageToVisibleRect(Point2D.Double pOnImg, int width, int height, Rectangle2D visibleRect) {

        double pOnRectX = pOnImg.x * visibleRect.getWidth() / width;
        double pOnRectY = pOnImg.y * visibleRect.getHeight() / height;

        return new Point2D.Double(pOnRectX, pOnRectY);
    }

    public static Point2D.Double fromVisibleRectToPlane(Point2D.Double xOnRect, Rectangle2D visibleRect) {
        double planeX = xOnRect.x + visibleRect.getX();
        double planeY = visibleRect.getY() - xOnRect.y;

        return new Point2D.Double(planeX, planeY);
    }
}
