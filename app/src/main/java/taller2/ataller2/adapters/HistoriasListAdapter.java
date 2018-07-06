package taller2.ataller2.adapters;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

import taller2.ataller2.PerfilActivity;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.ListadoHistoriasFragment;
import taller2.ataller2.R;
import taller2.ataller2.model.Perfil;
import taller2.ataller2.services.EmotionType;
import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.location.LocationService;

public class HistoriasListAdapter extends RecyclerView.Adapter<HistoriasListAdapter.HistoriasViewHolder>{

    private final ListadoHistoriasFragment.HistoriasListListener mHistoriasListListener;
    private List<Historia> mHistoria;

    public HistoriasListAdapter(List<Historia> historias, ListadoHistoriasFragment.HistoriasListListener listener){
        mHistoria = historias;
        mHistoriasListListener = listener;
    }

    public static class HistoriasViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final ImageView mPicture;
        private final VideoView mVideo;
        private final TextView mTitulo;
        private final TextView mDescripcion;
        private final TextView mUbicacion;
        private final TextView mFecha;
        private final ImageView mPictureUser;

        private final TextView mNombre;

        private final ImageView mMeGusta;
        private final ImageView mNoMeGusta;
        private final ImageView mMeDivierte;
        private final ImageView mMeAburre;
        private final ImageView mComentario;
        private final RecyclerView mRecyclerView;
        private final RelativeLayout mRelativeLayout;
        private final EditText mInputComentario;
        private final ImageView mSendComentario;

        HistoriasViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mPictureUser = (ImageView) itemView.findViewById(R.id.imgUsuarioHistoria);

            mPicture = (ImageView) itemView.findViewById(R.id.imageHistoria);
            mVideo = itemView.findViewById(R.id.videoHistoria);

            mTitulo = (TextView) itemView.findViewById(R.id.textTitulo);
            mDescripcion = (TextView) itemView.findViewById(R.id.textDescripcion);
            mUbicacion = (TextView) itemView.findViewById(R.id.textUbicacion);
            mFecha = (TextView) itemView.findViewById(R.id.textFechaHora);
            mNombre = itemView.findViewById(R.id.nombreUsuarioHistoria);

            mMeGusta = (ImageView) itemView.findViewById(R.id.me_gusta);
            mNoMeGusta = (ImageView) itemView.findViewById(R.id.no_me_gusta);
            mMeDivierte = (ImageView) itemView.findViewById(R.id.me_divierte);
            mMeAburre = (ImageView) itemView.findViewById(R.id.me_aburre);
            mComentario = (ImageView) itemView.findViewById(R.id.comentario_ly);
            mRecyclerView = itemView.findViewById(R.id.comentarios_historia);
            mRelativeLayout = itemView.findViewById(R.id.ingreso_comentario);

            mInputComentario = itemView.findViewById(R.id.input);
            mSendComentario = itemView.findViewById(R.id.fab_comentario);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public HistoriasListAdapter.HistoriasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_historia_larga, parent, false);
        HistoriasListAdapter.HistoriasViewHolder historiasViewHolder = new HistoriasListAdapter.HistoriasViewHolder(v);
        return historiasViewHolder;
    }


    @Override
    public void onBindViewHolder(final HistoriasViewHolder holder, int position) {
        final Historia historia = mHistoria.get(position);

        Bitmap originalBitmap = historia.getPictureUsr();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()){
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        }else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(
                holder.mView.getContext().getResources(), originalBitmap);
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        holder.mPictureUser.setImageDrawable(roundedDrawable);
        holder.mPicture.setImageBitmap(historia.getPicture());
        holder.mTitulo.setText(historia.getmTitulo());
        holder.mDescripcion.setText(historia.getDescription());
        holder.mFecha.setText(historia.getFecha());
        holder.mUbicacion.setText(historia.getUbicacion());

        holder.mRecyclerView.setLayoutManager(new LinearLayoutManager((Activity)(holder.mRecyclerView.getContext())));
        holder.mRecyclerView.setAdapter(new ComentsListAdapter(historia.getComentarios()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistoriasListListener.onHistoriaClicked(historia);
            }
        });

        holder.mMeGusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmotionType emotion = EmotionType.LIKE;
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actReaction(manager, historia,emotion);
            }
        });

        holder.mNoMeGusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmotionType emotion = EmotionType.DONT_LIKE;
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actReaction(manager, historia,emotion);

            }
        });

        holder.mMeDivierte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmotionType emotion = EmotionType.FUN;
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actReaction(manager, historia,emotion);
            }
        });

        holder.mMeAburre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmotionType emotion = EmotionType.BORE;
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actReaction(manager, historia,emotion);
            }
        });

        holder.mComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (View.VISIBLE == holder.mRecyclerView.getVisibility()){
                    holder.mRecyclerView.setVisibility(View.GONE);
                    holder.mRelativeLayout.setVisibility(View.GONE);
                }
                else{
                    holder.mRecyclerView.setVisibility(View.VISIBLE);
                    holder.mRelativeLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        holder.mSendComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentario = holder.mInputComentario.getText().toString();
                FragmentManager manager = ((Activity) v.getContext()).getFragmentManager();
                ServiceLocator.get(HistoriasService.class).actCommet(manager, historia,comentario);

            }
        });

        holder.mNombre.setText(historia.getNombre());
        holder.mNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PerfilActivity.class);
                intent.putExtra("id", historia.getUserID());
                intent.putExtra("nombre", historia.getNombre());
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mHistoria == null){
            return 0;
        }
        return mHistoria.size();
    }
}
