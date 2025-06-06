package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.TherapistDAO;
import com.dinidu.lk.pmt.dto.TherapistDTO;
import com.dinidu.lk.pmt.entity.Therapists;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;
import com.dinidu.lk.pmt.utils.projectTypes.TherapistStatus;

import java.sql.SQLException;
import java.util.ArrayList;
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
        return therapistDAO.delete(id);
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
        Therapists id = therapistDAO.getById(s);
        if (id == null) {
            System.out.println("No Patients found with the id: " + s);
            return null;
        } else {
            return id.getFullName();
        }
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

    @Override
    public TherapistDTO getTherapistByName(String therapistValue) throws SQLException, ClassNotFoundException {
        List<Therapists> therapists = therapistDAO.searchByName(therapistValue);
        if (therapists.isEmpty()) {
            System.out.println("No Patients found with the name: " + therapistValue);
            return null;
        } else {
            Therapists patients = therapists.get(0);
            return new TherapistDTO(
                    patients.getId() ,
                    patients.getFullName(),
                    patients.getAddress(),
                    patients.getPhoneNumber(),
                    patients.getEmail(),
                    patients.getStatus(),
                    patients.getCreatedBy(),
                    patients.getCreatedAt(),
                    patients.getUpdatedAt()
            );
        }
    }

    @Override
    public List<TherapistDTO> getAllAvailableTherapists() throws SQLException, ClassNotFoundException {
        List<Therapists> therapists = therapistDAO.fetchAll();
        System.out.println("Therapists From DB: " + therapists);
        List<TherapistDTO> availableTherapists = new ArrayList<>();
        for (Therapists therapist : therapists) {
            if (therapist.getStatus() == TherapistStatus.AVAILABLE) {
                System.out.println("Available Therapist: " + therapist);
                TherapistDTO dto = new TherapistDTO();
                dto.setId(therapist.getId());
                dto.setFullName(therapist.getFullName());
                dto.setEmail(therapist.getEmail());
                dto.setPhoneNumber(therapist.getPhoneNumber());
                dto.setAddress(therapist.getAddress());
                dto.setStatus(therapist.getStatus());
                availableTherapists.add(dto);
            }
        }
        return availableTherapists;
    }

    public void updateTherapist(TherapistDTO currentProject) throws SQLException,ClassNotFoundException{
        System.out.println("TherapistDTO: " + currentProject);
        System.out.println("Controller: " + this);
        therapistDAO.update(EntityDTOMapper.mapDTOToEntity(currentProject,
                Therapists.class));
    }
}
