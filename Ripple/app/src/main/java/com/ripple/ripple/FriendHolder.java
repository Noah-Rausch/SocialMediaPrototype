package com.ripple.ripple;

import java.util.ArrayList;

public class FriendHolder {
    private ArrayList<String> friendRequests = new ArrayList<>();
    private ArrayList<String> friends = new ArrayList<>();

    public FriendHolder(){

    }

    public void addFriend(String friend){
        friends.add(friend);
    }

    public void addRequest(String req){
        friendRequests.add(req);
    }

    public void removeRequest(String name){
        friendRequests.remove(name);
    }

    public ArrayList<String> getFriendRequests() {
        return friendRequests;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }
}
