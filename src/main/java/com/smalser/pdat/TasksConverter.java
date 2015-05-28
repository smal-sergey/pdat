package com.smalser.pdat;

import com.google.common.collect.Sets;
import com.smalser.pdat.core.cpm.Task;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.UserInitialEstimate;
import com.smalser.pdat.msproject.ProjectTask;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TasksConverter
{
    public ProjectInitialEstimates convertToEstimates(Collection<? extends ProjectTask> projectTasks)
    {
        ProjectInitialEstimates pie = new ProjectInitialEstimates();
        Map<String, List<ProjectTask>> expertIdToTasks = projectTasks.stream().collect(Collectors.groupingBy(t -> t.expertId));

        for (String expertId : expertIdToTasks.keySet())
        {
            UserInitialEstimate uie = new UserInitialEstimate(expertId);
            expertIdToTasks.get(expertId).stream().filter(t -> !t.isSummary).forEach(t -> uie.addEstimate(t.createEstimate()));
            pie.addUserEstimates(uie);
        }
        return pie;
    }

    public Set<Task> convertToTasks(Collection<? extends ProjectTask> projectTasks, Function<String, Double> idToCost)
    {
        Map<String, List<ProjectTask>> expertIdToTasks = projectTasks.stream().collect(Collectors.groupingBy(t -> t.expertId));
        List<ProjectTask> uniqueProjectTasks = expertIdToTasks.values().stream().findFirst().get();
        Map<String, Collection<String>> depends = uniqueProjectTasks.stream().collect(Collectors.toMap(t -> t.id, ProjectTask::getDependencies));

        return convertToTasks(uniqueProjectTasks, depends, idToCost);
    }

    public Set<Task> convertToTasks(Collection<? extends AbstractTask> tasks, Map<String, Collection<String>> depends,
                                    Function<String, Double> idToCost)
    {
        Map<String, Task> idToTask = new HashMap<>();

        for (AbstractTask task : tasks)
        {
            String taskId = task.id;
            idToTask.put(taskId, new Task(taskId, new BigDecimal(idToCost.apply(taskId))));
        }

        for (AbstractTask estimatedTask : tasks)
        {
            Task task = idToTask.get(estimatedTask.id);

            //todo filtering summary tasks
            depends.get(estimatedTask.id).stream().filter(idToTask::containsKey).forEach(depId -> task.dependencies.add(idToTask.get(depId)));
        }

        return Sets.newHashSet(idToTask.values());
    }
}












