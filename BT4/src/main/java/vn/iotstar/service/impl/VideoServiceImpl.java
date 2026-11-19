package vn.iotstar.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import vn.iotstar.dao.IVideoDao;
import vn.iotstar.dao.impl.VideoDaoImpl;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;

public class VideoServiceImpl implements IVideoService {
    private final IVideoDao videoDao = new VideoDaoImpl();

    @Override
    public void insert(Video video) {
        if (video.getVideoId() == null || video.getVideoId().isEmpty()) {
            video.setVideoId(UUID.randomUUID().toString());
        }
        if (video.getCreatedDate() == null) {
            video.setCreatedDate(new Date());
        }
        videoDao.insert(video);
    }

    @Override
    public void update(Video video) { videoDao.update(video); }

    @Override
    public void delete(String videoId) throws Exception { videoDao.delete(videoId); }

    @Override
    public Video findById(String videoId) { return videoDao.findById(videoId); }

    @Override
    public List<Video> findLatest(int limit) { return videoDao.findLatest(limit); }

    @Override
    public List<Video> findByUser(int userId, int page, int pageSize) {
        return videoDao.findByUser(userId, page, pageSize);
    }

    @Override
    public long countByUser(int userId) { return videoDao.countByUser(userId); }

    @Override
    public void increaseViews(String videoId) { videoDao.increaseViews(videoId); }
}
