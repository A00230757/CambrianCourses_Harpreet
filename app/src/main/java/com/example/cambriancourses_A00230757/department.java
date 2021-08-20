package com.example.cambriancourses_A00230757;
//department class with name , description and path
public class department {
    public String name;
    public String description;
    public String path;

    //constructor with no argumants
    department(){
        name="";
        description = "";
    }
    //constructor with arguments
    public department(String name,String description,String path)
    {
        this.name=name;
        this.description = description;
        this.path = path;
    }


}
