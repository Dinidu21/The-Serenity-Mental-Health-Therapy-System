package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.ProgramsDAO;
import com.dinidu.lk.pmt.entity.TherapyPrograms;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
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
        return List.of();
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<TherapyPrograms> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
