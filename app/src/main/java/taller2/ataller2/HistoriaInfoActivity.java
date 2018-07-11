package taller2.ataller2;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.squareup.picasso.Picasso;

import java.util.List;

import taller2.ataller2.adapters.ComentsListAdapter;
import taller2.ataller2.adapters.HistoriasListAdapter;
import taller2.ataller2.model.Comentario;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.Reaccion;
import taller2.ataller2.services.EmotionType;
import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.services.Picasso.CircleTransform;
import taller2.ataller2.services.Picasso.PicassoService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;


public class HistoriaInfoActivity extends AppCompatActivity {

    private String historiaID = "";
    private Historia miHistoria = null;
    private  ImageView mMeGusta;
    private  ImageView mNoMeGusta;
    private  ImageView mMeDivierte;
    private  ImageView mMeAburre;
    private  TextView mMeGustaCount;
    private  TextView mNoMeGustaCount;
    private  TextView mMeDivierteCount;
    private  TextView mMeAburreCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        historiaID = extras.getString("id_historia");
        setContentView(R.layout.layout_historia_larga);

        List<Historia> historias = ServiceLocator.get(HistoriasService.class).getHistorias(this);
        for (Historia historia : historias) {
           if (historia.getID().equals(historiaID)){
               miHistoria = historia;
           }
        }

        ImageView userPicture = findViewById(R.id.imgUsuarioHistoria);
        Picasso picasso = ServiceLocator.get(PicassoService.class).getPicasso();
        picasso.load(miHistoria.getPictureUsr()).fit().transform(new CircleTransform()).error(R.drawable.no_image).placeholder(R.drawable.progress_animation).into(userPicture);

        final VideoView videoView = findViewById(R.id.videoHistoria);
        final ImageView imageView = findViewById(R.id.imageHistoria);

