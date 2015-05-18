package com.smalser.pdat;

import com.smalser.pdat.core.calculator.ProjectDurationCalculator;
import com.smalser.pdat.core.cpm.CpmCalculator;
import com.smalser.pdat.core.cpm.Task;
import com.smalser.pdat.core.excel.XlsLogger;
import com.smalser.pdat.core.structure.AggregatedResult;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.Result;
import com.smalser.pdat.msproject.InputDataReader;
import com.smalser.pdat.msproject.MetaDataContainer;
import com.smalser.pdat.msproject.OutputDataWriter;
import com.smalser.pdat.msproject.ProjectTask;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TestRealMsProjectExport
{
    public static void main(String[] args)
    {
//        String sourceFile = "ms_project_files/real/План-график new21_04.xls";
        String sourceFile = "ms_project_files/real/Детальныи_ план внедрения v1 1.xls";
//        String sourceFile = "ms_project_files/real/Календарныи_ план проект ИСУП ТТК v 1.4.xls";
        String targetFile = sourceFile.replace(".xls", " updated.xls");
        String distribFile = sourceFile.replace(".xls", " distribution.xls");

        InputDataReader idr = new InputDataReader();
        Set<ProjectTask> projectTasks = idr.read(sourceFile).stream().filter(ProjectTask::notEmptyEstimate).collect(Collectors.toSet());
//        Set<ProjectTask> projectTasks = idr.read(sourceFile)
//                .stream().filter(pt -> pt.notEmptyEstimate() && pt.taskId != 9 && pt.taskId != 12 && pt.taskId != 13 && pt.taskId != 271)
//                .collect(Collectors.toSet());

        TasksConverter converter = new TasksConverter();

        //calculating each task
        ProjectInitialEstimates pie = converter.convertToEstimates(projectTasks);
        ProjectDurationCalculator calc = new ProjectDurationCalculator(pie);
        double gamma = 0.8;
        Map<String, Result> taskToResult = calc.calculateEachTask(gamma);

        //calculating critical path
        CpmCalculator cpmCalculator = new CpmCalculator();
        Set<Task> tasksForCpm = converter.convertToTasks(projectTasks, id -> taskToResult.get(id).distribution.getNumericalMean());
        List<Task> criticalTasks = cpmCalculator.criticalPath(tasksForCpm);
        cpmCalculator.print(criticalTasks.toArray(new Task[criticalTasks.size()]));
        List<Result> criticalResults = criticalTasks.stream().map(t -> taskToResult.get(t.id)).collect(Collectors.toList());

        //calculating result estimates
        AggregatedResult result = calc.aggregate(criticalResults, gamma);
        XlsLogger.dumpResult(distribFile, result);

        try (OutputDataWriter odw = new OutputDataWriter(sourceFile, targetFile))
        {
            for (String taskId : taskToResult.keySet())
            {
                Result taskResult = taskToResult.get(taskId);
                String value = String.format("%.2f days", taskResult.distribution.getNumericalMean());
                odw.writeValue(taskId, MetaDataContainer.COL_DURATION, value);
            }
            odw.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}