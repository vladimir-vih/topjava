package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.exception.NotExistStorageException;
import ru.javawebinar.topjava.exception.WrongRequestParameters;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.InMemoryMealStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final int CALORIES_LIMIT = 2000;
    private final Logger log = getLogger(MealServlet.class);
    private final MealStorage storage = new InMemoryMealStorage();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException, WrongRequestParameters {
        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "add":
                    log.debug("Got request to ADD new meal");
                    request.setAttribute("meal",
                            new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0));
                    request.getRequestDispatcher("/jsp/editMeal.jsp").forward(request, response);
                    return;
                case "edit": {
                    int id = getIntParam(request, "id");
                    log.debug("Got request to EDIT meal with id {}", id);
                    Meal meal = storage.get(id);
                    if (meal == null) throw new NotExistStorageException();
                    request.setAttribute("meal", meal);
                    log.debug("Added meal {} to the request. Start forwarding to edit page", id);
                    request.getRequestDispatcher("/jsp/editMeal.jsp").forward(request, response);
                    return;
                }
                case "delete": {
                    int id = getIntParam(request, "id");
                    log.debug("Got request to DELETE meal with id {}", id);
                    if (!storage.delete(id)) throw new NotExistStorageException();
                    log.debug("Meal id: {} deleted", id);
                    response.sendRedirect("meals");
                    return;
                }
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
        request.getRequestDispatcher("/jsp/meals.jsp").forward(request, response);
    }

    private int getIntParam(HttpServletRequest request, String name) throws WrongRequestParameters {
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        log.info("Got POST request to add or edit meal");
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("datetime"));
        String description = request.getParameter("description");
        Integer calories = getIntParam(request, "calories");
        Meal meal = new Meal(dateTime, description, calories);
        String idParam = request.getParameter("id");
        if (idParam.isEmpty()) {
            log.debug("Adding new meal with description {}", description);
            storage.add(meal);
        } else {
            Integer id = Integer.parseInt(idParam);
            meal.setId(id);
            Meal result = storage.update(meal);
            if (result == null) throw new NotExistStorageException();
            log.debug("Updated meal with id {}", id);
        }
        log.info("Redirecting to the meals list page");
        response.sendRedirect("meals");
    }
}
