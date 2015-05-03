package com.smalser.pdat;

import com.smalser.pdat.core.calculator.ProjectDurationCalculator;
import com.smalser.pdat.core.excel.XlsLogger;
import com.smalser.pdat.core.structure.AggregatedResult;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.Result;
import com.smalser.pdat.msproject.InputDataReader;
import com.smalser.pdat.msproject.MetaDataContainer;
import com.smalser.pdat.msproject.OutputDataWriter;

import java.util.Map;

public class TestRealMsProjectExport
{
    public static void main(String[] args) throws Exception
    {
        String sourceFile = "ms_project_files/real/План-график new21_04.xls";
        String targetFile = sourceFile.replace(".xls", " updated.xls");
        String distribFile = sourceFile.replace(".xls", " distribution.xls");

        InputDataReader idr = new InputDataReader();
        ProjectInitialEstimates pie = idr.read("ms_project_files/real/План-график new21_04.xls");
        ProjectDurationCalculator calc = new ProjectDurationCalculator(pie);
        double gamma = 0.8;
        Map<String, Result> taskToResult = calc.calculateEachTask(gamma);
        AggregatedResult result = calc.aggregate(taskToResult.values(), gamma);
        XlsLogger.dumpResult(distribFile, result);

        OutputDataWriter odw = new OutputDataWriter(sourceFile, targetFile);
        for (String taskId : taskToResult.keySet())
        {
            Result taskResult = taskToResult.get(taskId);
            String value = String.format("%.2f days", taskResult.distribution.getNumericalMean());
            odw.writeValue(taskId, MetaDataContainer.COL_DURATION, value);
        }
        odw.flush();
    }
}