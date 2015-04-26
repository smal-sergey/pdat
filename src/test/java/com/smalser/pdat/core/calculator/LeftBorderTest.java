package com.smalser.pdat.core.calculator;

import com.google.common.collect.Sets;
import com.smalser.pdat.core.structure.TaskConstraints;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Random;

import static com.smalser.pdat.core.structure.TaskInitialEstimate.uniform;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class LeftBorderTest
{
    private static final double DOUBLE_PRECISION = 0.001;
    private LeftBorder leftBorder;
    public static final double SPEED = 1;
    private TaskConstraints taskConstraints;

    @Before
    public void setUp() throws Exception
    {
        double gamma = 0.85;
        TaskInitialEstimate task = uniform("task1", 0, 10);

        taskConstraints = new TaskConstraints(Sets.newHashSet(task), gamma, SPEED);
        leftBorder = new LeftBorder(taskConstraints);
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
        assertTrue(leftBorder.value(t) >= taskConstraints.alpha);
    }

    @Test
    public void test_value_on_zero_time() throws Exception
    {
        double value = leftBorder.value(0);
        assertThat(value, closeTo(taskConstraints.alpha, DOUBLE_PRECISION));
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
}