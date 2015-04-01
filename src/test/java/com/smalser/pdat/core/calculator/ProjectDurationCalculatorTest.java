package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.Result;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import com.smalser.pdat.core.structure.UserInitialEstimate;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.smalser.pdat.core.structure.TaskInitialEstimate.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

@RunWith(Theories.class)
public class ProjectDurationCalculatorTest
{

    @DataPoints
    public static UserInitialEstimate[] estimates()
    {
        String taskId = "task1";
        List<UserInitialEstimate> estimates = new ArrayList<>();

        estimates.add(estimate(1, uniform(taskId, 1, 4)));
        estimates.add(estimate(2, uniform(taskId, 5, 7)));
        estimates.add(estimate(3, uniform(taskId, 1, 8)));

        estimates.add(estimate(4, normal(taskId, 5, 1)));
        estimates.add(estimate(5, normal(taskId, 7, 2)));
        estimates.add(estimate(6, normal(taskId, 8, 1)));

        estimates.add(estimate(7, triangular(taskId, 1, 4, 5)));
        estimates.add(estimate(8, triangular(taskId, 6, 7, 9)));
        estimates.add(estimate(9, triangular(taskId, 3, 5, 8)));

        estimates.add(estimate(10, trapezoidal(taskId, 1, 4, 5, 6)));
        estimates.add(estimate(11, trapezoidal(taskId, 7, 7, 8, 9)));
        estimates.add(estimate(12, trapezoidal(taskId, 3, 5, 7, 8)));
        estimates.add(estimate(13, trapezoidal(taskId, 2, 4, 6, 8)));

        return estimates.toArray(new UserInitialEstimate[estimates.size()]);
    }

    private static UserInitialEstimate estimate(Integer id, TaskInitialEstimate est)
    {
        UserInitialEstimate userEstimate = new UserInitialEstimate(1);
        userEstimate.addEstimate(est);
        return userEstimate;
    }

    @Theory
    public void test_calculate(UserInitialEstimate estimate1, UserInitialEstimate estimate2,
                               UserInitialEstimate estimate3) throws Exception
    {
        double gamma = 0.8;
        ProjectInitialEstimates initialData = new ProjectInitialEstimates();
        initialData.addUserEstimates(estimate1);
        initialData.addUserEstimates(estimate2);
        initialData.addUserEstimates(estimate3);

//        initialData.getTaskEstimates().get("task1").stream().forEach(System.out::println);

        ProjectDurationCalculator calc = new ProjectDurationCalculator(initialData);
        Result result = calc.calculate(gamma);

        assertThat(result, withIntervalProbability(closeTo(gamma, 0.01)));
    }

    @Test
    public void testCalculate() throws Exception
    {
        int upperBound = 10;
        double gamma = 0.8;
//        TaskInitialEstimate task = uniform("task1", 0, upperBound);
        TaskInitialEstimate task = normal("task1", 5, 1);
        TaskInitialEstimate task2 = uniform("task1", 3, 7);
//        TaskInitialEstimate task2 = normal("task1", 8, 1);

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

    @Test
    @Ignore
    public void for_resolve_problem() throws Exception
    {
        double gamma = 0.8;
        String taskId = "task1";

        ProjectInitialEstimates initialData = new ProjectInitialEstimates();
        initialData.addUserEstimates(estimate(1, trapezoidal(taskId, 7, 7, 8, 9)));
        initialData.addUserEstimates(estimate(2, trapezoidal(taskId, 7, 7, 8, 9)));
        initialData.addUserEstimates(estimate(3, trapezoidal(taskId, 7, 7, 8, 9)));

        ProjectDurationCalculator calc = new ProjectDurationCalculator(initialData);
        Result result = calc.calculate(gamma);

        result.dumpToXls("results.xlsx");
    }

    private Matcher<? super Result> withIntervalProbability(Matcher<? super Double> gammaMatcher)
    {
        return new TypeSafeDiagnosingMatcher<Result>()
        {
            private Result item;

            @Override
            protected boolean matchesSafely(Result item, org.hamcrest.Description mismatchDescription)
            {
                this.item = item;
                double probabilityOfInterval = item.getProbabilityOfInterval();
                mismatchDescription.appendText("Result with interval probability").appendValue(probabilityOfInterval);
                return gammaMatcher.matches(probabilityOfInterval);
            }

            @Override
            public void describeTo(org.hamcrest.Description description)
            {
                item.dumpToXls("test_fail.xlsx");
                gammaMatcher.describeTo(description);
            }
        };
    }
}