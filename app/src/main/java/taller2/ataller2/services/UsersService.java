package taller2.ataller2.services;
import java.util.List;

import taller2.ataller2.model.User;

public interface UsersService extends CustomService {
    List<User> getUsers();
    List<String> getUsersName();
}