package com.dinidu.lk.pmt.bo;

import com.dinidu.lk.pmt.bo.custom.Impl.*;

public class BOFactory {
    private static BOFactory boFactory;
    private BOFactory() {}
    public static BOFactory getInstance() {
        return boFactory==null?boFactory=new BOFactory():boFactory;
    }


    public enum BOTypes{
        USER,TIMESHEET,ProgramsBO,TherapistsBO,PatientBO
    }

    public SuperBO getBO(BOFactory.BOTypes daoTypes){
        return switch (daoTypes) {
            case USER -> new UserBOImpl();
            case TIMESHEET -> new TimesheetBOImpl();
            case ProgramsBO -> new ProgramsBOImpl();
            case TherapistsBO -> new TherapistsBOImpl();
            case PatientBO -> new PatientBOImpl();
            default -> null;
        };
    }
}
