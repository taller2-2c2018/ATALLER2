package taller2.ataller2.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.R;
import taller2.ataller2.adapters.ConversacionListAdapter;
import taller2.ataller2.services.ConversacionService;
import taller2.ataller2.services.MiPerfilService;
import taller2.ataller2.services.OnCallback;
import taller2.ataller2.services.PerfilService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;

public class ListadoConversacionesFragment extends Fragment implements Refresh{
    private ListadoConversacionesFragment.ConversacionesListListener mConversacionesListListener;

    private RecyclerView mRecycleView;
    private ProgressBar mProgressBar;
    private TextView mTextNoHayConversaciones;

    @Override
    public void refresh() {

    }

    public ListadoConversacionesFragment() {
    }

    public static ListadoConversacionesFragment newInstance(int columnCount) {
        ListadoConversacionesFragment fragment = new ListadoConversacionesFragment();
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
        View view = inflater.inflate(R.layout.fragment_conversaciones_recientes, container, false);
        mRecycleView = view.findViewById(R.id.listConversacionesRecientes);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProgressBar = view.findViewById(R.id.progressBar_conversaciones);
        mTextNoHayConversaciones = view.findViewById(R.id.textNoHayConversaciones);
        mTextNoHayConversaciones.setVisibility(View.GONE);
        showLoadingHistorias(true);

        final ConversacionService conversacionService = ServiceLocator.get(ConversacionService.class);

        conversacionService.getAmigosData(getActivity(), new OnCallback() {
            @Override
            public void onFinish() {
                List<Conversacion> conversaciones = new ArrayList<>();
                List<Amigo> amigos = conversacionService.getAmigos();
                for (Amigo amigo: amigos){
                    Conversacion conversacion = new Conversacion();
                    conversacion.setNombreConver(amigo.getNombre() + " " + amigo.getApellido());
                    conversaciones.add(conversacion);
                    conversacion.setDestinoID(amigo.getId());
                    conversacion.setOrigenID(ServiceLocator.get(FacebookService.class).getFacebookID());
                }
                mRecycleView.setAdapter(new ConversacionListAdapter(conversaciones, mConversacionesListListener));
                showLoadingHistorias(false);
                if (conversaciones.isEmpty()){
                    mTextNoHayConversaciones.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListadoConversacionesFragment.ConversacionesListListener) {
            mConversacionesListListener = (ListadoConversacionesFragment.ConversacionesListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CommerceListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mConversacionesListListener = null;
    }

    private ConversacionService getConversacionesService() {
        return ServiceLocator.get(ConversacionService.class);
    }
    private PerfilService getPerfilesService() {
        return ServiceLocator.get(PerfilService.class);
    }

    public interface ConversacionesListListener {
        void onConversacionClickedRechazar(Conversacion conversacion);
        void onConversacionClickedAceptar(Conversacion conversacion);
    }

    private void showLoadingHistorias(boolean loading) {
        mProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        mRecycleView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

}
