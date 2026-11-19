<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="type" %>
<%@ attribute name="value" %>
<%@ attribute name="error" %>
<%@ attribute name="hint" %>
<%@ attribute name="req" %>
<%@ attribute name="pattern" %>
<%@ attribute name="minlength" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="placeholder" %>
<%@ attribute name="autofocus" %>
<c:set var="inputType" value="${empty type ? 'text' : type}"/>
<div class="mb-3">
  <label class="form-label" for="${name}">${label}</label>
  <input id="${name}" name="${name}" type="${inputType}" class="form-control${empty error ? '' : ' is-invalid'}"
         <c:if test="${inputType != 'password'}">value="<c:out value='${value}'/>"</c:if>
         <c:if test="${req == 'true'}">required</c:if>
         <c:if test="${not empty pattern}">pattern="${pattern}"</c:if>
         <c:if test="${not empty minlength}">minlength="${minlength}"</c:if>
         <c:if test="${not empty maxlength}">maxlength="${maxlength}"</c:if>
         <c:if test="${not empty placeholder}">placeholder="${placeholder}"</c:if>
         <c:if test="${autofocus == 'true'}">autofocus</c:if>>
  <div class="invalid-feedback"><c:out value="${empty error ? hint : error}"/></div>
</div>
