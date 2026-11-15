<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Sửa Category</title></head>
<body>
<form action="<c:url value='/admin/category/update'/>" method="post" enctype="multipart/form-data">
  <input type="hidden" name="categoryid" value="${cate.categoryid}">
  <label>Category name:</label><br>
  <input type="text" name="categoryname" value="${cate.categoryname}" required><br>
  <label>Link images:</label><br>
  <input type="text" name="images" value="${cate.images}"><br>

  <c:choose>
    <c:when test="${fn:startsWith(cate.images, 'http')}">
      <c:set var="imgUrl" value="${cate.images}"/>
    </c:when>
    <c:otherwise>
      <c:url value="/image" var="imgUrl"><c:param name="fname" value="${cate.images}"/></c:url>
    </c:otherwise>
  </c:choose>
  <img height="150" width="200" src="${imgUrl}"/><br>

  <label>Upload images:</label><br>
  <input type="file" name="images1"><br>
  <label>Status</label><br>
  <input type="radio" id="ston" name="status" value="1" ${cate.status == 1 ? 'checked' : ''}>
  <label for="ston">Hoạt động</label><br>
  <input type="radio" id="stoff" name="status" value="0" ${cate.status != 1 ? 'checked' : ''}>
  <label for="stoff">Khóa</label>
  <br><br>
  <input type="submit" value="Update">
  <a href="<c:url value='/admin/categories'/>">Quay lại</a>
</form>
</body>
</html>