package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.Result;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import com.smalser.pdat.core.structure.UserInitialEstimate;
import org.junit.Test;

import static com.smalser.pdat.core.structure.TaskInitialEstimate.*;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class ProjectDurationCalculatorTest
{

    @Test
    public void testCalculate() throws Exception
    {
        int upperBound = 10;
        double gamma = 0.8;
//        TaskInitialEstimate task = uniform("task1", 0, upperBound);
        TaskInitialEstimate task = normal("task1", 5, 1);
        TaskInitialEstimate task2 = normal("task1", 8, 1);

//        TaskInitialEstimate task = triangular("task1", 0, 5, 10);
//        TaskInitialEstimate task = trapezoidal("task1", 0, 2, 6, 8);

        ProjectInitialEstimates initialData = new ProjectInitialEstimates();

        UserInitialEstimate userEstimate = new UserInitialEstimate(1);
        userEstimate.addEstimate(task);

        UserInitialEstimate user2Estimate = new UserInitialEstimate(2);
        user2Estimate.addEstimate(task2);

        initialData.addUserEstimates(userEstimate);
        initialData.addUserEstimates(user2Estimate);

        ProjectDurationCalculator calc = new ProjectDurationCalculator(initialData);
        Result result = calc.calculate(gamma);

        //todo create Result matcher
//        assertThat(result.getB() - result.getA(), closeTo(8.5, 0.01));

        result.dumpToXls("results.xlsx");
    }
}