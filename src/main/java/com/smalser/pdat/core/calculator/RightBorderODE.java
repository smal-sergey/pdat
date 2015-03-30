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
        double leftValue = leftBorder.value(t);
        yDot[0] = leftBorder.speed() * f(leftValue) / f(y[0]);
    }

    private double f(double t)
    {
        return distribution.density(t);
    }
}
