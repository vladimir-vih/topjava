<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<html>
<head>
    <c:set var="action" value="${param.action}"/>
    <title>${action.equals("edit") ? "Edit meal" : "Add meal"}</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<div>

    <form method="post" action="meals" enctype="application/x-www-form-urlencoded">
        <input type="hidden" name="id" value="${action.equals("edit") ? meal.id : ''}">
        <label>Meal Date and Time:
            <input type="datetime-local" name="datetime" placeholder="YYYY-MM-DD hh:mi"
                   value="${action.equals("edit") ? meal.dateTime : TimeUtil.getCurrentDateTime()}">
        </label>
        <br><br>
        <label>Description:
            <input type="text" name="description" placeholder="Meal description"
                   value="${action.equals("edit") ? meal.description : ''}">
        </label>
        <br><br>
        <label>Calories:
            <input type="number" name="calories" placeholder="Meal calories"
                   value="${action.equals("edit") ? meal.calories : ''}">
        </label>
        <button type="submit">Save</button>
        <button onclick="window.history.back();return false;">Cancel</button>
    </form>
</div>
</body>
</html>
