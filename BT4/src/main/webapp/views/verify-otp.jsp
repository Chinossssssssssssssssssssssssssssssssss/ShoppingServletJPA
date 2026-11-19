<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Kích hoạt tài khoản</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-6 col-lg-5">
    <div class="card shadow-sm">
      <div class="card-body p-4">
        <h3 class="text-center mb-3">Kích hoạt tài khoản</h3>
        <p>Mã OTP (6 số) đã được gửi tới email: <b><c:out value="${email}"/></b></p>
        <c:if test="${alert != null}"><div class="alert alert-danger">${alert}</div></c:if>
        <c:if test="${info != null}"><div class="alert alert-success">${info}</div></c:if>

        <form action="${ctx}/verify-otp" method="post" class="needs-validation" novalidate>
          <t:field name="otp" label="Mã OTP" value="${form.otp}" error="${errors.otp}"
                   req="true" pattern="[0-9]{6}" maxlength="6" hint="OTP gồm đúng 6 chữ số" autofocus="true"/>
          <button type="submit" class="btn btn-primary w-100">Kích hoạt</button>
        </form>

        <form action="${ctx}/verify-otp" method="post" class="mt-2">
          <input type="hidden" name="action" value="resend">
          <button type="submit" class="btn btn-link p-0">Gửi lại OTP</button>
        </form>
        <div class="mt-2"><a href="${ctx}/login">Về trang đăng nhập</a></div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
