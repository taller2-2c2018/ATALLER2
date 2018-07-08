package taller2.ataller2.model;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import taller2.ataller2.R;
import taller2.ataller2.SearchActivity;
import taller2.ataller2.adapters.AmistadesListAdapter;
import taller2.ataller2.services.AmistadesService;
import taller2.ataller2.services.ServiceLocator;
public class ListadoAmistadesFragment extends Fragment implements Refresh{

    private AmistadesListListener mAmistadesListListener;
    private FloatingActionButton mSearchView;
    private TextView mNoHayAmigosView;

    @Override
    public void refresh() {

    }


    public interface AmistadesListListener {
        void onAmistadClicked(Amistad amistad);
    }

    public ListadoAmistadesFragment() {
    }

    public static ListadoAmistadesFragment newInstance(int columnCount) {
        ListadoAmistadesFragment fragment = new ListadoAmistadesFragment();
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
        View view = inflater.inflate(R.layout.fragment_amistades_nuevas, container, false);

        mSearchView = view.findViewById(R.id.search);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        AmistadesService amistadesService = getAmistadesService();
        List<Amistad> amistades = amistadesService.getAmistades();
        mNoHayAmigosView = view.findViewById(R.id.textNoHayAmigos);
        mNoHayAmigosView.setVisibility(amistades.isEmpty() ? View.VISIBLE : View.GONE);

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new AmistadesListAdapter(amistades, mAmistadesListListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AmistadesListListener) {
            mAmistadesListListener = ( AmistadesListListener ) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CommerceListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAmistadesListListener = null;
    }

    private AmistadesService getAmistadesService() {
        return ServiceLocator.get(AmistadesService.class);
    }
}
