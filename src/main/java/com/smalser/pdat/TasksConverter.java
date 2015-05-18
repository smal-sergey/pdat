package com.smalser.pdat;

import com.google.common.collect.Sets;
import com.smalser.pdat.core.cpm.Task;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.UserInitialEstimate;
import com.smalser.pdat.msproject.ProjectTask;

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
            expertIdToTasks.get(expertId).stream().forEach(t -> uie.addEstimate(t.createEstimate()));
            pie.addUserEstimates(uie);
        }
        return pie;
    }

    public Set<Task> convertToTasks(Collection<? extends ProjectTask> projectTasks, Function<String, Double> idToCost)
    {
        Map<String, Task> idToTask = new HashMap<>();
        Map<String, List<ProjectTask>> expertIdToTasks = projectTasks.stream().collect(Collectors.groupingBy(t -> t.expertId));
        List<ProjectTask> uniqueProjectTasks = expertIdToTasks.values().stream().findFirst().get();

        for (ProjectTask projectTask : uniqueProjectTasks)
        {
            String taskId = projectTask.taskId + "";
            idToTask.put(taskId, new Task(taskId, idToCost.apply(taskId)));
        }

        for (ProjectTask projectTask : uniqueProjectTasks)
        {
            Task task = idToTask.get(projectTask.taskId + "");

            //todo filtering summary tasks
            projectTask.dependencies.stream().filter(dep -> idToTask.containsKey(dep + "")).forEach(depId -> task.dependencies.add(idToTask.get(depId + "")));
//            projectTask.dependencies.forEach(depId -> task.dependencies.add(idToTask.get(depId + "")));

//            System.out.print(task.id + ": ");
//            for (Task dep : task.dependencies)
//            {
//                System.out.print(dep.id + ", ");
//            }
//            System.out.print("\n");
        }

        return Sets.newHashSet(idToTask.values());
    }
}












