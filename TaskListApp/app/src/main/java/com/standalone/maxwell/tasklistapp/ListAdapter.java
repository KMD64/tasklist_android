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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

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

        System.out.printf("Position: %s,Type: %s \n",position,getItemViewType(position)==TYPE_CONTENT?"Content":"Header");

        if(convertView==null) {
            convertView = setupView(position);
        }
        holder = (ViewHolder) convertView.getTag();

        if(holder!=null) {
            holder.view.setText(getElementString(position));
            //setting content checkbox
            if(getItemViewType(position)==TYPE_CONTENT){
                CheckBox box =(CheckBox) holder.view;

                box.setTag(getItem(position));
                box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Todo todo = (Todo) buttonView.getTag();
                        System.out.printf("%s:%s\n",isChecked,todo.isCompleted);
                        if(isChecked==todo.isCompleted)return;
                        System.out.printf("Click! from %s : %s\n",todo.text,todo.isCompleted);

                        JsonObject object = new JsonObject();
                        object.addProperty("todo_id",todo.id);
                        Ion.with(buttonView.getContext())
                                .load("https://maxwell-tasklist.herokuapp.com/update")
                                .setJsonObjectBody(object).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                System.out.println(e);
                            }
                        });
                        todo.isCompleted=isChecked;
                    }
                });
                box.setChecked(((Todo)getItem(position)).isCompleted);
            }
        }

        return convertView;

    }


    private String getElementString(int position){
        int rowtype = getItemViewType(position);
        switch(rowtype){
            case TYPE_HEADER:{
                return ((Project)getItem(position)).title;
            }
            case TYPE_CONTENT:{
                Todo todo = (Todo)getItem(position);
                //System.out.printf("%s:%s\n",todo.text,todo.isCompleted);
                return todo.text;

            }

        }
        return "";
    }

    private View setupView(int position){
        int rowtype = getItemViewType(position);
        View view = null;
        ViewHolder holder  = new ViewHolder();
        switch(rowtype) {
            case TYPE_HEADER: {
                view = inflater.inflate(R.layout.listview_header,null);

                holder.view = view.findViewById(R.id.text_header);
                break;
            }

            case TYPE_CONTENT:{
                view = inflater.inflate(R.layout.listview_content,null);
                //Going to use later conversion to checkbox
                holder.view = view.findViewById(R.id.textcheck_todo);
                break;
            }
        }
        view.setTag(holder);
        return view;
    }

    public static class ViewHolder{
        public TextView view = null;

    }
}
