package com.standalone.maxwell.tasklistapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AddTodoActivity extends Activity {
    private String[] projects;
    int selected=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        projects = this.getIntent().getExtras().getStringArray("list_projects");

        Spinner spinner = (Spinner)findViewById(R.id.spinner_projects);
        final ArrayAdapter<String> adapter_array = new ArrayAdapter<>(this,R.layout.projectlist_cell,R.id.project_cell);
            adapter_array.addAll(projects);
        spinner.setAdapter(adapter_array);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        Button okButton = findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText text = findViewById(R.id.textinput_todo);
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
