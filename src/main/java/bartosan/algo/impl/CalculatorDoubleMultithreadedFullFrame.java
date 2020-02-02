package bartosan.algo.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

import bartosan.algo.CalculatorFullFrame;
import bartosan.algo.DrawAreaRect2D;


public class CalculatorDoubleMultithreadedFullFrame extends CalculatorDouble implements CalculatorFullFrame
{
    ThreadPoolExecutor threadPoolExecutor;

    public CalculatorDoubleMultithreadedFullFrame()
    {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    @Override
    public void calculateFrame(final DrawAreaRect2D drawArea, final int width, final int height, final int convergenceSteps, final int[] result)
    {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(height);

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
