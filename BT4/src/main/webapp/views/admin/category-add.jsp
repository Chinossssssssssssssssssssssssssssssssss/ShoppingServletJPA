<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Thêm Category</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-8 col-lg-6">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h3 class="mb-4">Thêm Category</h3>
      <form action="<c:url value='/admin/category/insert'/>" method="post" enctype="multipart/form-data"
            class="needs-validation" novalidate>
        <t:field name="categoryname" label="Category name" value="${form.categoryname}" error="${errors.categoryname}"
                 req="true" maxlength="255" hint="Vui lòng nhập tên category (tối đa 255 ký tự)"/>
        <t:field name="images" label="Link images (tùy chọn)" value="${form.images}" error="${errors.images}"
                 pattern="(https?://[^ ]+)?" maxlength="500" hint="Link ảnh phải bắt đầu bằng http:// hoặc https://"/>
        <div class="mb-3">
          <label class="form-label" for="images1">Upload images (tùy chọn)</label>
          <input type="file" id="images1" name="images1" accept="image/*"
                 class="form-control${empty errors.images1 ? '' : ' is-invalid'}">
          <div class="invalid-feedback"><c:out value="${errors.images1}"/></div>
        </div>
        <div class="mb-3">
          <label class="form-label d-block">Status</label>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" id="ston" name="status" value="1"
                   ${empty form || form.status == 1 ? 'checked' : ''}>
            <label class="form-check-label" for="ston">Hoạt động</label>
          </div>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" id="stoff" name="status" value="0"
                   ${not empty form && form.status == 0 ? 'checked' : ''}>
            <label class="form-check-label" for="stoff">Khóa</label>
          </div>
          <c:if test="${not empty errors.status}"><div class="text-danger small">${errors.status}</div></c:if>
        </div>
        <button type="submit" class="btn btn-primary">Insert</button>
        <a class="btn btn-outline-secondary" href="<c:url value='/admin/categories'/>">Quay lại</a>
      </form>
    </div></div>
  </div>
</div>
</body>
</html>
