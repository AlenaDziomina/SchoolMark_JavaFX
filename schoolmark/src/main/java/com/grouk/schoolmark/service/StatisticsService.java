package com.grouk.schoolmark.service;

import com.grouk.schoolmark.dao.MarkDao;
import com.grouk.schoolmark.dao.SubjectDao;

/**
 * Created by Alena on 15.02.2017.
 */
public class StatisticsService {
    private static final StatisticsService SERVICE;

    static {
        SERVICE = new StatisticsService();
    }

    private StatisticsService() {
    }

    public static StatisticsService getInstance() {
        return SERVICE;
    }
}
