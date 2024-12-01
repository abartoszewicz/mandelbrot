package bartosan.algo.impl;

import java.util.stream.IntStream;

import bartosan.algo.Calculator;
import bartosan.algo.DrawAreaRect2D;


public class MultithreadedDouble extends CalculatorDouble implements Calculator
{
    public MultithreadedDouble()
    {
    }

    @Override
    public void calculateFrame(final DrawAreaRect2D drawArea, final int width, final int height, final int convergenceSteps, final int[] result)
    {
        IntStream.range(0, height).parallel().forEach(y -> {
            double ci = drawArea.getHeight() / height * y + drawArea.getMinY();
            for (int x = 0; x < width; x++)
            {
                double cr = drawArea.getWidth() / width * x + drawArea.getMinX();
                int addr = x + y * width;
                result[addr] = checkConvergence(ci, cr, convergenceSteps);
            }
        });
    }
}
