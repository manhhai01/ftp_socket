/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.HibernateConfig;
import model.File;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author lamanhhai
 */
public class FileDao {
    public boolean save(File file) {
        Transaction transaction = null;
        Session session = null;
        boolean isInsert = false;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            // start the transaction
            transaction = session.beginTransaction();
            
            // save user object
            session.save(file);
            
            // commit the transaction d
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
}
