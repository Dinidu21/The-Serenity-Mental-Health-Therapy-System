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
        System.out.println("\nUpdating Patients with full name: " + currentIssue.getFullName());
        System.out.println("Updating Patients with email: " + currentIssue.getEmail());
        System.out.println("Updating Patients with address: " + currentIssue.getAddress());
        System.out.println("Updating Patients with phone number: " + currentIssue.getPhoneNumber());
        System.out.println("Updating Patients with medical history: " + currentIssue.getMedicalHistory());
        System.out.println("Updating Patients with registration date: " + currentIssue.getRegistrationDate());

        Patients patients = new Patients();
        patients.setId(currentIssue.getId());
        patients.setFullName(currentIssue.getFullName());
        patients.setEmail(currentIssue.getEmail());
        patients.setAddress(currentIssue.getAddress());
        patients.setPhoneNumber(currentIssue.getPhoneNumber());
        patients.setMedicalHistory(currentIssue.getMedicalHistory());
        patients.setRegistrationDate(currentIssue.getRegistrationDate());

        boolean isUpdated = patientsDAO.update(patients);
        if (isUpdated) {
            System.out.println("Patients updated successfully.");
            return true;
        } else {
            System.out.println("Failed to update Patients.");
            CustomErrorAlert.showAlert("ERROR", "Failed to update Patients.");
            return false;
        }
    }

    @Override
    public boolean deletePatient(Long id) throws SQLException, ClassNotFoundException {
        System.out.println("Deleting Patients with ID: " + id);
        boolean isDeleted = patientsDAO.deletePatient(id);
        if (isDeleted) {
            System.out.println("Patients deleted successfully.");
            return true;
        } else {
            System.out.println("Failed to delete Patients.");
            CustomErrorAlert.showAlert("ERROR", "Failed to delete Patients.");
            return false;
        }
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

    @Override
    public List<PatientsDTO> searchPatientsByName(String query) throws SQLException, ClassNotFoundException {
        List<Patients> patientsList = patientsDAO.searchByName(query);
        List<PatientsDTO> patientsDTOList = new ArrayList<>();
        for (Patients patients : patientsList) {
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
            System.out.println("Patient found: " + patientsDTO.getFullName());
        }
        return patientsDTOList;
    }

    @Override
    public PatientsDTO getPatientByName(String patientValue) throws SQLException, ClassNotFoundException {
        List<Patients> patientsList = patientsDAO.searchByName(patientValue);
        if (patientsList.isEmpty()) {
            System.out.println("No Patients found with the name: " + patientValue);
            return null;
        } else {
            Patients patients = patientsList.get(0);
            return new PatientsDTO(
                    patients.getId(),
                    patients.getFullName(),
                    patients.getEmail(),
                    patients.getAddress(),
                    patients.getPhoneNumber(),
                    patients.getMedicalHistory(),
                    patients.getRegistrationDate()
            );
        }
    }

    @Override
    public String getPatientNameById(long patientId) throws SQLException, ClassNotFoundException {
        Patients patients = patientsDAO.getPatientById(patientId);
        if (patients != null) {
            return patients.getFullName();
        } else {
            System.out.println("No Patients found with the ID: " + patientId);
            return null;
        }
    }
}
