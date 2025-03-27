package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.TimesheetDTO;

import java.sql.SQLException;

public interface TimeSheetBO extends SuperBO {
    boolean createTimeSheet(TimesheetDTO timesheetDTO) throws SQLException, ClassNotFoundException;
}
