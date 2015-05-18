package com.smalser.pdat.core.cpm;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

public class CpmCalculator
{
    public double maxCost;

    public List<Task> criticalPath(Set<Task> tasks)
    {
        // tasks whose critical cost has been calculated
        Set<Task> completed = new HashSet<>();
        // tasks whose critical cost needs to be calculated
        Set<Task> remaining = new HashSet<>(tasks);

        // Backflow algorithm
        // while there are tasks whose critical cost isn't calculated.
        while (!remaining.isEmpty())
        {
            boolean progress = false;

            // find a new task to calculate
            for (Iterator<Task> it = remaining.iterator(); it.hasNext(); )
            {
                Task task = it.next();
                if (completed.containsAll(task.dependencies))
                {
                    // all dependencies calculated, critical cost is max
                    // dependency
                    // critical cost, plus our cost
                    double critical = task.dependencies.stream().mapToDouble(t -> t.criticalCost).max().orElseGet(() -> 0);
                    task.criticalCost = critical + task.cost;

                    // set task as calculated an remove
                    completed.add(task);
                    it.remove();
                    // note we are making progress
                    progress = true;
                }
            }
            // If we haven't made any progress then a cycle must exist in
            // the graph and we wont be able to calculate the critical path
            if (!progress)
            {
                throw new RuntimeException("Cyclic dependency, algorithm stopped!");
            }
        }

        // get the cost
        maxCost(tasks);
        Set<Task> initialNodes = initials(tasks);
        calculateEarly(initialNodes);

        List<Task> result = Lists.newArrayList(completed);
        result.sort((o1, o2) -> Double.compare(o2.earlyStart, o1.earlyStart));
        return result.stream().sorted((o1, o2) -> Double.compare(o2.earlyStart, o1.earlyStart))
                .filter(Task::isCritical).collect(Collectors.toList());
    }

    private void calculateEarly(Set<Task> initials)
    {
        for (Task initial : initials)
        {
            initial.earlyStart = 0;
            initial.earlyFinish = initial.cost;
            setEarly(initial);
        }
    }

    private void setEarly(Task initial)
    {
        double completionTime = initial.earlyFinish;
        for (Task t : initial.dependencies)
        {
            if (completionTime >= t.earlyStart)
            {
                t.earlyStart = completionTime;
                t.earlyFinish = completionTime + t.cost;
            }
            setEarly(t);
        }
    }

    private Set<Task> initials(Set<Task> tasks)
    {
        Set<Task> remaining = new HashSet<>(tasks);
        for (Task t : tasks)
        {
            t.dependencies.forEach(remaining::remove);
        }

        System.out.print("Initial nodes: ");
        for (Task t : remaining)
        {
            System.out.print(t.name + " ");
        }
        System.out.print("\n\n");
        return remaining;
    }

    private void maxCost(Set<Task> tasks)
    {
        maxCost = tasks.stream().mapToDouble(t -> t.criticalCost).max().getAsDouble();
        System.out.println("Critical path length (cost): " + maxCost);
        tasks.forEach(t -> t.setLatest(maxCost));
    }

    public void print(Task[] tasks)
    {
        System.out.println("Task\tCost");
        for (Task t : tasks)
        {
            System.out.format("%s\t%.2f\n", t.id, t.cost);
        }
    }
}
