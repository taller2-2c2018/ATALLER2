package taller2.ataller2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.Filters.SearchFilter;
import taller2.ataller2.Filters.UserFilter;
import taller2.ataller2.adapters.NotificacionesListAdapter;
import taller2.ataller2.adapters.UserLisAdapter;
import taller2.ataller2.model.Perfil;
import taller2.ataller2.model.PerfilFragment;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.services.AmistadesService;
import taller2.ataller2.services.NotificacionesService;
import taller2.ataller2.services.OnCallback;
import taller2.ataller2.services.ServiceLocator;


public class SearchActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private PerfilFragment.PerfilListener mPerfilListListener;
    private RecyclerView recyclerView;
    private ScrollView mScrollView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mProgressBar = findViewById(R.id.progressBar_all_users);
        mScrollView = findViewById(R.id.users_list);
        mSearchView = findViewById(R.id.search_user);
        showLoadingAllUsers(true);
        recyclerView = findViewById(R.id.listUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setUpSearchView();
        ServiceLocator.get(AmistadesService.class).getAllUsers(this, new OnCallback(){
            @Override
            public void onFinish() {
                List<Perfil> lista = ServiceLocator.get(AmistadesService.class).processAllUsers();
                recyclerView.setAdapter(new UserLisAdapter(lista, mPerfilListListener));
                showLoadingAllUsers(false);
            }
        });
    }

    private void setUpSearchView() {
        mSearchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsersByText(newText, false);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsersByText(query, true);
                return true;
            }
        });
    }

    private void filterUsersByText(String searchText, boolean notifyNoneUsers) {
        List<Perfil> usuarios = ServiceLocator.get(AmistadesService.class).processAllUsers();

        if (usuarios != null) {
            UserFilter saerchFilter = new UserFilter(searchText);
            List<Perfil> filteredUsers = saerchFilter.applyUser(usuarios);
            loadUsers(filteredUsers, notifyNoneUsers);
        }
    }

    private void loadUsers(List<Perfil> usuarios, boolean notifyNoneUsers) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new UserLisAdapter(usuarios, mPerfilListListener));
        if (notifyNoneUsers && usuarios.size() < 1) {
            Toast.makeText(this.getApplicationContext(), "No existen usuarios con ese nombre", Toast.LENGTH_LONG).show();
        }
    }

    private void showLoadingAllUsers(boolean loading) {
        mProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        mScrollView.setVisibility(loading ? View.GONE : View.VISIBLE);
        mSearchView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
}