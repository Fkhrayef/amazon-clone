package com.fkhrayef.amazonclone.Service;

import com.fkhrayef.amazonclone.Model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {
    
    ArrayList<User> users = new ArrayList<>();

    public ArrayList<User> getUsers() {
        return users;
    }

    public Boolean addUser(User user) {
        // check if id is available
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                return false; // id is not available
            }
        }
        
        users.add(user);
        return true; // added successfully
    }

    public Integer updateUser(String id, User user) {
        
        // Look for the user and update it if found
        for (int i = 0; i < users.size() ; i++) {
            if (users.get(i).getId().equals(id)) {
                // check if new id is available
                for (User u : users) {
                    if (u.getId().equals(user.getId()) && !u.getId().equals(id)) {
                        return 3; // new id is not available
                    }
                }
                users.set(i, user);
                return 1; // updated successfully
            }
        }
        // if not found, return false
        return 2; // user not found
    }

    public Boolean deleteUser(String id) {
        // Look for the user and delete it if found
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                users.remove(i);
                return true;
            }
        }
        // if not found, return false
        return false;
    }

}
