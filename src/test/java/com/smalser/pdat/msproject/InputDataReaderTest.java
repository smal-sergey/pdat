package com.smalser.pdat.msproject;

import com.google.common.collect.Sets;
import com.smalser.pdat.core.distribution.TrapezoidalDistribution;
import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InputDataReaderTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    //todo write more complex tests

    @Test
    public void test_read_tasks_count() throws Exception
    {
        String projectFile = new XlsProjectBuilder().newFile()
                .addRow(1, "task1", false, 3, 0, 0, 0, 0, null)
                .addRow(2, "task2", false, 4, 0, 0, 0, 0, null)
                .addRow(3, "task3", false, 5, 0, 0, 0, 0, null).create();

        InputDataReader idr = new InputDataReader();
        Set<ProjectTask> tasks = idr.read(projectFile);

        assertThat(tasks, hasSize(3));
    }

    @Test
    public void test_read_durations() throws Exception
    {
        double taskId = 1;
        String taskName = "task1";
        String projectFile = new XlsProjectBuilder().newFile()
                .addRow(taskId, taskName, false, 3, 1, 2, 3, 4, "").create();

        InputDataReader idr = new InputDataReader();
        Set<ProjectTask> tasks = idr.read(projectFile);

        assertThat(tasks, hasSize(1));

        ProjectTask task = tasks.stream().findFirst().get();
        assertThat(Double.valueOf(task.id), closeTo(taskId, 0.01));
        assertThat(task.duration, closeTo(3, 0.01));
        assertThat(task.duration1, closeTo(1, 0.01));
        assertThat(task.duration2, closeTo(2, 0.01));
        assertThat(task.duration3, closeTo(3, 0.01));
        assertThat(task.duration4, closeTo(4, 0.01));
    }

    @Test
    public void test_read_normal_distribution() throws Exception
    {
        String projectFile = new XlsProjectBuilder().newFile()
                .addRow(1, "task1", false, 3, 1, 4, 7, 0, "N").create();

        InputDataReader idr = new InputDataReader();
        Set<ProjectTask> tasks = idr.read(projectFile);

        ProjectTask task = tasks.stream().findFirst().get();
        TaskInitialEstimate estimate = task.createEstimate();
        AbstractRealDistribution distribution = estimate.getDistribution();

        assertTrue(distribution instanceof NormalDistribution);
        assertThat(distribution.getNumericalMean(), closeTo(4, 0.01));
        assertThat(distribution.getNumericalVariance(), closeTo(1, 0.01));  //because of 6 sigma
    }

    @Test
    public void test_read_uniform_distribution() throws Exception
    {
        String projectFile = new XlsProjectBuilder().newFile()
                .addRow(1, "task1", false, 3, 1, 2, 3, 4, "U").create();

        InputDataReader idr = new InputDataReader();
        Set<ProjectTask> tasks = idr.read(projectFile);

        ProjectTask task = tasks.stream().findFirst().get();
        TaskInitialEstimate estimate = task.createEstimate();
        AbstractRealDistribution distribution = estimate.getDistribution();

        assertTrue(distribution instanceof UniformRealDistribution);
        assertThat(estimate.min(), closeTo(1, 0.01));
        assertThat(estimate.max(), closeTo(2, 0.01));
    }

    @Test
    public void test_read_triangular_distribution() throws Exception
    {
        String projectFile = new XlsProjectBuilder().newFile()
                .addRow(1, "task1", false, 3, 1, 2, 3, 4, "T").create();

        InputDataReader idr = new InputDataReader();
        Set<ProjectTask> tasks = idr.read(projectFile);

        ProjectTask task = tasks.stream().findFirst().get();
        TaskInitialEstimate estimate = task.createEstimate();
        AbstractRealDistribution distribution = estimate.getDistribution();

        assertTrue(distribution instanceof TriangularDistribution);
        assertThat(distribution.getNumericalMean(), closeTo(2, 0.01));  //because symmetric
        assertThat(estimate.min(), closeTo(1, 0.01));
        assertThat(estimate.max(), closeTo(3, 0.01));
    }

    @Test
    public void test_read_trapezoid_distribution() throws Exception
    {
        String projectFile = new XlsProjectBuilder().newFile()
                .addRow(1, "task1", false, 3, 1, 2, 3, 4, "Tr").create();

        InputDataReader idr = new InputDataReader();
        Set<ProjectTask> tasks = idr.read(projectFile);

        ProjectTask task = tasks.stream().findFirst().get();
        TaskInitialEstimate estimate = task.createEstimate();
        AbstractRealDistribution distribution = estimate.getDistribution();

        assertTrue(distribution instanceof TrapezoidalDistribution);
        assertThat(distribution.getNumericalMean(), closeTo(2.5, 0.01));  //because symmetric
        assertThat(estimate.min(), closeTo(1, 0.01));
        assertThat(estimate.max(), closeTo(4, 0.01));
    }

    @Test
    public void test_read_dependencies() throws Exception
    {
        String projectFile = new XlsProjectBuilder().newFile()
                .addRow(1, "task1", false, 3, 1, 2, 3, 4, "Tr")
                .addRow(2, "task2", false, 3, 1, 2, 3, 4, "Tr", "1")
                .addRow(3, "task3", false, 3, 1, 2, 3, 4, "Tr", "1;2")
                .create();

        InputDataReader idr = new InputDataReader();
        Set<ProjectTask> tasks = idr.read(projectFile);

        for (ProjectTask task : tasks)
        {
            switch ((int)Double.valueOf(task.id).doubleValue()){
                case 1:
                    assertTrue(task.dependencies.isEmpty());
                    break;
                case 2:
                    assertEquals(task.dependencies, Sets.newHashSet(1.0));
                    break;
                case 3:
                    assertEquals(task.dependencies, Sets.newHashSet(1.0, 2.0));
                    break;
            }
        }
    }

    private class XlsProjectBuilder
    {
        private FileOutputStream fileOut;
        private File projectFile;
        private HSSFWorkbook wb;
        private HSSFSheet sheet;
        private int curRow = 1;

        XlsProjectBuilder newFile() throws Exception
        {
            return newFile("temp_project.xls");
        }

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
            HSSFCell cellDuration4 = headers.createCell(7);
            cellDuration4.setCellValue(COL_DURATION_4);

            HSSFCell cellDistr = headers.createCell(8);
            cellDistr.setCellValue(COL_DISTRIBUTION);

            HSSFCell cellDepends = headers.createCell(9);
            cellDepends.setCellValue(COL_PREDECESSORS);

            return this;
        }

        XlsProjectBuilder addRow(double id, String name, boolean isSummary, double duration, double duration1,
                                 double duration2, double duration3, double duration4, String distribution, String depends)
        {

            HSSFRow row = sheet.createRow(curRow++);

            row.createCell(0).setCellValue(id);
            row.createCell(1).setCellValue(name);
            row.createCell(2).setCellValue(isSummary ? "Yes" : "No");
            row.createCell(3).setCellValue(stringify(duration));
            row.createCell(4).setCellValue(stringify(duration1));
            row.createCell(5).setCellValue(stringify(duration2));
            row.createCell(6).setCellValue(stringify(duration3));
            row.createCell(7).setCellValue(stringify(duration4));
            row.createCell(8).setCellValue(distribution);
            row.createCell(9).setCellValue(depends);

            return this;
        }

        XlsProjectBuilder addRow(double id, String name, boolean isSummary, double duration, double duration1,
                                 double duration2, double duration3, double duration4, String distribution)
        {
            return addRow(id, name, isSummary, duration, duration1, duration2, duration3, duration4, distribution, "");
        }

        String create() throws IOException
        {
            wb.write(fileOut);
            fileOut.flush();
            return projectFile.getAbsolutePath();
        }

        private String stringify(double dur)
        {
            return dur == 1.0 ? dur + " day" : dur + " days";
        }
    }
}