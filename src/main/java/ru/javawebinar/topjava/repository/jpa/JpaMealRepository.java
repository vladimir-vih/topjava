package ru.javawebinar.topjava.repository.jpa;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user = getUserReference(userId);
        meal.setUser(user);
        if (meal.isNew()) {
            em.persist(meal);
            return meal;
        } else {
            return em.createQuery("UPDATE Meal m " +
                            "SET m.dateTime=:dateTime, m.description=:description, m.calories=:calories " +
                            "WHERE m.id=:id AND m.user=:user")
                    .setParameter("dateTime", meal.getDateTime())
                    .setParameter("description", meal.getDescription())
                    .setParameter("calories", meal.getCalories())
                    .setParameter("id", meal.getId())
                    .setParameter("user", user)
                    .executeUpdate() == 0 ? null : meal;
        }
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createQuery("DELETE FROM Meal m WHERE m.id=:id AND m.user=:user")
                .setParameter("id", id)
                .setParameter("user", getUserReference(userId))
                .executeUpdate() != 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Meal get(int id, int userId) {
        List<Meal> meals = em.createQuery("SELECT m FROM Meal m WHERE m.id=:id AND m.user=:user",
                        Meal.class)
                .setParameter("id", id)
                .setParameter("user", getUserReference(userId))
                .getResultList();
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meal> getAll(int userId) {
        return em.createQuery("SELECT m FROM Meal m WHERE m.user=:user ORDER BY m.dateTime desc", Meal.class)
                .setParameter("user", getUserReference(userId))
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return em.createQuery("SELECT m " +
                        "FROM Meal m " +
                        "WHERE m.user=:user AND m.dateTime >=:startDate AND m.dateTime <:endDate " +
                        "ORDER BY m.dateTime desc", Meal.class)
                .setParameter("user", getUserReference(userId))
                .setParameter("startDate", startDateTime)
                .setParameter("endDate", endDateTime)
                .getResultList();
    }

    private User getUserReference(int userId){
        return em.getReference(User.class, userId);
    }
}