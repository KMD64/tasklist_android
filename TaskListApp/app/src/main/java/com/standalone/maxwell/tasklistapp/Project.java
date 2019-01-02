package com.standalone.maxwell.tasklistapp;


import java.util.ArrayList;

public class Project{
    public int id;
    public String title;
    public ArrayList<Todo> todos;
    public Project(int id,String title){
        this.id=id;
        this.title=title;
    }

}
