package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.PatientsDAO;
import com.dinidu.lk.pmt.entity.Patients;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PatientsDAOImpl implements PatientsDAO {
    @Override
    public boolean save(Patients dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Patients dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean insert(Patients patients) throws SQLException, ClassNotFoundException {
        Session session = null;
        Transaction transaction = null;
        try {
            if (patients == null) {
                throw new SQLException("Patients object is null.");
            }

            session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            session.persist(patients);
            transaction.commit();
            System.out.println("Patients inserted successfully: " + patients.getFullName());
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new SQLException("Error inserting Patients into database.", e);
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
    public List<Patients> fetchAll() throws SQLException, ClassNotFoundException {
        Session session = null;
        try {
            session = FactoryConfiguration.getInstance().getSession();
            return session.createQuery("FROM Patients", Patients.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error fetching all patients.", e);
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
    public List<Patients> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
