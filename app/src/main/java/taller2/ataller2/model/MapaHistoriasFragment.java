package taller2.ataller2.model;

import android.app.Activity;
import android.app.Dialog;

import taller2.ataller2.R;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.services.OnCallback;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.location.LocationService;

public class MapaHistoriasFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, Refresh{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 10;

    private View mMapView;
    private GoogleMap mMap;

    //private HashMap<Marker, Commerce> mCommercesFromMarkerMap = new HashMap();

    private GoogleMap.OnMyLocationClickListener mOnMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull android.location.Location location) {

                    mMap.setMinZoomPreference(12);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(), location.getLongitude()));
                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);

                    mMap.addCircle(circleOptions);
                }
            };

    private void chargeMap (){
        mMap.clear();
        enableMyLocationIfPermitted();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);

        //mCommercesFromMarkerMap.clear();
        List<Historia> historias = ServiceLocator.get(HistoriasService.class).getHistorias(getActivity());

        for (Historia historia : historias) {
            double latitud = 0;
            double longitud = 0;
            if (historia.getLatitud() != "" && historia.getLongitud() != ""){
                latitud = Double.parseDouble(historia.getLatitud());
                longitud = Double.parseDouble(historia.getLongitud());
            }
            LatLng location = new LatLng(latitud, longitud);
            mMap.addMarker(new MarkerOptions().position(location).title("1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

        LocationService locationService = ServiceLocator.get(LocationService.class);
        LocationO mCurrentLocation = locationService.getLocation(getContext());
        LatLng location = new LatLng(mCurrentLocation.getLatitud(), mCurrentLocation.getLongitud());
        //mMap.addMarker(new MarkerOptions().position(location).title("Aquí estoy").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        float zoomLevel = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
        mMap.setOnInfoWindowClickListener(this);
        //mMap.setInfoWindowAdapter(new CommerceInfoWindowAdapter(LayoutInflater.from(getActivity()), mCommercesFromMarkerMap));
    }

    private void enableMyLocationIfPermitted() {
        final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.commerce_map);

        setUpMapView();
        return view;
    }

    private void setUpMapView(){
        //map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        Activity mContext = getActivity();
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(mContext);
        if (resultCode == ConnectionResult.SUCCESS) {
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.commerce_map);
            mapFragment.getMapAsync(this);
        } else {
            Dialog dialog = googleAPI.getErrorDialog(mContext, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            dialog.show();
        }
    }

    private LocationService getLocationService() {
        return ServiceLocator.get(LocationService.class);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

            /*Commerce commerce = mCommercesFromMarkerMap.get(marker);
            if (commerce == null) return;

            Intent intent = new Intent();
            intent.setClass(getActivity(), CommerceDetailsActivity.class);
            intent.putExtra(getString(R.string.intent_data_commerce_id), commerce.getId());
            intent.putExtra(getString(R.string.intent_data_commerce_longitud_id), commerce.getLocation().getLongitud());
            intent.putExtra(getString(R.string.intent_data_commerce_latitud_id), commerce.getLocation().getLatitud());
            intent.putExtra(getString(R.string.intent_data_fromFavourites), false);
            startActivity(intent);
            */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        chargeMap();
    }

    @Override
    public void refresh() {
        chargeMap();
    }
}
