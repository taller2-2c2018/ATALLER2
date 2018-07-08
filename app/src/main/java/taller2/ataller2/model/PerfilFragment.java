package taller2.ataller2.model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import taller2.ataller2.LoginActivity;
import taller2.ataller2.adapters.HistoriasListAdapter;
import taller2.ataller2.services.OnCallback;
import taller2.ataller2.services.PerfilService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.R;
import taller2.ataller2.services.facebook.FacebookService;

public class PerfilFragment extends Fragment implements Refresh{

    private static int PICK_IMAGE = 2;

    private PerfilFragment.PerfilListener mPerfilListener;
    private RecyclerView mRecyclerView;
    private ListadoHistoriasFragment.HistoriasListListener mHistoriasListListener;

    private Perfil perfil;
    private ImageView iv;
    private TextView nombre;

    private ProgressBar mProgressBar;
    private ConstraintLayout mConstraintLayout;

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
        View view = inflater.inflate(R.layout.perfil, container, false);

        mProgressBar = view.findViewById(R.id.progressBar_perfil);
        mConstraintLayout = view.findViewById(R.id.perfil_view);

        showLoadingPerfil(true);

        iv = view.findViewById(R.id.imageViewPerfil);
        nombre = view.findViewById(R.id.textNombre);
        mRecyclerView = view.findViewById(R.id.historias_perfil_local);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ServiceLocator.get(PerfilService.class).
                updatePerfilData(this.getActivity(),
                                    ServiceLocator.get(FacebookService.class).getFacebookID(),
                                    new OnCallback(){
                                        @Override
                                        public void onFinish() {
                                            perfil = ServiceLocator.get(PerfilService.class).getMiPerfil();
                                            if (perfil == null){
                                                iv.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_img));
                                                nombre.setText("");
                                            }
                                            else{
                                                iv.setImageBitmap(perfil.getPicture());
                                                nombre.setText(ServiceLocator.get(FacebookService.class).getName());
                                            }
                                            HistoriasService historiasService = getHistoriasService();
                                            mRecyclerView.setAdapter(new HistoriasListAdapter(historiasService.getMisHistorias(getActivity()), mHistoriasListListener));
                                            showLoadingPerfil(false);
                                        }
                                    }
                );

        AppCompatButton exit = view.findViewById(R.id.salir_app);
        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                exit();
            }
        });

        ImageView subir = view.findViewById(R.id.subir_foto_perfil);
        subir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE){
            Uri imageUri = data.getData();
            iv.setImageURI(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
                ServiceLocator.get(PerfilService.class).updateFoto(this.getActivity(), bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //nombre.setText(ServiceLocator.get(FacebookService.class).getName());
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

    private void showLoadingPerfil(boolean loading) {
        mProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        mConstraintLayout.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
}