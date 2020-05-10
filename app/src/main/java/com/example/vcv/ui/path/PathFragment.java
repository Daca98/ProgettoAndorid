package com.example.vcv.ui.path;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.vcv.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PathFragment extends Fragment{

    private PathViewModel pathViewModel;
    GoogleMap map;
    MapView mapView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pathViewModel =
                ViewModelProviders.of(this).get(PathViewModel.class);
        View root = inflater.inflate(R.layout.fragment_path, container, false);

        mapView = (MapView) root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                map = mMap;

                //Creo primo segnaposto (verde)
                LatLng start = new LatLng(45.797077, 8.882996);
                MarkerOptions markerOptionsStart = new MarkerOptions();
                markerOptionsStart.position(start);
                markerOptionsStart.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                map.addMarker(markerOptionsStart);

                //Creo secondo segnaposto (rosso)
                LatLng destination = new LatLng(45.719114, 8.836077);
                MarkerOptions markerOptionsDest = new MarkerOptions();
                markerOptionsDest.position(destination);
                markerOptionsDest.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                map.addMarker(markerOptionsDest);

                // Metto focus sulla posizione
                CameraPosition cameraPosition = new CameraPosition.Builder().target(start).zoom(11).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return root;
    }
}
