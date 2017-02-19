package com.grouk.schoolmark.controller;

import com.grouk.schoolmark.exception.ControllerException;
import com.grouk.schoolmark.service.ChildService;
import com.grouk.schoolmark.service.MarkService;
import com.grouk.schoolmark.service.SubjectService;
import com.grouk.schoolmark.util.DateConverter;
import com.grouk.schoolmark.util.SceneLoader;
import com.grouk.schoolmark.view.MarkTableDataModel;
import com.grouk.schoolmark.view.TableDataModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controller for main window
 */
public class MainController implements Initializable {

    private final static String CHILDREN_FXML = "/fxml/children.fxml";
    private final static String SCHEDULE_FXML = "/fxml/schedule.fxml";
    private final static String STATISTICS_FXML = "/fxml/statistics.fxml";
    private static final Logger LOGGER = Logger.getLogger(MainController.class);
    public Button schBtn;

    @FXML
    private ComboBox<TableDataModel> chdCombo;

    @FXML
    private TableView<MarkTableDataModel> markList;

    @FXML
    private TableColumn<MarkTableDataModel, Integer> idMark;

    @FXML
    private TableColumn<MarkTableDataModel, LocalDate> markDate;

    @FXML
    private TableColumn<MarkTableDataModel, Integer> subjId;

    @FXML
    private TableColumn<MarkTableDataModel, String> markSubj;

    @FXML
    private TableColumn<MarkTableDataModel, Integer> mark;

    @FXML
    private TableColumn<MarkTableDataModel, String> markDesc;

    @FXML
    private ComboBox<TableDataModel> subjCombo;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField markText;

    @FXML
    private TextField descText;

    @FXML
    private ResourceBundle resources;

    private ObservableList<TableDataModel> data;
    private ObservableList<TableDataModel> subjData;
    private ObservableList<MarkTableDataModel> markData;

    private ChildService childService;
    private SubjectService subjectService;
    private MarkService markService;

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        childService = ChildService.getInstance();
        subjectService = SubjectService.getInstance();
        markService = MarkService.getInstance();

        initChildCombo();
        initSubjectCombo();
        data = chdCombo.getItems();
        subjData = subjCombo.getItems();
        markData = markList.getItems();

