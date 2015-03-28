package com.smalser.pdat.core.calculator;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.ode.ContinuousOutputModel;

public class RightBorder implements UnivariateFunction
{
    private final ContinuousOutputModel rightBorderValues;

    public RightBorder(ContinuousOutputModel rightBorderValues)
    {
        this.rightBorderValues = rightBorderValues;
    }

    @Override
    public double value(double t)
    {
        rightBorderValues.setInterpolatedTime(t);
        return rightBorderValues.getInterpolatedState()[0];
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for(int t = 0; t < 100; t++){
            sb.append(t).append("\t").append(value(t)).append("\n");
        }
        return sb.toString();
    }
}
