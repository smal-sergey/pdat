package com.smalser.pdat.core.distribution;

import org.apache.commons.math3.distribution.AbstractRealDistribution;

import static com.google.common.base.Preconditions.checkArgument;

public class TrapezoidalDistribution extends AbstractRealDistribution
{
    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final double h;

    public TrapezoidalDistribution(double a, double b, double c, double d)
    {
        super(new StubRandomGenerator());
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        validate(a, b, c, d);
        this.h = 2.0 / ((d - a) + (c - b));  //because square of trapeze = 1
    }

    private void validate(double a, double b, double c, double d)
    {
        checkArgument(a <= b, "a <= b violated");
        checkArgument(b < c, "b < c violated");
        checkArgument(c <= d, "c <= d violated");
    }

    @Override
    public double density(double x)
    {
        if (x <= a)
        {
            return 0;
        }
        else if (x <= b)
        {
            return (x - a) / (b - a) * h;
        }
        else if (x <= c)
        {
            return h;
        }
        else if (x <= d)
        {
            return (d - x) / (d - c) * h;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public double cumulativeProbability(double x)
    {
        double xh = density(x);

        if (x <= a)
        {
            return 0;
        }
        else if (x <= b)
        {
            return (x - a) * xh / 2;
        }
        else if (x <= c)
        {
            return (b - a) * h / 2 + (x - b) * h;
        }
        else if (x <= d)
        {
            return (b - a) * h / 2 + (c - b) * h + (h + xh) / 2 * (x - c);
        }
        else
        {
            return 1;
        }
    }

    @Override
    public double getNumericalMean()
    {
        double firstPart = h * (2 * b * b - a * b - a * a) / 6;
        double secondPart = (c * c - b * b) * h / 2;
        double thirdPart = h * (d * d + d * c - 2 * c * c) / 6;

        return firstPart + secondPart + thirdPart;
    }

    @Override
    public double getNumericalVariance()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public double getSupportLowerBound()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public double getSupportUpperBound()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public boolean isSupportLowerBoundInclusive()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public boolean isSupportUpperBoundInclusive()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public boolean isSupportConnected()
    {
        throw new IllegalStateException("Not implemented");
    }
}
