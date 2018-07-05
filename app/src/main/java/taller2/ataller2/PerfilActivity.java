package taller2.ataller2;
import android.app.Activity;
import android.content.Intent;
import android.media.FaceDetector;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import taller2.ataller2.adapters.HistoriasListAdapter;
import taller2.ataller2.model.ListadoHistoriasFragment;
import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.services.PerfilService;
import taller2.ataller2.services.ServiceLocator;

public class PerfilActivity extends AppCompatActivity {

    private ImageView agregar_amigo;
    private ImageView subir;

    private TextView mNombre;

    private String id;
    private RecyclerView mRecyclerView;
    private ListadoHistoriasFragment.HistoriasListListener mHistoriasListListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);
        final Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        final String nombre = intent.getStringExtra("nombre");


        agregar_amigo = findViewById(R.id.agregar_amigo);
        agregar_amigo.setVisibility(View.VISIBLE);
        agregar_amigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceLocator.get(PerfilService.class).solicitarAmistad((Activity) v.getContext(),id);
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

    private HistoriasService getHistoriasService() {
        return ServiceLocator.get(HistoriasService.class);
    }

}
