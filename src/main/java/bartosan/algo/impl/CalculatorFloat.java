package bartosan.algo.impl;

import bartosan.algo.Calculator;


public class CalculatorFloat implements Calculator
{

    @Override
    public int checkConvergence(final double ci, final double c, final int convergenceSteps)
    {
        float z = 0;
        float zi = 0;
        final float ciFloat = (float) ci;
        final float cFloat = (float) c;

        for (int i = 0; i < convergenceSteps; i++)
        {
            float ziT = 2 * (z * zi);
            float zT = z * z - (zi * zi);
            z = zT + cFloat;
            zi = ziT + ciFloat;
            float zz = z * z;
            if (zz >= 4.0f)
            {
                return i;
            }
            float zizi = zi * zi;
            if (zizi >= 4.0f)
            {
                return i;
            }

            if (zz + zizi >= 4.0f)
            {
                return i;
            }
        }
        return convergenceSteps;
    }
}
