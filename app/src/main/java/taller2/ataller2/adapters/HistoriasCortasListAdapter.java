package taller2.ataller2.adapters;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import taller2.ataller2.R;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.model.ListadoHistoriasFragment;

public class HistoriasCortasListAdapter extends RecyclerView.Adapter<HistoriasCortasListAdapter.HistoriasCortasViewHolder>{

    private final ListadoHistoriasFragment.HistoriasCortasListListener mHistoriasListListener;
    private List<HistoriaCorta> mHistoria;

    public HistoriasCortasListAdapter(List<HistoriaCorta> historias, ListadoHistoriasFragment.HistoriasCortasListListener listener){
        mHistoria = historias;
        mHistoriasListListener = listener;
    }

    public static class HistoriasCortasViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        //private final ImageView mPicture;
        private final ImageView mPictureUser;


        HistoriasCortasViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mPictureUser = (ImageView) itemView.findViewById(R.id.imageHistoriaCorta);
            //mPicture = (ImageView) itemView.findViewById(R.id.imageHistoria);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public HistoriasCortasListAdapter.HistoriasCortasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_historia_corta_imagen, parent, false);
        HistoriasCortasListAdapter.HistoriasCortasViewHolder historiasViewHolder = new HistoriasCortasListAdapter.HistoriasCortasViewHolder(v);
        return historiasViewHolder;
    }

    @Override
    public void onBindViewHolder(HistoriasCortasViewHolder holder, int position) {
        final HistoriaCorta historia = mHistoria.get(position);

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
        //holder.mPicture.setImageBitmap(historia.getPicture());

        holder.mView.setOnClickListener(new View.OnClickListener() {
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
