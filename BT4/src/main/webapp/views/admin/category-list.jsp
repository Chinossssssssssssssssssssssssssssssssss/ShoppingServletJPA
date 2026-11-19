<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<html>
<head><title>Danh sách Category</title></head>
<body>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h3 class="mb-0">Danh sách Category</h3>
  <a class="btn btn-primary" href="<c:url value='/admin/category/add'/>">+ Add Category</a>
</div>
<div class="table-responsive">
<table class="table table-bordered table-hover align-middle bg-white">
  <thead class="table-light">
    <tr><th>STT</th><th>Images</th><th>Category name</th><th>Status</th><th>Action</th></tr>
  </thead>
  <tbody>
  <c:forEach items="${listcate}" var="cate" varStatus="STT">
    <c:choose>
      <c:when test="${fn:startsWith(cate.images, 'http')}">
        <c:set var="imgUrl" value="${cate.images}"/>
      </c:when>
      <c:otherwise>
        <c:url value="/image" var="imgUrl"><c:param name="fname" value="${cate.images}"/></c:url>
      </c:otherwise>
    </c:choose>
    <tr>
      <td>${STT.index + 1}</td>
      <td><img height="90" width="130" style="object-fit:cover" src="${imgUrl}" alt="category"/></td>
      <td><c:out value="${cate.categoryname}"/></td>
      <td>
        <c:if test="${cate.status == 1}"><span class="badge bg-success">Hoạt động</span></c:if>
        <c:if test="${cate.status != 1}"><span class="badge bg-secondary">Khóa</span></c:if>
      </td>
      <td>
        <a class="btn btn-sm btn-outline-primary" href="<c:url value='/admin/category/edit?id=${cate.categoryid}'/>">Sửa</a>
        <a class="btn btn-sm btn-outline-danger" href="<c:url value='/admin/category/delete?id=${cate.categoryid}'/>"
           onclick="return confirm('Bạn có chắc muốn xóa?')">Xóa</a>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>
</div>
</body>
</html>
