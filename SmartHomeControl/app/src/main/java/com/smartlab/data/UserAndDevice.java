package com.smartlab.data;

import java.util.List;

public class UserAndDevice {

    private String username;

    private List<SmartDevice> lists;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<SmartDevice> getLists() {
        return lists;
    }

    public void setLists(List<SmartDevice> lists) {
        this.lists = lists;
    }
}