        if (miHistoria.getType().equals("mp3")){
            Uri uri = Uri.parse(miHistoria.getStringUri());
            videoView.setVideoURI(uri);
//            holder.mVideo.start();
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    videoView.start(); //need to make transition seamless.
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            videoView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent motionEvent)
                {
                    if (videoView.isPlaying())
                    {
                        videoView.pause();
                        return false;
                    }
                    else
                    {
                        videoView.start();
                        return false;
                    }
                }
            });
        }
        else{
            picasso.load(miHistoria.getStringUri()).fit().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.no_image).into(imageView);
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

        TextView nombreUsuario = findViewById(R.id.nombreUsuarioHistoria);
        TextView titulo =  findViewById(R.id.textTitulo);
        TextView ubicacion =  findViewById(R.id.textUbicacion);
        TextView descripcion =  findViewById(R.id.textDescripcion);
        TextView fechaYHora =  findViewById(R.id.textFechaHora);

        titulo.setText(miHistoria.getmTitulo());
        descripcion.setText(miHistoria.getDescription());
        fechaYHora.setText(miHistoria.getFecha());
        ubicacion.setText(miHistoria.getUbicacion());

        final RecyclerView recyclerView = findViewById(R.id.comentarios_historia);

        recyclerView.setLayoutManager(new LinearLayoutManager((Activity)(recyclerView.getContext())));
        recyclerView.setAdapter(new ComentsListAdapter(miHistoria.getComentarios()));

        mMeGustaCount = findViewById(R.id.me_gusta_count);
        mNoMeGustaCount = findViewById(R.id.no_me_gusta_count);
        mMeDivierteCount = findViewById(R.id.me_divierte_count);
        mMeAburreCount = findViewById(R.id.me_aburre_count);

        mMeGusta = findViewById(R.id.me_gusta);
        mNoMeGusta = findViewById(R.id.no_me_gusta);
        mMeDivierte = findViewById(R.id.me_divierte);
        mMeAburre = findViewById(R.id.me_aburre);

        mMeGusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmotionType emotion = EmotionType.LIKE;
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actReaction(manager, miHistoria,emotion);
                setReactionBackgrounds(emotion, miHistoria, v.getContext());
                mMeGustaCount.setText(String.valueOf(miHistoria.getCantMeGusta()));
                mNoMeGustaCount.setText(String.valueOf(miHistoria.getCantNoMeGusta()));
                mMeDivierteCount.setText(String.valueOf(miHistoria.getCantMeDivierte()));
                mMeAburreCount.setText(String.valueOf(miHistoria.getCantMeAburre()));

            }
        });


        mNoMeGusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmotionType emotion = EmotionType.DONT_LIKE;
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actReaction(manager, miHistoria,emotion);
                setReactionBackgrounds(emotion, miHistoria, v.getContext());
                mMeGustaCount.setText(String.valueOf(miHistoria.getCantMeGusta()));
                mNoMeGustaCount.setText(String.valueOf(miHistoria.getCantNoMeGusta()));
                mMeDivierteCount.setText(String.valueOf(miHistoria.getCantMeDivierte()));
                mMeAburreCount.setText(String.valueOf(miHistoria.getCantMeAburre()));
            }
        });

        mMeDivierte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmotionType emotion = EmotionType.FUN;
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actReaction(manager, miHistoria,emotion);
                setReactionBackgrounds(emotion, miHistoria, v.getContext());
                mMeGustaCount.setText(String.valueOf(miHistoria.getCantMeGusta()));
                mNoMeGustaCount.setText(String.valueOf(miHistoria.getCantNoMeGusta()));
                mMeDivierteCount.setText(String.valueOf(miHistoria.getCantMeDivierte()));
                mMeAburreCount.setText(String.valueOf(miHistoria.getCantMeAburre()));
            }
        });

        mMeAburre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmotionType emotion = EmotionType.BORE;
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actReaction(manager, miHistoria,emotion);
                setReactionBackgrounds(emotion, miHistoria, v.getContext());
                mMeGustaCount.setText(String.valueOf(miHistoria.getCantMeGusta()));
                mNoMeGustaCount.setText(String.valueOf(miHistoria.getCantNoMeGusta()));
                mMeDivierteCount.setText(String.valueOf(miHistoria.getCantMeDivierte()));
                mMeAburreCount.setText(String.valueOf(miHistoria.getCantMeAburre()));
            }
        });

        final RelativeLayout ingresoComentario = findViewById(R.id.ingreso_comentario);
        ImageView mComentario = findViewById(R.id.comentario_ly);
        mComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (View.VISIBLE == recyclerView.getVisibility()){
                    recyclerView.setVisibility(View.GONE);
                    ingresoComentario.setVisibility(View.GONE);
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    ingresoComentario.setVisibility(View.VISIBLE);
                }

            }
        });

        ImageView sendComentario = findViewById(R.id.fab_comentario);
        sendComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.input);
                String comentario = text.getText().toString();
                text.setText("");
                Comentario nuevo = new Comentario();
                nuevo.setComentario(comentario);
                nuevo.setNombre(ServiceLocator.get(FacebookService.class).getName());
                List<Comentario> comentarios = miHistoria.getComentarios();
                comentarios.add(nuevo);
                recyclerView.setAdapter(new ComentsListAdapter(comentarios));
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actCommet(manager, miHistoria,comentario);
            }
        });

        nombreUsuario.setText(miHistoria.getNombre());
        nombreUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PerfilActivity.class);
                intent.putExtra("id", miHistoria.getUserID());
                intent.putExtra("nombre", miHistoria.getNombre());
                v.getContext().startActivity(intent);
            }
        });

        if (miHistoria.getMiReaccion() != null){
            switch (miHistoria.getMiReaccion().getEmocion().getValue()){
                case 0:
                    mMeGusta.setBackground( mMeGusta.getContext().getDrawable(R.drawable.rounded_image));
                    break;
                case 1:
                    mNoMeGusta.setBackground(mNoMeGusta.getContext().getDrawable(R.drawable.rounded_image));
                    break;
                case 2:
                    mMeDivierte.setBackground(mMeDivierte.getContext().getDrawable(R.drawable.rounded_image));
                    break;
                case 3:
                    mMeAburre.setBackground(mMeAburre.getContext().getDrawable(R.drawable.rounded_image));
                    break;
            }

        }

        mMeGustaCount.setText(String.valueOf(miHistoria.getCantMeGusta()));
        mNoMeGustaCount.setText(String.valueOf(miHistoria.getCantNoMeGusta()));
        mMeDivierteCount.setText(String.valueOf(miHistoria.getCantMeDivierte()));
        mMeAburreCount.setText(String.valueOf(miHistoria.getCantMeAburre()));

    }

    private void setReactionBackgrounds(EmotionType emocion, Historia historia, Context context){
        mNoMeGusta.setBackground(context.getDrawable(R.drawable.rounded_image_blanco));
        mMeDivierte.setBackground(context.getDrawable(R.drawable.rounded_image_blanco));
        mMeAburre.setBackground(context.getDrawable(R.drawable.rounded_image_blanco));
        mMeGusta.setBackground(context.getDrawable(R.drawable.rounded_image_blanco));

        historia.setMiReaccion(emocion);
        Reaccion miReaccion = historia.getMiReaccion();

        if (miReaccion != null){
            switch (miReaccion.getEmocion().getValue()){
                case 0:
                    mMeGusta.setBackground(context.getDrawable(R.drawable.rounded_image));
                    break;
                case 1:
                    mNoMeGusta.setBackground(context.getDrawable(R.drawable.rounded_image));
                    break;
                case 2:
                    mMeDivierte.setBackground(context.getDrawable(R.drawable.rounded_image));
                    break;
                case 3:
                    mMeAburre.setBackground(context.getDrawable(R.drawable.rounded_image));
                    break;
                default:
                    break;
            }
        }
    }

}
