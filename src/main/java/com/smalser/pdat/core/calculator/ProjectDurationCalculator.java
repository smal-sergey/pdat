package com.smalser.pdat.core.calculator;

import com.google.common.base.Preconditions;
import com.smalser.pdat.core.distribution.MixedRealDistribution;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.Result;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.ode.ContinuousOutputModel;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        String task = taskToDuration.keySet().stream().findAny().get();
        return taskToDuration.get(task);
    }

    private Result calculateTask(Set<TaskInitialEstimate> estimates, double gamma)
    {
        AbstractRealDistribution distribution = createDistribution(estimates);
        TaskConstraints taskConstraints = new TaskConstraints(estimates, gamma, 0.1); //todo adaptive speed constant?

        double beta = findBeta(estimates, taskConstraints);
        System.out.println("Beta found! " + beta);

        LeftBorder leftBorder = new LeftBorder(taskConstraints);
        RightBorderODE rightBorderODE = new RightBorderODE(distribution, leftBorder);
        ContinuousOutputModel continuousModel = new ContinuousOutputModel();

        EventHandler integratorStopper = new IntegratorStopper(taskConstraints);

//        FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-4, 1, 1.0e-6, 1.0e-6);
        FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-3, 1, 1.0e-5, 1.0e-5);
        dp853.addStepHandler(continuousModel);
        dp853.addEventHandler(integratorStopper, 0.1, 1.0e-5, 30);
        dp853.integrate(rightBorderODE, 0.0, new double[]{beta}, 100 /*very big time*/, new double[]{beta});

        UnivariateFunction rightBorder = new RightBorder(continuousModel);

        check(leftBorder, rightBorder, distribution, gamma);

        System.out.println("Right border b(t) found!");

        //d(t) = b(t) - a(t)   possible duration interval, dates from and to. We need to minimize that interval
        UnivariateObjectiveFunction durationInterval = new UnivariateObjectiveFunction((
                t) -> rightBorder.value(t) - leftBorder.value(t));
        SearchInterval searchInterval = new SearchInterval(0, taskConstraints.getCalculatedMaxTime()); //todo think about initial t value
        BrentOptimizer optimizer = new BrentOptimizer(1.0e-6, 1.0e-6);
        UnivariatePointValuePair optimum = optimizer.optimize(searchInterval, durationInterval, GoalType.MINIMIZE, new MaxEval(100));
        double t = optimum.getPoint();

        double a = leftBorder.value(t);
        double b = rightBorder.value(t);

        System.out.println("alpha = " + taskConstraints.alpha);
        System.out.println("a = " + a + "\nb = " + b + "\nt = " + t);
//        System.out.println(rightBorder.toString());

        //todo Result must depend on t to draw plots
        return new Result(leftBorder, rightBorder, t, taskConstraints, distribution);
    }

    //todo temp correctness check
    private void check(UnivariateFunction leftBorder, UnivariateFunction rightBorder,
                       AbstractRealDistribution distribution, double gamma)
    {
        UnivariateIntegrator integrator = new TrapezoidIntegrator(0.001, 0.001, 1, 50);
        double a = leftBorder.value(0);
        double b = rightBorder.value(10);
        double integratedValue = integrator.integrate(100, distribution::density, a, b);
        Preconditions.checkState(Math.abs(integratedValue - gamma) < 0.01,
                "Integral in bounds [" + a + ", " + b + "] = " + integratedValue + " != " + gamma);
    }

    private double findBeta(Set<TaskInitialEstimate> estimates, TaskConstraints taskConstraints)
    {
        BetaFinder betaFinder = new BetaFinder(createDistribution(estimates), taskConstraints);
        return betaFinder.findMinBeta();
    }

    private AbstractRealDistribution createDistribution(Set<TaskInitialEstimate> estimates)
    {
        return new MixedRealDistribution(estimates.stream().map(TaskInitialEstimate::getDistribution).collect(Collectors.toList()));
    }
}
