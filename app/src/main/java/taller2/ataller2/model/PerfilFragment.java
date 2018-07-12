package taller2.ataller2.model;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.firestore.ServerTimestamp;
import com.squareup.picasso.Picasso;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import taller2.ataller2.LoginActivity;
import taller2.ataller2.adapters.HistoriasListAdapter;
import taller2.ataller2.services.MiPerfilService;
import taller2.ataller2.services.OnCallback;
import taller2.ataller2.services.OnCallbackImageUpload;
import taller2.ataller2.services.PerfilService;
import taller2.ataller2.services.Picasso.PicassoService;
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

    private TextView sexo;
    private TextView mail;
    private TextView fechaNacimiento;
    private ImageView editarPerfil;

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

        perfil = ServiceLocator.get(MiPerfilService.class).getMiPerfil();
        Picasso picasso = ServiceLocator.get(PicassoService.class).getPicasso();
        picasso.load(perfil.getPicture()).fit().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.no_image).into(iv);
        nombre.setText(perfil.getNombre());

        sexo = view.findViewById(R.id.sexo);
        mail = view.findViewById(R.id.mail);
        fechaNacimiento = view.findViewById(R.id.fechaNacimiento);
        editarPerfil = view.findViewById(R.id.modificar_perfi);

        sexo.setText(perfil.getSexo());
        mail.setText(perfil.getMail());
        fechaNacimiento.setText(perfil.getFechaNacimiento());

        editarPerfil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_perfil);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                lp.copyFrom(window.getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                final TextInputEditText mail_text = dialog.findViewById(R.id.textInputEditText);

                final EditText fechaNaciemiento_text = dialog.findViewById(R.id.etPlannedDate);

                final RadioGroup sexo_text = dialog.findViewById(R.id.radioGrp);

                AppCompatButton cancelar = dialog.findViewById(R.id.boton_rechazar);
                cancelar.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        dialog.dismiss();
                    }
                });
                AppCompatButton aceptar = dialog.findViewById(R.id.boton_aceptar);
                aceptar.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){

                        RadioButton male = dialog.findViewById(R.id.radioM);
                        RadioButton fem = dialog.findViewById(R.id.radioF);
                        String sexo_envio = "Male";
                        if (! male.isChecked()){
                            sexo_envio = "Female";
                        }

                        Perfil perfilNuevo = new Perfil(perfil.getNombre());
                        String[] parts = perfil.getNombre().split(" ");
                        perfilNuevo.setNombrenombre(parts[0]);
                        perfilNuevo.setApellido(parts[1]);
                        perfilNuevo.setMail(mail_text.getText().toString());
                        perfilNuevo.setFechaNacimiento(fechaNaciemiento_text.getText().toString());
                        perfilNuevo.setSexo(sexo_envio);
                        ServiceLocator.get(PerfilService.class).updatePerfil(getActivity(),perfilNuevo);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        HistoriasService historiasService = getHistoriasService();
        mRecyclerView.setAdapter(new HistoriasListAdapter(historiasService.getMisHistorias(getActivity()), mHistoriasListListener));
        showLoadingPerfil(false);

        AppCompatButton exit = view.findViewById(R.id.salir_app);
        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                exit();
            }
        });

        ImageView subir = view.findViewById(R.id.subir_foto_perfil2);
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
            ServiceLocator.get(HistoriasService.class).uploadImageFromMemory(iv, new OnCallbackImageUpload() {
                @Override
                public void onFinish(Uri uri) {
                    ServiceLocator.get(PerfilService.class).updateFoto(getActivity(), uri);
                    ServiceLocator.get(MiPerfilService.class).updateFoto(uri.toString());
                }
            });

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