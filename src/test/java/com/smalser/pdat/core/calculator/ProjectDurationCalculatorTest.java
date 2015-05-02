package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.excel.XlsLogger;
import com.smalser.pdat.core.structure.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        UserInitialEstimate userEstimate = new UserInitialEstimate(id);
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
        Map<String, Result> taskToDuration = calc.calculateEachTask(gamma);
        Result result = taskToDuration.values().stream().findFirst().get();

        assertThat(result, withIntervalProbability(closeTo(gamma, 0.01)));
        assertThat(result, hasMinSpread());
    }

    @Test
    public void test_aggregate_same_distributions() throws Exception
    {
        double gamma = 0.8;
        ProjectInitialEstimates initialData = new ProjectInitialEstimates();

        for (int i = 0; i < 100; i++)
        {
            UserInitialEstimate estimate = estimate(i, uniform("task" + i, 3, 7));
            initialData.addUserEstimates(estimate);
        }

        ProjectDurationCalculator calc = new ProjectDurationCalculator(initialData);
        Map<String, Result> idToEstimate = calc.calculateEachTask(gamma);
        AggregatedResult result = calc.aggregate(idToEstimate.values(), gamma);

        XlsLogger.dumpResult("results.xlsx", result);
    }

    @Test
    public void for_resolve_problem() throws Exception
    {
        double gamma = 0.95;
        String taskId = "task1";

        ProjectInitialEstimates initialData = new ProjectInitialEstimates();
        initialData.addUserEstimates(estimate(1, normal(taskId, 5, 1)));
        initialData.addUserEstimates(estimate(2, trapezoidal(taskId, 3, 5, 6, 7)));
        initialData.addUserEstimates(estimate(3, uniform(taskId, 4, 6)));

        ProjectDurationCalculator calc = new ProjectDurationCalculator(initialData);
        Map<String, Result> taskToDuration = calc.calculateEachTask(gamma);
        Result result = taskToDuration.values().stream().findFirst().get();

        XlsLogger.dumpResult("results.xlsx", result);
    }


    private Matcher<? super Result> hasMinSpread()
    {
        return new TypeSafeDiagnosingMatcher<Result>()
        {
            private Result item;

            @Override
            protected boolean matchesSafely(Result item, Description mismatchDescription)
            {
                this.item = item;
                Double maxTime = item.taskConstraints.getCalculatedMaxTime();
                Double calculatedSpread = item.getB() - item.getA();

                for (double t = 0.0; t < maxTime; t += 0.1)
                {
                    double b = item.rightBorder.value(t);
                    double a = item.leftBorder.value(t);

                    if (calculatedSpread - (b - a) > 0.1)
                    {
                        mismatchDescription.appendText("Found interval shorter then calculated one. t = ").appendValue(t).appendText(", a = ").appendValue(a).appendText(", b = ").appendValue(b).appendText("\n\t (b - a) = ").appendValue(b - a);
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void describeTo(Description description)
            {
                XlsLogger.dumpResult("test_fail.xlsx", item);
                double a = item.getA();
                double b = item.getB();
                description.appendText("Result with interval [").appendValue(a).appendText(", ").appendValue(b).appendText("] on t = ").appendValue(item.optimalTime).appendText(" is optimal.").appendText("Max time = ").appendValue(item.taskConstraints.getCalculatedMaxTime()).appendText("\n\t (b - a) = ").appendValue(b - a);
            }
        };
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
                mismatchDescription.appendText("Result with interval probability ").appendValue(probabilityOfInterval);
                return gammaMatcher.matches(probabilityOfInterval);
            }

            @Override
            public void describeTo(org.hamcrest.Description description)
            {
                XlsLogger.dumpResult("test_fail.xlsx", item);
                gammaMatcher.describeTo(description);
            }
        };
    }
}