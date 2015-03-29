package com.smalser.pdat.core.calculator;

import com.google.common.collect.Sets;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.junit.Test;

import static com.smalser.pdat.core.structure.TaskInitialEstimate.triangular;
import static com.smalser.pdat.core.structure.TaskInitialEstimate.uniform;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class BetaFinderTest
{

    @Test
    public void test_uniform_distribution() throws Exception
    {
        int upperBound = 10;
        double gamma = 0.85;
        TaskInitialEstimate task = uniform("task1", 0, upperBound);
        TaskConstraints taskConstraints = new TaskConstraints(Sets.newHashSet(task), gamma, 0.1);

        BetaFinder betaFinder = new BetaFinder(task.getDistribution(), taskConstraints);
        assertThat(betaFinder.findMinBeta(), closeTo(gamma * upperBound, 0.01));
    }

    @Test
    public void test_symmetric_triangle_distribution() throws Exception
    {
        double gamma = 0.5;
        TaskInitialEstimate task = triangular("task1", 0, 5, 10);
        TaskConstraints taskConstraints = new TaskConstraints(Sets.newHashSet(task), gamma, 0.1);

        BetaFinder betaFinder = new BetaFinder(task.getDistribution(), taskConstraints);
        assertThat(betaFinder.findMinBeta(), closeTo(5, 0.01));
    }
}