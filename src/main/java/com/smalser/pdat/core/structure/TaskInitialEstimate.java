package com.smalser.pdat.core.structure;

import com.smalser.pdat.AbstractTask;
import com.smalser.pdat.core.distribution.TrapezoidalDistribution;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import static com.google.common.base.Preconditions.checkArgument;

public class TaskInitialEstimate extends AbstractTask
{
    private final String description;
    private final double a1;
    private final double a2;
    private final double b1;
    private final double b2;
    private final AbstractRealDistribution distribution;

    public final Type type;

    private TaskInitialEstimate(String id, double a1, double a2, double b1, double b2, Type type,
                                AbstractRealDistribution distribution, String description)
    {
        super(id);
        this.a1 = a1;
        this.a2 = a2;
        this.b1 = b1;
        this.b2 = b2;
        this.type = type;
        this.distribution = distribution;
        this.description = description;
    }

    public static TaskInitialEstimate uniform(String taskId, double a, double b)
    {
        checkArgument(a < b, "a < b violated, " + taskId);
        return new TaskInitialEstimate(taskId, a, a, b, b, Type.UNIFORM, new UniformRealDistribution(a, b),
                String.format("Uniform(%f, %f)", a, b));
    }

    public static TaskInitialEstimate triangular(String taskId, double a, double b, double c)
    {
        checkArgument(a < b, "a < b violated, " + taskId);
        checkArgument(b < c, "b < c violated, " + taskId);
        return new TaskInitialEstimate(taskId, a, b, b, c, Type.TRIANGULAR, new TriangularDistribution(a, b, c),
                String.format("Triangular(%f, %f, %f)", a, b, c));
    }

    public static TaskInitialEstimate trapezoidal(String taskId, double a, double b, double c, double d)
    {
        checkArgument(a <= b, "a <= b violated, " + taskId);
        checkArgument(b < c, "b < c violated, " + taskId);
        checkArgument(c <= d, "c <= d violated, " + taskId);
        return new TaskInitialEstimate(taskId, a, b, c, d, Type.TRAPEZOIDAL, new TrapezoidalDistribution(a, b, c, d),
                String.format("Trapezoidal(%f, %f, %f, %f)", a, b, c, d));
    }

    public static TaskInitialEstimate normal(String taskId, double mean, double deviation)
    {
        checkArgument(deviation > 0, "deviation > 0 violated, " + taskId);

        //todo
        //six sigma rule
//        return new TaskInitialEstimate(id, mean - 3 * deviation, mean, mean, mean + 3 * deviation, Type.NORMAL, new NormalDistribution(mean, deviation));
        return new TaskInitialEstimate(taskId, mean - 4 * deviation, mean, mean, mean + 4 * deviation, Type.NORMAL, new NormalDistribution(mean, deviation),
                String.format("Normal(%f, %f)", mean, deviation));
    }

    public AbstractRealDistribution getDistribution()
    {
        return distribution;
    }

    public double min()
    {
        return a1;
    }

    public double max()
    {
        return b2;
    }

    @Override
    public String toString()
    {
        return description;
    }

    public enum Type
    {
        UNIFORM,
        TRIANGULAR,
        TRAPEZOIDAL,
        NORMAL
    }
}
