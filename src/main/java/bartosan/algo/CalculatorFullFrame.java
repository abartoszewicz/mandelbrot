package bartosan.algo;

public interface CalculatorFullFrame extends Calculator
{
    void calculateFrame(final DrawAreaRect2D drawArea, final int width, final int height, final int convergenceSteps, int[] result);

}
