package bartosan.constant;

import static java.lang.Math.pow;

import bartosan.algo.DrawAreaRect2D;


public class ViewParameters
{
    public static final double MAX_AREA = 2.75;
    public static final double MIN_AREA = 2.7E-10;
    public static final int ANIMATION_HALF_LENGTH = 100;
    public static final double FRAME_GROWTH = pow(MAX_AREA / MIN_AREA, 1.0 / ANIMATION_HALF_LENGTH);
    public static final int MARGIN = 50;
    public static final int EMPTY_COLOR = 0xffff0000;
    public static final int MAX_CONVERGENCE_DEPTH = 1500;
    public static final int[] CONVERGENCE_TO_RGB_MAP = new int[MAX_CONVERGENCE_DEPTH + 1];
    public static double CENTER_X = -0.0452407411;
    public static double CENTER_Y = 0.9868162204352258;
    public static DrawAreaRect2D MAX_AREA_2D = new DrawAreaRect2D(CENTER_X - MAX_AREA, CENTER_Y - MAX_AREA * 0.75,
        CENTER_X + MAX_AREA, CENTER_Y + MAX_AREA * 0.75);
    public static DrawAreaRect2D MIN_AREA_2D = new DrawAreaRect2D(CENTER_X - MIN_AREA, CENTER_Y - MIN_AREA * 0.75,
        CENTER_X + MIN_AREA, CENTER_Y + MIN_AREA * 0.75);
    public static int WINDOW_WIDTH = 800;
    public static int WINDOW_HEIGHT = 600;

    private ViewParameters()
    {
    }

}
