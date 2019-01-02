package com.standalone.maxwell.tasklistapp;

import android.app.Application;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter{
    public static final int TYPE_CONTENT=0;
    public static final int TYPE_HEADER=1;
    private ArrayList<Object> data = new ArrayList<>();
    private ArrayList<Integer> headerid = new ArrayList<>();
    private LayoutInflater inflater;



    public ListAdapter(Context context){
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addHeaderItem(final Project item){
        data.add(item);
        headerid.add(data.size()-1);
        for(Todo todo:item.todos)
            data.add(todo);
        notifyDataSetChanged();
    }
    public void insertContentItem(final int prjpos, final Todo item){
        data.add(headerid.get(prjpos+1),item);
        if(prjpos<headerid.size()-1)
        for(int i=prjpos+1;i<headerid.size();++i){
            headerid.set(i,headerid.get(i)+1);
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        return headerid.contains(position)?TYPE_HEADER:TYPE_CONTENT;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        int rowType = getItemViewType(position);
        if(convertView==null){
            holder=new ViewHolder();
            switch(rowType){
                case TYPE_HEADER:{
                    convertView = inflater.inflate(R.layout.listview_header,null);
                    holder.view = (TextView)convertView.findViewById(R.id.text_header);
                    break;
                }
                case TYPE_CONTENT:{
                    convertView = inflater.inflate(R.layout.listview_content,null);
                    holder.checkBox = (CheckBox) convertView.findViewById(R.id.textcheck_todo);

                    holder.checkBox.setText(((Todo) data.get(position)).text);
                    holder.view = holder.checkBox;

                    }
            }
            convertView.setTag(holder);

            System.out.printf("Setting holder %s to %s \n",holder.view.getText(),position);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }
        switch(rowType){
            case TYPE_CONTENT: {
                holder.view.setText(((Todo)data.get(position)).text);
                holder.checkBox.setChecked(((Todo) data.get(position)).isCompleted);
                holder.checkBox.setTag(data.get(position));//need to update task item
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Todo focusedTodo = (Todo)buttonView.getTag();
                        if(focusedTodo.isCompleted==isChecked)return;
                        JsonObject obj = new JsonObject();
                        obj.addProperty("todo_id",focusedTodo.id);
                        System.out.println(obj);
                        Ion.with(buttonView.getContext()).load("https://maxwell-tasklist.herokuapp.com/update")//That should be in main activity
                                .setJsonObjectBody(obj).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {

                            }
                        });
                    }

                });


                break;}
            case TYPE_HEADER:{holder.view.setText(((Project)data.get(position)).title);break;}
        }

        return convertView;
    }

    public static class ViewHolder{
        public TextView view = null;
        public CheckBox checkBox = null;
    }



}
