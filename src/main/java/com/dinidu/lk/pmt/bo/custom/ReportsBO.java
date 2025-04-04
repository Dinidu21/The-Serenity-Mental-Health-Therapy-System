package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.dto.ReportDTO;

import java.sql.SQLException;

public interface ReportsBO {
    boolean insertReport(ReportDTO reportDTO) throws SQLException , ClassNotFoundException;

}
