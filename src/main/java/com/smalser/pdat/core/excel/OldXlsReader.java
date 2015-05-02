package com.smalser.pdat.core.excel;

import com.google.common.base.Throwables;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;

class OldXlsReader implements XlsReader
{
    private Sheet sheet;

    OldXlsReader(String fileName) throws IOException
    {
        Workbook workbook;
        try
        {
            workbook = Workbook.getWorkbook(new File(fileName));
            sheet = workbook.getSheet(0);
        } catch (BiffException e)
        {
            Throwables.propagate(e);
        }
    }

    @Override
    public int getNumberOfRows()
    {
        return sheet.getRows();
    }

    @Override
    public int getNumberOfColumns()
    {
        return sheet.getColumns();
    }

    @Override
    public String getValue(int row, int col)
    {
        return sheet.getCell(col, row).getContents();
    }
}
