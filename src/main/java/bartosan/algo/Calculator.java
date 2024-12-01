package bartosan.algo;

public interface Calculator
{
    int checkConvergence(double ci, double c, int convergenceSteps);

    default void calculateFrame(final DrawAreaRect2D drawArea, final int width, final int height, final int convergenceSteps, int[] result) {
        double stepX = drawArea.getWidth() / width;
        double stepY = drawArea.getHeight() / height;
        int yR = 0;
        int adr = 0;
        for (double ci = drawArea.getMinY(); yR < height; ci = ci + stepY, yR++)
        {
            int xR = 0;
            for (double c = drawArea.getMinX(); xR < width; c = c + stepX, xR++, adr++)
            {

                int convergenceValue = checkConvergence(ci, c, convergenceSteps);
                result[adr] = convergenceValue;
            }
        }
    }
}
