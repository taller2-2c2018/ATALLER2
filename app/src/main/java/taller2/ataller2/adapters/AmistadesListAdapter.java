package taller2.ataller2.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import taller2.ataller2.R;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.Amistad;
import taller2.ataller2.model.ListadoAmistadesFragment;
import taller2.ataller2.services.AmistadesService;
import taller2.ataller2.services.ServiceLocator;

public class AmistadesListAdapter extends RecyclerView.Adapter<AmistadesListAdapter.AmistadesViewHolder> {

    private final ListadoAmistadesFragment.AmistadesListListener mAmistadesListListener;
    private List<Amistad> mAmistades;

    public AmistadesListAdapter(List<Amistad> amistades, ListadoAmistadesFragment.AmistadesListListener listener){
        mAmistades = new ArrayList();
        for (Amistad amistad : amistades){
            if (amistad.getActiva()){
                mAmistades.add(amistad);
            }
        }
        mAmistadesListListener = listener;
    }

    public static class AmistadesViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final ImageView mPicture;
        private final TextView mName;
        private final AppCompatButton mRechazar;
        private final AppCompatButton mAceptar;

        AmistadesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mPicture = (ImageView) itemView.findViewById(R.id.id_imagencontactonuevo);
            mName = (TextView) itemView.findViewById(R.id.id_nomcontactonuevo);
            mRechazar = itemView.findViewById(R.id.boton_rechazar_amigo);
            mAceptar = itemView.findViewById(R.id.boton_aceptar_amigo);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public AmistadesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contacto_pendiente, parent, false);
        // AmistadesViewHolder amistadesViewHolder = new AmistadesViewHolder(parent.getChildAt(0));

        AmistadesViewHolder amistadesViewHolder = new AmistadesViewHolder(v);
        return amistadesViewHolder;
    }

    @Override
    public void onBindViewHolder(final AmistadesViewHolder holder, int position) {
        final Amistad amistad = mAmistades.get(position);

        Bitmap originalBitmap = amistad.getPicture();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()){
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        }else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(
                holder.mView.getContext().getResources(), originalBitmap);
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        holder.mPicture.setImageDrawable(roundedDrawable);
        holder.mName.setText(amistad.getName());

        holder.mRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                ServiceLocator.get(AmistadesService.class).rechazarAmistad((Activity) vv.getContext(),amistad);
                holder.mView.setVisibility(View.GONE);
                amistad.setActiva(false);
            }
        });
        holder.mAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                ServiceLocator.get(AmistadesService.class).aceptarAmistad((Activity) vv.getContext(),amistad);
                holder.mView.setVisibility(View.GONE);
                amistad.setActiva(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mAmistades == null){
            return 0;
        }
        return mAmistades.size();
    }
}
