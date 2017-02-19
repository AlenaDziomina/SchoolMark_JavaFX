package com.grouk.schoolmark.controller;

import com.grouk.schoolmark.exception.ControllerException;
import com.grouk.schoolmark.model.Lesson;
import com.grouk.schoolmark.model.Subject;
import com.grouk.schoolmark.service.ScheduleService;
import com.grouk.schoolmark.service.SubjectService;
import com.grouk.schoolmark.util.SceneLoader;
import com.grouk.schoolmark.view.TableDataModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * Controller for editing schedule window
 * Created by Alena on 12.02.2017.
 */
public class ScheduleController implements InitDataController<Integer>, Initializable {
    private static final Logger LOGGER = Logger.getLogger(ScheduleController.class);

    private final static String SUBJECT_FXML = "/fxml/subject.fxml";

    @FXML
    private GridPane scheduleGrid;

    @FXML
    private ResourceBundle resources;

    private Map<Integer, Map<Integer, ComboBox<TableDataModel>>> comboBoxMap;

    private Integer childId;

    private Collection<Lesson> schedule;

    private ScheduleService scheduleService;
    private SubjectService subjectService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        scheduleService = ScheduleService.getInstance();
        subjectService = SubjectService.getInstance();
        initSubjectComboMap();
    }

    @Override
    public void initData(Integer data) {
        childId = data;
        resetChildScheduleData();
    }

    @FXML
    public void onSubjectButtonClick() {
        try {
            URL url = getClass().getResource(SUBJECT_FXML);
            String title = resources.getString("subject.title");
            Stage stage = new Stage();
            stage.setOnCloseRequest(we -> resetChildScheduleData());

            SceneLoader.loadSceneWithData(url, stage, title, resources, childId);
        } catch (Exception e) {
            logException(e);
        }
    }

    @FXML
    public void onSaveButtonClick() {
        try {
            List<Lesson> lessons = new ArrayList<>();
            comboBoxMap.entrySet().forEach(boxEntry -> {
                Integer day = boxEntry.getKey();
                boxEntry.getValue().entrySet().forEach(entry -> {
                    Integer number = entry.getKey();
                    ComboBox<TableDataModel> combo = entry.getValue();
                    Integer selectedId = combo.getSelectionModel().getSelectedItem().getId();
                    if (selectedId == 0) {
                        selectedId = null;
                    }
                    Integer lessonId = getComboLessonId(day, number);
                    Lesson lesson = new Lesson(lessonId, day, number, selectedId);
                    lessons.add(lesson);
                });
            });
            scheduleService.updateSchedule(lessons, childId);
        } catch (Exception e) {
            logException(e);
        }
    }

    private Integer getComboLessonId(Integer day, Integer number) {
        Optional<Lesson> lesson = schedule.stream().filter(ls -> ls.getDay().equals(day) && ls.getNumber().equals
                (number)).findAny();
        if (lesson.isPresent()) {
            return lesson.get().getId();
        }
        return null;
    }

    private void resetChildScheduleData() {
        resetSubjectData();
        resetScheduleData();
    }

    private void resetSubjectData() {
        Collection<TableDataModel> subjects = subjectService.getListByChildId(childId);

        subjects.add(new TableDataModel<Subject>());
        comboBoxMap.values().forEach(map -> map.values().forEach(comboBox -> {
            comboBox.getItems().setAll(subjects);
            comboBox.getSelectionModel().selectLast();
        }));
    }

    private void resetScheduleData() {
        schedule = scheduleService.getScheduleByChildId(childId);

        schedule.forEach(lesson -> {
            ComboBox<TableDataModel> comboBox = comboBoxMap.get(lesson.getDay()).get(lesson.getNumber());
            comboBox.getItems().stream()
                    .filter(subj -> subj.getId().equals(lesson.getSubjectId())).findAny()
                    .ifPresent(comboBox.getSelectionModel()::select);
        });
    }

    private void initSubjectComboMap() {
        comboBoxMap = scheduleGrid.getChildrenUnmodifiable().stream()
                .collect(toMap(this::getComboIndex, this::initComboMap));
    }

    private Integer getComboIndex(Node node) {
        String id = node.getId();
        return Integer.parseInt(id.substring(id.length() - 1));
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, ComboBox<TableDataModel>> initComboMap(Node vBox) {
        return ((VBox) vBox).getChildrenUnmodifiable().stream()
                .filter(ComboBox.class::isInstance)
                .map(child -> (ComboBox<TableDataModel>) child)
                .peek(this::initScheduleCombo)
                .collect(toMap(this::getComboIndex, Function.identity()));
    }

    private void initScheduleCombo(ComboBox<TableDataModel> combo) {
        combo.setConverter(new StringConverter<TableDataModel>() {
            @Override
            public String toString(TableDataModel subject) {
                return subject.getName();
            }

            @Override
            public TableDataModel fromString(String string) {
                return combo.getItems().stream().filter(subject -> subject.getName().equals(string))
                        .findFirst().orElseThrow(() -> new ControllerException("Can not find selected item by name "
                                + string));
            }
        });
    }

    private void logException(Exception e) {
        LOGGER.error(e.getMessage(), e);
    }
}
