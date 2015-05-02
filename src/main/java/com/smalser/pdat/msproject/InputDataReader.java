package com.smalser.pdat.msproject;

import com.google.common.collect.Sets;
import com.smalser.pdat.core.excel.XlsReader;
import com.smalser.pdat.core.excel.XlsReaderFactory;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import com.smalser.pdat.core.structure.UserInitialEstimate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InputDataReader
{
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "Task_Name";
    public static final String COL_SUMMARY = "Summary";
    public static final String COL_DURATION = "Duration";
    public static final String COL_DURATION_1 = "Duration1";    //pessimistic
    public static final String COL_DURATION_2 = "Duration2";    //expected
    public static final String COL_DURATION_3 = "Duration3";    //optimistic

    public ProjectInitialEstimates read(String... fileNames)
    {
        Set<String> headers = Sets.newHashSet(COL_ID, COL_NAME, COL_SUMMARY, COL_DURATION, COL_DURATION_1, COL_DURATION_2, COL_DURATION_3);
        Map<Integer, String> idxToCol = new HashMap<>();
        ProjectInitialEstimates pie = new ProjectInitialEstimates();

        int userId = 0;
        for (String fileName : fileNames)
        {
            UserInitialEstimate uie = new UserInitialEstimate(userId++);

            try
            {
                XlsReader xls = XlsReaderFactory.create(fileName);

                //read headers
                for (int i = 0; i < xls.getNumberOfColumns(); i++)
                {
                    String value = xls.getValue(0, i);
                    if (headers.contains(value))
                    {
                        idxToCol.put(i, value);
                    }
                }

                //read values
                for (int i = 1; i < xls.getNumberOfRows(); i++)
                {
                    double taskId;
                    String taskName = null;
                    boolean isSummary = true;
                    double duration = 0;
                    double duration1 = 0;
                    double duration2 = 0;
                    double duration3 = 0;

                    for (int j = 0; j < xls.getNumberOfColumns(); j++)
                    {
                        String value = xls.getValue(i, j);

                        if (idxToCol.containsKey(j))
                        {
                            String colName = idxToCol.get(j);
                            switch (colName)
                            {
                                case COL_ID:
                                    taskId = Double.valueOf(value);
                                    break;
                                case COL_NAME:
                                    taskName = value;
                                    break;
                                case COL_SUMMARY:
                                    isSummary = "Yes".equals(value);
                                    break;
                                case COL_DURATION:
                                    duration = Double.parseDouble(value.split(" ")[0].replace(",", "."));
                                    break;
                                case COL_DURATION_1:
                                    duration1 = Double.parseDouble(value.split(" ")[0].replace(",", "."));
                                    break;
                                case COL_DURATION_2:
                                    duration2 = Double.parseDouble(value.split(" ")[0].replace(",", "."));
                                    break;
                                case COL_DURATION_3:
                                    duration3 = Double.parseDouble(value.split(" ")[0].replace(",", "."));
                                    break;
                                default:
                                    throw new RuntimeException("Column " + colName + " is not supported yet");
                            }
                        }
                    }

                    if (!isSummary && notEmptyEstimate(duration, duration1, duration2, duration3))
                    {
                        uie.addEstimate(createEstimate(taskName, duration, duration1, duration2, duration3));
                    }
                }

            } catch (IOException e)
            {
                //ignore invalid files
                e.printStackTrace();
            }

            pie.addUserEstimates(uie);
        }

        return pie;
    }

    private boolean notEmptyEstimate(double duration, double duration1, double duration2, double duration3)
    {
        return !(duration == 0.0 && duration1 == 0.0 && duration2 == 0.0 && duration3 == 0.0);
    }

    private TaskInitialEstimate createEstimate(String taskName, double duration, double duration1, double duration2,
                                               double duration3)
    {
        if (duration1 == 0.0 && duration2 == 0.0 && duration3 == 0.0)
        {
            duration1 = duration * 0.9;
            duration2 = duration;
            duration3 = duration * 1.2;
        }
        return TaskInitialEstimate.triangular(taskName, duration1, duration2, duration3);
    }
}
