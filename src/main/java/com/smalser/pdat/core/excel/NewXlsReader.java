package com.smalser.pdat.core.excel;

import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NewXlsReader implements XlsReader
{
    private final String fileName;
    private int numberOfRows;
    private int numberOfColumns;
    private Map<Integer, List<Cell>> rows;

    NewXlsReader(String fileName) throws IOException
    {
        this.fileName = fileName;
        indexData();
    }

    private void indexData() throws IOException
    {
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(fileName));
        HSSFSheet sheet = wb.getSheetAt(0);

        numberOfRows = sheet.getLastRowNum() + 1;
        numberOfColumns = sheet.getRow(0).getLastCellNum();

        rows = new HashMap<>();
        int rowIdx = 0;

        for (Row row : sheet)
        {
            List<Cell> cols = Lists.newArrayList(new Cell[numberOfColumns]);
            rows.put(rowIdx++, cols);

            for (Cell c : row)
            {
                cols.add(c.getColumnIndex(), c);
            }
        }
    }

    @Override
    public int getNumberOfRows()
    {
        return numberOfRows;
    }

    @Override
    public int getNumberOfColumns()
    {
        return numberOfColumns;
    }

    @Override
    public String getValue(int row, int col)
    {
        Cell cell = rows.get(row).get(col);
        if (cell == null)
        {
            return "";
        }
        else
        {
            return cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? "" + cell.getNumericCellValue() : cell.getStringCellValue();
        }
    }
}
