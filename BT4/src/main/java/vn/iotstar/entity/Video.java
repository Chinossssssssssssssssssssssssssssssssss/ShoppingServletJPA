package vn.iotstar.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "videos")
@NamedQuery(name = "Video.findAll", query = "SELECT v FROM Video v")
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "VideoId")
    private String videoId;

    @Column(name = "Active")
    private int active;

    @Column(name = "Description", columnDefinition = "nvarchar(500) null")
    private String description;

    @Column(name = "Poster", columnDefinition = "nvarchar(500) null")
    private String poster;

    // Tên file video đã upload (lưu trong thư mục Constant.DIR)
    @Column(name = "VideoUrl", columnDefinition = "nvarchar(500) null")
    private String videoUrl;

    @Column(name = "Title", columnDefinition = "nvarchar(500) null")
    private String title;

    @Column(name = "Views")
    private int views;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreatedDate")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

    // Quan hệ n-1 với User (1 user có nhiều video)
    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    public Video() {
    }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public int getActive() { return active; }
    public void setActive(int active) { this.active = active; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
