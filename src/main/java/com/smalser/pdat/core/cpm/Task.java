package com.smalser.pdat.core.cpm;

import com.google.common.collect.Sets;

import java.util.Set;

public class Task
{
    public final String id;
    public final double cost;
    public final Set<Task> dependencies;

    // the cost of the task along the critical path
    public double criticalCost;
    // a name for the task for printing
    public String name;
    // the earliest start
    public double earlyStart;
    // the earliest finish
    public double earlyFinish;
    // the latest start
    public double latestStart;
    // the latest finish
    public double latestFinish;

    public Task(String id, double cost, Task... dependencies)
    {
        this.id = id;
        this.name = id;
        this.cost = cost;
        this.dependencies = Sets.newHashSet(dependencies);
        this.earlyFinish = -1;
    }

    public void setLatest(double maxCost)
    {
        latestStart = maxCost - criticalCost;
        latestFinish = latestStart + cost;
    }

    public boolean isCritical()
    {
        return earlyStart == latestStart;
    }

    @Override
    public String toString()
    {
        return id;
    }
}
