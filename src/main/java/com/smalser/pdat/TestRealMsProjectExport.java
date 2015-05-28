package com.smalser.pdat;

public class TestRealMsProjectExport
{
    public static void main(String[] args)
    {
//        String sourceFile = "ms_project_files/real/План-график new21_04.xls";
//        String sourceFile = "ms_project_files/real/Детальныи_ план внедрения v1 1.xls";
        String sourceFile = "ms_project_files/real/Календарныи_ план проект ИСУП ТТК v 1.4.xls";

        PdatFacade facade = new PdatFacade();
//        facade.estimateFilteringTasks(false, 0.8, pt -> pt.notEmptyEstimate() && !pt.id.equals("9.0") && !pt.id.equals("12.0")
//                && !pt.id.equals("13.0") && !pt.id.equals("271.0"), sourceFile);

        facade.estimate(false, 0.8, sourceFile);


    }
}