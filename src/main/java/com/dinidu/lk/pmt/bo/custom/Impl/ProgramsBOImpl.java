package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.ProgramsBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.ProgramsDAO;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.entity.TherapyPrograms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgramsBOImpl implements ProgramsBO {
    ProgramsDAO programsDAO =
            (ProgramsDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.PROGRAMS);

    @Override
    public boolean deleteProgram(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public void searchProgramByTherapistName(String projectName) throws SQLException, ClassNotFoundException {

    }

    @Override
    public String getProgramNameById(Long taskId) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public List<PatientsDTO> searchPatientsByName(String query) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public String getTherapistNameById(String projectId) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public List<PatientsDTO> searchProgramsByPatientName(String selectedIssueName) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<PatientsDTO> getAllPatients() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public void updateProgram(TherapyProgramsDTO currentTask) throws SQLException, ClassNotFoundException {

    }

    @Override
    public List<TherapyProgramsDTO> getAllPrograms() throws SQLException, ClassNotFoundException {
        List<TherapyPrograms> therapyPrograms = programsDAO.fetchAll();
        System.out.println("Fetched all programs: " + therapyPrograms);

        List<TherapyProgramsDTO> therapyProgramsDTOList = new ArrayList<>();

        for (TherapyPrograms program : therapyPrograms) {
            TherapyProgramsDTO dto = new TherapyProgramsDTO();
            dto.setId(program.getProgramId());
            dto.setName(program.getProgramName());
            dto.setFee(program.getFee());
            dto.setDuration(program.getDuration());
            therapyProgramsDTOList.add(dto);
        }
        System.out.println("Converted TherapyPrograms to TherapyProgramsDTO: " + therapyProgramsDTOList);
        return therapyProgramsDTOList;
    }


    @Override
    public boolean insertProgram(TherapyProgramsDTO thearpyTherapyProgramsDTO) throws SQLException, ClassNotFoundException {
        System.out.println("Inserting program: " + thearpyTherapyProgramsDTO);
        System.out.println("Program ID: " + thearpyTherapyProgramsDTO.getId());
        System.out.println("Program Name: " + thearpyTherapyProgramsDTO.getName());
        System.out.println("Program Fee: " + thearpyTherapyProgramsDTO.getFee());
        System.out.println("Program Duration: " + thearpyTherapyProgramsDTO.getDuration());
        TherapyPrograms entity = new TherapyPrograms();
        entity.setProgramName(thearpyTherapyProgramsDTO.getName());
        entity.setFee(thearpyTherapyProgramsDTO.getFee());
        entity.setDuration(thearpyTherapyProgramsDTO.getDuration());
        System.out.println("TherapyPrograms object created: " + entity);
        return programsDAO.insert(entity);
    }

    @Override
    public List<TherapyProgramsDTO> getProgramByTherapistId(String s) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<TherapyProgramsDTO> searchProgramsByName(String query) throws SQLException, ClassNotFoundException {
        List<TherapyPrograms> therapyPrograms = programsDAO.searchByName(query);
        System.out.println("Fetched programs by name: " + therapyPrograms);

        List<TherapyProgramsDTO> therapyProgramsDTOList = new ArrayList<>();

        for (TherapyPrograms program : therapyPrograms) {
            TherapyProgramsDTO dto = new TherapyProgramsDTO();
            dto.setId(program.getProgramId());
            dto.setName(program.getProgramName());
            dto.setFee(program.getFee());
            dto.setDuration(program.getDuration());
            therapyProgramsDTOList.add(dto);
        }
        System.out.println("Converted TherapyPrograms to TherapyProgramsDTO: " + therapyProgramsDTOList);
        return therapyProgramsDTOList;
    }

    @Override
    public long getLastProgramID() throws SQLException, ClassNotFoundException {
       return programsDAO.getLastProgramID();
    }
}
