package com.smalser.pdat.core.excel;

public interface XlsEditor extends AutoCloseable
{
    int getNumberOfRows();

    int getNumberOfColumns();

    String getValue(int row, int col);

    void setValue(int row, int col, String value);

    void flush();
}
