package taller2.ataller2.adapters;

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

import java.util.List;

import taller2.ataller2.model.Amistad;
import taller2.ataller2.model.ListadoAmistadesFragment;

public class AmistadesListAdapter extends RecyclerView.Adapter<AmistadesListAdapter.AmistadesViewHolder> {

    private final ListadoAmistadesFragment.AmistadesListListener mAmistadesListListener;
    private List<Amistad> mAmistades;

    public AmistadesListAdapter(List<Amistad> amistades, ListadoAmistadesFragment.AmistadesListListener listener){
        mAmistades = amistades;
        mAmistadesListListener = listener;
    }

    public static class AmistadesViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final ImageView mPicture;
        private final TextView mName;

        AmistadesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mPicture = (ImageView) itemView.findViewById(R.id.id_imagencontactonuevo);
            mName = (TextView) itemView.findViewById(R.id.id_nomcontactonuevo);
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

        AppCompatButton rechazar = v.findViewById(R.id.boton_rechazar_amigo);
        rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                v.setVisibility(View.GONE);
            }
        });
        AppCompatButton aceptar = v.findViewById(R.id.boton_aceptar_amigo);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                v.setVisibility(View.GONE);
            }
        });

        AmistadesViewHolder amistadesViewHolder = new AmistadesViewHolder(v);
        return amistadesViewHolder;
    }

    @Override
    public void onBindViewHolder(AmistadesViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return mAmistades.size();
    }
}
