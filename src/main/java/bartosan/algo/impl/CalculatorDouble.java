package bartosan.algo.impl;

import bartosan.algo.Calculator;


public class CalculatorDouble implements Calculator
{
    @Override
    public int checkConvergence(final double ci, final double c, final int convergenceSteps)
    {
        double z = 0;
        double zi = 0;
        for (int i = 0; i < convergenceSteps; i++)
        {
            double ziT = (z * zi) * 2;
            double zT = z * z - (zi * zi);
            z = zT + c;
            zi = ziT + ci;

            if (z * z + zi * zi >= 4.0)
            {
                return i;
            }
        }
        return convergenceSteps;
    }
}
