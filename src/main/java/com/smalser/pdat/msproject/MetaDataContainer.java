package com.smalser.pdat.msproject;

import com.google.common.collect.Sets;
import com.smalser.pdat.core.excel.XlsEditor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class MetaDataContainer
{
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "Task_Name";
    public static final String COL_SUMMARY = "Summary";
    public static final String COL_DURATION = "Duration";
    public static final String COL_DURATION_1 = "Duration1";    //pessimistic
    public static final String COL_DURATION_2 = "Duration2";    //expected
    public static final String COL_DURATION_3 = "Duration3";    //optimistic
    public static final String COL_PREDECESSORS = "Predecessors";

    protected static final Set<String> HEADERS = Sets.newHashSet(COL_ID, COL_NAME, COL_SUMMARY, COL_DURATION, COL_DURATION_1, COL_DURATION_2, COL_DURATION_3, COL_PREDECESSORS);

    protected Map<Integer, String> readHeaders(XlsEditor xls){
        Map<Integer, String> idxToCol = new HashMap<>();

        //read HEADERS
        for (int i = 0; i < xls.getNumberOfColumns(); i++)
        {
            String value = xls.getValue(0, i);
            if (HEADERS.contains(value))
            {
                idxToCol.put(i, value);
            }
        }

        return idxToCol;
    }
}
