package com.smalser.pdat.core.cpm;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class CpmCalculator
{
    public BigDecimal maxCost;

    public List<Task> criticalPath(Set<Task> tasks)
    {
//        System.out.println("Task\tCost\tCritical");
//        for (Task t : tasks)
//        {
//            System.out.println(t.id + " " + t.cost + " " + t.criticalCost);
//        }

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
                    BigDecimal critical = task.dependencies.stream().map(t -> t.criticalCost).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                    task.criticalCost = critical.add(task.cost);

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
        result.sort((o1, o2) -> o2.earlyStart.compareTo(o1.earlyStart));
        return result.stream().sorted((o1, o2) -> o2.earlyStart.compareTo(o1.earlyStart))
                .filter(Task::isCritical).collect(Collectors.toList());
    }

    private void calculateEarly(Set<Task> initials)
    {
        for (Task initial : initials)
        {
            initial.earlyStart = BigDecimal.ZERO;
            initial.earlyFinish = initial.cost;
            setEarly(initial);
        }
    }

    private void setEarly(Task initial)
    {
        BigDecimal completionTime = initial.earlyFinish;
        for (Task t : initial.dependencies)
        {
            if (completionTime.compareTo(t.earlyStart) >= 0)
            {
                t.earlyStart = completionTime;
                t.earlyFinish = completionTime.add(t.cost);
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

//        System.out.print("Initial nodes: ");
//        for (Task t : remaining)
//        {
//            System.out.print(t.name + " ");
//        }
//        System.out.print("\n\n");
        return remaining;
    }

    private void maxCost(Set<Task> tasks)
    {
//        System.out.println("Task\tCost\tCritical");
//        for (Task t : Lists.newArrayList(tasks).stream()
//                .sorted((o1, o2) -> Double.compare(Double.valueOf(o1.id), Double.valueOf(o2.id))).collect(Collectors.toList()))
//        {
//            System.out.format("%s\t%.2f\t%.2f\n", t.id, t.cost, t.criticalCost);
//        }

        maxCost = tasks.stream().map(t -> t.criticalCost).max(BigDecimal::compareTo).get();
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

        System.out.println("Sum: " + Lists.newArrayList(tasks).stream().map(t -> t.cost)
                .collect(Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)));
    }

    public void print2(Task[] tasks)
    {
        String format = "%1$-10s %2$-5s %3$-5s %4$-5s %5$-5s %6$-5s %7$-10s\n";
        System.out.format(format, "Task", "ES", "EF", "LS", "LF", "Slack", "Critical?");
        for (Task t : tasks)
        {
            System.out.format(format, (Object[]) t.toStringArray());
        }
    }
}
