<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Trang chủ</title></head>
<body>
<c:choose>
  <c:when test="${sessionScope.account == null}">
    <a href="${pageContext.request.contextPath}/login">Đăng nhập</a> |
    <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
  </c:when>
  <c:otherwise>
    <h2>Xin chào ${sessionScope.account.fullName}</h2>
    <p>Vai trò (roleid): ${sessionScope.account.roleid}</p>
    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
  </c:otherwise>
</c:choose>
</body>
</html>