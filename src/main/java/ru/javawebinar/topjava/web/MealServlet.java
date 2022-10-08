package ru.javawebinar.topjava.web;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MealServlet extends HttpServlet {
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
        } else {
            controller.update(Integer.parseInt(idString), meal);
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "delete":
                controller.delete(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect("meals");
                return;
            case "create": {
                final Meal meal = controller.create(new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                return;
            }
            case "update": {
                request.setAttribute("meal", controller.get(Integer.parseInt(request.getParameter("id"))));
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                return;
            }
            case "filter": {
                request.setAttribute("meals", controller.getAllFiltered(getDate(request, "dateFrom"),
                        getDate(request, "dateTo"), getTime(request, "timeFrom"),
                        getTime(request, "timeTo")));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            }
            case "all":
            default: {
                request.setAttribute("meals", controller.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            }
        }
    }

    private LocalDate getDate(HttpServletRequest request, String name) {
        String dateString = request.getParameter(name);
        return StringUtils.hasLength(dateString) ?
                LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    private LocalTime getTime(HttpServletRequest request, String name) {
        String timeString = request.getParameter(name);
        return StringUtils.hasLength(timeString) ?
                LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME) : null;
    }

    @Override
    public void destroy() {
        appCtx.close();
    }
}
