package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.dto.TaskReportData;
import com.dinidu.lk.pmt.entity.Report;

import java.sql.SQLException;
import java.util.Map;

public interface ReportsDAO extends CrudDAO<Report> {
    Map<Long, TaskReportData> getAllTaskReportData() throws SQLException,ClassNotFoundException;
}
