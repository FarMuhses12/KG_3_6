package ru.vsu.cs.legostaev.frameMain.panelCoordinatesPlane;

import ru.vsu.cs.legostaev.drawers.BufferedImagePlaneDrawer;
import ru.vsu.cs.legostaev.drawers.drawUtils.DrawUtils;
import ru.vsu.cs.legostaev.drawers.linedrawer.DDALineDrawer;
import ru.vsu.cs.legostaev.drawers.linedrawer.ILineDrawer;
import ru.vsu.cs.legostaev.drawers.pixeldrawer.BufferedImagePixelDrawer;
import ru.vsu.cs.legostaev.drawers.pixeldrawer.IPixelDrawer;
import ru.vsu.cs.legostaev.line.Spline2DLine;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PanelCoordinatesPlane extends JPanel {

    private static final Rectangle2D INITIAL_VISIBLE_RECT = new Rectangle2D.Double(-10, 10, 20, 10);

    private ILineDrawer lineDrawer;
    private JPanel panelDraw;
    private JPanel panelVerticalScroll;
    private JPanel panelHorizontalScroll;

    private Rectangle2D visibleRect;
    private List<Function<Double, Double>> listOfFunctions;
    private List<Color> listOfColors;
    private Integer selectedCurveIndex = null;
    private List<JLabel> listOfLabels;
    private MouseEvent lastDragEvent;


    public PanelCoordinatesPlane() {

        panelDraw = new JPanel() {
            @Override
            public void paint(Graphics g) {
                BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                IPixelDrawer pixelDrawer = new BufferedImagePixelDrawer(bi);
                BufferedImagePlaneDrawer bipd = new BufferedImagePlaneDrawer(bi, lineDrawer, pixelDrawer);
                drawAxes(bipd);
                drawFunctions(bipd);
                g.drawImage(bi, 0, 0, panelDraw);
                listOfLabels.forEach(Component::repaint);
            }
        };

        panelDraw.setLayout(null);

        panelVerticalScroll = new JPanel();
        panelHorizontalScroll = new JPanel();

        setLayout(new BorderLayout());
        this.add(panelDraw, BorderLayout.CENTER);
        this.add(panelVerticalScroll, BorderLayout.EAST);
        this.add(panelHorizontalScroll, BorderLayout.SOUTH);

        panelVerticalScroll.setBorder(new LineBorder(Color.RED));
        panelHorizontalScroll.setBorder(new LineBorder(Color.RED));

        listOfLabels = new ArrayList<>();

        visibleRect = INITIAL_VISIBLE_RECT;
        listOfFunctions = new ArrayList<>();
        listOfFunctions.add(new Spline2DLine());
        listOfFunctions.add(new Spline2DLine());

        listOfColors = new ArrayList<>();
        listOfColors.add(Color.BLACK);
        listOfColors.add(Color.BLACK);

        lineDrawer = new DDALineDrawer();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (selectedCurveIndex != null) {
                    Spline2DLine spline = (Spline2DLine) listOfFunctions.get(selectedCurveIndex);
                    Point2D.Double newPoint = new Point2D.Double(me.getX(), me.getY());
                    Point2D.Double newPointOnPlane = DrawUtils.fromImageToPlane(newPoint, panelDraw.getWidth(),
                            panelDraw.getHeight(), visibleRect);
                    spline.addPoint(newPointOnPlane.x, newPointOnPlane.y);
                    reDrawLabels();
                    repaint();
                }
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                int countOfRotations = event.getWheelRotation();
                double scaleX = countOfRotations * 0.1 * INITIAL_VISIBLE_RECT.getWidth();
                double scaleY = countOfRotations * 0.1 * INITIAL_VISIBLE_RECT.getHeight();
                visibleRect.setRect(visibleRect.getX() - scaleX,
                        visibleRect.getY() + scaleY,
                        visibleRect.getWidth() + 2 * scaleX,
                        visibleRect.getHeight() + 2 * scaleY);
                reDrawLabels();
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int dx = 0;
                int dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        dy += visibleRect.getHeight() * 0.2;
                        break;
                    case KeyEvent.VK_DOWN:
                        dy -= visibleRect.getHeight() * 0.2;
                        break;
                    case KeyEvent.VK_LEFT:
                        dx -= visibleRect.getWidth() * 0.2;
                        break;
                    case KeyEvent.VK_RIGHT :
                        dx += visibleRect.getWidth() * 0.2;
                        break;
                }
                visibleRect.setRect(visibleRect.getX() + dx, visibleRect.getY() + dy,
                        visibleRect.getWidth(), visibleRect.getHeight());
                reDrawLabels();
                repaint();
            }
        });

        panelVerticalScroll.addMouseWheelListener(new MouseWheelListener() {
            @Override public void mouseWheelMoved(MouseWheelEvent event) {
                int countOfRotations = event.getWheelRotation();
                double scaleY = countOfRotations * 0.1 * INITIAL_VISIBLE_RECT.getHeight();
                visibleRect.setRect(visibleRect.getX(),
                        visibleRect.getY() + scaleY,
                        visibleRect.getWidth(),
                        visibleRect.getHeight() + 2 * scaleY);
                reDrawLabels();
                repaint();
            }
        });

        panelHorizontalScroll.addMouseWheelListener(new MouseWheelListener() {
            @Override public void mouseWheelMoved(MouseWheelEvent event) {
                int countOfRotations = event.getWheelRotation();
                double scaleX = countOfRotations * 0.1 * INITIAL_VISIBLE_RECT.getWidth();
                visibleRect.setRect(visibleRect.getX() - scaleX,
                        visibleRect.getY(),
                        visibleRect.getWidth() + 2 * scaleX,
                        visibleRect.getHeight());
                reDrawLabels();
                repaint();
            }
        });
    }

    public void setSelectedCurveIndex(Integer selectedCurveIndex) {
        if (this.selectedCurveIndex != null) {
            setColorForFunction(this.selectedCurveIndex, Color.BLACK);
        }
        this.selectedCurveIndex = selectedCurveIndex;
        if (this.selectedCurveIndex != null) {
            setColorForFunction(this.selectedCurveIndex, Color.BLUE);
        }
    }


    public void drawAxes(BufferedImagePlaneDrawer bufferedImagePlaneDrawer) {
        bufferedImagePlaneDrawer.drawLine(visibleRect, visibleRect.getX(), 0, visibleRect.getX() + visibleRect.getWidth(), 0);
        bufferedImagePlaneDrawer.drawLine(visibleRect, 0, visibleRect.getY() - visibleRect.getHeight(), 0, visibleRect.getY());

        int countByX = bufferedImagePlaneDrawer.getBi().getWidth() / 100;
        int countByY = bufferedImagePlaneDrawer.getBi().getHeight() / 100;

        Point2D.Double scaleStep = new Point2D.Double(visibleRect.getWidth() / countByX,
                visibleRect.getHeight() / countByY);

        Graphics g = bufferedImagePlaneDrawer.getBi().getGraphics();
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        g.setColor(Color.BLACK);

        double lineSize = Math.min(visibleRect.getWidth(), visibleRect.getHeight()) /
                (Math.min(bufferedImagePlaneDrawer.getBi().getWidth(), bufferedImagePlaneDrawer.getBi().getHeight()) / 10.0);

        for (int iX = (int) (visibleRect.getX() / scaleStep.getX()); iX * scaleStep.getX() < visibleRect.getWidth() + visibleRect.getX(); iX++) {
            Point2D.Double onImage1 = DrawUtils.fromPlaneToImage(new Point2D.Double(scaleStep.x * iX, 0), visibleRect,
                    bufferedImagePlaneDrawer.getBi().getWidth(), bufferedImagePlaneDrawer.getBi().getHeight());
            bufferedImagePlaneDrawer.drawLine(visibleRect, scaleStep.x * iX, lineSize, scaleStep.x * iX, -lineSize);
            g.drawString(String.format("%.2f", scaleStep.x * iX), (int) onImage1.x + 4, (int) onImage1.y - 4);
        }

        for (int iY = (int) ((visibleRect.getY() - visibleRect.getHeight()) / scaleStep.getY()); iY * scaleStep.getY() < visibleRect.getY(); iY++) {
            Point2D.Double onImage3 = DrawUtils.fromPlaneToImage(new Point2D.Double(0, scaleStep.y * iY), visibleRect,
                    bufferedImagePlaneDrawer.getBi().getWidth(), bufferedImagePlaneDrawer.getBi().getHeight());
            bufferedImagePlaneDrawer.drawLine(visibleRect, lineSize, scaleStep.y * iY, -lineSize, scaleStep.y * iY);
            g.drawString(String.format("%.2f", scaleStep.y * iY), (int) onImage3.x + 4, (int) onImage3.y - 4);
        }

    }

    public void drawFunctions(BufferedImagePlaneDrawer bufferedImagePlaneDrawer) {
        int i = 0;
        for (Function<Double, Double> f: listOfFunctions) {
            if (listOfColors.get(i) != null) {
                bufferedImagePlaneDrawer.getPixelDrawer().setColor(listOfColors.get(i));
                bufferedImagePlaneDrawer.drawFunction(visibleRect, f, visibleRect.getMinX(), visibleRect.getMaxX());
            }
            i++;
        }
    }

    public void reDrawLabels() {
        for (JLabel lab : listOfLabels) {
            panelDraw.remove(lab);
        }
        listOfLabels.clear();
        if (selectedCurveIndex != null) {
            Spline2DLine spline = (Spline2DLine) listOfFunctions.get(selectedCurveIndex);
            setColorForFunction(selectedCurveIndex, Color.BLUE);
            List<Point2D.Double> listOfPoints = spline.getListOfPoints();
            for (int i = 0; i < listOfPoints.size(); i++) {
                JLabel label = new JLabel();
                //label.setIcon(new ImageIcon("./aaa.png"));
                label.setText(null);
                Point2D.Double pt = listOfPoints.get(i);
                Point2D.Double pOnImage = DrawUtils.fromPlaneToImage(pt, visibleRect, panelDraw.getWidth(), panelDraw.getHeight());
                label.setBounds((int) pOnImage.x, (int) pOnImage.y, 20, 20);
                if (panelDraw.getVisibleRect().contains(label.getLocation())) {
                    label.setName(String.valueOf(i));
                    label.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
                    label.setEnabled(true);
                    label.addMouseMotionListener(new MouseMotionAdapter() {
                        @Override
                        public void mouseDragged(MouseEvent e) {
                            if (lastDragEvent == null) {
                                lastDragEvent = new MouseEvent(panelDraw, 0, System.nanoTime(), 0, 0, 0, 0, false);
                            }
                            Spline2DLine spline = (Spline2DLine) listOfFunctions.get(selectedCurveIndex);
                            Point2D.Double oldSplinePointOnPlane = spline.getPoint(Integer.parseInt(label.getName()));
                            Point2D.Double oldSplinePointOnScreen = DrawUtils.fromPlaneToImage(oldSplinePointOnPlane, visibleRect, panelDraw.getWidth(),
                                    panelDraw.getHeight());
                            Point2D.Double newPoint = new Point2D.Double(oldSplinePointOnScreen.x + e.getX() - lastDragEvent.getX(),
                                    oldSplinePointOnScreen.y + e.getY() - lastDragEvent.getY());
                            Point2D.Double newPointOnPlane = DrawUtils.fromImageToPlane(newPoint, panelDraw.getWidth(),
                                    panelDraw.getHeight(), visibleRect);
                            spline.setPoint(Integer.parseInt(e.getComponent().getName()), newPointOnPlane.x, newPointOnPlane.y);
                            repaint();
                            reDrawLabels();
                            lastDragEvent = e;
                        }
                    });
                    listOfLabels.add(label);
                    add(label);
                    label.setVisible(true);
                    repaint();
                    label.repaint();
                }
            }
        }
    }

    public void addMiddleFunction(double normalizedValue) {
        Function<Double, Double> f1 = listOfFunctions.get(0);
        Function<Double, Double> f2 = listOfFunctions.get(1);
        if (listOfFunctions.size() > 2) {
            listOfFunctions.remove(2);
            listOfColors.remove(2);
        }
        Function<Double, Double> currentFunction = (x -> {
            Double f2res = f2.apply(x);
            Double f1res = f1.apply(x);
            return (f2res - f1res) * normalizedValue + f1res;
        });
        listOfFunctions.add(2, currentFunction);
        listOfColors.add(2, Color.RED);
    }

    public Color setColorForFunction(int index, Color newColor) {
        Color oldColor = listOfColors.get(index);
        listOfColors.set(index, newColor);
        return oldColor;
    }
}
