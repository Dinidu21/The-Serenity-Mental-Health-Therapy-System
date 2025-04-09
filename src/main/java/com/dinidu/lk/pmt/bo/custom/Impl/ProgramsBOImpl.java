package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.ProgramsBO;
import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.ProgramsDAO;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.entity.TherapyPrograms;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;
import org.hibernate.query.Query;

import java.sql.SQLException;
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
        return List.of();
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
        return List.of();
    }

    @Override
    public long getLastProgramID() throws SQLException, ClassNotFoundException {
        org.hibernate.Session session = null;
        try {
            // Obtain the Hibernate session
            session = FactoryConfiguration.getInstance().getSession();

            // Query to fetch the latest Program ID
            String hql = "SELECT p.programId FROM TherapyPrograms p ORDER BY p.id DESC";
            Query query = session.createQuery(hql);
            query.setMaxResults(1); // Get only the latest record

            // Execute the query and get the result
            Object lastProgramID = query.uniqueResult();

            // Handle null case and cast to long
            if (lastProgramID == null) {
                System.out.println("No Program ID found, returning 0");
                return 0L; // Return 0 if no records exist (or throw an exception if preferred)
            }

            // Assuming programId is Long in the entity, safely convert to primitive long
            System.out.println("Last Program ID is: " + lastProgramID);
            return ((Long) lastProgramID).longValue(); // Safely unbox Long to long

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error fetching last Program ID from database.", e);
        } finally {
            if (session != null) {
                session.close(); // Close session after use
            }
        }
    }
}
