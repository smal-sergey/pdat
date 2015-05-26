package com.smalser.pdat.core.cpm;

import com.google.common.collect.Sets;
import com.smalser.pdat.AbstractTask;

import java.math.BigDecimal;
import java.util.Set;

public class Task extends AbstractTask
{
    public final BigDecimal cost;
    public final Set<Task> dependencies;

    // the cost of the task along the critical path
    public BigDecimal criticalCost;
    // a name for the task for printing
    public String name;
    // the earliest start
    public BigDecimal earlyStart;
    // the earliest finish
    public BigDecimal earlyFinish;
    // the latest start
    public BigDecimal latestStart;
    // the latest finish
    public BigDecimal latestFinish;

    public Task(String id, BigDecimal cost, Task... dependencies)
    {
        super(id);
        this.name = id;
        this.cost = cost;
        this.dependencies = Sets.newHashSet(dependencies);
        this.earlyStart = BigDecimal.ZERO;
        this.earlyFinish = new BigDecimal(-1);
        this.latestStart = BigDecimal.ZERO;
        this.latestFinish = BigDecimal.ZERO;
    }

    public Task(String id, int cost, Task... dependencies)
    {
        this(id, BigDecimal.valueOf(cost), dependencies);
    }

    public void setLatest(BigDecimal maxCost)
    {
        latestStart = maxCost.subtract(criticalCost);
        latestFinish = latestStart.add(cost);
    }

    public boolean isCritical()
    {
        return earlyStart.compareTo(latestStart) == 0;
    }

    public String[] toStringArray()
    {
        String criticalCond = isCritical() ? "Yes" : "No";
        return new String[]{name, earlyStart + "", earlyFinish + "", latestStart + "", latestFinish + "", latestStart.subtract(earlyStart) + "", criticalCond};
    }

    @Override
    public String toString()
    {
        return id;
    }
}
