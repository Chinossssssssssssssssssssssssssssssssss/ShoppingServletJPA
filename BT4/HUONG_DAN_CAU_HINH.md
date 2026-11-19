# Cấu hình sau khi đè file

1. Eclipse: chuột phải project -> Maven -> Update Project (Alt+F5), tick "Force Update of Snapshots/Releases".
2. Cloudinary (Bài 3) - KHÔNG hard-code khoá trong code. Đặt biến môi trường cho Tomcat:
   Eclipse -> Servers -> double-click Tomcat -> Open launch configuration -> tab Environment -> New:
     CLOUDINARY_CLOUD_NAME = <cloud name>
     CLOUDINARY_API_KEY    = <api key>
     CLOUDINARY_API_SECRET = <api secret>
   (hoặc 1 biến: CLOUDINARY_URL = cloudinary://API_KEY:API_SECRET@CLOUD_NAME)
   Gửi OTP vẫn dùng MAIL_USER / MAIL_PASS như cũ.
3. Nếu Maven không tải được sitemesh 3.3.0 (hoặc dùng Tomcat 10.1) -> đổi version trong pom.xml thành 3.2.1.
