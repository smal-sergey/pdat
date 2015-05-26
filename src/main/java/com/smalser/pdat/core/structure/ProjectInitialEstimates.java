package com.smalser.pdat.core.structure;

import java.util.*;

public class ProjectInitialEstimates
{
    private Set<UserInitialEstimate> userEstimates = new HashSet<>();

    public void addUserEstimates(UserInitialEstimate userEstimate)
    {
        userEstimates.add(userEstimate);
    }

    public Map<String, Set<TaskInitialEstimate>> getTaskEstimates()
    {
        Map<String, Set<TaskInitialEstimate>> taskToEstimate = new HashMap<>();
        for (UserInitialEstimate userEstimate : userEstimates)
        {
            SortedSet<TaskInitialEstimate> userTasks = userEstimate.getAllTasks();
            for (TaskInitialEstimate userTask : userTasks)
            {
                String taskId = userTask.id;
                if (!taskToEstimate.containsKey(taskId))
                {
                    taskToEstimate.put(taskId, new HashSet<>());
                }

                Set<TaskInitialEstimate> taskEstimates = taskToEstimate.get(taskId);
                taskEstimates.add(userTask);
            }
        }
        return taskToEstimate;
    }
}
