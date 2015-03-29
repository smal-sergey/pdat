package com.smalser.pdat.core.distribution;

import org.apache.commons.math3.random.AbstractRandomGenerator;

public class StubRandomGenerator extends AbstractRandomGenerator
{
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
