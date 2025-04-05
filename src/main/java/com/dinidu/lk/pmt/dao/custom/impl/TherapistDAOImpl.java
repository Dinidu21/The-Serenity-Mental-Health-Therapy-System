package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.TherapistDAO;
import com.dinidu.lk.pmt.entity.Therapists;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
        return false;
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
        return false;
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
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
