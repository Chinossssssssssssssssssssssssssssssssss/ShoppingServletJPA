<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Video của tôi</title></head>
<body>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h3 class="mb-0">Video của <c:out value="${sessionScope.account.fullName}"/> (${total} video)</h3>
  <a class="btn btn-primary" href="${ctx}/video/add">+ Thêm video</a>
</div>

<c:if test="${empty videos}"><p class="text-muted">Bạn chưa có video nào.</p></c:if>

<c:if test="${not empty videos}">
<div class="table-responsive">
<table class="table table-bordered table-hover align-middle bg-white">
  <thead class="table-light">
    <tr>
      <th>STT</th><th>Poster</th><th>Tiêu đề</th><th>Danh mục</th><th>Lượt xem</th>
      <th>Trạng thái</th><th>Ngày tạo</th><th>Hành động</th>
    </tr>
  </thead>
  <tbody>
  <c:forEach items="${videos}" var="v" varStatus="st">
    <c:url value="/video/detail" var="detailUrl"><c:param name="id" value="${v.videoId}"/></c:url>
    <c:url value="/video/edit" var="editUrl"><c:param name="id" value="${v.videoId}"/></c:url>
    <tr>
      <td>${(currentPage - 1) * pageSize + st.index + 1}</td>
      <td style="width:130px">
        <c:if test="${not empty v.poster}">
          <c:url value="/image" var="posterUrl"><c:param name="fname" value="${v.poster}"/></c:url>
          <img class="thumb rounded" src="${posterUrl}" alt="poster">
        </c:if>
      </td>
      <td><a href="${detailUrl}"><c:out value="${v.title}"/></a></td>
      <td><c:out value="${v.category.categoryname}"/></td>
      <td>${v.views}</td>
      <td>
        <c:if test="${v.active == 1}"><span class="badge bg-success">Hoạt động</span></c:if>
        <c:if test="${v.active != 1}"><span class="badge bg-secondary">Ẩn</span></c:if>
      </td>
      <td><fmt:formatDate value="${v.createdDate}" pattern="dd/MM/yyyy HH:mm"/></td>
      <td class="text-nowrap">
        <a class="btn btn-sm btn-outline-primary" href="${editUrl}">Sửa</a>
        <form action="${ctx}/video/delete" method="post" class="d-inline"
              onsubmit="return confirm('Bạn có chắc muốn xóa video này?')">
          <input type="hidden" name="id" value="${v.videoId}">
          <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
        </form>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>
</div>

<%-- Phân trang: 6 video / trang --%>
<nav>
  <ul class="pagination">
    <c:if test="${currentPage > 1}">
      <li class="page-item"><a class="page-link" href="${ctx}/video?page=${currentPage - 1}">&laquo; Trước</a></li>
    </c:if>
    <c:forEach begin="1" end="${totalPages}" var="i">
      <li class="page-item ${i == currentPage ? 'active' : ''}"><a class="page-link" href="${ctx}/video?page=${i}">${i}</a></li>
    </c:forEach>
    <c:if test="${currentPage < totalPages}">
      <li class="page-item"><a class="page-link" href="${ctx}/video?page=${currentPage + 1}">Sau &raquo;</a></li>
    </c:if>
  </ul>
</nav>
</c:if>
</body>
</html>
