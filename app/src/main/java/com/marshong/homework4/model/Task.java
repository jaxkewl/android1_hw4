package com.marshong.homework4.model;

/**
 * Created by martin on 3/4/2015.
 */
public class Task {

    private String taskName;
    private String taskDescr;
    private int id;

    public Task(String taskName, String taskDescr) {
        setTaskDescr(taskDescr);
        setTaskName(taskName);
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescr() {
        return taskDescr;
    }

    public void setTaskDescr(String taskDescr) {
        this.taskDescr = taskDescr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ID" + getId() + " - " + getTaskName() + ": " + getTaskDescr();
    }
}
