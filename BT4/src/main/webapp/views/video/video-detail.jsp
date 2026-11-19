<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head><title><c:out value="${video.title}"/></title></head>
<body>
<h3 class="mb-3"><c:out value="${video.title}"/></h3>

<c:url value="/media" var="mediaUrl"><c:param name="fname" value="${video.videoUrl}"/></c:url>
<c:url value="/image" var="posterUrl"><c:param name="fname" value="${video.poster}"/></c:url>

<div class="ratio ratio-16x9 mb-3" style="max-width:900px">
<c:choose>
  <%-- video YouTube: nhúng bằng iframe --%>
  <c:when test="${not empty ytId}">
    <iframe src="https://www.youtube.com/embed/${ytId}" title="<c:out value='${video.title}'/>"
            allow="accelerometer; autoplay; encrypted-media; picture-in-picture"
            allowfullscreen></iframe>
  </c:when>
  <%-- video upload: giữ nguyên như cũ --%>
  <c:when test="${not empty video.poster}">
    <video controls preload="metadata" poster="${posterUrl}"><source src="${mediaUrl}">Trình duyệt của bạn không hỗ trợ thẻ video.</video>
  </c:when>
  <c:otherwise>
    <video controls preload="metadata"><source src="${mediaUrl}">Trình duyệt của bạn không hỗ trợ thẻ video.</video>
  </c:otherwise>
</c:choose>
</div>

<p class="text-muted">
  <b>${video.views}</b> lượt xem
  · Ngày đăng: <fmt:formatDate value="${video.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
  <c:if test="${video.user != null}"> · Đăng bởi: <c:out value="${video.user.fullName}"/></c:if>
  <c:if test="${video.category != null}"> · Danh mục: <c:out value="${video.category.categoryname}"/></c:if>
</p>
<p style="white-space:pre-wrap"><c:out value="${video.description}"/></p>
</body>
</html>