package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.Result;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

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

        return null;
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

    private AbstractRealDistribution createDistribution(Set<TaskInitialEstimate> estimates)
    {
        //todo create real mixed distribution
        return new UniformRealDistribution(0, 10);
    }

}
