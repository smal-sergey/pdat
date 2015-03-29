package com.smalser.pdat.core.distribution;

import org.apache.commons.math3.random.AbstractRandomGenerator;

public class TrapezoidalRandomGenerator extends AbstractRandomGenerator
{
    public final double a;
    public final double b;
    public final double c;
    public final double d;

    public TrapezoidalRandomGenerator(double a, double b, double c, double d)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public void setSeed(long seed)
    {
    }

    @Override
    public double nextDouble()
    {
        throw new IllegalStateException("Not implemented");
    }
}
