package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.ReportsBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.ReportsDAO;
import com.dinidu.lk.pmt.dto.ReportDTO;
import com.dinidu.lk.pmt.dto.TaskReportData;
import com.dinidu.lk.pmt.entity.Report;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;

import java.sql.SQLException;
import java.util.Map;

public class ReportBOImpl implements ReportsBO {
    ReportsDAO reportsDAO = (ReportsDAO)
            DAOFactory.getDaoFactory().
                getDAO(DAOFactory.DAOTypes.REPORTS);

    @Override
    public boolean insertReport(ReportDTO reportDTO) throws SQLException, ClassNotFoundException {
        return reportsDAO.insert(EntityDTOMapper.mapDTOToEntity(reportDTO, Report.class));
    }

    @Override
    public Map<Long, TaskReportData> getAllTaskReportData() throws SQLException, ClassNotFoundException {
        return reportsDAO.getAllTaskReportData();
    }
}
