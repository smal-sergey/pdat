package com.smalser.pdat.core.distribution;

import org.apache.commons.math3.random.AbstractRandomGenerator;

import java.util.Random;

public class DefaultRandomGenerator extends AbstractRandomGenerator
{
    private Random random;

    @Override
    public void setSeed(long seed)
    {
        random = new Random(seed);
    }

    @Override
    public double nextDouble()
    {
        if(random == null){
            setSeed(System.currentTimeMillis());
        }
        return random.nextDouble();
    }
}
