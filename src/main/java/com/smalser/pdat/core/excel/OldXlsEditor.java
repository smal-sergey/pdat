package com.smalser.pdat.core.excel;

import com.google.common.base.Throwables;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.IOException;

class OldXlsEditor implements XlsEditor
{
    private Sheet sheet;
    private WritableSheet writableSheet;
    private WritableWorkbook writableWorkbook;

    OldXlsEditor(String fileName) throws IOException
    {
        try
        {
            sheet = Workbook.getWorkbook(new File(fileName)).getSheet(0);
            writableWorkbook = Workbook.createWorkbook(new File(fileName));
            writableSheet = writableWorkbook.createSheet("First Sheet", 0);
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

    @Override
    public void setValue(int row, int col, String value)
    {
        try
        {
            writableSheet.addCell(new Label(col, row, value));
        } catch (WriteException e)
        {
            Throwables.propagate(e);
        }
    }

    @Override
    public void flush()
    {
        try
        {
            writableWorkbook.write();
        } catch (Exception e)
        {
            Throwables.propagate(e);
        }
    }

    @Override
    public void close() throws Exception
    {
        writableWorkbook.close();
    }
}
