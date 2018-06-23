package taller2.ataller2.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import taller2.ataller2.model.Historia;
import taller2.ataller2.model.ListadoHistoriasFragment;
import taller2.ataller2.R;
import taller2.ataller2.services.EmotionType;
import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.services.ServiceLocator;

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
        private final TextView mTitulo;
        private final TextView mDescripcion;
        private final TextView mUbicacion;
        private final TextView mFecha;
        private final ImageView mPictureUser;

        private final ImageView mMeGusta;
        private final ImageView mNoMeGusta;
        private final ImageView mMeDivierte;
        private final ImageView mMeAburre;

        HistoriasViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mPictureUser = (ImageView) itemView.findViewById(R.id.imgUsuarioHistoria);
            mPicture = (ImageView) itemView.findViewById(R.id.imageHistoria);
            mTitulo = (TextView) itemView.findViewById(R.id.textTitulo);
            mDescripcion = (TextView) itemView.findViewById(R.id.textDescripcion);
            mUbicacion = (TextView) itemView.findViewById(R.id.textUbicacion);
            mFecha = (TextView) itemView.findViewById(R.id.textFechaHora);

            mMeGusta = (ImageView) itemView.findViewById(R.id.me_gusta);
            mNoMeGusta = (ImageView) itemView.findViewById(R.id.no_me_gusta);
            mMeDivierte = (ImageView) itemView.findViewById(R.id.me_divierte);
            mMeAburre = (ImageView) itemView.findViewById(R.id.me_aburre);
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
    public void onBindViewHolder(HistoriasViewHolder holder, int position) {
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
                mHistoriasListListener.onHistoriaClicked(historia);
            }
        });

        holder.mMeDivierte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistoriasListListener.onHistoriaClicked(historia);
            }
        });

        holder.mMeAburre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistoriasListListener.onHistoriaClicked(historia);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mHistoria.size();
    }
}
