<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Trang chủ</title></head>
<body>
<h3 class="mb-3">10 video mới nhất</h3>
<c:if test="${empty latestVideos}"><p class="text-muted">Chưa có video nào.</p></c:if>

<div class="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-3">
  <c:forEach items="${latestVideos}" var="v">
    <c:url value="/video/detail" var="detailUrl"><c:param name="id" value="${v.videoId}"/></c:url>
    <div class="col">
      <div class="card h-100 shadow-sm">
        <c:choose>
          <c:when test="${not empty v.poster}">
            <c:url value="/image" var="posterUrl"><c:param name="fname" value="${v.poster}"/></c:url>
            <a href="${detailUrl}"><img src="${posterUrl}" alt="poster" class="card-img-top thumb"></a>
          </c:when>
          <c:otherwise><a href="${detailUrl}"><div class="card-img-top thumb"></div></a></c:otherwise>
        </c:choose>
        <div class="card-body p-2">
          <a class="fw-bold text-decoration-none" href="${detailUrl}"><c:out value="${v.title}"/></a>
          <div class="small text-muted mt-1">
            ${v.views} lượt xem
            <c:if test="${v.user != null}"> · <c:out value="${v.user.fullName}"/></c:if><br>
            <fmt:formatDate value="${v.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
          </div>
        </div>
      </div>
    </div>
  </c:forEach>
</div>
</body>
</html>
