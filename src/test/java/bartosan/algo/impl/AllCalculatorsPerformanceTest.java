package bartosan.algo.impl;

import java.io.IOException;

import org.junit.Test;

import bartosan.algo.Calculator;
import bartosan.main.Main;
import bartosan.util.ReflectionHelper;


public class AllCalculatorsPerformanceTest
{
    public AllCalculatorsPerformanceTest()
    {
    }

    @Test
    public void compareAllImplementationsGreatTest()
    {
        Class[] implementations = new Class[0];
        try
        {
            implementations = ReflectionHelper.getClasses("bartosan.algo.impl", Calculator.class);
        }
        catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
        }
        for (Class impl : implementations)
        {
            try
            {
                Main.runTestWithImplementation((Calculator) impl.newInstance());
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }
}
