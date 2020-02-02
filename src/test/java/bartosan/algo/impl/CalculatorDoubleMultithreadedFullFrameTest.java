package bartosan.algo.impl;

import org.junit.Test;

import bartosan.main.Main;


public class CalculatorDoubleMultithreadedFullFrameTest
{
    public CalculatorDoubleMultithreadedFullFrameTest()
    {
    }

    @Test
    public void checkConvergence()
    {
        MyAssertion.assertDoesNotThrow(() -> Main.runTestWithImplementation(new CalculatorDoubleMultithreadedFullFrame()));
    }
}