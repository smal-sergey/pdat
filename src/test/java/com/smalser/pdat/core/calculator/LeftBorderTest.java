package com.smalser.pdat.core.calculator;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class LeftBorderTest
{
    private static final double DOUBLE_PRECISION = 0.001;
    private LeftBorder leftBorder;
    public static final double SPEED = 1;

    @Before
    public void setUp() throws Exception
    {
        int alpha = 0;
        int maxValue = 10;

        leftBorder = new LeftBorder(alpha, SPEED, maxValue);
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
    public void test_linear_value(Double a, Double b)
    {
        assumeTrue(a >= 0 && b >= 0);
        assumeTrue(a > b);
        assertTrue(leftBorder.value(a) > leftBorder.value(b));
    }

    @Theory
    public void test_not_negative_value(Double t)
    {
        assumeTrue(t >= 0);
        assertTrue(leftBorder.value(t) >= leftBorder.alpha);
    }

    @Test
    public void test_value_on_zero_time() throws Exception
    {
        double value = leftBorder.value(0);
        assertThat(value, closeTo(leftBorder.alpha, DOUBLE_PRECISION));
    }

    @Test
    public void test_some_value() throws Exception
    {
        double value = leftBorder.value(3);
        assertThat(value, closeTo(3, DOUBLE_PRECISION));
    }

    @Test
    public void test_speed() throws Exception
    {
        assertThat(leftBorder.speed(), closeTo(SPEED, DOUBLE_PRECISION));
    }

    @Test
    public void test_max_time_with_alpha_0() throws Exception
    {
        assertThat(leftBorder.maxTime(), closeTo(10, DOUBLE_PRECISION));
    }

    @Test
    public void test_max_time_with_alpha_3() throws Exception
    {
        leftBorder = new LeftBorder(3, SPEED, 10);
        assertThat(leftBorder.maxTime(), closeTo(7, DOUBLE_PRECISION));
    }
}