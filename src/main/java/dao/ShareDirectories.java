/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.HibernateConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author lamanhhai
 */
public class ShareDirectories {
    public boolean save(ShareDirectories sd) {
        Transaction transaction = null;
        Session session = null;
        boolean isInsert = false;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            // start the transaction
            transaction = session.beginTransaction();
            
            // save user object
            session.save(sd);
            
            // commit the transaction
            transaction.commit();
            
            isInsert = true;
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
        
        return isInsert;
    }
    
    public boolean update(ShareDirectories sd) {
        Transaction transaction = null;
        Session session = null;
        boolean isUpdate = false;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            // start the transaction
            transaction = session.beginTransaction();
            
            // save or update user object
            session.saveOrUpdate(sd);
            
            // commit the transaction
            transaction.commit();
            
            isUpdate = true;
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
        
        return isUpdate;
    }
}
