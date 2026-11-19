<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Đăng ký</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-8 col-lg-6">
    <div class="card shadow-sm">
      <div class="card-body p-4">
        <h3 class="text-center mb-4">Tạo tài khoản mới</h3>
        <c:if test="${alert != null}"><div class="alert alert-danger">${alert}</div></c:if>

        <form action="${ctx}/register" method="post" class="needs-validation" novalidate>
          <t:field name="username" label="Tài khoản" value="${form.username}" error="${errors.username}"
                   req="true" pattern="[A-Za-z0-9_]{4,20}" hint="Tài khoản 4-20 ký tự, chỉ gồm chữ, số và dấu _"/>
          <t:field name="fullname" label="Họ tên" value="${form.fullname}" error="${errors.fullname}"
                   req="true" minlength="2" maxlength="100" hint="Họ tên từ 2 đến 100 ký tự"/>
          <t:field name="email" label="Email" type="email" value="${form.email}" error="${errors.email}"
                   req="true" maxlength="100" hint="Email không hợp lệ"/>
          <t:field name="phone" label="Số điện thoại" value="${form.phone}" error="${errors.phone}"
                   req="true" pattern="(0|[+]84)[0-9]{9}" hint="Số điện thoại không hợp lệ (VD: 0912345678)"/>
          <t:field name="password" label="Mật khẩu" type="password" error="${errors.password}"
                   req="true" pattern="(?=.*[A-Za-z])(?=.*[0-9]).{8,50}" hint="Mật khẩu 8-50 ký tự, gồm cả chữ và số"/>
          <t:field name="confirmPassword" label="Nhập lại mật khẩu" type="password" error="${errors.confirmPassword}"
                   req="true" hint="Vui lòng nhập lại mật khẩu"/>
          <button type="submit" class="btn btn-primary w-100">Tạo tài khoản</button>
        </form>

        <div class="mt-3 text-center">Đã có tài khoản? <a href="${ctx}/login">Đăng nhập</a></div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
