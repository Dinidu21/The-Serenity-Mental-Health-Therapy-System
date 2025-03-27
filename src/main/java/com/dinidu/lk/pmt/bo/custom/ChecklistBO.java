package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.ChecklistDTO;
import javafx.beans.property.LongProperty;

import java.sql.SQLException;
import java.util.List;

public interface ChecklistBO extends SuperBO {
    List<ChecklistDTO> getChecklistsByTaskId(LongProperty id) throws SQLException,ClassNotFoundException;
    List<ChecklistDTO> getAllChecklists() throws SQLException,ClassNotFoundException;
    boolean insertChecklist(ChecklistDTO checklistDTO) throws SQLException,ClassNotFoundException;
    boolean deleteChecklist(LongProperty longProperty) throws SQLException,ClassNotFoundException;
    boolean updateChecklist(ChecklistDTO currentChecklist) throws SQLException,ClassNotFoundException;
}
