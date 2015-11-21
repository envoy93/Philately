package com.philately.model;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

/**
 * Created by kirill on 25.10.2015.
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static Session session = null;

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession(){
        if (session == null) {
            session = HibernateUtil.getSessionFactory().openSession();
        }

        return session;
    }

    public static void shutdown() {
        // Close caches and connection pools
        session.close();
        getSessionFactory().close();
    }

}
