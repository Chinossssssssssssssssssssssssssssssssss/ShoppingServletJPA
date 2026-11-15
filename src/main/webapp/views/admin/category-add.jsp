<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Thêm Category</title></head>
<body>
<form action="<c:url value='/admin/category/insert'/>" method="post" enctype="multipart/form-data">
  <label>Category name:</label><br>
  <input type="text" name="categoryname" required><br>
  <label>Link images:</label><br>
  <input type="text" name="images"><br>
  <label>Upload images:</label><br>
  <input type="file" name="images1"><br>
  <label>Status</label><br>
  <input type="radio" id="ston" name="status" value="1" checked>
  <label for="ston">Hoạt động</label><br>
  <input type="radio" id="stoff" name="status" value="0">
  <label for="stoff">Khóa</label>
  <br><br>
  <input type="submit" value="Insert">
  <a href="<c:url value='/admin/categories'/>">Quay lại</a>
</form>
</body>
</html>