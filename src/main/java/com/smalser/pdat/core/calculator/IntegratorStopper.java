package com.smalser.pdat.core.calculator;

import org.apache.commons.math3.ode.events.EventHandler;

public class IntegratorStopper implements EventHandler
{
    private final LeftBorder leftBorder;

    public IntegratorStopper(LeftBorder leftBorder)
    {
        this.leftBorder = leftBorder;
    }

    @Override
    public void init(double t0, double[] y0, double t)
    {
    }

    @Override
    public double g(double t, double[] y)
    {
        return leftBorder.maxLeftBorderValue - leftBorder.value(t);
    }

    @Override
    public Action eventOccurred(double t, double[] y, boolean increasing)
    {
        return Action.STOP;
    }

    @Override
    public void resetState(double t, double[] y)
    {
    }
}
