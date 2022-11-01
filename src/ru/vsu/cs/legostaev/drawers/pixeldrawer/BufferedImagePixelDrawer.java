package ru.vsu.cs.legostaev.drawers.pixeldrawer;

import ru.vsu.cs.legostaev.drawers.pixeldrawer.IPixelDrawer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedImagePixelDrawer implements IPixelDrawer {

    private BufferedImage bi;
    private Color currColor = Color.BLACK;

    public BufferedImagePixelDrawer(BufferedImage bi) {
        this.bi = bi;
        Graphics graphics = this.bi.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0,0, this.bi.getWidth(), this.bi.getHeight());
    }

    public BufferedImage getBufferedImage() {
        return bi;
    }

    public void setBufferedImage(BufferedImage bi) {
        this.bi = bi;
    }

    @Override
    public void putPixel(double x, double y) {
        Graphics graphics = bi.getGraphics();
        graphics.setColor(currColor);
        graphics.drawRect((int) x, (int) y, 0, 0);
    }

    @Override
    public void setColor(Color color) {
         currColor = color;
    }
    
    @Override
    public Color getColor() {
        return  currColor;
    }
    
}
