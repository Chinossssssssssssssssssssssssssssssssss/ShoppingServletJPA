<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="acc" value="${sessionScope.account}"/>
<%-- khi lỗi thì giữ giá trị vừa nhập (form), không thì lấy thông tin hiện tại --%>
<c:set var="curName" value="${not empty form ? form.fullname : acc.fullName}"/>
<c:set var="curPhone" value="${not empty form ? form.phone : acc.phone}"/>
<c:set var="curEmail" value="${not empty form ? form.email : acc.email}"/>
<c:if test="${not empty acc.avatar}">
  <c:choose>
    <c:when test="${fn:startsWith(acc.avatar, 'http')}"><c:set var="avatarUrl" value="${acc.avatar}"/></c:when>
    <c:otherwise><c:url value="/image" var="avatarUrl"><c:param name="fname" value="${acc.avatar}"/></c:url></c:otherwise>
  </c:choose>
</c:if>
<html>
<head><title>Hồ sơ cá nhân</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-8 col-lg-6">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h3 class="mb-4">Hồ sơ cá nhân</h3>

      <c:if test="${param.success == '1'}"><div class="alert alert-success">Cập nhật hồ sơ thành công.</div></c:if>
      <c:if test="${param.locked == '1'}"><div class="alert alert-danger">Nhập sai OTP quá nhiều lần, thay đổi đã bị hủy. Vui lòng thử lại.</div></c:if>
      <c:if test="${param.taken == '1'}"><div class="alert alert-danger">Email mới đã được người khác sử dụng, thay đổi đã bị hủy.</div></c:if>
      <c:if test="${alert != null}"><div class="alert alert-danger">${alert}</div></c:if>

      <%-- enctype multipart/form-data bắt buộc để upload ảnh --%>
      <form action="${ctx}/profile" method="post" enctype="multipart/form-data" class="needs-validation" novalidate>
        <div class="text-center mb-3">
          <c:choose>
            <c:when test="${not empty avatarUrl}">
              <img id="avatarPreview" src="${avatarUrl}" alt="avatar" class="rounded-circle border"
                   width="120" height="120" style="object-fit:cover">
            </c:when>
            <c:otherwise>
              <img id="avatarPreview" alt="avatar" class="rounded-circle border bg-secondary-subtle"
                   width="120" height="120" style="object-fit:cover" src="data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==">
            </c:otherwise>
          </c:choose>
        </div>

        <div class="mb-3">
          <label class="form-label" for="avatar">Ảnh đại diện (tối đa 5MB)</label>
          <input type="file" id="avatar" name="avatar" accept="image/*"
                 class="form-control${empty errors.avatar ? '' : ' is-invalid'}">
          <div class="invalid-feedback"><c:out value="${errors.avatar}"/></div>
          <div class="form-text">Ảnh được lưu trên Cloudinary. Bỏ trống nếu không đổi.</div>
        </div>

        <div class="mb-3">
          <label class="form-label">Tài khoản</label>
          <input type="text" class="form-control" value="<c:out value='${acc.userName}'/>" disabled>
        </div>

        <t:field name="fullname" label="Họ tên" value="${curName}" error="${errors.fullname}"
                 req="true" minlength="2" maxlength="100" hint="Họ tên từ 2 đến 100 ký tự"/>
        <t:field name="phone" label="Số điện thoại" value="${curPhone}" error="${errors.phone}"
                 req="true" pattern="(0|[+]84)[0-9]{9}" hint="Số điện thoại không hợp lệ (VD: 0912345678)"/>
        <t:field name="email" label="Email" type="email" value="${curEmail}" error="${errors.email}"
                 req="true" maxlength="100" hint="Email không hợp lệ"/>
        <div class="form-text mb-3">Nếu đổi email, hệ thống sẽ gửi mã OTP tới email mới để xác nhận.</div>

        <button type="submit" class="btn btn-primary">Cập nhật</button>
        <a class="btn btn-outline-secondary" href="${ctx}/home">Về trang chủ</a>
      </form>
    </div></div>
  </div>
</div>
<script>
  // xem trước ảnh vừa chọn
  document.getElementById('avatar').addEventListener('change', function () {
    if (this.files && this.files[0]) {
      document.getElementById('avatarPreview').src = URL.createObjectURL(this.files[0]);
    }
  });
</script>
</body>
</html>
