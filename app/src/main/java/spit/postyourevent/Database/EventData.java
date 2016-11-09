package spit.postyourevent.Database;

import java.util.HashMap;

import spit.postyourevent.Constants;

/**
 * Created by Tejas on 06/10/2016.
 */

public class EventData {

    private String name;
    private String description;
    private String eventTime;
    private String venue;
    private String userName;

    public EventData() {
    }

    public EventData(String name, String description, String eventTime, String venue, String userData ) {
        this.description = description;
        this.name = name;
        this.eventTime = eventTime;
        this.userName = userData;
        this.venue = venue;
    }

    public HashMap<String,Object> getHashMap(){
        HashMap<String,Object> hashmap = new HashMap<>();
        hashmap.put(Constants.EVENT_NAME,name);
        hashmap.put(Constants.EVENT_DESCRIPTION,description);
        hashmap.put(Constants.EVENT_TIME,eventTime);
        hashmap.put(Constants.EVENT_VENUE,venue);
        hashmap.put(Constants.EVENT_USER,userName.replace("@gmail.com",""));
        return hashmap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String geteventTime() {
        return eventTime;
    }

    public void seteventTime(String time) {
        this.eventTime = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
