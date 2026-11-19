<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Đăng nhập</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-6 col-lg-5">
    <div class="card shadow-sm">
      <div class="card-body p-4">
        <h3 class="text-center mb-4">Đăng nhập</h3>

        <c:if test="${param.activated == '1'}">
          <div class="alert alert-success">Kích hoạt tài khoản thành công, hãy đăng nhập.</div>
        </c:if>
        <c:if test="${param.reset == '1'}">
          <div class="alert alert-success">Đổi mật khẩu thành công, hãy đăng nhập bằng mật khẩu mới.</div>
        </c:if>
        <c:if test="${alert != null}">
          <div class="alert alert-danger">${alert}
            <c:if test="${needActivate}">
              <br><a href="${ctx}/verify-otp">Nhập mã OTP để kích hoạt (hoặc gửi lại OTP)</a>
            </c:if>
          </div>
        </c:if>

        <form action="${ctx}/login" method="post" class="needs-validation" novalidate>
          <t:field name="username" label="Tài khoản" value="${form.username}" error="${errors.username}"
                   req="true" hint="Vui lòng nhập tài khoản" autofocus="true"/>
          <t:field name="password" label="Mật khẩu" type="password" error="${errors.password}"
                   req="true" hint="Vui lòng nhập mật khẩu"/>
          <div class="form-check mb-3">
            <input class="form-check-input" type="checkbox" name="remember" id="remember">
            <label class="form-check-label" for="remember">Nhớ tôi</label>
          </div>
          <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
        </form>

        <div class="mt-3 text-center">
          <a href="${ctx}/forgot-password">Quên mật khẩu?</a><br>
          Chưa có tài khoản? <a href="${ctx}/register">Đăng ký</a>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
