package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.TherapySessionsBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.PatientsDAO;
import com.dinidu.lk.pmt.dao.custom.ProgramsDAO;
import com.dinidu.lk.pmt.dao.custom.TherapistDAO;
import com.dinidu.lk.pmt.dao.custom.TherapySessionsDAO;
import com.dinidu.lk.pmt.dto.TherapySessionsDTO;
import com.dinidu.lk.pmt.entity.Patients;
import com.dinidu.lk.pmt.entity.Therapists;
import com.dinidu.lk.pmt.entity.TherapyPrograms;
import com.dinidu.lk.pmt.entity.TherapySessions;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TherapySessionsBOImpl implements TherapySessionsBO {

    TherapySessionsDAO therapySessionsDAO =
            (TherapySessionsDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.SESSIONS);
    PatientsDAO patientsDAO =
            (PatientsDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.Patients);
    TherapistDAO therapistsDAO =
            (TherapistDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.Therapist);
    ProgramsDAO programsDAO =
            (ProgramsDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.PROGRAMS);

    @Override
    public boolean insert(TherapySessionsDTO sessionDTO) throws SQLException, ClassNotFoundException {
        if (sessionDTO == null) {
            throw new SQLException("TherapySessionsDTO cannot be null.");
        }
        if (sessionDTO.getTherapyProgramId() == 0L || sessionDTO.getTherapyProgramId() == 0) {
            throw new SQLException("Therapy program ID must be a valid non-zero value.");
        }
        TherapySessions entity = new TherapySessions();
        entity.setId(null);
        System.err.println("Inserting TherapySessions with IDs: " +
                "patientId=" + sessionDTO.getPatientId() +
                ", therapistId=" + sessionDTO.getTherapistId() +
                ", therapyProgramId=" + sessionDTO.getTherapyProgramId());
        Patients patient = patientsDAO.getById(sessionDTO.getPatientId());
        Therapists therapist = therapistsDAO.getById(sessionDTO.getTherapistId());
        TherapyPrograms program = programsDAO.getById(sessionDTO.getTherapyProgramId());
        if (patient == null) {
            throw new SQLException("Invalid patient ID: " + sessionDTO.getPatientId());
        }
        if (therapist == null) {
            throw new SQLException("Invalid therapist ID: " + sessionDTO.getTherapistId());
        }
        if (program == null) {
            throw new SQLException("Invalid therapy program ID: " + sessionDTO.getTherapyProgramId());
        }
        entity.setPatient(patient);
        entity.setTherapist(therapist);
        entity.setTherapyProgram(program);
        entity.setSessionDate(sessionDTO.getSessionDate());
        entity.setSessionMadeDate(sessionDTO.getSessionMadeDate() != null ? sessionDTO.getSessionMadeDate() : LocalDate.now());
        entity.setSessionTime(sessionDTO.getSessionTime());
        entity.setDescription(sessionDTO.getDescription());
        entity.setStatus(sessionDTO.getStatus());
        return therapySessionsDAO.insert(entity);
    }

    @Override
    public List<TherapySessionsDTO> getAllSessions() throws SQLException, ClassNotFoundException {
        List<TherapySessions> therapySessions = therapySessionsDAO.fetchAll();

        if (therapySessions == null || therapySessions.isEmpty()) {
            return Collections.emptyList();
        }

        List<TherapySessionsDTO> dtoList = new ArrayList<>();

        for (TherapySessions session : therapySessions) {
            try {
                Long id = session.getId();
                String therapistId = session.getTherapist() != null ? session.getTherapist().getId() : null;
                Long patientId = session.getPatient() != null ? session.getPatient().getId() : null;
                Long programId = session.getTherapyProgram() != null ? session.getTherapyProgram().getProgramId() : null;

                TherapySessionsDTO dto = new TherapySessionsDTO(
                        id,
                        therapistId,
                        patientId,
                        programId,
                        session.getSessionDate(),
                        session.getSessionTime(),
                        session.getSessionMadeDate(),
                        session.getDescription(),
                        session.getStatus()
                );

                dtoList.add(dto);
                System.out.println("Mapped TherapySession (ID=" + id + ") to DTO: " + dto);
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to map TherapySession (ID=" + session.getId() + "): " + e.getMessage());
            }
        }

        return dtoList;
    }

    @Override
    public boolean updateChecklist(TherapySessionsDTO sessionsDTO) throws SQLException, ClassNotFoundException {

        if (sessionsDTO == null) {
            throw new SQLException("TherapySessionsDTO cannot be null.");
        }
        if (sessionsDTO.getId() == 0L){
            throw new SQLException("TherapySessionsDTO ID cannot be null.");
        }
        TherapySessions entity = new TherapySessions();
        entity.setId(sessionsDTO.getId());
        entity.setDescription(sessionsDTO.getDescription());
        entity.setSessionDate(sessionsDTO.getSessionDate());
        entity.setSessionMadeDate(sessionsDTO.getSessionMadeDate());
        entity.setSessionTime(sessionsDTO.getSessionTime());
        entity.setStatus(sessionsDTO.getStatus());
        entity.setPatient(patientsDAO.getById(sessionsDTO.getPatientId()));
        entity.setTherapyProgram(programsDAO.getById(sessionsDTO.getTherapyProgramId()));
        entity.setTherapist(therapistsDAO.getById(sessionsDTO.getTherapistId()));
        return therapySessionsDAO.update(entity);
    }

    @Override
    public boolean deleteChecklist(long id) throws SQLException, ClassNotFoundException {
        if (id <= 0) {
            throw new SQLException("Invalid therapy session ID: " + id);
        }
        return therapySessionsDAO.deleteSessions(id);
    }
}