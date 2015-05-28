package com.smalser.pdat.msproject;

import com.smalser.pdat.core.excel.XlsEditor;
import com.smalser.pdat.core.excel.XlsEditorFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InputDataReader extends MetaDataContainer
{
    public Set<ProjectTask> read(String... fileNames)
    {
        Set<ProjectTask> tasks = new HashSet<>();

        int userId = 0;
        for (String fileName : fileNames)
        {
            try
            {
                XlsEditor xls = XlsEditorFactory.create(fileName);
                Map<Integer, String> idxToCol = readHeaders(xls);
                tasks.addAll(getTasksFromFile(xls, idxToCol, userId++ + ""));
            } catch (IOException e)
            {
                //ignore invalid files
                e.printStackTrace();
            }
        }

        return tasks;
    }

    private Set<ProjectTask> getTasksFromFile(XlsEditor xls, Map<Integer, String> idxToCol, String userId)
    {
        Set<ProjectTask> tasks = new HashSet<>();

        //read values
        for (int i = 1; i < xls.getNumberOfRows(); i++)
        {
            double taskId = 0;
            String taskName = "";
            String dependencies = "";
            String distribution = "";
            boolean isSummary = false;
            double duration = 0;
            double duration1 = 0;
            double duration2 = 0;
            double duration3 = 0;
            double duration4 = 0;

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
                        case COL_DISTRIBUTION:
                            distribution = value;
                            break;
                        case COL_MILESTONE:
                        case COL_SUMMARY:
                            isSummary = isSummary || "Yes".equals(value);
                            break;
                        case COL_DURATION:
                            duration = parseDuration(value);
                            break;
                        case COL_DURATION_1:
                            duration1 = parseDuration(value);
                            break;
                        case COL_DURATION_2:
                            duration2 = parseDuration(value);
                            break;
                        case COL_DURATION_3:
                            duration3 = parseDuration(value);
                            break;
                        case COL_DURATION_4:
                            duration4 = parseDuration(value);
                            break;
                        case COL_PREDECESSORS:
                            dependencies = value;
                            break;
                        default:
                            throw new RuntimeException("Column " + colName + " is not supported yet");
                    }
                }
            }

            //todo create builder
            tasks.add(new ProjectTask(userId, taskId, taskName, dependencies, distribution, isSummary, duration, duration1, duration2, duration3, duration4));
        }
        return tasks;
    }

    private double parseDuration(String value)
    {
        return Double.parseDouble(value.split(" ")[0].replace(",", "."));
    }
}
