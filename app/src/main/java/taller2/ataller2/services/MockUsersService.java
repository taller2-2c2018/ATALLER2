package taller2.ataller2.services;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.User;

public class MockUsersService implements UsersService {

    List<User> mUsers = new ArrayList<>();
    private Context mContext;

    public MockUsersService(Context context){
        armarUsers();
        mContext = context;
    }

    @Override
    public List<User> getUsers() {
        armarUsers();
        return mUsers;
    }

    @Override
    public List<String> getUsersName() {
        armarUsers();
        List<String> names = new ArrayList<>();
        for (User user: mUsers) {
            names.add(user.getName());
        }
        return names;
    }

    private void armarUsers () {
        User user1 = new User("Fernando Nitz");
        User user2 = new User("Lionel Messi");
        User user3 = new User("Ringo Starr");
        User user4 = new User("Ramiro Funes Mori");
        User user5 = new User("Luca Proda");

        mUsers.add(user1);
        mUsers.add(user2);
        mUsers.add(user3);
        mUsers.add(user4);
        mUsers.add(user5);
    }

}
