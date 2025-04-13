package com.dinidu.lk.pmt.dao;

import com.dinidu.lk.pmt.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;
    private DAOFactory(){
    }
    public static DAOFactory getDaoFactory(){
        return (daoFactory==null)?daoFactory
                =new DAOFactory():daoFactory;
    }

    public enum DAOTypes{
        USER,TASKS, Therapist,QUERY, TIMESHEET, PROGRAMS,Patients
    }

    public SuperDAO getDAO(DAOTypes daoTypes){
        return switch (daoTypes) {
            case USER -> new UserDAOImpl();
            case QUERY -> new QueryDAOImpl();
            case Therapist -> new TherapistDAOImpl();
            case PROGRAMS -> new ProgramsDAOImpl();
            case Patients -> new PatientsDAOImpl();
            default -> null;
        };
    }
}
