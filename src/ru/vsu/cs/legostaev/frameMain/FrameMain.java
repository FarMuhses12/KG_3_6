package ru.vsu.cs.legostaev.frameMain;

import ru.vsu.cs.legostaev.frameMain.panelCoordinatesPlane.PanelCoordinatesPlane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.*;

public class FrameMain extends JFrame implements ActionListener {

    private static final int COUNT_OF_TRANSFORMATIONS = 100;

    private int totalDuration = 5;
    private final Timer timer = new Timer(totalDuration * 1000 / COUNT_OF_TRANSFORMATIONS, this);
    private int ticksFromStart = 0;

    private JPanel panelMain;
    private JSlider sliderTime;
    private JButton buttonStop;
    private JButton buttonStart;
    private JButton buttonReset;
    private JPanel panelTime;
    private JPanel panelCurve;
    private JSpinner spinnerTime;
    private JButton buttonResetSelection;
    private PanelCoordinatesPlane panelDraw;
    private ButtonGroup buttonGroupSelectedCurve;

    public FrameMain() {

        this.setTitle("FrameMain");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelDraw = new PanelCoordinatesPlane();
        panelDraw.setBorder(new LineBorder(Color.BLACK));

        buttonGroupSelectedCurve = new ButtonGroup();
        JRadioButton buttonFirstCurve = new JRadioButton("1");
        buttonFirstCurve.setActionCommand("1");
        JRadioButton buttonSecondCurve = new JRadioButton("2");
        buttonSecondCurve.setActionCommand("2");
        buttonGroupSelectedCurve.add(buttonFirstCurve);
        buttonGroupSelectedCurve.add(buttonSecondCurve);

        panelTime.setBorder(new EmptyBorder(20, 20, 20, 20));

        panelMain.setLayout(new BorderLayout());
        panelMain.add(panelTime, BorderLayout.NORTH);
        panelMain.add(panelCurve, BorderLayout.SOUTH);
        panelMain.add(panelDraw, BorderLayout.CENTER);

        JPanel panelSelectedCurve = new JPanel();
        panelSelectedCurve.setLayout(new BorderLayout());
        panelSelectedCurve.add(buttonFirstCurve, BorderLayout.WEST);
        panelSelectedCurve.add(buttonSecondCurve, BorderLayout.EAST);
        panelCurve.add(panelSelectedCurve, BorderLayout.WEST);
        panelCurve.setBorder(new EmptyBorder(20, 20, 20, 20));

        sliderTime.setMaximum(COUNT_OF_TRANSFORMATIONS);
        spinnerTime.setValue(totalDuration);

        panelTime.setFocusable(false);
        sliderTime.setFocusable(false);
        spinnerTime.setFocusable(false);
        ((JSpinner.DefaultEditor) spinnerTime.getEditor()).getTextField().setFocusable(false);
        buttonReset.setFocusable(false);
        buttonStart.setFocusable(false);
        buttonStop.setFocusable(false);
        buttonFirstCurve.setFocusable(false);
        buttonSecondCurve.setFocusable(false);
        panelSelectedCurve.setFocusable(false);
        buttonResetSelection.setFocusable(false);
        panelDraw.grabFocus();

        setSize(1000, 500);
        setLocationRelativeTo(null);

        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.start();
            }
        });
        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
            }
        });
        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ticksFromStart = 0;
                sliderTime.setValue(ticksFromStart);
                panelDraw.setColorForFunction(2, null);
                panelDraw.repaint();
            }
        });
        sliderTime.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ticksFromStart = sliderTime.getValue();
                double normalizedValue = ticksFromStart / (double) COUNT_OF_TRANSFORMATIONS;
                panelDraw.addMiddleFunction(normalizedValue);
                panelDraw.repaint();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                panelDraw.dispatchEvent(e);
            }
        });
        spinnerTime.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                totalDuration = (int) spinnerTime.getValue();
                timer.setDelay(totalDuration * 1000 / COUNT_OF_TRANSFORMATIONS);
            }
        });
        buttonResetSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonGroupSelectedCurve.clearSelection();
                panelDraw.setSelectedCurveIndex(null);
                panelDraw.repaint();
            }
        });
        ChangeListener buttonsSelectedCurveChangeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changEvent) {
                AbstractButton aButton = (AbstractButton)changEvent.getSource();
                ButtonModel aModel = aButton.getModel();
                boolean selected = aModel.isSelected();
                if (selected) {
                    panelDraw.setSelectedCurveIndex(Integer.parseInt(aModel.getActionCommand()) - 1);
                } else if (!buttonFirstCurve.isSelected() && !buttonSecondCurve.isSelected()) {
                    panelDraw.setSelectedCurveIndex(null);
                }
                repaint();
            }
        };
        buttonFirstCurve.addChangeListener(buttonsSelectedCurveChangeListener);
        buttonSecondCurve.addChangeListener(buttonsSelectedCurveChangeListener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            if (ticksFromStart < COUNT_OF_TRANSFORMATIONS) {
                ticksFromStart++;
                sliderTime.setValue(ticksFromStart);
                double normalizedValue = ticksFromStart / (double) COUNT_OF_TRANSFORMATIONS;
                panelDraw.addMiddleFunction(normalizedValue);
                panelDraw.repaint();
            } else {
                timer.stop();
            }
        }
    }
}
