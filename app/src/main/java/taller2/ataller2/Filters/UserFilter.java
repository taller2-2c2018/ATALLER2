package taller2.ataller2.Filters;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.Perfil;

public class UserFilter implements Filter {
    String nombreUserFiltro;

    public UserFilter( String nombreFiltro ) {
        nombreUserFiltro = nombreFiltro.toLowerCase();
    }

    @Override
    public List<String> apply(List<String> userList) {
        List<String> filteredList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            String user = userList.get(i);
            if (user.toLowerCase().contains(nombreUserFiltro)) {
                filteredList.add(user);
            }
        }
        return filteredList;
    }

    @Override
    public List<Perfil> applyUser(List<Perfil> userList) {
        List<Perfil> filteredList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            Perfil user = userList.get(i);
            if (user.getNombre().toLowerCase().contains(nombreUserFiltro)) {
                filteredList.add(user);
            }
        }
        return filteredList;
    }

}