package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.IVideoDao;
import vn.iotstar.entity.Video;

public class VideoDaoImpl implements IVideoDao {

    @Override
    public void insert(Video video) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(video);
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
    public void update(Video video) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(video);
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
    public void delete(String videoId) throws Exception {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Video video = em.find(Video.class, videoId);
            if (video == null) {
                throw new Exception("Không tìm thấy video");
            }
            em.remove(video);
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
    public Video findById(String videoId) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.find(Video.class, videoId);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video> findLatest(int limit) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT v FROM Video v WHERE v.active = 1 ORDER BY v.createdDate DESC", Video.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video> findByUser(int userId, int page, int pageSize) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT v FROM Video v WHERE v.user.id = :uid ORDER BY v.createdDate DESC", Video.class)
                    .setParameter("uid", userId)
                    .setFirstResult(page * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByUser(int userId) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(v) FROM Video v WHERE v.user.id = :uid", Long.class)
                    .setParameter("uid", userId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void increaseViews(String videoId) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("UPDATE Video v SET v.views = v.views + 1 WHERE v.videoId = :id")
                    .setParameter("id", videoId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
