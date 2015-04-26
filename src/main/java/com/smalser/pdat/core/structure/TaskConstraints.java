package com.smalser.pdat.core.structure;

import org.apache.commons.math3.analysis.UnivariateFunction;

import java.util.Set;

public class TaskConstraints
{
    public final double leftBound;
    public final double rightBound;
    public final double alpha;
    public final double leftBorderSpeed;
    public final double gamma;

    private Double calculatedMaxTime;

    public TaskConstraints(Set<TaskInitialEstimate> estimates, double gamma, double leftBorderSpeed)
    {
        this.gamma = gamma;
        this.leftBorderSpeed = leftBorderSpeed;
        leftBound = findLeftBound(estimates);
        rightBound = findRightBound(estimates);
        alpha = leftBound;
    }

    private double findLeftBound(Set<TaskInitialEstimate> estimates)
    {
        return estimates.stream().map(TaskInitialEstimate::min).min(Double::compareTo).get();
    }

    private double findRightBound(Set<TaskInitialEstimate> estimates)
    {
        return estimates.stream().map(TaskInitialEstimate::max).max(Double::compareTo).get();
    }

    public void dumpBorders(UnivariateFunction leftBorder, UnivariateFunction rightBorder)
    {
        double t = -1;
        double left;
        double right;
        do
        {
            t += 1;
            left = leftBorder.value(t);
            right = rightBorder.value(t);
            System.out.println(String.format("%f\t%f\t%f", t, left, right));

        } while (left < rightBound);
    }

    public Double getCalculatedMaxTime()
    {
        return calculatedMaxTime;
    }

    public void setCalculatedMaxTime(double calculatedMaxTime)
    {
        this.calculatedMaxTime = calculatedMaxTime;
    }
}
