package taller2.ataller2.model;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taller2.ataller2.LoginActivity;
import taller2.ataller2.adapters.HistoriasListAdapter;
import taller2.ataller2.services.PerfilService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.HistoriasService;
import taller2.ataller2.R;
public class PerfilFragment extends Fragment implements Refresh{

    private PerfilFragment.PerfilListener mPerfilListener;
    private RecyclerView mRecyclerView;
    private ListadoHistoriasFragment.HistoriasListListener mHistoriasListListener;

    @Override
    public void refresh() {

    }


    public interface PerfilListener {
        void onPerfilClicked(Perfil perfil);
    }

    public PerfilFragment() {
    }

    public static PerfilFragment newInstance(int columnCount) {
        PerfilFragment fragment = new PerfilFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = container.getChildAt(0);
        View view = inflater.inflate(R.layout.activity_mi_perfil, container, false);

        mRecyclerView = view.findViewById(R.id.historias_perfil_local);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        HistoriasService historiasService = getHistoriasService();
        mRecyclerView.setAdapter(new HistoriasListAdapter(historiasService.getHistorias(this.getActivity()), mHistoriasListListener));

        AppCompatButton exit = view.findViewById(R.id.salir_app);

        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                exit();
            }
        });

        return view;
    }

    public void exit(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PerfilFragment.PerfilListener) {
            mPerfilListener = (PerfilFragment.PerfilListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PerfilListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPerfilListener = null;
    }

    private HistoriasService getHistoriasService() {
        return ServiceLocator.get(HistoriasService.class);
    }

    private PerfilService getPerfilService() {
        return ServiceLocator.get(PerfilService.class);
    }
}