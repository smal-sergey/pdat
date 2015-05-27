package com.smalser.pdat.core.structure;

import com.smalser.pdat.AbstractTask;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class EstimatedTask extends AbstractTask
{
    public final AbstractRealDistribution distribution;
    public final double leftBound;
    public final double rightBound;
    public final double a;
    public final double b;

    public EstimatedTask(String id, double leftBound, double rightBound, double a, double b,
                         AbstractRealDistribution distribution)
    {
        super(id);
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.a = a;
        this.b = b;

        this.distribution = distribution;
    }

    public double density(double x)
    {
        return distribution.density(x);
    }
}
