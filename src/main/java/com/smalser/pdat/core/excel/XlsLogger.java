package com.smalser.pdat.core.excel;

import com.smalser.pdat.core.structure.AggregatedResult;
import com.smalser.pdat.core.structure.EstimatedTask;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.function.Function;

public class XlsLogger
{
    public static void dumpResult(String fileName, EstimatedTask estimatedTask)
    {
        dumpToXls2(fileName, estimatedTask::density, estimatedTask.getLeftBound(), estimatedTask.getRightBound(), estimatedTask.getA(), estimatedTask.getB());
    }

    public static void dumpResult(String fileName, AggregatedResult result)
    {
        dumpToXls2(fileName, result.distribution::density, result.leftBound, result.rightBound, result.a, result.b);
    }

    public static void dumpToXls(String fileName, Function<Double, Double> density, double min, double max)
    {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("result");

        final double STEP = 0.05;
        final int NUM_OF_COLUMNS = (int) ((max - min) / STEP) + 1;

        Row timeRow = sheet.createRow(0);
        Row valueRow = sheet.createRow(1);
        Cell cell;
        for (int colIndex = 0; colIndex < NUM_OF_COLUMNS; colIndex++)
        {
            double x = min + STEP * colIndex;

            cell = timeRow.createCell((short) colIndex);
            cell.setCellValue(x);

            cell = valueRow.createCell((short) colIndex);
            cell.setCellValue(density.apply(x));
        }

        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 10, 15, 20);

        Chart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        LineChartData lineData = chart.getChartDataFactory().createLineChartData();

        ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, NUM_OF_COLUMNS - 1));
        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, NUM_OF_COLUMNS - 1));
        lineData.addSeries(xs, ys1);

        chart.plot(lineData, bottomAxis, leftAxis);

        try (FileOutputStream fileOut = new FileOutputStream(fileName))
        {
            wb.write(fileOut);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void dumpToXls2(String fileName, Function<Double, Double> density, double min, double max,
                                  double leftBound, double rightBound)
    {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("result");

        final double STEP = 0.05;
        final int NUM_OF_COLUMNS = (int) ((max - min) / STEP) + 1;

        Row timeRow = sheet.createRow(0);
        Row valueRow = sheet.createRow(1);
        Cell cell;
        for (int colIndex = 0; colIndex < NUM_OF_COLUMNS; colIndex++)
        {
            double x = min + STEP * colIndex;

            cell = timeRow.createCell((short) colIndex);
            cell.setCellValue(x);

            cell = valueRow.createCell((short) colIndex);
            cell.setCellValue(density.apply(x));

//            System.out.println(x + "\t" + density.apply(x));
        }


        int NUM_OF_COLUMNS_2 = 5;
        createValues(leftBound, density.apply(leftBound), sheet, 2, NUM_OF_COLUMNS_2);
        createValues(rightBound, density.apply(rightBound), sheet, 4, NUM_OF_COLUMNS_2);

        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 10, 15, 20);

        Chart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        ScatterChartData chartData = chart.getChartDataFactory().createScatterChartData();

        ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);

        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        bottomAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        bottomAxis.setMinimum(min - 5);
        bottomAxis.setMaximum(max + 5);

        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, NUM_OF_COLUMNS - 1));
        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, NUM_OF_COLUMNS - 1));

        ChartDataSource<Number> axs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, NUM_OF_COLUMNS_2 - 1));
        ChartDataSource<Number> ays = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(3, 3, 0, NUM_OF_COLUMNS_2 - 1));

        ChartDataSource<Number> bxs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(4, 4, 0, NUM_OF_COLUMNS_2 - 1));
        ChartDataSource<Number> bys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(5, 5, 0, NUM_OF_COLUMNS_2 - 1));

        chartData.addSerie(xs, ys1);
        chartData.addSerie(axs, ays);
        chartData.addSerie(bxs, bys);

        chart.plot(chartData, bottomAxis, leftAxis);

        try (FileOutputStream fileOut = new FileOutputStream(fileName))
        {
            wb.write(fileOut);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void createValues(double point, double maxDensity, Sheet sheet, int rowIndex, int columnsNumber)
    {
        Row pointRow = sheet.createRow(rowIndex);
        Row pointValueRow = sheet.createRow(rowIndex + 1);
        Cell cell;
        for (int colIndex = 0; colIndex < columnsNumber; colIndex++)
        {
            cell = pointRow.createCell((short) colIndex);
            cell.setCellValue(point);

            cell = pointValueRow.createCell((short) colIndex);
            cell.setCellValue(maxDensity / columnsNumber * colIndex);
        }
    }

}
