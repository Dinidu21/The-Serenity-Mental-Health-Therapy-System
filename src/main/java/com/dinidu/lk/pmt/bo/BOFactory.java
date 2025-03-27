package com.dinidu.lk.pmt.bo;

import com.dinidu.lk.pmt.bo.custom.Impl.*;

public class BOFactory {
    private static BOFactory boFactory;
    private BOFactory() {}
    public static BOFactory getInstance() {
        return boFactory==null?boFactory=new BOFactory():boFactory;
    }


    public enum BOTypes{
        USER,TASKS,PROJECTS,ISSUES,QUERY,TIMESHEET,REPORTS,CHECKLISTS,ATTACHMENTS,TEAM_ASSIGNMENTS
    }

    public SuperBO getBO(BOFactory.BOTypes daoTypes){
        return switch (daoTypes) {
            case USER -> new UserBOImpl();
            case PROJECTS -> new ProjectsBOImpl();
/*            case TASKS -> new TasksBOImpl();*/
            case TIMESHEET -> new TimesheetBOImpl();
            case REPORTS -> new ReportBOImpl();
            case CHECKLISTS -> new ChecklistBOImpl();
            case ISSUES -> new IssueBOImpl();
            case ATTACHMENTS -> new AttachmentBOImpl();
            case TEAM_ASSIGNMENTS -> new TeamAssignmentBOImpl();
            default -> null;
        };
    }
}
