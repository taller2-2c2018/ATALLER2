package taller2.ataller2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import taller2.ataller2.R;
import taller2.ataller2.model.Comentario;

public class ComentsListAdapter extends RecyclerView.Adapter<ComentsListAdapter.ComentsViewHolder>{

    private List<Comentario> mComentarios;

    public ComentsListAdapter(List<Comentario> comentarios){
        mComentarios = comentarios;
    }

    public static class ComentsViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mComent;
        private final TextView mNombre;

        ComentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mComent = (TextView) itemView.findViewById(R.id.desc_comentario);
            mNombre = (TextView) itemView.findViewById(R.id.nombre_comentario);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ComentsListAdapter.ComentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comentario, parent, false);
        ComentsListAdapter.ComentsViewHolder comentarioViewHolder = new ComentsListAdapter.ComentsViewHolder(v);
        return comentarioViewHolder;
    }

    @Override
    public void onBindViewHolder(ComentsViewHolder holder, int position) {
        final Comentario comentario = mComentarios.get(position);
        holder.mComent.setText(comentario.getComentario());
        holder.mNombre.setText(comentario.getNombre());
    }

    @Override
    public int getItemCount() {
        if (mComentarios == null){
            return 0;
        }
        return mComentarios.size();
    }
}
