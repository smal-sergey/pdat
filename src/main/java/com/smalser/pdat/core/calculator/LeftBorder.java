package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.structure.TaskConstraints;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * C == leftBorderSpeed
 * a(t) = alpha + C * t
 * */
public class LeftBorder implements UnivariateFunction
{
    public final TaskConstraints taskConstraints;

    public LeftBorder(TaskConstraints taskConstraints)
    {
        this.taskConstraints = taskConstraints;
    }

    @Override
    public double value(double t)
    {
        return taskConstraints.alpha + taskConstraints.leftBorderSpeed * t;
    }

    public double speed()
    {
        return taskConstraints.leftBorderSpeed;
    }
}
