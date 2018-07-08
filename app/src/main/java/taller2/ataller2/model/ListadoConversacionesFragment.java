package taller2.ataller2.model;

import android.content.Context;
import android.media.FaceDetector;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import taller2.ataller2.adapters.ConversacionListAdapter;
import taller2.ataller2.services.ConversacionService;
import taller2.ataller2.services.OnCallback;
import taller2.ataller2.services.PerfilService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.R;
import taller2.ataller2.services.facebook.FacebookService;

public class ListadoConversacionesFragment extends Fragment implements Refresh{
    private ListadoConversacionesFragment.ConversacionesListListener mConversacionesListListener;

    private RecyclerView mRecycleView;
    private ProgressBar mProgressBar;

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

        showLoadingHistorias(true);

        PerfilService perfilService = getPerfilesService();
        perfilService.updatePerfilData(this.getActivity(), ServiceLocator.get(FacebookService.class).getFacebookID(), new OnCallback() {
            @Override
            public void onFinish() {
                ConversacionService conversacionService = getConversacionesService();
                mRecycleView.setAdapter(
                        new ConversacionListAdapter(conversacionService.getConversaciones(getActivity()), mConversacionesListListener));
                showLoadingHistorias(false);
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
