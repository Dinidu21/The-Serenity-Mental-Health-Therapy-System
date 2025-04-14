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
    public boolean deleteProgram(long id) throws SQLException, ClassNotFoundException {
        System.out.println("Deleting program with ID: " + id);
        TherapyPrograms therapyPrograms = programsDAO.getById(id);
        if (therapyPrograms != null) {
            boolean b = programsDAO.deleteProgram(id);
            if (!b) {
                System.out.println("Failed to delete program with ID: " + id);
                return false;
            }
            System.out.println("Program deleted successfully.");
            return true;
        } else {
            System.out.println("Program not found with ID: " + id);
            return false;
        }
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
        System.out.println("Updating program: " + currentTask);
        System.out.println("Program ID: " + currentTask.getProgramId());
        System.out.println("Program Name: " + currentTask.getName());
        System.out.println("Program Fee: " + currentTask.getFee());
        System.out.println("Program Duration: " + currentTask.getDuration());

        // Retrieve the existing entity by programId

        TherapyPrograms entity = programsDAO.getById(currentTask.getProgramId());
        if (entity == null) {
            throw new SQLException("TherapyPrograms with ID " + currentTask.getProgramId() + " not found.");
        }

        // Update the entity's fields with DTO values
        entity.setProgramName(currentTask.getName());
        entity.setFee(currentTask.getFee());
        entity.setDuration(currentTask.getDuration());

        System.out.println("TherapyPrograms object updated: " + entity);
        programsDAO.update(entity);
    }
    @Override
    public List<TherapyProgramsDTO> getAllPrograms() throws SQLException, ClassNotFoundException {
        List<TherapyPrograms> therapyPrograms = programsDAO.fetchAll();
        System.out.println("Fetched all programs: " + therapyPrograms);

        List<TherapyProgramsDTO> therapyProgramsDTOList = new ArrayList<>();

        for (TherapyPrograms program : therapyPrograms) {
            TherapyProgramsDTO dto = new TherapyProgramsDTO();
            dto.setProgramId(program.getProgramId());
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
        System.out.println("Program ID: " + thearpyTherapyProgramsDTO.getProgramId());
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
            dto.setProgramId(program.getProgramId());
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

    @Override
    public List<TherapyProgramsDTO> getProgramById(long proId) throws SQLException, ClassNotFoundException {
        TherapyPrograms programById = programsDAO.getProgramById(proId);
        System.out.println("Fetched program by ID: " + programById);
        List<TherapyProgramsDTO> therapyProgramsDTOList = new ArrayList<>();
        if (programById != null) {
            TherapyProgramsDTO dto = new TherapyProgramsDTO(
                    programById.getProgramId(),
                    programById.getProgramName(),
                    programById.getDuration(),
                    programById.getFee()
            );
            therapyProgramsDTOList.add(dto);
        }

        System.out.println("Converted TherapyPrograms to TherapyProgramsDTO: " + therapyProgramsDTOList);

        if (therapyProgramsDTOList.isEmpty()) {
            System.out.println("No program found with ID: " + proId);
        } else {
            System.out.println("Program found: " + therapyProgramsDTOList.get(0));
        }
        return therapyProgramsDTOList;
    }

    @Override
    public TherapyProgramsDTO getProgramByName(String name) throws SQLException, ClassNotFoundException {
        TherapyPrograms program = programsDAO.getByName(name);
        System.out.println("Fetched program by name Programs BO getProgramByName: " + program);
        return program != null ?
                new TherapyProgramsDTO(program.getProgramId(), program.getProgramName(), program.getDuration(), program.getFee()) :
                null;
    }

}
