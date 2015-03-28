package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.Result;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.ode.ContinuousOutputModel;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProjectDurationCalculator
{
    private final ProjectInitialEstimates initialData;

    public ProjectDurationCalculator(ProjectInitialEstimates initialData)
    {
        this.initialData = initialData;
    }

    public Result calculate(double gamma)
    {
        Map<String, Set<TaskInitialEstimate>> taskToEstimates = initialData.getTaskEstimates();
        Map<String, Result> taskToDuration = new HashMap<>();

        for (String taskId : taskToEstimates.keySet())
        {
            Set<TaskInitialEstimate> estimates = taskToEstimates.get(taskId);
            taskToDuration.put(taskId, calculateTask(estimates, gamma));
        }

        //todo create all tasks result duration integration phase

        //now just return first task duration
        return taskToDuration.get("task1");
    }

    private Result calculateTask(Set<TaskInitialEstimate> estimates, double gamma)
    {
        AbstractRealDistribution distribution = createDistribution(estimates);
        double alpha = findAlpha(estimates);
        double beta = findBeta(estimates, alpha, gamma);
        double leftBorderSpeed = 0.1;   //todo adaptive speed constant?

        //todo here is ERROR!!! we should calculate max beta with respect to gamma constraint
        LeftBorder leftBorder = new LeftBorder(alpha, leftBorderSpeed, findMaxLeftBorderValue(estimates));
        RightBorderODE rightBorderODE = new RightBorderODE(distribution, leftBorder);
        ContinuousOutputModel continuousModel = new ContinuousOutputModel();

        EventHandler integratorStopper = new IntegratorStopper(leftBorder);

        FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-4, 1, 1.0e-6, 1.0e-6);
        dp853.addStepHandler(continuousModel);
        dp853.addEventHandler(integratorStopper, 0.1, 0.1, 100);
        dp853.integrate(rightBorderODE, 0.0, new double[]{beta}, leftBorder.maxTime() + 10 /*very big time*/, new double[]{beta});

        UnivariateFunction rightBorder = new RightBorder(continuousModel);

        //d(t) = b(t) - a(t)   possible duration interval, dates from and to. We need to minimize that interval
        UnivariateObjectiveFunction durationInterval = new UnivariateObjectiveFunction(
                (t) -> rightBorder.value(t) - leftBorder.value(t));
        SearchInterval searchInterval = new SearchInterval(0, leftBorder.maxTime()); //todo think about initial t value
        BrentOptimizer optimizer = new BrentOptimizer(1.0e-6, 1.0e-6);
        UnivariatePointValuePair optimum = optimizer.optimize(searchInterval, durationInterval, GoalType.MINIMIZE, new MaxEval(100));
        double t = optimum.getPoint();

        double a = leftBorder.value(t);
        double b = rightBorder.value(t);

        System.out.println("a = " + a + "\nb = " + b + "\nt = " + t);
        System.out.println(rightBorder.toString());

        //todo Result must depend on t to draw plots
        return new Result(a, b);
    }

    private double findBeta(Set<TaskInitialEstimate> estimates, double alpha, double gamma)
    {
        BetaFinder betaFinder = new BetaFinder(estimates, createDistribution(estimates), alpha, gamma);
        return betaFinder.findMinBeta();
    }

    private double findAlpha(Set<TaskInitialEstimate> estimates)
    {
        return estimates.stream().map(e -> e.a1).min(Double::compareTo).get();
    }

    private double findMaxLeftBorderValue(Set<TaskInitialEstimate> estimates)
    {
        //todo very rough estimate
        return estimates.stream().map(e -> e.b2).max(Double::compareTo).get();
    }

    private AbstractRealDistribution createDistribution(Set<TaskInitialEstimate> estimates)
    {
        //todo create real mixed distribution
        return new UniformRealDistribution(0, 10);
    }

}
