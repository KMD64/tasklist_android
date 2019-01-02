package com.standalone.maxwell.tasklistapp;

public class Todo{
    public int id;
    public int project_id;
    public String text;
    public boolean isCompleted;
    public Todo(int id,int project_id, String desc,boolean isCompleted){
        this.id=id;
        this.project_id = project_id;
        text = desc;
        this.isCompleted=isCompleted;
    }

}
