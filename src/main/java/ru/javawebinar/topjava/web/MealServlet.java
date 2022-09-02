package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.exception.WrongRequestParameters;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MealMapStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final int CALORIES_LIMIT = 2000;
    private final Logger log = getLogger(MealServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException, WrongRequestParameters {
        MealStorage storage = MealMapStorage.getInstance();
        String action = request.getParameter("action");
        if (action != null) {
            Integer id;
            switch (action) {
                case "add":
                    log.debug("Got request to ADD new meal");
                    forwardRequest(request, response, action);
                    break;
                case "edit":
                    id = getIntParam(request, "id");
                    log.debug("Got request to EDIT meal with id {}", id);
                    Meal meal = storage.get(id);
                    request.setAttribute("meal", meal);
                    log.debug("Added meal {} to the request. Start forwarding to edit page", id);
                    forwardRequest(request, response, action);
                    break;
                case "delete":
                    id = getIntParam(request, "id");
                    log.debug("Got request to DELETE meal with id {}", id);
                    storage.delete(id);
                    log.debug("Meal id: {} deleted", id);
                    response.sendRedirect("meals");
                    return;
                default:
                    log.debug("Got unknown action. redirect to meals list");
                    response.sendRedirect("meals");
                    return;
            }
        }
        log.debug("Got {} request for meals list", request.getMethod());
        request.setAttribute("mealsList",
                MealsUtil.filteredByStreams(storage.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_LIMIT));
        log.debug("Added meals list for the GET request. Start forwarding to the meals list page.");
        request.getRequestDispatcher("/WEB-INF/jsp/meals.jsp").forward(request, response);
    }

    private Integer getIntParam(HttpServletRequest request, String name) throws WrongRequestParameters {
        int integer;
        try {
            integer = Integer.parseInt(request.getParameter(name));
        } catch (NumberFormatException | NullPointerException e) {
            String message = "Got wrong " + name + " parameter in the /meals " + request.getMethod() + " request";
            log.debug(message);
            throw new WrongRequestParameters(message);
        }
        return integer;
    }

    private void forwardRequest(HttpServletRequest request, HttpServletResponse response, String action) throws ServletException, IOException {
        request.setAttribute("action", action);
        request.getRequestDispatcher("WEB-INF/jsp/editmeal.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("datetime"));
        String description = request.getParameter("description");
        Integer calories = getIntParam(request, "calories");
        Meal meal = new Meal(dateTime, description, calories);
        MealStorage storage = MealMapStorage.getInstance();
        String action = request.getParameter("action");
        switch (action) {
            case "add":
                storage.add(meal);
                break;
            case "edit":
                Integer id = getIntParam(request, "id");
                meal.setId(id);
                storage.update(meal);
                break;
            default:
                String message = "Got wrong ACTION parameter in the /meals " + request.getMethod() + " request";
                log.debug(message);
                throw new WrongRequestParameters(message);
        }
        response.sendRedirect("meals");
    }
}
