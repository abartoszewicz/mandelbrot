package bartosan.algo.impl;

import org.junit.Test;

import bartosan.main.Main;


public class CalculatorDouble4ProfileTest
{
    public CalculatorDouble4ProfileTest()
    {
    }

    @Test
    public void checkConvergence()
    {
        MyAssertion.assertDoesNotThrow(() -> Main.runTestWithImplementation(new CalculatorDouble4Profile()));
    }
}