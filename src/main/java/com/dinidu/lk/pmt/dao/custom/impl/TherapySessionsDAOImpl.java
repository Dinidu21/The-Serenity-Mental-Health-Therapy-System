package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.TherapySessionsDAO;
import com.dinidu.lk.pmt.entity.TherapySessions;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TherapySessionsDAOImpl implements TherapySessionsDAO {
    @Override
    public boolean save(TherapySessions dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(TherapySessions dto) throws SQLException, ClassNotFoundException {
        Session session = null;
        Transaction transaction = null;
        try {
            if (dto == null) {
                throw new SQLException("TherapySessions object is null.");
            }

            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            session.update(dto);
            transaction.commit();
            System.out.println("TherapySessions updated successfully: ID=" + dto.getId());
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new SQLException("Error updating TherapySessions in database.", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean insert(TherapySessions therapySessions) throws SQLException, ClassNotFoundException {
        org.hibernate.Session session = null;
        org.hibernate.Transaction transaction = null;
        try {

            if (therapySessions == null) {
                throw new SQLException("TherapySessions object is null.");
            }

            if (therapySessions.getPatient() == null || therapySessions.getTherapist() == null ||
                    therapySessions.getTherapyProgram() == null) {
                throw new SQLException("Patient, Therapist, or TherapyProgram cannot be null.");
            }

            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            session.persist(therapySessions);
            transaction.commit();
            System.out.println("TherapySessions inserted successfully: ID=" + therapySessions.getId());
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new SQLException("Error inserting TherapySessions into database.", e);
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
    public List<TherapySessions> fetchAll() throws SQLException, ClassNotFoundException {
        org.hibernate.Session session = null;
        try {
            session = FactoryConfiguration.getInstance().getSession();
            String hql = "FROM TherapySessions";
            Query<TherapySessions> query = session.createQuery(hql, TherapySessions.class);
            List<TherapySessions> sessionsList = query.getResultList();
            System.out.println("Fetched " + sessionsList.size() + " therapy sessions");
            return sessionsList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error fetching all TherapySessions from database.", e);
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
    public List<TherapySessions> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }

    @Override
    public boolean deleteSessions(long id) throws SQLException, ClassNotFoundException {
        if (id <= 0) {
            return false;
        }

        Transaction transaction = null;
        try {
            Session session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            TherapySessions sessionEntity = session.get(TherapySessions.class, id);
            if (sessionEntity == null) {
                return false;
            }
            session.delete(sessionEntity);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new SQLException("Error deleting therapy session with ID: " + id, e);
        }
    }

    @Override
    public TherapySessions getSessionIdByDesc(String selectedSession) throws SQLException, ClassNotFoundException {
        Session session = null;
        try {
            session = FactoryConfiguration.getInstance().getSession();
            String hql = "FROM TherapySessions WHERE description = :description";
            Query<TherapySessions> query = session.createQuery(hql, TherapySessions.class);
            query.setParameter("description", selectedSession);
            List<TherapySessions> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                return null;
            }
            return resultList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error fetching TherapySession by description: " + selectedSession, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public TherapySessions findById(Long sessionId) throws SQLException, ClassNotFoundException {
        Session session = null;
        try {
            session = FactoryConfiguration.getInstance().getSession();
            TherapySessions therapySession = session.get(TherapySessions.class, sessionId);
            if (therapySession == null) {
                throw new SQLException("TherapySession with ID " + sessionId + " not found.");
            }
            return therapySession;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error fetching TherapySession by ID: " + sessionId, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
