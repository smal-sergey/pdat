package com.smalser.pdat.msproject;

import com.google.common.collect.Sets;
import com.smalser.pdat.AbstractTask;
import com.smalser.pdat.core.structure.TaskInitialEstimate;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProjectTask extends AbstractTask
{
    private Pattern dependencyPattern = Pattern.compile("(\\d+)([SF][SF])?([+-]\\d+ \\w+)?");

    public final String expertId;
    public final String taskName;
    public final String distribution;
    public final Set<Double> dependencies;
    public final boolean isSummary;
    public final double duration;
    public final double duration1;
    public final double duration2;
    public final double duration3;
    public final double duration4;

    public ProjectTask(String expertId, double taskId, String taskName, String dependencies, String distribution,
                       boolean isSummary, double duration, double duration1, double duration2, double duration3,
                       double duration4)
    {
        super(taskId + "");
        this.expertId = expertId;
        this.taskName = taskName;
        this.distribution = distribution;
        this.duration4 = duration4;
        this.dependencies = parse(dependencies);
        this.isSummary = isSummary;
        this.duration = duration;
        this.duration1 = duration1;
        this.duration2 = duration2;
        this.duration3 = duration3;
    }

    private Set<Double> parse(String dependencies)
    {
        return Sets.newHashSet(dependencies.split(";")).stream().filter(d -> !d.isEmpty()).map(this::parseDependencyId).collect(Collectors.toSet());
    }

    private double parseDependencyId(String dep)
    {
        Matcher m = dependencyPattern.matcher(dep);
        if (m.matches())
        {
            return Double.parseDouble(m.group(1));
        }
        throw new RuntimeException("Can't parse dependency id from " + dep);
    }

    public boolean notEmptyEstimate()
    {
        return !isSummary && !(duration == 0.0 && duration1 == 0.0 && duration2 == 0.0 && duration3 == 0.0);
    }

    public TaskInitialEstimate createEstimate()
    {
        double a = duration1;
        double b = duration2;
        double c = duration3;
        double d = duration4;

        if (duration1 == 0.0 && duration2 == 0.0 && duration3 == 0.0)
        {
            a = duration * 0.9;
            b = duration;
            c = duration * 1.2;
        }

        TaskInitialEstimate estimate;
        switch (distribution)
        {
            case "U":
                estimate = TaskInitialEstimate.uniform(id, a, b);
                break;
            case "N":
                estimate = TaskInitialEstimate.normal(id, b, (b - a) / 3);
                break;
            case "T":
                estimate = TaskInitialEstimate.triangular(id, a, b, c);
                break;
            case "Tr":
                estimate = TaskInitialEstimate.trapezoidal(id, a, b, c, d);
                break;
            default:
                estimate = TaskInitialEstimate.triangular(id, a, b, c);
        }

        return estimate;
    }

    public Collection<String> getDependencies()
    {
        return dependencies.stream().map(dep -> dep + "").collect(Collectors.toList());
    }
}




















