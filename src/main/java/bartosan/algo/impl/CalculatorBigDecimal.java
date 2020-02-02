package bartosan.algo.impl;

import java.math.BigDecimal;


public class CalculatorBigDecimal //implements Calculator
{
    //dead slow! use at your own risk!
    public int checkConvergence(final double ci, final double c, final int convergenceSteps)
    {
        BigDecimal z = new BigDecimal(0);
        BigDecimal zi = new BigDecimal(0);

        for (int i = 0; i < convergenceSteps; i++)
        {
            BigDecimal ziT = z.multiply(zi).multiply(BigDecimal.valueOf(2));  //2 * (z * zi);
            BigDecimal zT = z.pow(2).subtract(zi.pow(2));
            z = zT.add(BigDecimal.valueOf(c));
            zi = ziT.add(BigDecimal.valueOf(ci));

            if (z.pow(2).add(zi.pow(2)).compareTo(BigDecimal.valueOf(4.0)) > 0)
            {
                return i;
            }
        }
        return convergenceSteps;
    }
}
