package taller2.ataller2.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taller2.ataller2.adapters.NotificacionesListAdapter;
import taller2.ataller2.services.NotificacionesService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.R;
public class ListadoNotificacionesFragment extends Fragment implements Refresh{

    private NotificacionesListListener mNotificacionesListListener;

    @Override
    public void refresh() {

    }


    public interface NotificacionesListListener {
        void onNotificacionClicked(Notificacion notificacion);
    }

    public ListadoNotificacionesFragment() {
    }

    public static ListadoNotificacionesFragment newInstance(int columnCount) {
        ListadoNotificacionesFragment fragment = new ListadoNotificacionesFragment();
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
        View view = inflater.inflate(R.layout.fragmemt_notificaciones_pendientes, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            NotificacionesService notificacionesService = getNotificacionesService();
            recyclerView.setAdapter(new NotificacionesListAdapter(notificacionesService.getNotificaciones(), mNotificacionesListListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NotificacionesListListener) {
            mNotificacionesListListener = ( NotificacionesListListener ) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CommerceListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNotificacionesListListener = null;
    }

    private NotificacionesService getNotificacionesService() {
        return ServiceLocator.get(NotificacionesService.class);
    }
}