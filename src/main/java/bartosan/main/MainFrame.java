package bartosan.main;

import bartosan.algo.Calculator;
import bartosan.algo.DrawAreaRect2D;
import bartosan.algo.PixelPerformanceCounter;
import bartosan.algo.impl.CalculatorFloat;
import bartosan.util.ReflectionHelper;

import javax.swing.*;

import java.util.List;

import static bartosan.constant.ViewParameters.*;

public class MainFrame extends JFrame {
    private final MainPanel panel;
    private Calculator currentImplementation;
    private long frameNumber;
    private int implNumber = 0;

    private final List<Calculator> instanceList;
    private boolean animationRunning = false;

    public MainFrame(String name, int windowWidth, int windowHeight) {
        super(name);
        setVisible(true);
        setSize(windowWidth, windowHeight);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new MainPanel(windowWidth, windowHeight);
        super.add(panel);
        currentImplementation = new CalculatorFloat();
        instanceList = getCalculatorImplementations();
        startAnimation();
    }

    long recalculateAndRepaint(DrawAreaRect2D drawArea) {
        synchronized (panel) {
            long startDrawingTime = System.currentTimeMillis();
            currentImplementation.calculateFrame(drawArea, panel.getWidth(), panel.getHeight(), MAX_CONVERGENCE_DEPTH, panel.getInternalCopyBuffer());
            long endDrawingTime = System.currentTimeMillis();
            panel.drawBufferOnImage(MAX_CONVERGENCE_DEPTH);
            super.repaint();
            return endDrawingTime - startDrawingTime;
        }
    }

    private void startAnimation() {
        if (!animationRunning) {
            System.out.println("Starting animation");
            animationRunning = true;
            new Thread(() -> {
                implNumber = 0;
                while (implNumber < getNumberOfImplementations()) {
                    currentImplementation = getNextImplementation(implNumber++);
                    setTitle(currentImplementation.getClass().getSimpleName());

                    frameNumber = 0;
                    long pixelCount = 0;
                    long totalCalculationTime = 0;
                    while (frameNumber <= 2 * ANIMATION_HALF_LENGTH) {
                        long calculationTime = recalculateAndRepaint(animateDrawArea(frameNumber));
                        totalCalculationTime += calculationTime;
                        pixelCount += panel.getPixelCount();
                        frameNumber++;
                    }

                    PixelPerformanceCounter pixelPerformanceCounter = new PixelPerformanceCounter(pixelCount, totalCalculationTime);
                    System.out.println(currentImplementation.getClass().getSimpleName() + " has drawn " + pixelPerformanceCounter);
                }
                animationRunning = false;
            }).start();

            System.out.println("Stopping animation");
        }
    }

    private Calculator getNextImplementation(int i) {
        return instanceList.get(i);
    }

    private int getNumberOfImplementations() {
        return instanceList.size();
    }

    private List<Calculator> getCalculatorImplementations() {
        return ReflectionHelper.getInstancesOf("bartosan.algo.impl", Calculator.class);
    }

    private DrawAreaRect2D animateDrawArea(final long l)
    {
        DrawAreaRect2D result;
        if (l < ANIMATION_HALF_LENGTH)
        {
            result = MAX_AREA_2D;
            for (int i = 0; i < l; i++)
            {
                result = result.grow(1.0 / FRAME_GROWTH);
            }
        }
        else
        {
            result = MIN_AREA_2D;
            for (int i = 100; i < l; i++)
            {
                result = result.grow(FRAME_GROWTH);
            }
        }
        return result;
    }

}



