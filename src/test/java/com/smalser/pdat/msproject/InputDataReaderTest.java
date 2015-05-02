package com.smalser.pdat.msproject;

import com.smalser.pdat.core.structure.ProjectInitialEstimates;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import static com.smalser.pdat.msproject.InputDataReader.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;

public class InputDataReaderTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void test_read_tasks_count() throws Exception
    {
        String projectFile = new XlsProjectBuilder().newFile("temp_project.xls")
                .addRow(1, "task1", false, 3, 0, 0, 0)
                .addRow(2, "task2", false, 4, 0, 0, 0)
                .create();

        InputDataReader idr = new InputDataReader();
        ProjectInitialEstimates pie = idr.read(projectFile);

        assertThat(pie.getTaskEstimates().keySet(), hasSize(2));
    }

    @Test
    public void test_read_durations() throws Exception
    {
        String taskId = "task1";
        String projectFile = new XlsProjectBuilder().newFile("temp_project.xls")
                .addRow(1, taskId, false, 3, 1, 2, 3)
                .create();

        InputDataReader idr = new InputDataReader();
        ProjectInitialEstimates pie = idr.read(projectFile);
        Set<TaskInitialEstimate> ties = pie.getTaskEstimates().get(taskId);
        assertThat(ties, hasSize(1));

        TaskInitialEstimate tie = ties.stream().findFirst().get();
        assertThat(tie.min(), closeTo(1, 0.01));
        assertThat(tie.max(), closeTo(3, 0.01));
    }

    private class XlsProjectBuilder
    {
        private FileOutputStream fileOut;
        private File projectFile;
        private HSSFWorkbook wb;
        private HSSFSheet sheet;
        private int curRow = 1;

        XlsProjectBuilder newFile(String fileName) throws Exception
        {
            projectFile = folder.newFile(fileName);
            fileOut = new FileOutputStream(projectFile);
            wb = new HSSFWorkbook();
            sheet = wb.createSheet("some_sheet");

            HSSFRow headers = sheet.createRow(0);

            HSSFCell cellId = headers.createCell(0);
            cellId.setCellValue(COL_ID);

            HSSFCell cellName = headers.createCell(1);
            cellName.setCellValue(COL_NAME);

            HSSFCell cellSummary = headers.createCell(2);
            cellSummary.setCellValue(COL_SUMMARY);

            HSSFCell cellDuration = headers.createCell(3);
            cellDuration.setCellValue(COL_DURATION);
            HSSFCell cellDuration1 = headers.createCell(4);
            cellDuration1.setCellValue(COL_DURATION_1);
            HSSFCell cellDuration2 = headers.createCell(5);
            cellDuration2.setCellValue(COL_DURATION_2);
            HSSFCell cellDuration3 = headers.createCell(6);
            cellDuration3.setCellValue(COL_DURATION_3);

            return this;
        }

        XlsProjectBuilder addRow(int id, String name, boolean isSummary, double duration,
                                 double duration1, double duration2, double duration3){

            HSSFRow row = sheet.createRow(curRow++);

            row.createCell(0).setCellValue(id);
            row.createCell(1).setCellValue(name);
            row.createCell(2).setCellValue(isSummary ? "Yes" : "No");
            row.createCell(3).setCellValue(stringify(duration));
            row.createCell(4).setCellValue(stringify(duration1));
            row.createCell(5).setCellValue(stringify(duration2));
            row.createCell(6).setCellValue(stringify(duration3));

            return this;
        }

        String create() throws IOException
        {
            wb.write(fileOut);
            fileOut.flush();
            return projectFile.getAbsolutePath();
        }

        private String stringify(double dur){
            return dur == 1.0 ? dur + " day" : dur + " days";
        }
    }
}