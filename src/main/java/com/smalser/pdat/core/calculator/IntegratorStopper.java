package com.smalser.pdat.core.calculator;

import org.apache.commons.math3.ode.events.EventHandler;

public class IntegratorStopper implements EventHandler
{
    private final TaskConstraints taskConstraints;

    public IntegratorStopper(TaskConstraints taskConstraints)
    {
        this.taskConstraints = taskConstraints;
    }

    @Override
    public void init(double t0, double[] y0, double t)
    {
//        System.out.println("init " + taskConstraints.rightBound);
    }

    @Override
    public double g(double t, double[] y)
    {
//        System.out.println(t + "\t" + y[0] + "\t" + (taskConstraints.rightBound - y[0]));
        return taskConstraints.rightBound - y[0];
    }

    @Override
    public Action eventOccurred(double t, double[] y, boolean increasing)
    {
        taskConstraints.setCalculatedMaxTime(t);
        return Action.STOP;
    }

    @Override
    public void resetState(double t, double[] y)
    {
    }
}
