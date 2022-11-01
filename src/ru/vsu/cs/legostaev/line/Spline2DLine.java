package ru.vsu.cs.legostaev.line;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Interpolates given values by B-Splines.
 *
 * @author krueger
 */
public class Spline2DLine implements Function<Double, Double> {

    private double[] xx;
    private double[] yy;

    private double[] a;
    private double[] b;
    private double[] c;
    private double[] d;

    /**
     * tracks the last index found since that is mostly commonly the next one used
     */
    private int storageIndex = 0;

    /**
     * Creates a new Spline.
     *
     * @param xx
     * @param yy
     */
    public Spline2DLine(double[] xx, double[] yy) {
        setValues(xx, yy);
    }

    public Spline2DLine() {
        this(new double[0], new double[0]);
    }

    @Override
    public Double apply(Double x) {
        if (xx.length == 0) {
            return Double.NaN;
        }

        if (xx.length == 1) {
            if (xx[0] == x) {
                return yy[0];
            } else {
                return Double.NaN;
            }
        }

        if (xx.length == 2) {
            return (x - xx[0]) * (yy[1] - yy[0]) / (xx[1] - xx[0]) + yy[0];
        }

        int index = Arrays.binarySearch(xx, x);
        if (index > 0) {
            return yy[index];
        }

        index = -(index + 1) - 1;
        if (index < 0) {
            return a[0] + b[0] * (x - xx[0]); //yy[0]
        }

        if (index == xx.length - 1) {
            return a[index] + getDx(xx[xx.length - 1] - (xx[xx.length - 1] - xx[xx.length - 2]) / 8) * (x - xx[index]);
        }

        return a[index]
                + b[index] * (x - xx[index])
                + c[index] * Math.pow(x - xx[index], 2)
                + d[index] * Math.pow(x - xx[index], 3);
    }

    /**
     * Set values for this Spline.
     *
     * @param xx
     * @param yy
     */
    public void setValues(double[] xx, double[] yy) {
        this.xx = xx;
        this.yy = yy;
        if (xx.length > 1) {
            calculateCoefficients();
        }
    }

    /**
     * Returns an interpolated value.
     *
     * @param x
     * @return the interpolated value
     */

    /**
     * Returns an interpolated value. To be used when a long sequence of values
     * are required in order, but ensure checkValues() is called beforehand to
     * ensure the boundary checks from getValue() are made
     *
     * @param x
     * @return the interpolated value
     */
    public double getFastValue(double x) {
        // Fast check to see if previous index is still valid
        if (storageIndex > -1 && storageIndex < xx.length - 1 && x > xx[storageIndex] && x < xx[storageIndex + 1]) {

        } else {
            int index = Arrays.binarySearch(xx, x);
            if (index > 0) {
                return yy[index];
            }
            index = -(index + 1) - 1;
            storageIndex = index;
        }

        if (storageIndex < 0) {
            return yy[0];
        }
        double value = x - xx[storageIndex];
        return a[storageIndex]
                + b[storageIndex] * value
                + c[storageIndex] * (value * value)
                + d[storageIndex] * (value * value * value);
    }

    /**
     * Used to check the correctness of this spline
     */
    public boolean checkValues() {
        if (xx.length < 2) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the first derivation at x.
     *
     * @param x
     * @return the first derivation at x
     */
    public double getDx(double x) {
        if (xx.length == 0 || xx.length == 1) {
            return 0;
        }

        int index = Arrays.binarySearch(xx, x);
        if (index < 0) {
            index = -(index + 1) - 1;
        }

        return b[index]
                + 2 * c[index] * (x - xx[index])
                + 3 * d[index] * Math.pow(x - xx[index], 2);
    }

    /**
     * Calculates the Spline coefficients.
     */
    private void calculateCoefficients() {
        int N = yy.length;
        a = new double[N];
        b = new double[N];
        c = new double[N];
        d = new double[N];

        double[] h = new double[N - 1];
        for (int i = 0; i < N - 1; i++) {
            a[i] = yy[i];
            h[i] = xx[i + 1] - xx[i];
            // h[i] is used for division later, avoid a NaN
            if (h[i] == 0.0) {
                h[i] = 0.01;
            }
        }

        if (N == 2) {
            a[0] = yy[0];
            b[0] = (yy[1] - yy[0]) / (3 * h[0]);
            return;
        }

        a[N - 1] = yy[N - 1];

        double[][] A = new double[N - 2][N - 2];
        double[] y = new double[N - 2];
        for (int i = 0; i < N - 2; i++) {
            y[i] = 3 * ((yy[i + 2] - yy[i + 1]) / h[i + 1] - (yy[i + 1] - yy[i]) / h[i]);

            A[i][i] = 2 * (h[i] + h[i + 1]);

            if (i > 0) {
                A[i][i - 1] = h[i];
            }

            if (i < N - 3) {
                A[i][i + 1] = h[i + 1];
            }
        }

        solve(A, y);

        for (int i = 0; i < N - 2; i++) {
            c[i + 1] = y[i];
            b[i] = (a[i + 1] - a[i]) / h[i] - (2 * c[i] + c[i + 1]) / 3 * h[i];
            d[i] = (c[i + 1] - c[i]) / (3 * h[i]);
        }
        b[N - 2] = (a[N - 1] - a[N - 2]) / h[N - 2] - (2 * c[N - 2] + c[N - 1]) / 3 * h[N - 2];
        d[N - 2] = (c[N - 1] - c[N - 2]) / (3 * h[N - 2]);
    }

    /**
     * Solves Ax=b and stores the solution in b.
     */
    public void solve(double[][] A, double[] b) {
        int n = b.length;
        for (int i = 1; i < n; i++) {
            A[i][i - 1] = A[i][i - 1] / A[i - 1][i - 1];
            A[i][i] = A[i][i] - A[i - 1][i] * A[i][i - 1];
            b[i] = b[i] - A[i][i - 1] * b[i - 1];
        }

        b[n - 1] = b[n - 1] / A[n - 1][n - 1];
        for (int i = b.length - 2; i >= 0; i--) {
            b[i] = (b[i] - A[i][i + 1] * b[i + 1]) / A[i][i];
        }
    }

    public List<Point2D.Double> getListOfPoints() {
        List<Point2D.Double> resList = new ArrayList<>();
        for (int i = 0; i < xx.length; i++) {
            resList.add(new Point2D.Double(xx[i], yy[i]));
        }
        return resList;
    }

    public void setPoint(int index, double x, double y) {
        xx[index] = x;
        yy[index] = y;
        sortValuesByX();
        if (xx.length > 1) {
            calculateCoefficients();
        }
    }

    public void addPoint(double x, double y) {
        double[] xxNew = new double[xx.length + 1];
        System.arraycopy(xx, 0, xxNew, 0, xx.length);
        xxNew[xxNew.length - 1] = x;
        double[] yyNew = new double[yy.length + 1];
        System.arraycopy(yy, 0, yyNew, 0, yy.length);
        yyNew[yyNew.length - 1] = y;
        xx = xxNew;
        yy = yyNew;
        sortValuesByX();
        if (xx.length > 1) {
            calculateCoefficients();
        }
    }

    public void sortValuesByX() {
        List<Point2D.Double> listOfPoints = getListOfPoints();
        listOfPoints.sort((o1, o2) -> (int) (o1.getX() - o2.getX()));
        for (int i = 0; i < listOfPoints.size(); i++) {
            xx[i] = listOfPoints.get(i).getX();
            yy[i] = listOfPoints.get(i).getY();
        }
    }

    public Point2D.Double getPoint(int index) {
        return new Point2D.Double(xx[index], yy[index]);
    }


}
