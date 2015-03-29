package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.Result;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import com.smalser.pdat.core.structure.UserInitialEstimate;
import org.junit.Test;

import static com.smalser.pdat.core.structure.TaskInitialEstimate.uniform;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class ProjectDurationCalculatorTest
{

    @Test
    public void testCalculate() throws Exception
    {
        int upperBound = 10;
        double gamma = 0.85;
        TaskInitialEstimate task = uniform("task1", 0, upperBound);

        ProjectInitialEstimates initialData = new ProjectInitialEstimates();
        UserInitialEstimate userEstimate = new UserInitialEstimate(1);
        userEstimate.addEstimate(task);
        initialData.addUserEstimates(userEstimate);
        ProjectDurationCalculator calc = new ProjectDurationCalculator(initialData);
        Result result = calc.calculate(gamma);

        //todo create Result matcher
        assertThat(result.b - result.a, closeTo(8.5, 0.01));
    }
}