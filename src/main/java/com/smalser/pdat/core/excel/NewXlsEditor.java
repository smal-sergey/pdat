package com.smalser.pdat.core.excel;

import com.google.common.base.Throwables;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class NewXlsEditor implements XlsEditor
{
    private final String fileName;
    private Map<Integer, Row> rows;
    private HSSFSheet sheet;
    private HSSFWorkbook wb;

    NewXlsEditor(String fileName) throws IOException
    {
        this.fileName = fileName;
        indexData();
    }

    private void indexData() throws IOException
    {
        wb = new HSSFWorkbook(new FileInputStream(fileName));
        sheet = wb.getSheetAt(0);

        rows = new HashMap<>();
        int rowIdx = 0;

        for (Row row : sheet)
        {
            rows.put(rowIdx++, row);
        }
    }

    @Override
    public int getNumberOfRows()
    {
        return sheet.getLastRowNum() + 1;
    }

    @Override
    public int getNumberOfColumns()
    {
        return sheet.getRow(0).getLastCellNum();
    }

    @Override
    public String getValue(int row, int col)
    {
        Cell cell = rows.get(row).getCell(col);
        if (cell == null)
        {
            return "";
        }
        else
        {
            return cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? "" + cell.getNumericCellValue() : cell.getStringCellValue();
        }
    }

    @Override
    public void setValue(int row, int col, String value)
    {
        if (!rows.containsKey(row))
        {
            rows.put(row, sheet.createRow(row));
        }

        Row r = rows.get(row);
        Cell cell = r.createCell(col);
        cell.setCellValue(value);
    }

    @Override
    public void flush()
    {
        try (FileOutputStream fileOut = new FileOutputStream(new File(fileName)))
        {
            wb.write(fileOut);
            fileOut.flush();
        } catch (IOException e)
        {
            Throwables.propagate(e);
        }
    }

    @Override
    public void close() throws Exception
    {
        wb.close();
    }
}
