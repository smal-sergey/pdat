package com.smalser.pdat.core.structure;

import com.smalser.pdat.core.calculator.TaskConstraints;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

public class Result
{
    private final UnivariateFunction leftBorder;
    private final UnivariateFunction rightBorder;
    private final double optimalTime;
    private final TaskConstraints taskConstraints;
    private final AbstractRealDistribution distribution;

    public Result(UnivariateFunction leftBorder, UnivariateFunction rightBorder, double optimalTime,
                  TaskConstraints taskConstraints, AbstractRealDistribution distribution)
    {
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.optimalTime = optimalTime;
        this.taskConstraints = taskConstraints;
        this.distribution = distribution;
    }

    public double getA()
    {
        return leftBorder.value(optimalTime);
    }

    public double getB()
    {
        return rightBorder.value(optimalTime);
    }

    public double getLeftBound()
    {
        return taskConstraints.leftBound;
    }

    public double getRightBound()
    {
        return taskConstraints.rightBound;
    }

    public double density(double x)
    {
        return distribution.density(x);
    }

    public void dumpToXls(String fileName)
    {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("result");

        double rightBound = getRightBound();
        double leftBound = getLeftBound();

        final double STEP = 0.1;
        final int NUM_OF_COLUMNS = (int) ((rightBound - leftBound) / STEP) + 1;

        Row timeRow = sheet.createRow(0);
        Row valueRow = sheet.createRow(1);
        Cell cell;
        for (int colIndex = 0; colIndex < NUM_OF_COLUMNS; colIndex++)
        {
            double x = leftBound + STEP * colIndex;

            cell = timeRow.createCell((short) colIndex);
            cell.setCellValue(x);

            cell = valueRow.createCell((short) colIndex);
            cell.setCellValue(density(x));
        }


        int NUM_OF_COLUMNS_2 = 5;
        createValues(getA(), sheet, 2, NUM_OF_COLUMNS_2);
        createValues(getB(), sheet, 4, NUM_OF_COLUMNS_2);

        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);

        Chart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        ScatterChartData data = chart.getChartDataFactory().createScatterChartData();
//        LineChartData data = chart.getChartDataFactory().createLineChartData();

        ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, NUM_OF_COLUMNS - 1));
        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, NUM_OF_COLUMNS - 1));

        ChartDataSource<Number> axs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, NUM_OF_COLUMNS_2 - 1));
        ChartDataSource<Number> ays = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(3, 3, 0, NUM_OF_COLUMNS_2 - 1));

        ChartDataSource<Number> bxs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(4, 4, 0, NUM_OF_COLUMNS_2 - 1));
        ChartDataSource<Number> bys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(5, 5, 0, NUM_OF_COLUMNS_2 - 1));

//        data.addSeries(xs, ys1);
//        data.addSeries(axs, ays);
//        data.addSeries(bxs, bys);
        data.addSerie(xs, ys1);
        data.addSerie(axs, ays);
        data.addSerie(bxs, bys);

        chart.plot(data, bottomAxis, leftAxis);

        try (FileOutputStream fileOut = new FileOutputStream(fileName))
        {
            wb.write(fileOut);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void createValues(double point, Sheet sheet, int rowIndex, int columnsNumber)
    {
        Row pointRow = sheet.createRow(rowIndex);
        Row pointValueRow = sheet.createRow(rowIndex + 1);
        Cell cell;
        for (int colIndex = 0; colIndex < columnsNumber; colIndex++)
        {
            cell = pointRow.createCell((short) colIndex);
            cell.setCellValue(point);

            cell = pointValueRow.createCell((short) colIndex);
            cell.setCellValue(density(point) / columnsNumber * colIndex);
        }
    }
}
