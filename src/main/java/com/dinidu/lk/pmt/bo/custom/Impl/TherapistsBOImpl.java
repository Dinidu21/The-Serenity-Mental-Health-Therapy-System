package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.TherapistDAO;
import com.dinidu.lk.pmt.dto.TherapistDTO;
import com.dinidu.lk.pmt.entity.Therapists;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TherapistsBOImpl implements TherapistsBO {
    TherapistDAO therapistDAO =
            (TherapistDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.Therapist);


    public List<TherapistDTO> getAllTherapists() throws SQLException,ClassNotFoundException {
        List<Therapists> therapists = therapistDAO.fetchAll();
        System.out.println("Therapists From DB: " + therapists);
        return EntityDTOMapper.mapEntityListToDTOList(therapists,
                TherapistDTO.class);

    }

    public boolean deleteTherapists(String id) throws SQLException,ClassNotFoundException{
        return false;
    }

    @Override
    public Optional<Object> isTherapistIdTaken(String fullProjectId) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public boolean insert(TherapistDTO therapistDTO)  throws SQLException, ClassNotFoundException {
        System.out.println("TherapistDTO: " + therapistDTO);
        System.out.println("Controller: " + this);
        return therapistDAO.insert(EntityDTOMapper.mapDTOToEntity(therapistDTO,
                Therapists.class));
    }

    @Override
    public String getTherapistIdByTaskId(long l) {
        return "";
    }

    @Override
    public String getTherapistNameById(String s) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public String getTherapistIdByName(String selectedProjectName) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public List<TherapistDTO> getTherapistById(String projectId) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Map<String, String> getAllTherapistNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<TherapistDTO> searchTherapistByName(String query) throws SQLException, ClassNotFoundException {
        List<Therapists> therapists = therapistDAO.searchByName(query);
        System.out.println("Therapists From DB: " + therapists);
        return EntityDTOMapper.mapEntityListToDTOList(therapists,
                TherapistDTO.class);
    }

    public void updateTherapist(TherapistDTO currentProject) throws SQLException,ClassNotFoundException{

    }
}
