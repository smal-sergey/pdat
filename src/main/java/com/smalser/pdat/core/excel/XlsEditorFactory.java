package com.smalser.pdat.core.excel;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class XlsEditorFactory
{
    public static XlsEditor create(String fileName) throws IOException
    {
        try
        {
            new HSSFWorkbook(new FileInputStream(fileName));
            return new NewXlsEditor(fileName);
        } catch (OldExcelFormatException e)
        {
            return new OldXlsEditor(fileName);
        }
    }
}
