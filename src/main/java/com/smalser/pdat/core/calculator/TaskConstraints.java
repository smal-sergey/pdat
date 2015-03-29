package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.structure.TaskInitialEstimate;

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
        return estimates.stream().map(e -> e.a1).min(Double::compareTo).get();
    }

    private double findRightBound(Set<TaskInitialEstimate> estimates)
    {
        return estimates.stream().map(e -> e.b2).max(Double::compareTo).get();
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
