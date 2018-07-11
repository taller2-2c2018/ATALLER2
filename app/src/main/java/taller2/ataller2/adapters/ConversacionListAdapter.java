package taller2.ataller2.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import taller2.ataller2.ConversationActivity;
import taller2.ataller2.model.Conversacion;
import taller2.ataller2.model.ListadoConversacionesFragment;
import taller2.ataller2.model.Mensaje;
import taller2.ataller2.R;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.notifications.NotificationService;

public class ConversacionListAdapter extends RecyclerView.Adapter<ConversacionListAdapter.ConversacionesViewHolder>  {

    private final ListadoConversacionesFragment.ConversacionesListListener mConversacionesListListener;
    private List<Conversacion> mConversaciones;

    private Dialog mConversacion;

    public ConversacionListAdapter(List<Conversacion> conversaciones, ListadoConversacionesFragment.ConversacionesListListener listener){
        mConversaciones = conversaciones;
        mConversacionesListListener = listener;
    }

    public static class ConversacionesViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mNameUserConver;
        private final TextView mDescripcion;
        private final TextView mCantMsjSinLeer;

        ConversacionesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mNameUserConver = (TextView) itemView.findViewById(R.id.txtConversacionUserName);
            mDescripcion = (TextView) itemView.findViewById(R.id.txtConversacionUltimoContenido);
            mCantMsjSinLeer = (TextView) itemView.findViewById(R.id.txtConversacionCantMsjs);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ConversacionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_conversacion_box, parent, false);
        ConversacionesViewHolder conversacionesViewHolder = new ConversacionesViewHolder(v);
        return conversacionesViewHolder;
    }

    @Override
    public void onBindViewHolder(final ConversacionesViewHolder holder, int position) {
        final Conversacion conversacion = mConversaciones.get(position);

        holder.mNameUserConver.setText(conversacion.getNombreConver());
        holder.mDescripcion.setText(conversacion.getDescription());

        int tieneMsjsSinLeer = conversacion.getCantMsjSinLeer();
        if ( tieneMsjsSinLeer > 0 ) {
            String textoAGenerar = "(" + String.valueOf(tieneMsjsSinLeer) + ")";
            holder.mCantMsjSinLeer.setText(textoAGenerar);
            holder.mCantMsjSinLeer.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(v.getContext(), ConversationActivity.class);
                intent.putExtra("origID", conversacion.getOrigenID());
                intent.putExtra("destID", conversacion.getDestinoID());
                v.getContext().startActivity(intent);

            }
        });
    }

    private CardView createCardView(Context context) {
        CardView cardview = new CardView(context);
        cardview.setRadius(10.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cardview.setElevation(2);
            //cardview.setCardBackgroundColor(getColor(R.color.light_brown));
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 10, 16, 8);
        cardview.setLayoutParams(params);
        return cardview;
    }

    private TextView createTextView(Context context) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(params);
        tv.setTextSize(20);

        return tv;
    }

    @Override
    public int getItemCount() {
        return mConversaciones.size();
    }
}
