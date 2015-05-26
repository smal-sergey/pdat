package com.smalser.pdat.core.structure;

import java.util.SortedSet;
import java.util.TreeSet;

public class UserInitialEstimate
{
    final String id;
    private final TreeSet<TaskInitialEstimate> tasks = new TreeSet<>((o1, o2) -> o1.id.compareTo(o2.id));

    public UserInitialEstimate(String id)
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
