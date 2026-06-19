package com.example.Family_life_backend.Registry;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class OnlineUserRegistry {

    private final Set<String> onlineUsers = new HashSet<>();

    public void add(String userId) {
        onlineUsers.add(userId);
    }

    public void remove(String userId) {
        onlineUsers.remove(userId);
    }

    public int count() {
        return onlineUsers.size();
    }
}
