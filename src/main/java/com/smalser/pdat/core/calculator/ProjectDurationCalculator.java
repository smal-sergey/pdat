package com.smalser.pdat.core.calculator;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.smalser.pdat.core.distribution.MixedRealDistribution;
import com.smalser.pdat.core.excel.XlsLogger;
import com.smalser.pdat.core.structure.EstimatedTask;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.TaskConstraints;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.ode.ContinuousOutputModel;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import java.util.*;
import java.util.stream.Collectors;

public class ProjectDurationCalculator
{
    private final ProjectInitialEstimates initialData;

    public ProjectDurationCalculator(ProjectInitialEstimates initialData)
    {
        this.initialData = initialData;
    }

    public Map<String, EstimatedTask> calculateEachTask(double gamma)
    {
        Map<String, Set<TaskInitialEstimate>> taskToEstimates = initialData.getTaskEstimates();
        Map<String, EstimatedTask> taskToDuration = new HashMap<>();

        for (String taskId : taskToEstimates.keySet())
        {
            Set<TaskInitialEstimate> estimates = taskToEstimates.get(taskId);
            MixedRealDistribution taskDistribution = createDistribution(estimates);

            try
            {
                taskToDuration.put(taskId, calculateTask(taskId, findLeftBound(estimates), findRightBound(estimates), taskDistribution, gamma));

            } catch (Exception e)
            {
                XlsLogger.dumpToXls("Error.xls", taskDistribution::density, taskDistribution.minBound, taskDistribution.maxBound);
                Throwables.propagate(e);
            }
        }

        return taskToDuration;
    }

    public EstimatedTask aggregate(Collection<? extends EstimatedTask> tasks, double gamma)
    {
        double M0 = tasks.stream().mapToDouble(result -> result.distribution.getNumericalMean()).sum();
        double sumVariance = tasks.stream().mapToDouble(result -> result.distribution.getNumericalVariance()).sum();
        double D0 = Math.sqrt(sumVariance);

        AbstractRealDistribution sumDistrib = new NormalDistribution(M0, D0);
        BrentSolver solver = new BrentSolver(1.0e-4);
        double latestEstimate = solver.solve(Integer.MAX_VALUE, x -> sumDistrib.probability(M0, x) - gamma / 2, M0, M0 + 4 * D0);

        return new EstimatedTask("aggregatedResult", M0 - 5 * D0, M0 + 5 * D0, M0 - (latestEstimate - M0), latestEstimate, sumDistrib);
    }

    public EstimatedTask calculateTask(String id, double minValue, double maxValue,
                                       AbstractRealDistribution taskDistribution, double gamma)
    {
        TaskConstraints taskConstraints = new TaskConstraints(minValue, maxValue, gamma, 0.1); //todo adaptive speed constant?

        double beta = findBeta(taskDistribution, taskConstraints);
//        System.out.println("Beta found! " + beta);

        LeftBorder leftBorder = new LeftBorder(taskConstraints);
        RightBorderODE rightBorderODE = new RightBorderODE(taskDistribution, leftBorder);
        ContinuousOutputModel continuousModel = new ContinuousOutputModel();

        EventHandler integratorStopper = new IntegratorStopper(taskConstraints);

//        FirstOrderIntegrator integrator = new DormandPrince853Integrator(1.0e-5, 1, 1.0e-5, 1.0e-5);
//        AdamsBashforthIntegrator integrator = new AdamsBashforthIntegrator(2, 1.0e-5, 1.0e-2, 1.0e-5, 1.0e-5);   //integrated normal distribution
        DormandPrince54Integrator integrator = new DormandPrince54Integrator(1.0e-5, 1.0e-1, 1.0e-4, 1.0e-4);
        integrator.addStepHandler(continuousModel);
        integrator.addEventHandler(integratorStopper, 0.1, 1.0e-5, 50);
        integrator.integrate(rightBorderODE, 0, new double[]{beta}, Double.MAX_VALUE /*very big time*/, new double[]{beta});

        UnivariateFunction rightBorder = new RightBorder(continuousModel);

//        System.out.println("!!!!!");
//        taskConstraints.dumpBorders(leftBorder, rightBorder);
//        System.out.println("!!!!!");
//        check(leftBorder, rightBorder, distribution, gamma);

//        System.out.println("Right border b(t) found!");

        //d(t) = b(t) - a(t)   possible duration interval, dates from and to. We need to minimize that interval
        UnivariateObjectiveFunction durationInterval = new UnivariateObjectiveFunction((t) -> rightBorder.value(t) - leftBorder.value(t));
        SearchInterval searchInterval = new SearchInterval(0, taskConstraints.getCalculatedMaxTime()); //todo think about initial t value
        BrentOptimizer optimizer = new BrentOptimizer(1.0e-6, 1.0e-6);
        UnivariatePointValuePair optimum = optimizer.optimize(searchInterval, durationInterval, GoalType.MINIMIZE, new MaxEval(100));
        double t = optimum.getPoint();

        double leftBound = taskConstraints.leftBound;
        double rightBound = taskConstraints.rightBound;
        double a = leftBorder.value(t);
        double b = rightBorder.value(t);

        return new EstimatedTask(id, leftBound, rightBound, a, b, taskDistribution);
    }

    //todo temp correctness check
    private void check(UnivariateFunction leftBorder, UnivariateFunction rightBorder,
                       AbstractRealDistribution distribution, double gamma)
    {
        double a = leftBorder.value(0);
        double b = rightBorder.value(0);
        double integratedValue = distribution.probability(a, b);

        System.out.println("b - a = " + (b - a));
        Preconditions.checkState(Math.abs(integratedValue - gamma) < 0.01, "Integral in bounds [" + a + ", " + b + "] = " + integratedValue + " != " + gamma);

        System.out.println("OK! Integral in bounds [" + a + ", " + b + "] = " + integratedValue);
    }

    private double findBeta(AbstractRealDistribution taskDistribution, TaskConstraints taskConstraints)
    {
        BetaFinder betaFinder = new BetaFinder(taskDistribution, taskConstraints);
        return betaFinder.findMinBeta();
    }

    private double findLeftBound(Set<TaskInitialEstimate> estimates)
    {
        return estimates.stream().map(TaskInitialEstimate::min).min(Double::compareTo).get();
    }

    private double findRightBound(Set<TaskInitialEstimate> estimates)
    {
        return estimates.stream().map(TaskInitialEstimate::max).max(Double::compareTo).get();
    }

    private MixedRealDistribution createDistribution(Set<TaskInitialEstimate> estimates)
    {
        List<AbstractRealDistribution> components = estimates.stream().map(TaskInitialEstimate::getDistribution).collect(Collectors.toList());
        double a = estimates.stream().mapToDouble(TaskInitialEstimate::min).min().getAsDouble();
        double b = estimates.stream().mapToDouble(TaskInitialEstimate::max).max().getAsDouble();
        return new MixedRealDistribution(components, a, b);
    }
}
