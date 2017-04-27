package com.example.bindubansal.mycpdemo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllStudentActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;

    EditText eTxtSearch;

    ArrayList<Student> studentList;

    ContentResolver resolver;

    StudentAdapter adapter;

    Student student;

    int pos;

    RequestQueue requestQueue;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_student);

        listView = (ListView)findViewById(R.id.listView);
        eTxtSearch = (EditText)findViewById(R.id.editTextSearch);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);

        requestQueue = Volley.newRequestQueue(this);

        resolver = getContentResolver();

        eTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String str = charSequence.toString();

                if (adapter!= null){
                    adapter.filter(str);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //retrieveFromDB();
        retrieveFromCloud();
    }
    void retrieveFromCloud(){

        progressDialog.show();
        studentList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Util.RETRIEVE_STUDENT_PHP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("students");

                    int id = 0;
                    String n = "", p = "", e = "", g = "", c = "";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        id = jObj.getInt("id");
                        n = jObj.getString("name");
                        p = jObj.getString("phone");
                        e = jObj.getString("email");
                        g = jObj.getString("gender");
                        c = jObj.getString("city");

                        studentList.add(new Student(id, n, p, e, g, c));
                    }

                    adapter = new StudentAdapter(AllStudentActivity.this, R.layout.listitem, studentList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(AllStudentActivity.this);

                    progressDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(AllStudentActivity.this, "some Exception", Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(AllStudentActivity.this,"Some Error",Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(stringRequest);//execute the request

    }

    void retrieveFromDB(){
        studentList = new ArrayList<>();

        String[] projection = {Util.COL_ID,Util.COL_NAME,Util.COL_PHONE,Util.COL_EMAIL,Util.COL_GENDER,Util.COL_CITY};
        Cursor cursor = resolver.query(Util.STUDENT_URI,projection,null,null,null);

        if(cursor!=null){

            int i=0;
            String n="",p="",e="",g="",c="";

            while (cursor.moveToNext()){
                i = cursor.getInt(cursor.getColumnIndex(Util.COL_ID));
                n = cursor.getString(cursor.getColumnIndex(Util.COL_NAME));
                p = cursor.getString(cursor.getColumnIndex(Util.COL_PHONE));
                e = cursor.getString(cursor.getColumnIndex(Util.COL_EMAIL));
                g = cursor.getString(cursor.getColumnIndex(Util.COL_GENDER));
                c = cursor.getString(cursor.getColumnIndex(Util.COL_CITY));

                studentList.add(new Student(i,n,p,e,g,c));
            }

            adapter = new StudentAdapter(this,R.layout.listitem,studentList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        pos = i;

        student = studentList.get(i);
        showOptions();
    }

    void showOptions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"View","Update","Delete"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i){
                    case 0:
                        showStudent();
                        break;

                    case 1:
                        Intent intent = new Intent(AllStudentActivity.this,MainActivity.class);
                        intent.putExtra("keyStudent",student);
                        startActivity(intent);
                        break;

                    case 2:
                        deleteStudent();
                        break;
                }
            }
        });

        builder.create().show();
    }

    void showStudent(){

        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setTitle("Details of "+student.getName());
        builder.setMessage(student.toString());
        builder.setPositiveButton("Done",null);
        builder.create().show();
    }

    void deleteStudent(){

        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setTitle("Delete "+ student.getName());
        builder.setMessage("Do you wish to Delete?");
        builder.setPositiveButton("Ok ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               /* //code goes here to delete record from db
                String where = Util.COL_ID+ " = "+student.getId();

                //String where = Util.COL_NAME+ " = "+student.getName();
                int j = resolver.delete(Util.STUDENT_URI,where,null);

                if (j>0){
                    studentList.remove(pos);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(AllStudentActivity.this,student.getName()+" deleted Successfully..",Toast.LENGTH_LONG).show();

                }*/
                     deleteFromCloud();
            }
        });

        builder.setNegativeButton("Cancel",null);
        builder.create().show();
    }

    void deleteFromCloud(){
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Util.DELETE_STUDENT_PHP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");

                    if(success == 1){
                        studentList.remove(pos);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(AllStudentActivity.this,message,Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(AllStudentActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(AllStudentActivity.this,"some Exception",Toast.LENGTH_LONG).show();
                }

            }

            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AllStudentActivity.this,"Some volley error: "+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        })
        {
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String, String> map = new HashMap<>();
                map.put("id",String.valueOf(student.getId()));
                return map;
}
};
requestQueue.add(request);
}
}