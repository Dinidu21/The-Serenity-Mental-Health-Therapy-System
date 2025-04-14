package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.ProgramsDAO;
import com.dinidu.lk.pmt.entity.TherapyPrograms;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import javafx.beans.property.StringProperty;
import org.hibernate.HibernateException;
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
        org.hibernate.Session session = null;
        org.hibernate.Transaction transaction = null;
        try {
            if (dto == null || dto.getProgramId() == null) {
                CustomErrorAlert.showAlert("Error", "TherapyPrograms object or its ID is null.");
                return false;
            }
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            session.merge(dto);
            transaction.commit();
            System.out.println("TherapyPrograms updated successfully: " + dto.getProgramName());
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new SQLException("Error updating TherapyPrograms in database.", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
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
                session.close();
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

    @Override
    public TherapyPrograms getById(Long programId) throws SQLException, ClassNotFoundException {
        org.hibernate.Session session = null;
        try {
            // Obtain the Hibernate session
            session = FactoryConfiguration.getInstance().getSession();

            // Fetch the entity by ID
            TherapyPrograms entity = session.get(TherapyPrograms.class, programId);

            // Optional: Log result
            System.out.println("Fetched TherapyPrograms: " + entity);
            return entity;

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error fetching TherapyPrograms with ID " + programId, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean deleteProgram(long id) throws SQLException, ClassNotFoundException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            TherapyPrograms entity = session.get(TherapyPrograms.class, id);
            if (entity == null) {
                System.out.println("TherapyPrograms with ID " + id + " not found.");
                return false;
            }
            session.remove(entity);
            transaction.commit();
            System.out.println("TherapyPrograms with ID " + id + " deleted successfully.");
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new SQLException("Error deleting TherapyPrograms with ID " + id, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public TherapyPrograms getByName(String name) throws SQLException, ClassNotFoundException {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        try {
            Session session = FactoryConfiguration.getInstance().getSession();
            Query<TherapyPrograms> query = session.createQuery(
                    "FROM TherapyPrograms WHERE lower(programName) LIKE lower(:name)",
                    TherapyPrograms.class
            );
            query.setParameter("name", name);
            return query.uniqueResult();
        } catch (HibernateException e) {
            throw new SQLException("Error fetching therapy program by name: " + name, e);
        }
    }
}
