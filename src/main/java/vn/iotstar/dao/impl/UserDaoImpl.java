package vn.iotstar.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.UserDao;
import vn.iotstar.entity.User;

public class UserDaoImpl implements UserDao {

    @Override
    public User get(String username) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.userName = :v", User.class)
                     .setParameter("v", username)
                     .getResultStream()
                     .findFirst()
                     .orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public void insert(User user) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private boolean exists(String field, String value) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            Long n = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u." + field + " = :v", Long.class)
                    .setParameter("v", value)
                    .getSingleResult();
            return n > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean checkExistEmail(String email) { return exists("email", email); }

    @Override
    public boolean checkExistUsername(String username) { return exists("userName", username); }

    @Override
    public boolean checkExistPhone(String phone) { return exists("phone", phone); }
}