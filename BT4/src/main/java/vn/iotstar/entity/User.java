package vn.iotstar.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "\"User\"")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String userName;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "password")
    private String passWord;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "roleid")
    private int roleid;

    @Column(name = "phone")
    private String phone;

    @Column(name = "createddate")
    private Date createdDate;

    // null (tài khoản cũ trước khi có OTP) được coi là đã kích hoạt
    @Column(name = "active")
    private Boolean active;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otpexpiry")
    private LocalDateTime otpExpiry;

    // Quan hệ 1-n: 1 user có nhiều video
    @OneToMany(mappedBy = "user")
    private List<Video> videos = new ArrayList<>();

    public User() {}

    public User(String email, String userName, String fullName, String passWord,
                String avatar, int roleid, String phone, Date createdDate) {
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.passWord = passWord;
        this.avatar = avatar;
        this.roleid = roleid;
        this.phone = phone;
        this.createdDate = createdDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassWord() { return passWord; }
    public void setPassWord(String passWord) { this.passWord = passWord; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public int getRoleid() { return roleid; }
    public void setRoleid(int roleid) { this.roleid = roleid; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public boolean isActivated() { return active == null || active; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public LocalDateTime getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }

    public List<Video> getVideos() { return videos; }
    public void setVideos(List<Video> videos) { this.videos = videos; }
}