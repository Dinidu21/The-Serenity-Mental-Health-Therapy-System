package com.dinidu.lk.pmt.bo;

import com.dinidu.lk.pmt.bo.custom.Impl.*;

public class BOFactory {
    private static BOFactory boFactory;
    private BOFactory() {}
    public static BOFactory getInstance() {
        return boFactory==null?boFactory=new BOFactory():boFactory;
    }


    public enum BOTypes{
        USER,TIMESHEET, PROGRAM, THERAPIST, PATIENTS
    }

    public SuperBO getBO(BOFactory.BOTypes daoTypes){
        return switch (daoTypes) {
            case USER -> new UserBOImpl();
            case TIMESHEET -> new TimesheetBOImpl();
            case PROGRAM -> new ProgramsBOImpl();
            case THERAPIST -> new TherapistsBOImpl();
            case PATIENTS -> new PatientBOImpl();
        };
    }
}
