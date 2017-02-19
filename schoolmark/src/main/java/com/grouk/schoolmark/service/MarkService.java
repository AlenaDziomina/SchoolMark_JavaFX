package com.grouk.schoolmark.service;

import com.grouk.schoolmark.dao.MarkDao;
import com.grouk.schoolmark.dao.SubjectDao;
import com.grouk.schoolmark.model.Mark;
import com.grouk.schoolmark.model.Subject;
import com.grouk.schoolmark.view.MarkTableDataModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * School Mark service
 * Created by Alena on 12.02.2017.
 */
public class MarkService {
    private static final MarkService SERVICE;
    private static final MarkDao MARK_DAO;
    private static final SubjectDao SUBJECT_DAO;

    static {
        SERVICE = new MarkService();
        MARK_DAO = new MarkDao();
        SUBJECT_DAO = new SubjectDao();
    }

    private MarkService() {
    }

    public static MarkService getInstance() {
        return SERVICE;
    }

    public Collection<MarkTableDataModel> getMarkListByChildId(Integer childId) {
        List<Subject> subjects = SUBJECT_DAO.getSubjectListByChildId(childId);
        Map<Integer, String> subjectMap = subjects.stream()
                .collect(Collectors.toMap(Subject::getId, Subject::getName));
        List<Mark> markList = MARK_DAO.getMarkListByChildId(childId);
        Collection<MarkTableDataModel> markTableDataModels = markList.stream().map(MarkTableDataModel::new).collect
                (Collectors.toList());
        markTableDataModels.forEach(m -> m.setSubject(subjectMap.get(m.getSubjectId())));
        return markTableDataModels;
    }

    public void delete(Integer id) {
        MARK_DAO.deleteMark(id);
    }

    public Integer save(Integer childId, Date date, Integer subjectId, Integer mark, String description) {
        return MARK_DAO.addMark(childId, date, subjectId, mark, description);
    }

    public void update(Integer id, Date date, Integer subjectId, Integer mark, String description) {
        MARK_DAO.updateMark(id, date, subjectId, mark, description);
    }

}
