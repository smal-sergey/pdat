package com.smalser.pdat.core.excel;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class XlsReaderFactory
{
    public static XlsReader create(String fileName) throws IOException
    {
        try
        {
            new HSSFWorkbook(new FileInputStream(fileName));
            return new NewXlsReader(fileName);
        } catch (OldExcelFormatException e)
        {
            return new OldXlsReader(fileName);
        }
    }
}
