package com.example.bindubansal.mycpdemo;

import java.io.Serializable;

/**
 * Created by BINDU BANSAL on 21/04/2017.
 */

public class Student implements Serializable {
//  POJO or BEAN file
    //attributes
    int id;
    String name,phone,email,gender,city;

    //Constructors
    public Student(){

    }

    public Student(int id,String name, String phone, String email, String gender, String city) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.city= city;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getPhone() {

        return phone;
    }

    public void setPhone(String phone) {

        this.phone = phone;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getGender() {

        return gender;
    }

    public void setGender(String gender) {

        this.gender = gender;
    }

    public String getCity() {

        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}

