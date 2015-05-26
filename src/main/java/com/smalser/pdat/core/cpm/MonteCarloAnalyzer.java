package com.smalser.pdat.core.cpm;

import com.smalser.pdat.TasksConverter;
import com.smalser.pdat.core.structure.EstimatedTask;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MonteCarloAnalyzer
{
    public EmpiricalDistribution analyze(Collection<EstimatedTask> estimatedTasks,
                                         Map<String, Collection<String>> depends, int iterations)
    {
        double[] durations = new double[iterations];

        for (int i = 0; i < iterations; i++)
        {
            durations[i] = simulateProject(estimatedTasks, depends);
        }
        EmpiricalDistribution dist = new EmpiricalDistribution(40);
        dist.load(durations);
        return dist;
    }

    public void print(EmpiricalDistribution dist, OutputStreamWriter writer) throws IOException
    {
        for (SummaryStatistics statistics : dist.getBinStats())
        {
            StatisticalSummary summary = statistics.getSummary();

            writer.write(String.format("%.2f\t", summary.getMean()));

            for (int i = 0; i < summary.getN(); i++)
            {
                writer.write("|");
            }
            writer.write("\n");
        }
    }

    private double simulateProject(Collection<EstimatedTask> estimatedTasks, Map<String, Collection<String>> depends)
    {
        Map<String, Double> idToCost = estimatedTasks.stream().collect(Collectors.toMap(t -> t.id, t -> t.distribution.sample()));

        TasksConverter converter = new TasksConverter();
        Set<Task> tasksForCpm = converter.convertToTasks(estimatedTasks, depends, idToCost::get);
        CpmCalculator cpmCalculator = new CpmCalculator();
        List<Task> criticalTasks = cpmCalculator.criticalPath(tasksForCpm);
        return criticalTasks.stream().mapToDouble(t -> t.cost.doubleValue()).sum();
    }
}
