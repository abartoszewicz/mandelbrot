package bartosan.algo.impl;

import org.junit.Test;

import bartosan.main.Main;


public class CalculatorFloatTest
{
    public CalculatorFloatTest()
    {
    }

    @Test
    public void checkConvergence()
    {
        MyAssertion.assertDoesNotThrow(() -> MyAssertion.assertDoesNotThrow(() -> Main.runTestWithImplementation(new CalculatorFloat())));
    }
}