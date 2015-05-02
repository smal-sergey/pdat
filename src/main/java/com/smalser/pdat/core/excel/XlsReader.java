package com.smalser.pdat.core.excel;

public interface XlsReader
{
    int getNumberOfRows();
    int getNumberOfColumns();
    String getValue(int row, int col);
}
