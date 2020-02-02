package bartosan.algo.impl;

import bartosan.algo.Calculator;


public class CalculatorDoubleWrapped implements Calculator
{
    @Override
    public int checkConvergence(final double ci, final double c, final int convergenceSteps)
    {
        Double z = 0.0;
        Double zi = 0.0;
        for (int i = 0; i < convergenceSteps; i++)
        {
            Double ziT = 2 * (z * zi);
            Double zT = z * z - (zi * zi);
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
