package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.TherapistDAO;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.entity.Therapists;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TherapistDAOImpl implements TherapistDAO {

    @Override
    public boolean save(Therapists dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Therapists dto) throws SQLException, ClassNotFoundException {
        Session session = null;
        Transaction transaction = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            session.merge(dto);
            transaction.commit();
            System.out.println("Therapist updated successfully: " + dto);
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating therapist: " + e.getMessage());
            CustomErrorAlert.showAlert("Error", "Update Failed Failed to update therapist details.");
            e.printStackTrace();
            return false;

        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean insert(Therapists therapists) throws SQLException, ClassNotFoundException {
        Transaction transaction = null;
        Session session = null;
        try {
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            session.persist(therapists);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            System.out.println("Error occurred while inserting therapist: " + e.getMessage());
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        Session session = null;
        Transaction transaction = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();

            Therapists therapist = session.get(Therapists.class, idOrName);

            if (therapist == null) {
                // Try to find by name if ID doesn't match
                Query<Therapists> query = session.createQuery(
                        "FROM Therapists WHERE fullName = :name", Therapists.class);
                query.setParameter("name", idOrName);
                therapist = query.uniqueResult();
            }

            if (therapist != null) {
                session.delete(therapist);
                transaction.commit();
                return true;
            } else {
                System.err.println("Therapist not found with ID or Name: " + idOrName);
                return false;
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting therapist: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Therapists> fetchAll() throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        List<Therapists> therapistsList;
        try {
            session.beginTransaction();
            therapistsList = session.createQuery("FROM Therapists", Therapists.class).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            throw new SQLException("Error fetching all therapists", e);
        } finally {
            session.close();
        }
        return therapistsList;
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<Therapists> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            // Using HQL to query the Therapists by name
            String hql = "FROM Therapists t WHERE t.fullName LIKE :searchQuery";
            Query<Therapists> query = session.createQuery(hql, Therapists.class);

            // Use LIKE for partial matching
            query.setParameter("searchQuery", "%" + searchQuery + "%");

            // Execute the query and return the result list
            return query.list();
        } catch (Exception e) {
            throw new SQLException("Error executing search query", e);
        } finally {
            session.close();
        }
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }

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
    public boolean insertProgram(TherapyProgramsDTO therapyProgramsDTO) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<TherapyProgramsDTO> getProgramByTherapistId(String s) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<TherapyProgramsDTO> searchProgramsByName(String query) throws SQLException, ClassNotFoundException {
        return List.of();
    }
}
