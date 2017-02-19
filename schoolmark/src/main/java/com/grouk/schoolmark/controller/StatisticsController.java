package com.grouk.schoolmark.controller;

import com.grouk.schoolmark.service.StatisticsService;
import com.grouk.schoolmark.service.SubjectService;
import com.grouk.schoolmark.view.TableDataModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Alena on 15.02.2017.
 */
public class StatisticsController implements InitDataController<Integer>, Initializable {

    @FXML
    private ResourceBundle resources;

    private StatisticsService statisticsService;

    private Integer childId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statisticsService = StatisticsService.getInstance();
    }

    @Override
    public void initData(Integer childId) {
        this.childId = childId;
    }
}
