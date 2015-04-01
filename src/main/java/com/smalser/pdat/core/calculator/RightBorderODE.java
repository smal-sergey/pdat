package com.smalser.pdat.core.calculator;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

/**
 * a(t) == left border value
 * C == leftBorderSpeed
 *
 *               f(a(t))
 * b'(t) = C * ----------
 *               f(b(t))
 * */
public class RightBorderODE implements FirstOrderDifferentialEquations
{
    private final AbstractRealDistribution distribution;
    private final LeftBorder leftBorder;

    public RightBorderODE(AbstractRealDistribution distribution, LeftBorder leftBorder)
    {
        this.distribution = distribution;
        this.leftBorder = leftBorder;
    }


    @Override
    public int getDimension()
    {
        return 1;
    }

    @Override
    public void computeDerivatives(double t, double[] y,
                                   double[] yDot) throws MaxCountExceededException, DimensionMismatchException
    {
        double a = leftBorder.value(t);
        double b = y[0];

        //todo create constant
        if(f(b) < 1.0e-5)
        {
            yDot[0] = 0;
        }
        else
        {
            yDot[0] = leftBorder.speed() * f(a) / f(b);
        }
    }

    private double f(double t)
    {
        return distribution.density(t);
    }
}
