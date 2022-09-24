package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeFilterEnum;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRestController controller;

    private ConfigurableApplicationContext appCtx;

    @Override
    public void init() {
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        controller = appCtx.getBean(MealRestController.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(null, LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")),
                Integer.parseInt(request.getParameter("userId")));
        String idString = request.getParameter("id");
        if (idString.isEmpty()) {
            controller.create(meal);
            log.info("Create {}", meal);
        } else {
            int id = Integer.parseInt(idString);
            meal.setId(id);
            controller.update(id, meal);
            log.info("Update {}", meal);
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete id={}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                return;
            case "create": {
                final Meal meal = controller.create(new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                        "", 1000));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                return;
            }
            case "update": {
                final Meal meal = controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                return;
            }

            case "all":
            default:
                Map<DateTimeFilterEnum, String> filter = new EnumMap<>(DateTimeFilterEnum.class);
                for (DateTimeFilterEnum item : DateTimeFilterEnum.values()) {
                    String paramString = request.getParameter(item.getParamName());
                    if (paramString != null && !paramString.trim().isEmpty()) filter.put(item, paramString);
                }
                if (filter.size() == 0) {
                    log.info("getAll");
                    request.setAttribute("meals", controller.getAll());
                } else {
                    log.info("getAll filtered by date/time");
                    request.setAttribute("meals", controller.getAllFiltered(filter));
                }
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        appCtx.close();
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
