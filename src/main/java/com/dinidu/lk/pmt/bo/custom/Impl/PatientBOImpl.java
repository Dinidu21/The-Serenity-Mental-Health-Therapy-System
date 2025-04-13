package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.PatientsDAO;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.entity.Patients;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientBOImpl implements PatientBO {
    PatientsDAO patientsDAO =
            (PatientsDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.Patients);

    @Override
    public String getTherapistsIdByName(String value) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public Long getProgramIdByName(String selectedTaskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }

    @Override
    public boolean updatePatient(PatientsDTO currentIssue) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean deletePatient(Long id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String getProgramNameById(long taskId) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public String getTherapistNameById(String projectId) throws SQLException, ClassNotFoundException {
        return "";
    }

    @Override
    public List<String> getActivePatients() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<String> getProgramsByTherapist(String selectedProject) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<String> getActiveTherapistsNames() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<PatientsDTO> getAllPatients() throws SQLException, ClassNotFoundException {
        List<Patients> allPatients = patientsDAO.fetchAll();
        List<PatientsDTO> patientsDTOList = new ArrayList<>();
        for (Patients patients : allPatients) {
            PatientsDTO patientsDTO = new PatientsDTO(
                    patients.getId(),
                    patients.getFullName(),
                    patients.getEmail(),
                    patients.getAddress(),
                    patients.getPhoneNumber(),
                    patients.getMedicalHistory(),
                    patients.getRegistrationDate()
            );
            patientsDTOList.add(patientsDTO);
        }
        return patientsDTOList;
    }

    @Override
    public boolean createPatient(PatientsDTO patientsDTO) throws SQLException, ClassNotFoundException {
        System.out.println("\nCreating Patients with full name: " + patientsDTO.getFullName());
        System.out.println("Creating Patients with email: " + patientsDTO.getEmail());
        System.out.println("Creating Patients with address: " + patientsDTO.getAddress());
        System.out.println("Creating Patients with phone number: " + patientsDTO.getPhoneNumber());
        System.out.println("Creating Patients with medical history: " + patientsDTO.getMedicalHistory());
        System.out.println("Creating Patients with registration date: " + patientsDTO.getRegistrationDate());

        Patients patients = new Patients();
        patients.setFullName(patientsDTO.getFullName());
        patients.setEmail(patientsDTO.getEmail());
        patients.setAddress(patientsDTO.getAddress());
        patients.setPhoneNumber(patientsDTO.getPhoneNumber());
        patients.setMedicalHistory(patientsDTO.getMedicalHistory());
        patients.setRegistrationDate(patientsDTO.getRegistrationDate());
        boolean isCreated = patientsDAO.insert(patients);
        if (isCreated) {
            System.out.println("Patients created successfully.");
            return true;
        } else {
            System.out.println("Failed to create Patients.");
            CustomErrorAlert.showAlert("ERROR", "Failed to create Patients.");
            return false;
        }
    }

    @Override
    public Long getUserIdByName(String memberName) throws SQLException, ClassNotFoundException {
        return 0L;
    }

    @Override
    public List<String> getProgramByTherapist(String selectedProject) throws SQLException, ClassNotFoundException {
        return List.of();
    }
}
