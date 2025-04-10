package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.ProgramsDAO;
import com.dinidu.lk.pmt.entity.TherapyPrograms;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgramsDAOImpl implements ProgramsDAO {
    @Override
    public boolean save(TherapyPrograms dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(TherapyPrograms dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean insert(TherapyPrograms therapyPrograms) throws SQLException, ClassNotFoundException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            session.persist(therapyPrograms);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            CustomErrorAlert.showAlert("Error", "Failed to insert program into the database.");
            throw new SQLException("Error inserting program into the database.");
        } finally {
            if (session != null) {
                session.close();  // Close session after use
            }
        }
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<TherapyPrograms> fetchAll() throws SQLException, ClassNotFoundException {
        Session session = null;
        try {
            session = FactoryConfiguration.getInstance().getSession();
            // Create an HQL query to fetch all TherapyPrograms entities
            String hql = "FROM TherapyPrograms";
            Query<TherapyPrograms> query = session.createQuery(hql, TherapyPrograms.class);
            // Execute the query and get the list of results
            List<TherapyPrograms> therapyProgramsList = query.getResultList();
            System.out.println("Fetched " + therapyProgramsList.size() + " therapy programs");
            return therapyProgramsList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error fetching all TherapyPrograms from database.", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<TherapyPrograms> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        Session session = null;
        List<TherapyPrograms> programs = new ArrayList<>();

        try {
            session = FactoryConfiguration.getInstance().getSession();
            session.beginTransaction();

            // HQL query (case-insensitive search using lower() function)
            String hql = "FROM TherapyPrograms WHERE lower(programName) LIKE :name";
            Query<TherapyPrograms> query = session.createQuery(hql, TherapyPrograms.class);
            query.setParameter("name", "%" + searchQuery.toLowerCase() + "%");

            programs = query.list();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return programs;
    }


    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }

    @Override
    public long getLastProgramID() throws SQLException, ClassNotFoundException {
        Session session = null;
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

    @Override
    public TherapyPrograms getProgramById(long proId) throws SQLException, ClassNotFoundException {
        Session session = null;
        TherapyPrograms program = null;

        try {
            session = FactoryConfiguration.getInstance().getSession();
            session.beginTransaction();

            String hql = "FROM TherapyPrograms WHERE programId = :id";
            Query<TherapyPrograms> query = session.createQuery(hql, TherapyPrograms.class);
            query.setParameter("id", proId);

            program = query.uniqueResult();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return program;
    }
}
