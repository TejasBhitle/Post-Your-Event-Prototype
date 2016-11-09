package spit.postyourevent.Database;

import java.util.HashMap;

import spit.postyourevent.Constants;

public class UserData {

    private String name;
    private String email_id;
    private String contact_no;
    private String branch;
    private String year;
    private String rollno;

    public HashMap<String,Object> getHashMap(){
        HashMap<String,Object> hashmap = new HashMap<>();
        hashmap.put(Constants.USER_NAME,name);
        hashmap.put(Constants.USER_EMAIL,email_id);
        hashmap.put(Constants.USER_CONTACT,contact_no);
        hashmap.put(Constants.USER_ROLL_NO,rollno);
        hashmap.put(Constants.USER_BRANCH,branch);
        hashmap.put(Constants.USER_YEAR,year);
        return hashmap;
    }


    public UserData(String name, String email_id, String branch, String year, String contact_no, String rollno) {
        this.branch = branch;
        this.contact_no = contact_no;
        this.email_id = email_id;
        this.name = name;
        this.year = year;
        this.rollno = rollno;
    }

    public String getBranch() {
        return branch;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getContact_no() {
        return contact_no;
    }

    public String getEmail_id() {
        return email_id;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(String year) {
        this.year = year;
    }

}
