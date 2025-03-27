package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.ChecklistBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.ChecklistDAO;
import com.dinidu.lk.pmt.dto.ChecklistDTO;
import com.dinidu.lk.pmt.entity.Checklist;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;
import javafx.beans.property.LongProperty;

import java.sql.SQLException;
import java.util.List;

public class ChecklistBOImpl implements ChecklistBO {
    ChecklistDAO checklistDAO = (ChecklistDAO)
            DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.CHECKLISTS);

    @Override
    public List<ChecklistDTO> getChecklistsByTaskId(LongProperty id) throws SQLException, ClassNotFoundException {
        return EntityDTOMapper.mapEntityListToDTOList(checklistDAO.getChecklistsByTaskId(id),
                ChecklistDTO.class);
    }

    @Override
    public List<ChecklistDTO> getAllChecklists() throws SQLException, ClassNotFoundException {
        return EntityDTOMapper.mapEntityListToDTOList(checklistDAO.fetchAll(),
                ChecklistDTO.class);
    }

    @Override
    public boolean insertChecklist(ChecklistDTO checklistDTO) throws SQLException, ClassNotFoundException {
        return checklistDAO.insert(EntityDTOMapper.mapDTOToEntity(checklistDTO, Checklist.class));
    }

    @Override
    public boolean deleteChecklist(LongProperty longProperty) throws SQLException, ClassNotFoundException {
        return checklistDAO.deleteChecklist(longProperty);
    }

    @Override
    public boolean updateChecklist(ChecklistDTO currentChecklist) throws SQLException, ClassNotFoundException {
        return checklistDAO.update(EntityDTOMapper.mapDTOToEntity(currentChecklist, Checklist.class));
    }
}
