package com.example.bindubansal.mycpdemo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    EditText eTxtName;

    EditText eTxtPhone;

    EditText eTxtEmail;

    RadioButton rbMale;

    RadioButton rbFemale;

    Spinner spCity;

    ArrayAdapter<String> adapter;

    Button btnSubmit;

    Student student, rcvStudent;

    ContentResolver resolver;

    boolean updateMode;

    RequestQueue requestQueue;

    ProgressDialog progressDialog;

    ConnectivityManager connectivityManager;

    NetworkInfo networkInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eTxtName = (EditText) findViewById(R.id.editTextName);

        eTxtPhone = (EditText) findViewById(R.id.editTextPhone);

        eTxtEmail = (EditText) findViewById(R.id.editTextEmail);

        rbMale = (RadioButton) findViewById(R.id.radioButtonMale);

        rbFemale = (RadioButton) findViewById(R.id.radioButtonFemale);

        spCity = (Spinner) findViewById(R.id.spinnerCity);

        btnSubmit = (Button) findViewById(R.id.buttonSubmit);
        btnSubmit.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);

        student = new Student();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        adapter.add("--Select City--");
        adapter.add("Ludhiana");
        adapter.add("Chandigarh");
        adapter.add("Delhi");
        adapter.add("Bengaluru");
        adapter.add("California");

        spCity.setAdapter(adapter);

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i != 0) {
                    student.setCity(adapter.getItem(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rbMale.setOnCheckedChangeListener(this);
        rbFemale.setOnCheckedChangeListener(this);

        resolver = getContentResolver();


        requestQueue = Volley.newRequestQueue(this);

        Intent rcv = getIntent();
        updateMode = rcv.hasExtra("keyStudent");

        if (updateMode) {

            rcvStudent = (Student) rcv.getSerializableExtra("keyStudent");
            eTxtName.setText(rcvStudent.getName());
            eTxtPhone.setText(rcvStudent.getPhone());
            eTxtEmail.setText(rcvStudent.getEmail());


            if (rcvStudent.getGender().equals("Male")) {

                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);

            }

            int p = 0;

            for (int i = 0;i<adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(rcvStudent.getCity())) {
                    p = i;
                    break;
                }
            }

            spCity.setSelection(p);

            btnSubmit.setText("Update");

        }
    }


    boolean isNetworkConnected() {

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());

    }


   /* public void clickHandler(View view) {

        if ((view.getId() == R.id.buttonSubmit)) {
            student.setName(eTxtName.getText().toString().trim());
            student.setName(eTxtPhone.getText().toString().trim());
            student.setEmail(eTxtEmail.getText().toString().trim());

            insertIntoDB();
        }


           if (validateFields()) {

                if (isNetworkConnected())
                               insertIntoCloud();
                    //         else
                    Toast.makeText(this, "Please connect to internet", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Please correct input", Toast.LENGTH_LONG).show();
            }}
                   }
    }*/


    @Override
    public void onClick(View view) {
        if ((view.getId() == R.id.buttonSubmit)) {
            student.setName(eTxtName.getText().toString().trim());
            student.setPhone(eTxtPhone.getText().toString().trim());
            student.setEmail(eTxtEmail.getText().toString().trim());

           // insertIntoDB();

            if (validateFields()) {
                if (isNetworkConnected())
                           insertIntoCloud();
                    else
             Toast.makeText(this, "Please connect to internet", Toast.LENGTH_LONG).show();
        } else {
        Toast.makeText(this, "Please correct input", Toast.LENGTH_LONG).show();
         }
         }
    }

    void insertIntoCloud(){

        String url = "";

        if(!updateMode){
            url = Util.INSERT_STUDENT_PHP;
        }else {
            url = Util.UPDATE_STUDENT_PHP;
        }
        progressDialog.show();
          //volley string request

       /* StringRequest request = new StringRequest(Request.Method.POST, Util.INSERT_STUDENT_PHP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Response:" +response, Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Some error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        })*/

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                JSONObject jsonObject = new JSONObject(response);
                int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");

                    if (success == 1){
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                        if (updateMode)
                            finish();
                    }else {
                        Toast.makeText(MainActivity.this,"SOme Exception",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Response:" +response, Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Some error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        })
        {
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> map = new HashMap<>();
                map.put("name",student.getName());
                map.put("phone",student.getPhone());
                map.put("email",student.getEmail());
                map.put("gender",student.getGender());
                map.put("city",student.getCity());

                return  map;
            }
        };

        requestQueue.add(request);//execute the request,send it to server

        clearFields();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        int id = compoundButton.getId();

        if (b) {

            if (id == R.id.radioButtonMale) {
                student.setGender("Male");
            } else {
                student.setGender("Female");
            }
        }
    }


    void insertIntoDB() {

        ContentValues values = new ContentValues();

        values.put(Util.COL_NAME, student.getName());
        values.put(Util.COL_PHONE, student.getPhone());
        values.put(Util.COL_EMAIL, student.getEmail());
        values.put(Util.COL_GENDER, student.getGender());
        values.put(Util.COL_CITY, student.getCity());

        if (!updateMode) {

        Uri dummy = resolver.insert(Util.STUDENT_URI, values);
        Toast.makeText(this, student.getName() + "Registered Successfully" + dummy.getLastPathSegment(), Toast.LENGTH_LONG).show();


         Log.i("Insert",student.toString());

        clearFields();

        } else {
            String where = Util.COL_ID + " = " + rcvStudent.getId();
            int i = resolver.update(Util.STUDENT_URI, values, where, null);
            if (i > 0) {
                Toast.makeText(this, "Updation Successfull", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    void clearFields() {

        eTxtName.setText("");
        eTxtPhone.setText("");
        eTxtEmail.setText("");
        spCity.setSelection(0);
        rbMale.setChecked(false);
        rbFemale.setChecked(false);

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 101, 0, "All Students");

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == 101) {
            Intent i = new Intent(MainActivity.this, AllStudentActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    boolean validateFields() {
        boolean flag = true;

        if (student.getName().isEmpty()) {

            flag = false;
            eTxtName.setError("Please Enter Name");
        }
            if (student.getPhone().isEmpty()) {

                flag = false;
                eTxtPhone.setError("Please Enter Phone");
            } else {
                if (student.getPhone().length() < 10) {

                    flag = false;
                    eTxtPhone.setError("Please Enter 10 didgits phone number");
                }
            }

            if(student.getEmail().isEmpty()) {
               flag = false;
                eTxtEmail.setError("Please Enter Email");
            } else {
                if (!student.getEmail().contains("@") && student.getEmail().contains(".")) {

                    flag = false;
                    eTxtEmail.setError("Please Enter Correct Email");
                }
            }

            return flag;
        }
}
