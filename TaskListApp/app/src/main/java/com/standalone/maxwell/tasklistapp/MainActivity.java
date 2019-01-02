package com.standalone.maxwell.tasklistapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;



import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainActivity extends AppCompatActivity {
    private ListAdapter adapter_list;
    private List<Project> list_projects;
    private List<String> list_strings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list_strings = new ArrayList<>();
        adapter_list= new ListAdapter(this);
        list_projects = new ArrayList<>();
        setContentView(R.layout.activity_main);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/OpenSans-Light.ttf").build());

        //Loading data
        Ion.with(this)
                .load(getString(R.string.server_domain) + getString(R.string.request_index))
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>(){

                    @Override
                    public void onCompleted(Exception e, JsonArray result) {

                        if(result == null){
                            System.out.println(e);
                        }
                        else{
                            System.out.println(result);
                            for(JsonElement element:result){
                                Project current_project = new Gson().fromJson(element,Project.class);
                                System.out.println(current_project.title);
                                list_projects.add(current_project);
                                list_strings.add(current_project.title);
                                //Loading todos for project
                                Ion.with(getApplicationContext())
                                        .load(getString(R.string.server_domain) + getString(R.string.request_gettodolist) + "?project_id="+current_project.id )
                                        .asJsonArray().setCallback(new FutureCallback<JsonArray>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonArray result) {
                                        if(result==null){
                                            System.out.println(e);
                                        }
                                        else{
                                            if(result.toString()!="[]"){//Empty array
                                            ArrayList<Todo> todos = new ArrayList<>();
                                            for(JsonElement elem:result) {
                                                Todo todo = new Gson().fromJson(elem, Todo.class);
                                                System.out.println(todo.text);
                                                todos.add(todo);
                                                }
                                                for(Project project:list_projects){
                                                    System.out.println(project.title);
                                                    if(project.id==todos.get(0).project_id){

                                                        project.todos=todos;//todos has at least 1 element
                                                        loadFinished(project);
                                                        break;
                                                    }
                                                }
                                            }

                                        }
                                    }
                                });

                            }
                        }
                    }
                });


        ListView lst = (ListView) findViewById(R.id.list_todo);
        lst.setAdapter(adapter_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToAddTodo(view);
            }
        });
    }
    private void loadFinished(Project prj){
        adapter_list.addHeaderItem(prj);
    }

    private void switchToAddTodo(View view){
        String[] list = new String[list_projects.size()];
        for(int i=0;i<list.length;++i){list[i] = list_projects.get(i).title;}
        Intent intent = new Intent(this,AddTodoActivity.class);
        intent.putExtra("list_projects",list);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK) {
            System.out.println("Sending data");
            JsonObject obj = new JsonObject();
            obj.addProperty("project_id",list_projects.get(data.getIntExtra("selected",0)).id);
            obj.addProperty("text",data.getStringExtra("text"));
            System.out.println(obj);
            Ion.with(this)
                    .load(getString(R.string.server_domain) + getString(R.string.request_create))
                    .setJsonObjectBody(obj).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if(result ==null)
                        System.out.println(e);
                    else{
                        System.out.println(result);

                            Todo todo = new Gson().fromJson(result, Todo.class);
                            list_projects.get(data.getIntExtra("selected", 0)).todos.add(todo);
                            adapter_list.insertContentItem(data.getIntExtra("selected", 0), todo);

                    }
                }
            });


        }
    }
    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
