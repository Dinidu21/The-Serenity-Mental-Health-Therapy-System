package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.TherapySessionsDAO;
import com.dinidu.lk.pmt.entity.TherapySessions;
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
        return false;
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
}
