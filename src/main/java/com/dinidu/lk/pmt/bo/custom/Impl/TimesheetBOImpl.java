package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.TimeSheetBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.TimeSheetDAO;
import com.dinidu.lk.pmt.dto.TimesheetDTO;
import com.dinidu.lk.pmt.entity.Timesheet;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;

import java.sql.SQLException;

public class TimesheetBOImpl implements TimeSheetBO {
    TimeSheetDAO timeSheetDAO = (TimeSheetDAO) DAOFactory.
            getDaoFactory().
            getDAO(DAOFactory.DAOTypes.TIMESHEET);

    @Override
    public boolean createTimeSheet(TimesheetDTO timesheetDTO) throws SQLException, ClassNotFoundException {
        return timeSheetDAO.save(EntityDTOMapper.mapDTOToEntity(timesheetDTO, Timesheet.class));
    }
}
