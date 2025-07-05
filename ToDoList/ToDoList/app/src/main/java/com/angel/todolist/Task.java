package com.angel.todolist;

public class Task {
    private String title;
    private boolean completed;
    public Task(String title,boolean completed){
        this.title=title;
        this.completed=completed;
    }
    public String getTitle(){
        return title;

    }
    public boolean isCompleted(){
        return completed;
    }
    public void setCompleted(boolean completed){
        this.completed=completed;
    }
    public void setTitle(String title){
        this.title=title;
    }
}
