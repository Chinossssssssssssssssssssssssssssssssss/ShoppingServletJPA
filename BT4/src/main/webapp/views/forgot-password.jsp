<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Quên mật khẩu</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-6 col-lg-5">
    <div class="card shadow-sm">
      <div class="card-body p-4">
        <h3 class="text-center mb-3">Quên mật khẩu</h3>
        <p class="text-muted">Nhập email đã đăng ký, hệ thống sẽ gửi mã OTP để đặt lại mật khẩu.</p>
        <c:if test="${alert != null}">
          <div class="alert alert-danger">${alert}
            <c:if test="${needActivate}"><br><a href="${ctx}/verify-otp">Kích hoạt tài khoản</a></c:if>
          </div>
        </c:if>
        <form action="${ctx}/forgot-password" method="post" class="needs-validation" novalidate>
          <t:field name="email" label="Email" type="email" value="${form.email}" error="${errors.email}"
                   req="true" hint="Vui lòng nhập email hợp lệ" autofocus="true"/>
          <button type="submit" class="btn btn-primary w-100">Gửi mã OTP</button>
        </form>
        <div class="mt-3 text-center"><a href="${ctx}/login">Quay lại đăng nhập</a></div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
