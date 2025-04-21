package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.config.FactoryConfiguration;
import com.dinidu.lk.pmt.dao.custom.PaymentsDAO;
import com.dinidu.lk.pmt.entity.Payments;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PaymentsDAOImpl implements PaymentsDAO {
    @Override
    public boolean save(Payments dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Payments dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean insert(Payments payments) throws SQLException, ClassNotFoundException {
        Transaction transaction = null;
        try {
            Session session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            session.persist(payments);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Payments> fetchAll() throws SQLException, ClassNotFoundException {
        Transaction transaction = null;
        try {
            Session session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            List<Payments> paymentsList = session.createQuery("FROM Payments", Payments.class).list();
            transaction.commit();
            return paymentsList;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<Payments> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }

    @Override
    public Payments findById(Long id) throws SQLException, ClassNotFoundException {
        Transaction transaction = null;
        try {
            Session session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            Payments payment = session.get(Payments.class, id);
            transaction.commit();
            return payment;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteID(Long id) throws SQLException, ClassNotFoundException {
        Transaction transaction = null;
        try {
            Session session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            Payments payment = session.get(Payments.class, id);
            if (payment != null) {
                session.delete(payment);
                transaction.commit();
                return true;
            } else {
                return false; // Payment not found
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Payments> findAll() throws SQLException, ClassNotFoundException {
        Transaction transaction = null;
        try {
            Session session = FactoryConfiguration.getInstance().getSession();
            transaction = session.beginTransaction();
            List<Payments> paymentsList = session.createQuery("FROM Payments", Payments.class).list();
            transaction.commit();
            return paymentsList;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
