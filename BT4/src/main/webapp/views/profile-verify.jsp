<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Xác nhận email mới</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-6 col-lg-5">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h3 class="text-center mb-3">Xác nhận email mới</h3>
      <p>Mã OTP (6 số) đã được gửi tới email mới: <b><c:out value="${newEmail}"/></b>.
         Nhập mã để hoàn tất cập nhật hồ sơ.</p>
      <c:if test="${alert != null}"><div class="alert alert-danger">${alert}</div></c:if>
      <c:if test="${info != null}"><div class="alert alert-success">${info}</div></c:if>

      <form action="${ctx}/profile/verify" method="post" class="needs-validation" novalidate>
        <t:field name="otp" label="Mã OTP" value="${form.otp}" error="${errors.otp}"
                 req="true" pattern="[0-9]{6}" maxlength="6" hint="OTP gồm đúng 6 chữ số" autofocus="true"/>
        <button type="submit" class="btn btn-primary w-100">Xác nhận</button>
      </form>

      <div class="d-flex justify-content-between mt-3">
        <form action="${ctx}/profile/verify" method="post">
          <input type="hidden" name="action" value="resend">
          <button type="submit" class="btn btn-link p-0">Gửi lại OTP</button>
        </form>
        <form action="${ctx}/profile/verify" method="post">
          <input type="hidden" name="action" value="cancel">
          <button type="submit" class="btn btn-link text-danger p-0">Hủy thay đổi</button>
        </form>
      </div>
    </div></div>
  </div>
</div>
</body>
</html>
