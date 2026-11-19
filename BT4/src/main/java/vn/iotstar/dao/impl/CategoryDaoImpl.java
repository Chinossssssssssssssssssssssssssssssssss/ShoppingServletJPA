package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.entity.Category;

public class CategoryDaoImpl implements ICategoryDao {

    @Override
    public void insert(Category category) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(category);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Category category) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(category);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int cateid) throws Exception {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = em.find(Category.class, cateid);
            if (category != null) {
                em.remove(category);
            } else {
                throw new Exception("Không tìm thấy");
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Category findById(int cateid) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.find(Category.class, cateid);
        } finally {
            em.close();
        }
    }

    @Override
    public Category findByCategoryname(String name) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT c FROM Category c WHERE c.categoryname = :catename";
            TypedQuery<Category> query = em.createQuery(jpql, Category.class);
            query.setParameter("catename", name);
            List<Category> list = query.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> findAll() {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createNamedQuery("Category.findAll", Category.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> searchByName(String catname) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT c FROM Category c WHERE c.categoryname LIKE :catname";
            TypedQuery<Category> query = em.createQuery(jpql, Category.class);
            query.setParameter("catname", "%" + catname + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            TypedQuery<Category> query = em.createNamedQuery("Category.findAll", Category.class);
            query.setFirstResult(page * pagesize);
            query.setMaxResults(pagesize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int count() {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            Long n = em.createQuery("SELECT COUNT(c) FROM Category c", Long.class).getSingleResult();
            return n.intValue();
        } finally {
            em.close();
        }
    }
}