        idMark.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        markDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        markDate.setCellFactory(column -> new TableCell<MarkTableDataModel, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(myDateFormatter.format(item));
                }
            }
        });
        subjId.setCellValueFactory(cellData -> cellData.getValue().subjectIdProperty().asObject());
        markSubj.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
        mark.setCellValueFactory(cellData -> cellData.getValue().markProperty().asObject());
        markDesc.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        markText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            if (!newValue.matches("\\d*")) {
                markText.setText(newValue.replaceAll("[^\\d]", ""));
                newValue = markText.getText();
            }
            Integer mark1 = Integer.parseInt(newValue);
            if (mark1 > 10) {
                String s = newValue.substring(0, newValue.length() - 1);
                markText.setText(s);
            }
        });

        resetChildMarkData();
        setSelectedDate(new Date());
    }

    private void initChildCombo() {
        chdCombo.setConverter(new StringConverter<TableDataModel>() {
            @Override
            public String toString(TableDataModel child) {
                return child.getName();
            }

            @Override
            public TableDataModel fromString(String string) {
                return data.stream().filter(subject -> subject.getName().equals(string)).findFirst()
                        .orElseThrow(() -> new ControllerException("Can not find selected item by name " + string));
            }
        });

        chdCombo.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            clearInputData();
            resetScheduleData();
        });
    }

    private void initSubjectCombo() {
        subjCombo.setConverter(new StringConverter<TableDataModel>() {
            @Override
            public String toString(TableDataModel subject) {
                return subject.getName();
            }

            @Override
            public TableDataModel fromString(String string) {
                return subjData.stream().filter(subject -> subject.getName().equals(string))
                        .findFirst().orElseThrow(() -> new ControllerException("Can not find selected item by name "
                                + string));
            }
        });
    }

    @FXML
    public void onChildButtonClick() {
        try {
            URL url = getClass().getResource(CHILDREN_FXML);
            String title = resources.getString("child.title");
            Stage stage = new Stage();
            stage.setOnCloseRequest(we -> resetChildMarkData());

            SceneLoader.loadScene(url, stage, title, resources);
        } catch (Exception e) {
            logException(e);
        }
    }


    @FXML
    public void onScheduleButtonClick() {
        try {
            TableDataModel child = getSelectedChild();
            if (child == null) {
                return;
            }

            URL url = getClass().getResource(SCHEDULE_FXML);
            String title = resources.getString("schedule.title");
            Stage stage = new Stage();
            stage.setOnCloseRequest(we -> resetScheduleData());

            SceneLoader.loadSceneWithData(url, stage, title, resources, child.getId());
        } catch (Exception e) {
            logException(e);
        }
    }

    @FXML
    private void onStatisticsButtonClick(){
        try {
            TableDataModel child = getSelectedChild();
            if (child == null) {
                return;
            }

            URL url = getClass().getResource(STATISTICS_FXML);
            String title = resources.getString("statistics.title");
            Stage stage = new Stage();

            SceneLoader.loadSceneWithData(url, stage, title, resources, child.getId());
        } catch (Exception e) {
            logException(e);
        }
    }

    @FXML
    public void onAddMarkButtonClick() {
        try {
            TableDataModel child = getSelectedChild();
            TableDataModel subject = getSelectedSubject();
            if (child != null && subject != null) {
                Integer childId = child.getId();
                Integer subjectId = subject.getId();
                Date newDate = getSelectedDate();
                Integer mark = Integer.parseInt(markText.getText());
                String description = descText.getText();
                if (mark > 0) {
                    Integer id = markService.save(childId, newDate, subjectId, mark, description);
                    String subjectName = getSelectedSubject().getName();
                    MarkTableDataModel newMark = new MarkTableDataModel(id, DateConverter.convert(newDate), subjectId, subjectName,
                            mark, description);
                    markData.add(newMark);
                }
            }
        } catch (Exception e) {
            logException(e);
        }
    }

    @FXML
    public void onUpdateMarkButtonClick() {
        try {
            TableDataModel child = getSelectedChild();
            TableDataModel subject = getSelectedSubject();
            MarkTableDataModel selectedMark = getSelectedMark();
            if (child != null && getSelectedSubject() != null && selectedMark != null) {
                Integer markId = selectedMark.getId();
                Integer subjectId = subject.getId();
                Date newDate = getSelectedDate();
                Integer mark = Integer.parseInt(markText.getText());
                String description = descText.getText();
                if (mark > 0) {
                    markService.update(markId, newDate, subjectId, mark, description);
                    String subjectName = getSelectedSubject().getName();
                    selectedMark.setDate(datePicker.getValue());
                    selectedMark.setSubjectId(subjectId);
                    selectedMark.setSubject(subjectName);
                    selectedMark.setMark(mark);
                    selectedMark.setDescription(description);
                }
            }
        } catch (Exception e) {
            logException(e);
        }
    }

    @FXML
    public void onDeleteMarkButtonClick() {
        try {
            TableDataModel child = getSelectedChild();
            MarkTableDataModel selectedMark = getSelectedMark();
            if (child != null && selectedMark != null) {
                Integer markId = selectedMark.getId();
                markService.delete(markId);
                markData.remove(selectedMark);
            }
        } catch (Exception e) {
            logException(e);
        }
    }

    @FXML
    private void onMarkTableClick() {
        try {
            MarkTableDataModel mark = getSelectedMark();
            if (mark != null) {
                datePicker.setValue(mark.getDate());
                int selectedSubjectId = mark.getSubjectId();
                TableDataModel selectedModel = subjData.stream().filter(subj -> subj.getId().equals(selectedSubjectId))
                        .findFirst().orElseThrow(() -> new ControllerException("Can not find selected item by subjectId " +
                                selectedSubjectId));

                subjCombo.getSelectionModel().select(selectedModel);
                markText.setText(String.valueOf(mark.getMark()));
                descText.setText(mark.getDescription());
            } else {
                clearInputData();
            }
        } catch (Exception e) {
            logException(e);
        }
    }

    private MarkTableDataModel getSelectedMark() {
        return markList.getSelectionModel().getSelectedItem();
    }

    private Date getSelectedDate() {
        LocalDate localDate = datePicker.getValue();
        return DateConverter.convert(localDate);
    }

    private void setSelectedDate(Date date) {
        LocalDate localDate = DateConverter.convert(date);
        datePicker.setValue(localDate);
    }

    private void resetScheduleData() {
        TableDataModel child = getSelectedChild();
        if (child != null) {
            Integer selectedChildId = child.getId();
            resetSubjectData(selectedChildId);
            resetMarkData(selectedChildId);
        } else {
            clearAllData();
        }
    }

    private void clearAllData() {
        markData.clear();
        subjData.clear();
        clearInputData();
    }

    private void clearInputData() {
        setSelectedDate(new Date());
        subjCombo.getSelectionModel().select(null);
        markText.setText(null);
        descText.setText(null);
    }

    private void resetSubjectData(Integer childId) {
        Collection<TableDataModel> subject = subjectService.getListByChildId(childId);
        subjData.setAll(subject);
    }

    private void resetMarkData(Integer childId) {
        Collection<MarkTableDataModel> marks = markService.getMarkListByChildId(childId);
        markData.setAll(marks);
    }

    private void resetChildMarkData() {
        resetChildData();
        resetScheduleData();
    }

    private void resetChildData() {
        Collection<TableDataModel> children = childService.getList();
        data.setAll(children);
        chdCombo.getSelectionModel().selectFirst();
    }

    private TableDataModel getSelectedChild() {
        return chdCombo.getSelectionModel().getSelectedItem();
    }

    private TableDataModel getSelectedSubject() {
        return subjCombo.getSelectionModel().getSelectedItem();
    }

    private void logException(Exception e) {
        LOGGER.error(e.getMessage(), e);
    }
}
