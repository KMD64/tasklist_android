package com.standalone.maxwell.tasklistapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AddTodoActivity extends Activity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private String[] projects;
    private ListView list_view;
    private int selected = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        projects = this.getIntent().getExtras().getStringArray("list_projects");

        list_view = findViewById(R.id.list_projects);
        ArrayAdapter<String> adapter_array = new ArrayAdapter<>(this,R.layout.projectlist_cell,R.id.project_cell);
            adapter_array.addAll(projects);
            if(!adapter_array.isEmpty()){
        list_view.setAdapter(adapter_array);
        list_view.setSelection(0);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(selected!= position) {
                    selected = position;
                }
                System.out.println(id);
            }
        });
        }

        ImageButton cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        ImageButton okButton = findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.textinput_todo);
                Intent intent = new Intent();
                intent.putExtra("text",text.getText().toString());
                intent.putExtra("selected",selected);
                setResult(RESULT_OK,intent);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);finish();
    }
}
