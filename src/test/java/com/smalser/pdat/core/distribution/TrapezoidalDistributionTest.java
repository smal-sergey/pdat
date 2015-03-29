package com.smalser.pdat.core.distribution;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class TrapezoidalDistributionTest
{
    public static final Double A = 0.0;
    public static final Double B = 0.5;
    public static final Double C = 1.0;
    public static final Double D = 1.5;
    private TrapezoidalDistribution distribution;

    @Before
    public void setUp() throws Exception
    {
        distribution = new TrapezoidalDistribution(A, B, C, D);
    }

    @DataPoints
    public static Double[] data()
    {
        Double[] Doubles = new Double[100];

        double d = -1.0;
        double step = 4.0 / 100;

        for (int i = 0; i < 100; i++, d += step)
        {
            Doubles[i] = d;
        }
        return Doubles;
    }

    @Theory
    public void test_density_on_ab(Double x0, Double x1) throws Exception
    {
        assumeTrue(x0 >= A && x0 <= B);
        assumeTrue(x1 >= A && x1 <= B);
        assumeTrue(x0 <= x1);

        assertTrue(distribution.density(x0) <= distribution.density(x1));
        assertTrue(distribution.density(x1) <= 1);
        assertTrue(distribution.density(x0) >= 0);
    }

    @Theory
    public void test_density_on_bc(Double x0) throws Exception
    {
        assumeTrue(x0 >= B && x0 <= C);

        assertTrue(distribution.density(x0) == 1);
    }

    @Theory
    public void test_density_on_cd(Double x0, Double x1) throws Exception
    {
        assumeTrue(x0 >= C && x0 <= D);
        assumeTrue(x1 >= C && x1 <= D);
        assumeTrue(x0 <= x1);

        assertTrue(distribution.density(x0) >= distribution.density(x1));
        assertTrue(distribution.density(x0) <= 1);
        assertTrue(distribution.density(x1) >= 0);
    }

    @Theory
    public void test_cumulative_probability_on_ab(Double x0) throws Exception
    {
        assumeTrue(x0 >= A && x0 <= B);

        assertTrue(distribution.cumulativeProbability(x0) >= 0);
        assertTrue(distribution.cumulativeProbability(x0) <= 0.25);
    }

    @Theory
    public void test_cumulative_probability_on_bc(Double x0) throws Exception
    {
        assumeTrue(x0 >= B && x0 <= C);

        assertTrue(distribution.cumulativeProbability(x0) >= 0.25);
        assertTrue(distribution.cumulativeProbability(x0) <= 0.75);
    }

    @Theory
    public void test_cumulative_probability_on_cd(Double x0) throws Exception
    {
        assumeTrue(x0 >= C && x0 <= D);

        assertTrue(distribution.cumulativeProbability(x0) >= 0.75);
        assertTrue(distribution.cumulativeProbability(x0) <= 1);
    }

    @Theory
    public void test_cumulative_probability_on_other(Double x0) throws Exception
    {
        if (x0 < A)
        {
            assertTrue(distribution.cumulativeProbability(x0) == 0);
        }
        else if (x0 > D)
        {
            assertTrue(distribution.cumulativeProbability(x0) == 1);
        }
        //else already tested
    }
}