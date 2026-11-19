<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- avatar của user đang đăng nhập (link Cloudinary hoặc file cũ trong thư mục upload) --%>
<c:if test="${sessionScope.account != null && not empty sessionScope.account.avatar}">
  <c:choose>
    <c:when test="${fn:startsWith(sessionScope.account.avatar, 'http')}">
      <c:set var="navAvatar" value="${sessionScope.account.avatar}"/>
    </c:when>
    <c:otherwise>
      <c:url value="/image" var="navAvatar"><c:param name="fname" value="${sessionScope.account.avatar}"/></c:url>
    </c:otherwise>
  </c:choose>
</c:if>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><sitemesh:write property="title"/> - Shopping</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/assets/style.css">
  <sitemesh:write property="head"/>
</head>
<body class="bg-light d-flex flex-column min-vh-100">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container">
    <a class="navbar-brand fw-bold" href="${ctx}/home">Shopping</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="mainNav">
      <ul class="navbar-nav me-auto">
        <li class="nav-item"><a class="nav-link" href="${ctx}/home">Trang chủ</a></li>
        <c:if test="${sessionScope.account != null}">
          <li class="nav-item"><a class="nav-link" href="${ctx}/video">Video của tôi</a></li>
        </c:if>
        <c:if test="${sessionScope.account != null && sessionScope.account.roleid == 1}">
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/categories">Quản lý Category</a></li>
        </c:if>
      </ul>
      <ul class="navbar-nav align-items-lg-center">
        <c:choose>
          <c:when test="${sessionScope.account == null}">
            <li class="nav-item"><a class="nav-link" href="${ctx}/login">Đăng nhập</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/register">Đăng ký</a></li>
          </c:when>
          <c:otherwise>
            <li class="nav-item">
              <a class="nav-link d-flex align-items-center gap-2" href="${ctx}/profile">
                <c:choose>
                  <c:when test="${not empty navAvatar}">
                    <img src="${navAvatar}" alt="avatar" class="rounded-circle" width="28" height="28" style="object-fit:cover">
                  </c:when>
                  <c:otherwise>
                    <span class="rounded-circle bg-secondary d-inline-block" style="width:28px;height:28px"></span>
                  </c:otherwise>
                </c:choose>
                <span><c:out value="${sessionScope.account.fullName}"/></span>
              </a>
            </li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/logout">Đăng xuất</a></li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </div>
</nav>

<main class="container my-4 flex-grow-1">
  <sitemesh:write property="body"/>
</main>

<footer class="bg-dark text-light text-center py-3 mt-auto">
  <small>&copy; 2026 Shopping Servlet - IUTSTAR</small>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Bài 2: validation phía client cho mọi form có class "needs-validation"
  document.querySelectorAll('form.needs-validation').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      if (!form.checkValidity()) { e.preventDefault(); e.stopPropagation(); }
      form.classList.add('was-validated');
    });
  });
</script>
</body>
</html>
