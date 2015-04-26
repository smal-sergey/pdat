package com.smalser.pdat.core.calculator;

import com.google.common.collect.Sets;
import com.smalser.pdat.core.structure.TaskConstraints;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.apache.commons.math3.ode.events.EventHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Random;

import static com.smalser.pdat.core.structure.TaskInitialEstimate.uniform;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class IntegratorStopperTest
{
    private TaskConstraints taskConstraints;
    private IntegratorStopper stopper;

    @Before
    public void setUp() throws Exception
    {
        int upperBound = 10;
        double gamma = 0.85;
        TaskInitialEstimate task = uniform("task1", 0, upperBound);

        taskConstraints = new TaskConstraints(Sets.newHashSet(task), gamma, 0.1);
        stopper = new IntegratorStopper(taskConstraints);
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
    public void test_same_sign_before_final_point(Double value0, Double value1) throws Exception
    {
        assumeTrue(value0 >= 0 && value0 < taskConstraints.rightBound);
        assumeTrue(value1 >= 0 && value1 < taskConstraints.rightBound);

        double g0 = stopper.g(0, new double[]{value0});
        double g1 = stopper.g(0, new double[]{value1});

        assertTrue(Math.signum(g0) == Math.signum(g1));
    }

    @Theory
    public void test_same_sign_after_final_point(Double value0, Double value1) throws Exception
    {
        assumeTrue(value0 >= 0 && value0 > taskConstraints.rightBound);
        assumeTrue(value1 >= 0 && value1 > taskConstraints.rightBound);

        double g0 = stopper.g(0, new double[]{value0});
        double g1 = stopper.g(0, new double[]{value1});

        assertTrue(Math.signum(g0) == Math.signum(g1));
    }

    @Theory
    public void test_opposite_sign_on_both_sides_of_final_point(Double value0, Double value1) throws Exception
    {
        assumeTrue(value0 >= 0 && value0 > taskConstraints.rightBound);
        assumeTrue(value1 >= 0 && value1 < taskConstraints.rightBound);

        double g0 = stopper.g(0, new double[]{value0});
        double g1 = stopper.g(0, new double[]{value1});

        assertTrue(Math.signum(g0) == -Math.signum(g1));
    }

    @Test
    public void test_event_is_stop() throws Exception
    {
        EventHandler.Action result = stopper.eventOccurred(0, null, true);
        assertThat(result, is(EventHandler.Action.STOP));
    }

    @Test
    public void test_remember_time_on_event_occured() throws Exception
    {
        double time = 42.42;
        assertNull(taskConstraints.getCalculatedMaxTime());
        stopper.eventOccurred(time, null, true);
        assertThat(taskConstraints.getCalculatedMaxTime(), closeTo(time, 0.01));
    }
}