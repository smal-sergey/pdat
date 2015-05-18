package com.smalser.pdat.msproject;

import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.smalser.pdat.core.excel.XlsEditor;
import com.smalser.pdat.core.excel.XlsEditorFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class OutputDataWriter extends MetaDataContainer implements AutoCloseable
{
    private XlsEditor xls;
    private BiMap<String, Integer> headers;

    public OutputDataWriter(String sourceFileName, String targetFileName) throws IOException
    {
        Files.copy(new File(sourceFileName).toPath(), new File(targetFileName).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        xls = XlsEditorFactory.create(targetFileName);
        headers = HashBiMap.create(readHeaders(xls)).inverse();
    }

    public void writeValue(String taskId, String valueName, String value){
        int taskNameCol = headers.get(COL_ID);
        int col = headers.get(valueName);
        int row = 0;

        for (; row < xls.getNumberOfRows(); row++)
        {
            if(xls.getValue(row, taskNameCol).equals(taskId))
            {
                break;
            }
        }

        xls.setValue(row, col, value);
    }

    public void flush()
    {
        xls.flush();
    }

    @Override
    public void close() throws Exception
    {
        xls.close();
    }
}
