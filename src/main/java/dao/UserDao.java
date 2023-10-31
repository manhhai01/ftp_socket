/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.HibernateConfig;
import java.util.List;
import model.Directory;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author lamanhhai
 */
public class UserDao {

    public User getUserById(int id) {
        Transaction transaction = null;
        User user = null;
        Session session = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();

            // start the transaction
            transaction = session.beginTransaction();

            // get user object by id
            user = session.get(User.class, id);

            // commit the transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

        return user;
    }

<<<<<<< HEAD
=======
    public User getUserByUsername(String username) {
        Transaction transaction = null;
        User user = null;
        Session session = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();

            // start the transaction
            transaction = session.beginTransaction();

            // get user object by id
            String hql = "FROM User WHERE username = :username";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);
            user = query.getSingleResult();

            // commit the transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

        return user;
    }

>>>>>>> 228b47d64764da7d950f9378a685c4538eeee7fe
    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        Transaction transaction = null;
        List<User> users = null;
        Session session = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();

            // start the transaction
            transaction = session.beginTransaction();

            // get all users
            users = session.createQuery("from User").list();

            // commit the transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

        return users;
    }

    public boolean save(User user) {
        Transaction transaction = null;
        Session session = null;
        boolean isInsert = false;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            // start the transaction
            transaction = session.beginTransaction();

            // save user object
            session.save(user);

            // commit the transaction
            transaction.commit();

            isInsert = true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

        return isInsert;
    }

    public boolean update(User user) {
        Transaction transaction = null;
        Session session = null;
        boolean isUpdate = false;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            // start the transaction
            transaction = session.beginTransaction();

            // save or update user object
            session.saveOrUpdate(user);

            // commit the transaction
            transaction.commit();

            isUpdate = true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

        return isUpdate;
    }

    public boolean remove(int id) {
        Transaction transaction = null;
        User user = null;
        Session session = null;
        boolean isDelete = false;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            // start the transaction
            transaction = session.beginTransaction();

            user = session.get(User.class, id);

            // save or update user object
            session.delete(user);

            // commit the transaction
            transaction.commit();

            isDelete = true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

        return isDelete;
    }

    public User getUserByUserName(String username) {
        Transaction transaction = null;
        User user = null;
        Session session = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();

            // Bắt đầu giao dịch
            transaction = session.beginTransaction();

            // Sử dụng HQL để truy vấn người dùng bằng username
            String hql = "FROM User u WHERE u.username = :username";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);

            user = query.uniqueResult(); // Trả về kết quả duy nhất hoặc null nếu không tìm thấy

            // Kết thúc giao dịch
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

        return user;
    }

}
