package com.dinidu.lk.pmt.config;

import com.dinidu.lk.pmt.entity.*;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;



@Getter
public class FactoryConfiguration {
    private static FactoryConfiguration factoryConfigurationInstance;
    private final SessionFactory sessionFactory;

    private FactoryConfiguration() {
        try {
            Configuration cfg = new Configuration().configure();
            cfg.addAnnotatedClass(Users.class);
            cfg.addAnnotatedClass(Roles.class);
            cfg.addAnnotatedClass(Permissions.class);

            sessionFactory = cfg.buildSessionFactory();
            System.out.println("SessionFactory created successfully");
        } catch (Exception e) {
            System.err.println("Failed to create SessionFactory: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Hibernate initialization failed", e);
        }
    }

    public static FactoryConfiguration getInstance() {
        if (factoryConfigurationInstance == null) {
            synchronized (FactoryConfiguration.class) {
                if (factoryConfigurationInstance == null) {
                    factoryConfigurationInstance = new FactoryConfiguration();
                }
            }
        }
        return factoryConfigurationInstance;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }
}