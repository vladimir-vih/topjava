<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>${meal.id == null ? "Add meal" : "Edit meal"}</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<h3>${meal.id == null ? "Add meal" : "Edit meal"}</h3>
<div>

    <form method="post" action="meals" enctype="application/x-www-form-urlencoded">
        <input type="hidden" name="id" value="${meal.id}">
        <label>Meal Date and Time:
            <input type="datetime-local" name="datetime" placeholder="YYYY-MM-DD hh:mi" value="${meal.dateTime}">
        </label>
        <br><br>
        <label>Description:
            <input type="text" name="description" placeholder="Meal description" value="${meal.description}">
        </label>
        <br><br>
        <label>Calories:
            <input type="number" name="calories" placeholder="Meal calories" value="${meal.calories}">
        </label>
        <button type="submit">Save</button>
        <button onclick="window.history.back();return false;">Cancel</button>
    </form>
</div>
</body>
</html>
