package com.smalser.pdat.core.calculator;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * C == leftBorderSpeed
 * a(t) = alpha + C * t
 * */
public class LeftBorder implements UnivariateFunction
{
    public final double alpha;
    public final double maxLeftBorderValue;
    private final double leftBorderSpeed;

    public LeftBorder(double alpha, double leftBorderSpeed, double maxLeftBorderValue)
    {
        this.alpha = alpha;
        this.leftBorderSpeed = leftBorderSpeed;
        this.maxLeftBorderValue = maxLeftBorderValue;
    }

    @Override
    public double value(double t)
    {
        return alpha + leftBorderSpeed * t;
    }

    public double speed()
    {
        return leftBorderSpeed;
    }

    public double maxTime()
    {
        return (maxLeftBorderValue - alpha) / speed();
    }
}
