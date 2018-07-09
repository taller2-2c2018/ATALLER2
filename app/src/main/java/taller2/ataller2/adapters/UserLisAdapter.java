package taller2.ataller2.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import taller2.ataller2.PerfilActivity;
import taller2.ataller2.R;
import taller2.ataller2.model.Perfil;
import taller2.ataller2.model.PerfilFragment;
import taller2.ataller2.services.Picasso.PicassoService;
import taller2.ataller2.services.ServiceLocator;

public class UserLisAdapter extends RecyclerView.Adapter<UserLisAdapter.UserViewHolder>{

    private final PerfilFragment.PerfilListener mUserListener;
    private List<Perfil> mPerfiles;

    public UserLisAdapter(List<Perfil> perfiles, PerfilFragment.PerfilListener listener){
        mPerfiles = perfiles;
        mUserListener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final ImageView mPicture;
        private final TextView mName;

        UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mPicture = (ImageView) itemView.findViewById(R.id.id_user_imagencontactonuevo);
            mName = (TextView) itemView.findViewById(R.id.id_user_nomcontactonuevo);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public UserLisAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);
        UserLisAdapter.UserViewHolder userViewHolder = new UserLisAdapter.UserViewHolder(v);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(UserLisAdapter.UserViewHolder holder, int position) {
        final Perfil perfil = mPerfiles.get(position);
        Picasso picasso = ServiceLocator.get(PicassoService.class).getPicasso();
        picasso.load(perfil.getPicture()).fit().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.no_image).into(holder.mPicture);
        holder.mName.setText(perfil.getNombre());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PerfilActivity.class);
                intent.putExtra("id", perfil.getId());
                intent.putExtra("nombre", perfil.getNombre());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPerfiles.size();
    }
}