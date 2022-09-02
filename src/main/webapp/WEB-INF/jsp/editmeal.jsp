<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Edit Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<div>

    <form method="post" action="meals" enctype="application/x-www-form-urlencoded">
        <input type="hidden" name="action" value="${action}">
        <c:if test="${action.equals(\"edit\")}">
            <input type="hidden" name="id" value="${meal.id}">
        </c:if>
        <label>Meal Date and Time:
            <c:choose>
                <c:when test="${action.equals(\"edit\")}">
                    <input type="datetime-local" name="datetime" value="${meal.dateTime}">
                </c:when>
                <c:when test="${action.equals(\"add\")}">
                    <input type="datetime-local" name="datetime" placeholder="YYYY-MM-DD hh:mi:ss">
                </c:when>
            </c:choose>
        </label>
        <br><br>
        <label>Description:
            <c:choose>
                <c:when test="${action.equals(\"edit\")}">
                    <input type="text" name="description" value="${meal.description}">
                </c:when>
                <c:when test="${action.equals(\"add\")}">
                    <input type="text" name="description" placeholder="Meal description">
                </c:when>
            </c:choose>
        </label>
        <br><br>
        <label>Calories:
            <c:choose>
                <c:when test="${action.equals(\"edit\")}">
                    <input type="text" name="calories" value="${meal.calories}">
                </c:when>
                <c:when test="${action.equals(\"add\")}">
                    <input type="text" name="calories" placeholder="Meal calories">
                </c:when>
            </c:choose>
        </label>
        <button type="submit">Save</button>
        <button onclick="window.history.back();return false;">Cancel</button>
    </form>
</div>
</body>
</html>
