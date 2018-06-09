package taller2.ataller2.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taller2.ataller2.adapters.AmistadesListAdapter;
import taller2.ataller2.services.AmistadesService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.R;
public class ListadoAmistadesFragment extends Fragment {

    private AmistadesListListener mAmistadesListListener;


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

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            AmistadesService amistadesService = getAmistadesService();
            recyclerView.setAdapter(new AmistadesListAdapter(amistadesService.getAmistades(), mAmistadesListListener));
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
