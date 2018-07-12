package taller2.ataller2;
import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.media.FaceDetector;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import taller2.ataller2.adapters.HistoriasListAdapter;
import taller2.ataller2.model.ListadoHistoriasFragment;
import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.services.MiPerfilService;
import taller2.ataller2.services.OnCallback;
import taller2.ataller2.services.PerfilService;
import taller2.ataller2.services.Picasso.PicassoService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.location.LocationService;

public class PerfilActivity extends AppCompatActivity {

    private ImageView agregar_amigo;
    private ImageView amigo_agregado;
    private ImageView amigo;
    private ImageView subir;
    private ImageView fotoPerfil;

    private TextView mNombre;

    private String id;
    private RecyclerView mRecyclerView;
    private ListadoHistoriasFragment.HistoriasListListener mHistoriasListListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);
        Bundle extras = getIntent().getExtras();
        final String id = extras.getString("id");
        final String nombre = extras.getString("nombre");
        final String fotoID = extras.getString("fotoID");

        boolean esAmigo = estaEnAmigos(id);
        boolean hayPeticion = estaEnPeticiones(id);
        boolean noSoyYo = noSoyYo(id);

        fotoPerfil = findViewById(R.id.imageViewPerfil);
        Picasso picasso = ServiceLocator.get(PicassoService.class).getPicasso();
        picasso.load(fotoID).fit().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.no_image).into(fotoPerfil);
        
        agregar_amigo = findViewById(R.id.agregar_amigo);
        amigo_agregado = findViewById(R.id.amigo_agregado);
        amigo = findViewById(R.id.amigo);

        if (!esAmigo && !hayPeticion && noSoyYo){
            agregar_amigo.setVisibility(View.VISIBLE);
        } else if (!esAmigo && hayPeticion && noSoyYo) {
            amigo_agregado.setVisibility(View.VISIBLE);
        } else if (esAmigo){
            amigo.setVisibility(View.VISIBLE);
        }

        agregar_amigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceLocator.get(PerfilService.class).solicitarAmistad((Activity) v.getContext(),id, new OnCallback(){
                    @Override
                    public void onFinish() {
                        ServiceLocator.get(MiPerfilService.class).agregarPeticion(id);
                        agregar_amigo.setVisibility(View.GONE);
                        amigo_agregado.setVisibility(View.VISIBLE);
                        Toast.makeText(PerfilActivity.this,"Se ha enviado la solicitud de amistad", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        subir = findViewById(R.id.subir_foto_perfil);
        subir.setVisibility(View.GONE);

        mNombre = findViewById(R.id.textNombre);
        mNombre.setText(nombre);

        mRecyclerView = findViewById(R.id.historias_perfil_local);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        HistoriasService historiasService = getHistoriasService();
        mRecyclerView.setAdapter(new HistoriasListAdapter(historiasService.getMisHistorias(this,id), mHistoriasListListener));

    }

    private boolean estaEnAmigos(String id){
        List<String> amigos =  ServiceLocator.get(MiPerfilService.class).getMiPerfil().getAmigos();
        for (String amigo : amigos){
            if (amigo.equals(id)){
                return true;
            }
        }
        return false;
    }

    private boolean estaEnPeticiones(String id){
        List<String> peticiones =  ServiceLocator.get(MiPerfilService.class).getMiPerfil().getPeticiones();
        for (String peticion : peticiones){
            if (peticion.equals(id)){
                return true;
            }
        }
        return false;
    }

    private boolean noSoyYo(String id){
        return !id.equals(ServiceLocator.get(FacebookService.class).getFacebookID());
    }

    private HistoriasService getHistoriasService() {
        return ServiceLocator.get(HistoriasService.class);
    }

}
