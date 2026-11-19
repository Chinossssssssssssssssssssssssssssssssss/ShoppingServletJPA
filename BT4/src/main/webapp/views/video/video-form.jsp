<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isEdit" value="${video != null}"/>
<%-- khi lỗi validation thì dùng lại giá trị vừa nhập (form), không thì lấy từ video trong DB --%>
<c:set var="curTitle" value="${not empty form ? form.title : video.title}"/>
<c:set var="curDesc" value="${not empty form ? form.description : video.description}"/>
<c:set var="curCate" value="${not empty form ? form.categoryId : video.category.categoryid}"/>
<c:set var="curActive" value="${not empty form ? form.active : (isEdit ? video.active : 1)}"/>
<%-- link YouTube: giữ giá trị vừa nhập, hoặc điền sẵn link cũ khi sửa --%>
<c:set var="curLink" value="${not empty param.videoLink ? param.videoLink : (isEdit && fn:startsWith(video.videoUrl, 'http') ? video.videoUrl : '')}"/>
<html>
<head><title>${isEdit ? 'Sửa video' : 'Thêm video'}</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-9 col-lg-7">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h3 class="mb-4">${isEdit ? 'Sửa video' : 'Thêm video'}</h3>

      <%-- enctype multipart/form-data bắt buộc khi upload file --%>
      <form action="${ctx}/video/${isEdit ? 'update' : 'insert'}" method="post" enctype="multipart/form-data"
            class="needs-validation" novalidate>
        <c:if test="${isEdit}"><input type="hidden" name="id" value="${video.videoId}"></c:if>

        <t:field name="title" label="Tiêu đề" value="${curTitle}" error="${errors.title}"
                 req="true" maxlength="200" hint="Vui lòng nhập tiêu đề (tối đa 200 ký tự)"/>

        <div class="mb-3">
          <label class="form-label" for="description">Mô tả</label>
          <textarea id="description" name="description" rows="4" maxlength="500"
                    class="form-control${empty errors.description ? '' : ' is-invalid'}"><c:out value="${curDesc}"/></textarea>
          <div class="invalid-feedback"><c:out value="${errors.description}"/></div>
        </div>

        <div class="mb-3">
          <label class="form-label" for="categoryId">Danh mục</label>
          <select id="categoryId" name="categoryId" class="form-select">
            <option value="0">-- Không chọn --</option>
            <c:forEach items="${listcate}" var="cate">
              <option value="${cate.categoryid}" ${curCate == cate.categoryid ? 'selected' : ''}>
                <c:out value="${cate.categoryname}"/>
              </option>
            </c:forEach>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label d-block">Trạng thái</label>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" id="act1" name="active" value="1" ${curActive == 1 ? 'checked' : ''}>
            <label class="form-check-label" for="act1">Hoạt động</label>
          </div>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" id="act0" name="active" value="0" ${curActive != 1 ? 'checked' : ''}>
            <label class="form-check-label" for="act0">Ẩn</label>
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label" for="poster">Poster (ảnh)</label>
          <c:if test="${isEdit && not empty video.poster}">
            <c:url value="/image" var="posterUrl"><c:param name="fname" value="${video.poster}"/></c:url>
            <div class="mb-2"><img class="rounded" style="width:200px;height:120px;object-fit:cover" src="${posterUrl}" alt="poster"></div>
          </c:if>
          <input type="file" id="poster" name="poster" accept="image/*"
                 class="form-control${empty errors.poster ? '' : ' is-invalid'}">
          <div class="invalid-feedback"><c:out value="${errors.poster}"/></div>
          <c:if test="${isEdit}"><div class="form-text">Bỏ trống nếu không đổi</div></c:if>
        </div>

        <div class="mb-3">
          <label class="form-label" for="videoLink">Link video YouTube</label>
          <input type="url" id="videoLink" name="videoLink" value="<c:out value='${curLink}'/>"
                 placeholder="https://www.youtube.com/watch?v=xxxxxxxxxxx"
                 class="form-control${empty errors.videoLink ? '' : ' is-invalid'}">
          <div class="invalid-feedback"><c:out value="${errors.videoLink}"/></div>
        </div>

        <div class="mb-3">
          <label class="form-label" for="videoFile">Hoặc tải file video lên (mp4, webm, ogg)</label>
          <input type="file" id="videoFile" name="videoFile" accept="video/mp4,video/webm,video/ogg"
                 class="form-control${empty errors.videoFile ? '' : ' is-invalid'}">
          <div class="invalid-feedback"><c:out value="${errors.videoFile}"/></div>
          <div class="form-text">
            Chọn <b>một trong hai</b>: link YouTube hoặc file. Nếu nhập cả hai thì file được ưu tiên.
            <c:if test="${isEdit}"> Bỏ trống cả hai nếu không đổi video.</c:if>
          </div>
        </div>

        <button type="submit" class="btn btn-primary">${isEdit ? 'Cập nhật' : 'Thêm mới'}</button>
        <a class="btn btn-outline-secondary" href="${ctx}/video">Hủy</a>
      </form>
    </div></div>
  </div>
</div>
</body>
</html>