package taller2.ataller2.Filters;

import java.util.List;

import taller2.ataller2.model.Perfil;
import taller2.ataller2.model.User;

public interface Filter {
    List<String> apply(List<String> commerceList);
    List<Perfil> applyUser(List<Perfil> commerceList);
}
