package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.entity.Video;

public interface IVideoDao {
    void insert(Video video);
    void update(Video video);
    void delete(String videoId) throws Exception;
    Video findById(String videoId);

    /** N video active mới nhất (trang chủ). */
    List<Video> findLatest(int limit);

    /** Video của 1 user, phân trang. page bắt đầu từ 0. */
    List<Video> findByUser(int userId, int page, int pageSize);
    long countByUser(int userId);

    void increaseViews(String videoId);
}
