package bartosan.algo.impl;

import bartosan.algo.Calculator;


public class CalculatorDouble4Profile implements Calculator
{
    @Override
    public int checkConvergence(final double ci, final double c, final int convergenceSteps)
    {
        double z = 0;
        double zi = 0;
        for (int i = 0; i < convergenceSteps; i++)
        {
            double ziT = firstMul(z, zi);// (z * zi) * 2;
            double zT = secondMul(z, zi);//z * z - (zi * zi);
            z = firstAdd(zT, c);      //zT + c;
            zi = secondAdd(ziT, ci);  // ziT + ci;

            if (condition(z, zi))  //z * z + zi * zi >= 4.0)
            {
                return i;
            }
        }
        return convergenceSteps;
    }

    private double secondAdd(final double ziT, final double ci)
    {
        return ziT + ci;
    }

    private double firstAdd(final double zT, final double c)
    {
        return zT + c;
    }

    private boolean condition(final double z, final double zi)
    {
        double zz = z * z;
        if (zz > 4.0)
        {
            return true;
        }

        return zz + zi * zi >= 4.0;
    }

    private double firstMul(final double z, final double zi)
    {
        return z * zi * 2;
    }

    private double secondMul(final double z, final double zi)
    {
        return z * z - zi * zi;
    }

}
