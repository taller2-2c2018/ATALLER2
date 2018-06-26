package taller2.ataller2.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taller2.ataller2.adapters.ConversacionListAdapter;
import taller2.ataller2.services.ConversacionService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.R;
public class ListadoConversacionesFragment extends Fragment implements Refresh{
    private ListadoConversacionesFragment.ConversacionesListListener mConversacionesListListener;

    @Override
    public void refresh() {

    }


    public interface ConversacionesListListener {
        void onConversacionClickedRechazar(Conversacion conversacion);
        void onConversacionClickedAceptar(Conversacion conversacion);
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

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ConversacionService conversacionService = getConversacionesService();
            recyclerView.setAdapter(new ConversacionListAdapter(conversacionService.getConversaciones(), mConversacionesListListener));
        }
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
}
