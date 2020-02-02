package bartosan.algo.impl;

import org.junit.Test;

import bartosan.main.Main;


public class CalculatorOpenCLFullFrameTest
{
    public CalculatorOpenCLFullFrameTest()
    {
    }

    @Test
    public void checkConvergence()
    {
        Main.runTestWithImplementation(new CalculatorDoubleMultithreadedFullFrame());
    }
}