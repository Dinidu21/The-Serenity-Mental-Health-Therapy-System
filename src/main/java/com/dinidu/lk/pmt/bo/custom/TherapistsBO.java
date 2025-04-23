package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.TherapistDTO;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TherapistsBO extends SuperBO {
    List<TherapistDTO> getAllTherapists() throws SQLException,ClassNotFoundException;
    void updateTherapist(TherapistDTO currentProject) throws SQLException,ClassNotFoundException;
    boolean deleteTherapists(String id)throws SQLException,ClassNotFoundException;
    Optional<Object> isTherapistIdTaken(String fullProjectId) throws SQLException,ClassNotFoundException;
    boolean insert(TherapistDTO therapistDTO) throws SQLException, ClassNotFoundException;
    String getTherapistIdByTaskId(long l) throws SQLException, ClassNotFoundException;
    String getTherapistNameById(String s) throws SQLException, ClassNotFoundException;
    String getTherapistIdByName(String selectedProjectName) throws SQLException, ClassNotFoundException;
    List<TherapistDTO> getTherapistById(String projectId) throws SQLException, ClassNotFoundException;
    Map<String, String> getAllTherapistNames() throws SQLException,ClassNotFoundException;
    List<TherapistDTO> searchTherapistByName(String query) throws SQLException, ClassNotFoundException;
    TherapistDTO getTherapistByName(String therapistValue) throws  SQLException, ClassNotFoundException;
    List<TherapistDTO> getAllAvailableTherapists() throws SQLException, ClassNotFoundException;
}
