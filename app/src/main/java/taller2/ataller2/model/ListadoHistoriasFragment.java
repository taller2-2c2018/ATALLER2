package taller2.ataller2.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;
import java.util.List;

import taller2.ataller2.CameraActivity;
import taller2.ataller2.Filters.SearchFilter;
import taller2.ataller2.SearchActivity;
import taller2.ataller2.adapters.HistoriasCortasListAdapter;
import taller2.ataller2.adapters.HistoriasListAdapter;
import taller2.ataller2.services.OnCallback;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.UsersService;
import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.R;

public class ListadoHistoriasFragment extends Fragment{

    private HistoriasListListener mHistoriasListListener;
    private HistoriasCortasListListener mHistoriasCortasListListener;

    private UsersListListener mUsersListListener;
    private AppCompatButton mButtonNuevaHistoriaView;

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewCortas;
    private ProgressBar mLoadingHistorias;
    private NestedScrollView mScrollHistoria;

    public interface HistoriasListListener {
        void onHistoriaClicked(Historia historia);
    }

    public interface HistoriasCortasListListener {
        void onHistoriaCortaClicked(HistoriaCorta historia);
    }

    public interface UsersListListener {
        void onUserClicked(User user);
    }

    public ListadoHistoriasFragment() {
    }

    public static ListadoHistoriasFragment newInstance(int columnCount) {
        ListadoHistoriasFragment fragment = new ListadoHistoriasFragment();
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
        View view = inflater.inflate(R.layout.fragment_historias_recientes2, container, false);

        mButtonNuevaHistoriaView = view.findViewById(R.id.buttonIngresaHistoria);

        mLoadingHistorias = view.findViewById(R.id.progressBar_historias);

        mScrollHistoria = view.findViewById(R.id.scroll_historias);

        showLoadingHistorias(true);
        mRecyclerView = view.findViewById(R.id.listHistoriasRecientes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewCortas = view.findViewById(R.id.listHistoriasCortasRecientes);
        mRecyclerViewCortas.setLayoutManager(new LinearLayoutManager(getActivity()));

        HistoriasService historiasService = getHistoriasService();
        historiasService.updateHistoriasData(this.getActivity(),new OnCallback() {
            @Override
            public void onFinish() {
                showLoadingHistorias(false);


                HistoriasService historiasService = getHistoriasService();
                mRecyclerView.setAdapter(new HistoriasListAdapter(historiasService.
                        getHistorias(getActivity()), mHistoriasListListener));
                List<HistoriaCorta> historiasCortas = historiasService.getHistoriasCortas(getActivity());
                HistoriaCorta historia = new HistoriaCorta();
//                Bitmap bitmap = drawableToBitmap(mScrollHistoria.getContext().getDrawable(R.drawable.rounded_image));
                Bitmap icon = BitmapFactory.decodeResource(mScrollHistoria.getResources(), R.drawable.default_img);
                historia.setPicture(icon);
                historia.setMock(true);
                historia.setPictureUsr(icon);
                historia.setStringUri("https://th.seaicons.com/wp-content/uploads/2016/02/text-plus-icon-1.png");
//                historia.setPicture(bitmap);
//                historia.setType("png");
                historiasCortas.add(0,historia);
                mRecyclerViewCortas.setAdapter(new HistoriasCortasListAdapter(historiasCortas, mHistoriasCortasListListener,getActivity()));
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mRecyclerViewCortas.setLayoutManager(layoutManager);


            }
        });




        setUpNuevaHistoriaView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HistoriasListListener) {
            mHistoriasListListener = ( HistoriasListListener ) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CommerceListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHistoriasListListener = null;
    }

    private HistoriasService getHistoriasService() {
        return ServiceLocator.get(HistoriasService.class);
    }

    private UsersService getUsersService() {
        return ServiceLocator.get(UsersService.class);
    }


    private void setUpNuevaHistoriaView() {
        mButtonNuevaHistoriaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CameraActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    private void enterUserPerfil(String query){

    }

    private void filterUsersByText(String searchText, boolean notifyNoneUsers) {
        List<String> usuarios = getHistoriasService().getUsers();
        if (usuarios != null) {
            SearchFilter saerchFilter = new SearchFilter(searchText);
            List<String> filteredUsers = saerchFilter.apply(usuarios);
            loadUsers(filteredUsers, notifyNoneUsers);
        }
    }

    private void loadUsers(List<String> usuarios, boolean notifyNoneUsers) {
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mRecyclerView.setAdapter(new UserListAdapter(usuarios, mUsersListListener));
        if (notifyNoneUsers && usuarios.size() < 1) {
            Toast.makeText(getContext(), "No existen usuarios con ese nombre", Toast.LENGTH_LONG).show();
        }
    }

    private void showLoadingHistorias(boolean loading) {
        mLoadingHistorias.setVisibility(loading ? View.VISIBLE : View.GONE);
        mScrollHistoria.setVisibility(loading ? View.GONE : View.VISIBLE);
        mButtonNuevaHistoriaView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}