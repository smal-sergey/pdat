package com.smalser.pdat;

import com.smalser.pdat.core.calculator.ProjectDurationCalculator;
import com.smalser.pdat.core.cpm.CpmCalculator;
import com.smalser.pdat.core.cpm.MonteCarloAnalyzer;
import com.smalser.pdat.core.cpm.Task;
import com.smalser.pdat.core.excel.XlsLogger;
import com.smalser.pdat.core.structure.EstimatedTask;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.msproject.InputDataReader;
import com.smalser.pdat.msproject.MetaDataContainer;
import com.smalser.pdat.msproject.OutputDataWriter;
import com.smalser.pdat.msproject.ProjectTask;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.EmpiricalDistribution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
//        Set<ProjectTask> projectTasks = idr.read(sourceFile).stream().filter(ProjectTask::notEmptyEstimate).collect(Collectors.toSet());
        Set<ProjectTask> projectTasks = idr.read(sourceFile).stream().filter(pt -> pt.notEmptyEstimate() && !pt.id.equals("9.0") && !pt.id.equals("12.0") && !pt.id.equals("13.0") && !pt.id.equals("271.0")).collect(Collectors.toSet());

        TasksConverter converter = new TasksConverter();

        //calculating each task
        ProjectInitialEstimates pie = converter.convertToEstimates(projectTasks);
        ProjectDurationCalculator calc = new ProjectDurationCalculator(pie);
        double gamma = 0.95;
        Map<String, EstimatedTask> taskToResult = calc.calculateEachTask(gamma);

        EstimatedTask result;
        boolean calculateFast = false; //todo read from input parameters
        if (calculateFast)
        {
            //calculating critical path
            CpmCalculator cpmCalculator = new CpmCalculator();
            Set<Task> tasksForCpm = converter.convertToTasks(projectTasks, id -> taskToResult.get(id).distribution.getNumericalMean());
            List<Task> criticalTasks = cpmCalculator.criticalPath(tasksForCpm);
            cpmCalculator.print(criticalTasks.toArray(new Task[criticalTasks.size()]));
            List<EstimatedTask> criticalEstimatedTasks = criticalTasks.stream().map(t -> taskToResult.get(t.id)).collect(Collectors.toList());

            //calculating result estimates
            result = calc.aggregate(criticalEstimatedTasks, gamma);
        }
        else
        {
            System.out.println("Start Monte-Carlo Analysis");
            //todo Monte-Carlo test start
            long start = System.currentTimeMillis();
            MonteCarloAnalyzer mca = new MonteCarloAnalyzer();
            EmpiricalDistribution empDist = mca.analyze(taskToResult.values(), projectTasks.stream().collect(Collectors.toMap(t -> t.id, ProjectTask::getDependencies)));

            NormalDistribution nd = new NormalDistribution(empDist.getNumericalMean(), Math.sqrt(empDist.getNumericalVariance()));
            double min = nd.getMean() - 3 * Math.sqrt(nd.getNumericalVariance());
            double max = nd.getMean() + 3 * Math.sqrt(nd.getNumericalVariance());
            result = calc.calculateTask("aggregatedResult", min, max, nd, gamma);

            System.out.println("Monte-carlo analysis duration: " + ((double) (System.currentTimeMillis() - start)) / 1000);
            try (OutputStreamWriter writer = new FileWriter(new File("final gist.txt")))
            {
                mca.print(empDist, writer);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            //todo Monte-Carlo test end
        }

        XlsLogger.dumpResult(distribFile, result);
        System.out.println("A = " + result.a + "\nB = " + result.b);

        try (OutputDataWriter odw = new OutputDataWriter(sourceFile, targetFile))
        {
            for (String taskId : taskToResult.keySet())
            {
                EstimatedTask taskEstimatedTask = taskToResult.get(taskId);
                String value = String.format("%.2f days", taskEstimatedTask.distribution.getNumericalMean());
                odw.writeValue(taskId, MetaDataContainer.COL_DURATION, value);
            }
            odw.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}