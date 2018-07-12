package taller2.ataller2.adapters;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.util.List;

import taller2.ataller2.CameraActivity;
import taller2.ataller2.R;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.model.ListadoHistoriasFragment;
import taller2.ataller2.services.Picasso.CircleTransform;
import taller2.ataller2.services.Picasso.PicassoService;
import taller2.ataller2.services.ServiceLocator;

public class HistoriasCortasListAdapter extends RecyclerView.Adapter<HistoriasCortasListAdapter.HistoriasCortasViewHolder>{

    private final ListadoHistoriasFragment.HistoriasCortasListListener mHistoriasListListener;
    private List<HistoriaCorta> mHistoria;
    private FragmentActivity mActivity;
    private boolean primero = true;

    public HistoriasCortasListAdapter(List<HistoriaCorta> historias, ListadoHistoriasFragment.HistoriasCortasListListener listener, FragmentActivity activity){
        mHistoria = historias;
        mHistoriasListListener = listener;
        mActivity = activity;
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
    public void onBindViewHolder(HistoriasCortasViewHolder holder, final int position) {
        final HistoriaCorta historia = mHistoria.get(position);

        Bitmap originalBitmap = historia.getPictureUsr();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()){
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        }else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }
        final Picasso picasso = ServiceLocator.get(PicassoService.class).getPicasso();
        picasso.load(historia.getStringUri()).fit().transform(new CircleTransform()).error(R.drawable.no_image).placeholder(R.drawable.progress_animation).into(holder.mPictureUser);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(historia.getMock()){
                    Intent intent = new Intent(mActivity, CameraActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("esFlash", 1);
                    intent.putExtras(b);
                    mActivity.startActivity(intent);
                    primero = false;
                } else {
                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_historia_corta);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = dialog.getWindow();
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    lp.copyFrom(window.getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(lp);
                    window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                    ImageView imagen = dialog.findViewById(R.id.img_corta);
                    picasso.load(historia.getStringUri()).fit().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.no_image).into(imagen);

                    ImageView cancelButton = dialog.findViewById(R.id.button_cerrar);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    //mHistoriasListListener.onHistoriaCortaClicked(historia);
                }
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
