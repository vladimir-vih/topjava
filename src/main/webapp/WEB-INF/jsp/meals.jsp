<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<html>
<head>
    <link rel="stylesheet" href="css/table.css">
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<h4><a href="meals?action=add">Add new meal</a></h4>
<div>
    <table>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th>Edit</th>
            <th>Delete</th>
        </tr>
        <c:forEach var="meal" items="${mealsList}">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
            <tr style="color: ${meal.excess ? 'red' : 'green'};">
                <td>
                        ${TimeUtil.dateToString(meal.dateTime)}
                </td>
                <td>
                        ${meal.description}
                </td>
                <td>
                        ${meal.calories}
                </td>
                <td>
                    <a href="meals?action=edit&id=${meal.id}">Edit</a>
                </td>
                <td>
                    <a href="meals?action=delete&id=${meal.id}">Delete</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
