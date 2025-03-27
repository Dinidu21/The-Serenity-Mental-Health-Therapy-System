package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.ReportDTO;
import com.dinidu.lk.pmt.dto.TaskReportData;

import java.sql.SQLException;
import java.util.Map;

public interface ReportsBO extends SuperBO {
    boolean insertReport(ReportDTO reportDTO) throws SQLException, ClassNotFoundException;
    Map<Long, TaskReportData> getAllTaskReportData() throws SQLException, ClassNotFoundException;
}
