package com.smalser.pdat;

import com.smalser.pdat.core.excel.XlsLogger;
import com.smalser.pdat.core.calculator.ProjectDurationCalculator;
import com.smalser.pdat.core.structure.AggregatedResult;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.Result;
import com.smalser.pdat.msproject.InputDataReader;

import java.util.Map;

public class TestRealMsProjectExport
{
    public static void main(String[] args) throws Exception
    {
        InputDataReader idr = new InputDataReader();
        ProjectInitialEstimates pie = idr.read("ms_project_files/real/План-график new21_04.xls");
        ProjectDurationCalculator calc = new ProjectDurationCalculator(pie);
        double gamma = 0.8;
        Map<String, Result> taskToResult = calc.calculateEachTask(gamma);
        AggregatedResult result = calc.aggregate(taskToResult.values(), gamma);
        XlsLogger.dumpResult("ms_project_files/real/План-график new21_04 result.xls", result);
    }
}