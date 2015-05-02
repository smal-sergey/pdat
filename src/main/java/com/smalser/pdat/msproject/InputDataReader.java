package com.smalser.pdat.msproject;

import com.google.common.collect.Sets;
import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import com.smalser.pdat.core.structure.UserInitialEstimate;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
                HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(fileName));
                HSSFSheet sheet = wb.getSheetAt(0);
                Row row;
                Cell cell;

                Iterator<Row> rows = sheet.rowIterator();

                //read headers
                row = rows.next();
                Iterator<Cell> cells = row.cellIterator();

                while (cells.hasNext())
                {
                    cell = cells.next();
                    String value = cell.getStringCellValue();
                    if (headers.contains(value))
                    {
                        idxToCol.put(cell.getColumnIndex(), value);
                    }
                }

                //read values
                while (rows.hasNext())
                {
                    row = rows.next();
                    cells = row.cellIterator();

                    double taskId;
                    String taskName = null;
                    boolean isSummary = true;
                    double duration = 0;
                    double duration1 = 0;
                    double duration2 = 0;
                    double duration3 = 0;

                    while (cells.hasNext())
                    {
                        cell = cells.next();
                        String value = cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? "" + cell.getNumericCellValue() : cell.getStringCellValue();
                        int idx = cell.getColumnIndex();

                        if (idxToCol.containsKey(idx))
                        {
                            switch (idxToCol.get(idx)){
                                case COL_ID: taskId = Double.valueOf(value); break;
                                case COL_NAME: taskName = value; break;
                                case COL_SUMMARY: isSummary = "Yes".equals(value); break;
                                case COL_DURATION: duration = Double.valueOf(value.split(" ")[0]); break;
                                case COL_DURATION_1: duration1 = Double.valueOf(value.split(" ")[0]); break;
                                case COL_DURATION_2: duration2 = Double.valueOf(value.split(" ")[0]); break;
                                case COL_DURATION_3: duration3 = Double.valueOf(value.split(" ")[0]); break;
                                default: throw new RuntimeException("Column " + idxToCol.get(idx) + " is not supported yet");
                            }
                        }
                    }

                    if(!isSummary){
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

    private TaskInitialEstimate createEstimate(String taskName, double duration, double duration1,
                                               double duration2, double duration3)
    {
        if(duration1 == 0.0 && duration2 == 0.0 && duration3 == 0.0){
            duration1 = duration * 0.9;
            duration2 = duration;
            duration3 = duration * 1.2;
        }
        return TaskInitialEstimate.triangular(taskName, duration1, duration2, duration3);
    }
}
