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
    public final UnivariateFunction leftBorder;
    public final UnivariateFunction rightBorder;
    public final TaskConstraints taskConstraints;
    public final double optimalTime;

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

    public void dumpToXls(String fileName, boolean isLine)
    {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("result");

        double rightBound = getRightBound();
        double leftBound = getLeftBound();

        final double STEP = 0.01;
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
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 10, 15, 20);

        Chart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        ScatterChartData chartData = chart.getChartDataFactory().createScatterChartData();
        LineChartData lineData = chart.getChartDataFactory().createLineChartData();

        ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, NUM_OF_COLUMNS - 1));
        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, NUM_OF_COLUMNS - 1));

        ChartDataSource<Number> axs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, NUM_OF_COLUMNS_2 - 1));
        ChartDataSource<Number> ays = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(3, 3, 0, NUM_OF_COLUMNS_2 - 1));

        ChartDataSource<Number> bxs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(4, 4, 0, NUM_OF_COLUMNS_2 - 1));
        ChartDataSource<Number> bys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(5, 5, 0, NUM_OF_COLUMNS_2 - 1));

        if (isLine)
        {
            lineData.addSeries(xs, ys1);
        }
        else
        {
            chartData.addSerie(xs, ys1);
            chartData.addSerie(axs, ays);
            chartData.addSerie(bxs, bys);
        }

        chart.plot(isLine ? lineData : chartData, bottomAxis, leftAxis);

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

    public double getProbabilityOfInterval()
    {
        return distribution.probability(getA(), getB());
    }
}
