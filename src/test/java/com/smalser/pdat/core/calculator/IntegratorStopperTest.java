package com.smalser.pdat.core.calculator;

import org.apache.commons.math3.ode.events.EventHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Random;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class IntegratorStopperTest
{

    private IntegratorStopper stopper;
    private LeftBorder leftBorder;

    @Before
    public void setUp() throws Exception
    {
        leftBorder = new LeftBorder(0, 1, 10);
        stopper = new IntegratorStopper(leftBorder);
        stopper.init(0, null, 0);
    }

    @DataPoints
    public static Double[] times()
    {
        Random r = new Random();
        Double[] doubles = new Double[100];
        for (int i = 0; i < 100; i++)
        {
            doubles[i] = r.nextDouble() * i;
        }
        return doubles;
    }

    @Theory
    public void test_same_sign_before_final_point(Double t0, Double t1) throws Exception
    {
        assumeTrue(t0 >= 0 && t0 < leftBorder.maxLeftBorderValue);
        assumeTrue(t1 >= 0 && t1 < leftBorder.maxLeftBorderValue);

        double value0 = stopper.g(t0, null);
        double value1 = stopper.g(t1, null);

        assertTrue(Math.signum(value0) == Math.signum(value1));
    }

    @Theory
    public void test_same_sign_after_final_point(Double t0, Double t1) throws Exception
    {
        assumeTrue(t0 >= 0 && t0 > leftBorder.maxLeftBorderValue);
        assumeTrue(t1 >= 0 && t1 > leftBorder.maxLeftBorderValue);

        double value0 = stopper.g(t0, null);
        double value1 = stopper.g(t1, null);

        assertTrue(Math.signum(value0) == Math.signum(value1));
    }

    @Theory
    public void test_opposite_sign_on_both_sides_of_final_point(Double t0, Double t1) throws Exception
    {
        assumeTrue(t0 >= 0 && t0 > leftBorder.maxLeftBorderValue);
        assumeTrue(t1 >= 0 && t1 < leftBorder.maxLeftBorderValue);

        double value0 = stopper.g(t0, null);
        double value1 = stopper.g(t1, null);

        assertTrue(Math.signum(value0) == -Math.signum(value1));
    }

    @Test
    public void testEventOccurred() throws Exception
    {
        assertThat(stopper.eventOccurred(0, null, true), is(EventHandler.Action.STOP));
    }
}