package vn.iotstar.service.impl;

import java.time.LocalDateTime;

import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import vn.iotstar.util.Constant;
import vn.iotstar.util.EmailUtil;
import vn.iotstar.util.OtpUtil;
import vn.iotstar.util.PasswordUtil;

public class UserServiceImpl implements UserService {
    UserDao userDao = new UserDaoImpl();

    @Override
    public User login(String username, String password) {
        User user = this.get(username);
        if (user == null || password == null) {
            return null;
        }
        String stored = user.getPassWord();
        if (PasswordUtil.isHashed(stored)) {
            return PasswordUtil.matches(password, stored) ? user : null;
        }
        // Tài khoản cũ còn lưu mật khẩu thô: cho đăng nhập 1 lần rồi nâng cấp sang hash
        if (password.equals(stored)) {
            user.setPassWord(PasswordUtil.hash(password));
            userDao.update(user);
            return user;
        }
        return null;
    }

    @Override
    public User get(String username) {
        return userDao.get(username);
    }

    @Override
    public User getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Override
    public void insert(User user) {
        userDao.insert(user);
    }

    @Override
    public boolean register(String username, String password, String email, String fullname, String phone) {
        if (userDao.checkExistUsername(username)) {
            return false;
        }
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        User user = new User(email, username, fullname, PasswordUtil.hash(password), null, 5, phone, date);
        user.setActive(false); // chờ kích hoạt bằng OTP
        userDao.insert(user);
        return true;
    }

    @Override
    public void sendOtp(String email) {
        User user = userDao.getByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email không tồn tại");
        }
        String otp = OtpUtil.generate();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(Constant.OTP_EXPIRE_MINUTES));
        userDao.update(user);
        try {
            EmailUtil.send(email, "Mã xác nhận OTP",
                    "Xin chào " + user.getFullName() + ",\n\n"
                  + "Mã OTP của bạn là: " + otp + "\n"
                  + "Mã có hiệu lực trong " + Constant.OTP_EXPIRE_MINUTES + " phút. "
                  + "Tuyệt đối không chia sẻ mã này cho người khác.");
        } catch (Exception e) {
            throw new IllegalStateException("Không gửi được email: " + e.getMessage(), e);
        }
    }

    private boolean otpValid(User user, String otp) {
        return user != null
                && otp != null
                && user.getOtp() != null
                && user.getOtp().equals(otp.trim())
                && user.getOtpExpiry() != null
                && LocalDateTime.now().isBefore(user.getOtpExpiry());
    }

    @Override
    public boolean activateAccount(String email, String otp) {
        User user = userDao.getByEmail(email);
        if (!otpValid(user, otp)) {
            return false;
        }
        user.setActive(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userDao.update(user);
        return true;
    }

    @Override
    public boolean resetPassword(String email, String otp, String newPassword) {
        User user = userDao.getByEmail(email);
        if (!otpValid(user, otp) || !user.isActivated()) {
            return false;
        }
        user.setPassWord(PasswordUtil.hash(newPassword));
        user.setOtp(null);          // OTP dùng 1 lần
        user.setOtpExpiry(null);
        userDao.update(user);
        return true;
    }

    @Override
    public User getById(int id) {
        return userDao.findById(id);
    }

    @Override
    public User updateProfile(int id, String fullName, String phone, String email, String avatar) {
        User user = userDao.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setEmail(email);
        if (avatar != null && !avatar.isBlank()) {
            user.setAvatar(avatar);
        }
        userDao.update(user);
        return userDao.findById(id);
    }

    @Override
    public boolean checkExistEmail(String email) { return userDao.checkExistEmail(email); }

    @Override
    public boolean checkExistUsername(String username) { return userDao.checkExistUsername(username); }

    @Override
    public boolean checkExistPhone(String phone) { return userDao.checkExistPhone(phone); }
}
