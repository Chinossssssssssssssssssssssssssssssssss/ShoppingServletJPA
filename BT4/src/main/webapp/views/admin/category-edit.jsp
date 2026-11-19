<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- khi lỗi validation thì dùng lại giá trị người dùng vừa nhập (form), không thì lấy từ DB (cate) --%>
<c:set var="curName" value="${not empty form ? form.categoryname : cate.categoryname}"/>
<c:set var="curImages" value="${not empty form ? form.images : cate.images}"/>
<c:set var="curStatus" value="${not empty form ? form.status : cate.status}"/>
<html>
<head><title>Sửa Category</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-8 col-lg-6">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h3 class="mb-4">Sửa Category</h3>
      <form action="<c:url value='/admin/category/update'/>" method="post" enctype="multipart/form-data"
            class="needs-validation" novalidate>
        <input type="hidden" name="categoryid" value="${cate.categoryid}">
        <t:field name="categoryname" label="Category name" value="${curName}" error="${errors.categoryname}"
                 req="true" maxlength="255" hint="Vui lòng nhập tên category (tối đa 255 ký tự)"/>
        <t:field name="images" label="Link images" value="${curImages}" error="${errors.images}"
                 pattern="(https?://[^ ]+)?" maxlength="500" hint="Link ảnh phải bắt đầu bằng http:// hoặc https://"/>

        <c:choose>
          <c:when test="${fn:startsWith(cate.images, 'http')}">
            <c:set var="imgUrl" value="${cate.images}"/>
          </c:when>
          <c:otherwise>
            <c:url value="/image" var="imgUrl"><c:param name="fname" value="${cate.images}"/></c:url>
          </c:otherwise>
        </c:choose>
        <div class="mb-3"><img height="150" width="200" style="object-fit:cover" src="${imgUrl}" alt="category"/></div>

        <div class="mb-3">
          <label class="form-label" for="images1">Upload images</label>
          <input type="file" id="images1" name="images1" accept="image/*"
                 class="form-control${empty errors.images1 ? '' : ' is-invalid'}">
          <div class="invalid-feedback"><c:out value="${errors.images1}"/></div>
        </div>
        <div class="mb-3">
          <label class="form-label d-block">Status</label>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" id="ston" name="status" value="1" ${curStatus == 1 ? 'checked' : ''}>
            <label class="form-check-label" for="ston">Hoạt động</label>
          </div>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" id="stoff" name="status" value="0" ${curStatus != 1 ? 'checked' : ''}>
            <label class="form-check-label" for="stoff">Khóa</label>
          </div>
          <c:if test="${not empty errors.status}"><div class="text-danger small">${errors.status}</div></c:if>
        </div>
        <button type="submit" class="btn btn-primary">Update</button>
        <a class="btn btn-outline-secondary" href="<c:url value='/admin/categories'/>">Quay lại</a>
      </form>
    </div></div>
  </div>
</div>
</body>
</html>
