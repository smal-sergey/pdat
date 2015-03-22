package com.smalser.pdat.core.structure;

import java.util.SortedSet;
import java.util.TreeSet;

public class UserInitialEstimate
{
    final int id;
    private final TreeSet<TaskInitialEstimate> tasks = new TreeSet<>((o1, o2) -> o1.taskId.compareTo(o2.taskId));

    public UserInitialEstimate(int id)
    {
        this.id = id;
    }

    public void addEstimate(TaskInitialEstimate taskEstimate)
    {
        tasks.add(taskEstimate);
    }

    public SortedSet<TaskInitialEstimate> getAllTasks()
    {
        return tasks;
    }
}
