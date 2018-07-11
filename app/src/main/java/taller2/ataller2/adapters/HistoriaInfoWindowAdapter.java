package taller2.ataller2.adapters;


import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import taller2.ataller2.R;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.ListadoHistoriasFragment;
import taller2.ataller2.services.Picasso.CircleTransform;
import taller2.ataller2.services.Picasso.PicassoService;
import taller2.ataller2.services.ServiceLocator;

public class HistoriaInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater mInflater;
    private HashMap<Marker, Historia> mHistoriasFromMarkerMap = new HashMap();
    private ListadoHistoriasFragment.HistoriasListListener mHistoriaListListener;

    public HistoriaInfoWindowAdapter(LayoutInflater inflater, final HashMap<Marker, Historia> historiaFromMarkerMap){
        mInflater = inflater;
        mHistoriasFromMarkerMap = historiaFromMarkerMap;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        View v = mInflater.inflate(R.layout.layout_map_historia, null);
        final Historia historia = mHistoriasFromMarkerMap.get(marker);

        CardView cardView = v.findViewById(R.id.cv_historia_mapa);
        ImageView historiaPicture = v.findViewById(R.id.image_mapa);
        final TextView historiaName = (TextView) v.findViewById(R.id.nombre_historia_mapa);

        Picasso picasso = ServiceLocator.get(PicassoService.class).getPicasso();
        //picasso.load(historia.getStringUri()).fit().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.no_image).into(historiaPicture);
        picasso.load(historia.getPictureUsr()).fit().transform(new CircleTransform()).error(R.drawable.no_image).placeholder(R.drawable.progress_animation).into(historiaPicture);
        historiaName.setText(historia.getNombre());

        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }

}
