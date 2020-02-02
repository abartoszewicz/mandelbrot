package bartosan.algo.impl;

import org.junit.Test;

import bartosan.main.Main;


public class CalculatorDoubleTest
{
    public CalculatorDoubleTest()
    {
    }

    @Test
    public void checkConvergence()
    {
        MyAssertion.assertDoesNotThrow(() -> Main.runTestWithImplementation(new CalculatorDouble()));
    }
}