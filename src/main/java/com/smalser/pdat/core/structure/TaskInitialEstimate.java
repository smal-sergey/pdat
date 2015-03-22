package com.smalser.pdat.core.structure;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class TaskInitialEstimate
{
    public final String taskId;
    public final double a1;
    public final double a2;
    public final double b1;
    public final double b2;
    public final Type type;

    private TaskInitialEstimate(String taskId, double a1, double a2, double b1, double b2, Type type)
    {
        this.taskId = taskId;
        this.a1 = a1;
        this.a2 = a2;
        this.b1 = b1;
        this.b2 = b2;
        this.type = type;
        this.validate();
    }

    public static TaskInitialEstimate uniform(String taskId, double a, double b)
    {
        return new TaskInitialEstimate(taskId, a, a, b, b, Type.UNIFORM);
    }

    public static TaskInitialEstimate triangular(String taskId, double a, double ab, double b)
    {
        return new TaskInitialEstimate(taskId, a, ab, ab, b, Type.TRIANGULAR);
    }

    public static TaskInitialEstimate trapezoidal(String taskId, double a1, double a2, double b1, double b2)
    {
        return new TaskInitialEstimate(taskId, a1, a2, b1, b2, Type.TRAPEZOIDAL);
    }

    public static TaskInitialEstimate normal(String taskId, double a1, double a2, double b1, double b2)
    {
        return new TaskInitialEstimate(taskId, a1, a2, b1, b2, Type.NORMAL);
    }

    public AbstractRealDistribution getDistribution()
    {
        switch (type)
        {
            case UNIFORM:
                return new UniformRealDistribution(a1, b1);
            case TRIANGULAR:
                return new TriangularDistribution(a1, a2, b2);
            case TRAPEZOIDAL:
                throw new UnsupportedOperationException("Not implemented");
            case NORMAL:
                throw new UnsupportedOperationException("Not implemented");
        }
        return null;
    }

    private void validate()
    {
        if (a1 < 0 || a2 < 0 || b1 < 0 || b2 < 0)
        {
            throw new RuntimeException("Estimated values can't be less then 0: " + this.toString());
        }

        boolean valid = true;

        switch (type)
        {
            case UNIFORM:
                valid = Double.compare(a1, a2) == 0 && Double.compare(b1, b2) == 0 && Double.compare(a1, b1) < 0;
                break;
            case TRIANGULAR:
                valid = Double.compare(a2, b1) == 0 && Double.compare(a1, a2) < 0 && Double.compare(b1, b2) < 0;
                break;
            case TRAPEZOIDAL:
            case NORMAL:
                valid = Double.compare(a1, a2) < 0 && Double.compare(a2, b1) < 0 && Double.compare(b1, b2) < 0;
                break;
        }
        if (!valid)
        {
            throw new RuntimeException("Estimated values are not valid: " + this.toString());
        }
    }

    @Override
    public String toString()
    {
        return "InputData{" +
                "a1=" + a1 +
                ", a2=" + a2 +
                ", b1=" + b1 +
                ", b2=" + b2 +
                ", type=" + type +
                '}';
    }

    public enum Type
    {
        UNIFORM,
        TRIANGULAR,
        TRAPEZOIDAL,
        NORMAL
    }
}
