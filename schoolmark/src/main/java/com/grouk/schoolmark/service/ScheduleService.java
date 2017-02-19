package com.grouk.schoolmark.service;

import com.grouk.schoolmark.dao.LessonDao;
import com.grouk.schoolmark.model.Lesson;

import java.util.Collection;
import java.util.List;

/**
 * Service of school lessons schedule
 * Created by Alena on 12.02.2017.
 */
public class ScheduleService {
    private static final ScheduleService SERVICE;
    private static final LessonDao LESSON_DAO;

    static {
        SERVICE = new ScheduleService();
        LESSON_DAO = new LessonDao();
    }

    private ScheduleService() {
    }

    public static ScheduleService getInstance() {
        return SERVICE;
    }

    public Collection<Lesson> getScheduleByChildId(Integer childId) {
        return LESSON_DAO.getScheduleByChildId(childId);
    }

    public void updateSchedule(List<Lesson> schedule, Integer childId) {
        schedule.stream().filter(ls -> ls.getId() == null && ls.getSubjectId() != null).forEach(ls -> LESSON_DAO.addLesson(childId,
                ls.getSubjectId(), ls.getDay(), ls.getNumber()));
        schedule.stream().filter(ls -> ls.getId() != null && ls.getSubjectId() == null).forEach(ls -> LESSON_DAO
                .deleteLesson(ls.getId()));
        schedule.stream().filter(ls -> ls.getId() != null && ls.getSubjectId() != null).forEach(ls ->
                LESSON_DAO.updateLesson(ls.getSubjectId(), ls.getId()));
    }
}
