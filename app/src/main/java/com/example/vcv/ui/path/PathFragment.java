package com.example.vcv.ui.path;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.vcv.R;
import com.example.vcv.utility.PersonalMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PathFragment extends Fragment{

    private PathViewModel pathViewModel;
    private ArrayList<PersonalMap> maps = new ArrayList<>();
    private Spinner paths;
    private GoogleMap map;
    private MapView mapView;
    View root;
    Bundle savedInstanceState;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pathViewModel =
                ViewModelProviders.of(this).get(PathViewModel.class);
        root = inflater.inflate(R.layout.fragment_path, container, false);
        PathViewModel.pathFragment = this;
        this.savedInstanceState = savedInstanceState;
        paths = (Spinner) root.findViewById(R.id.s_paths);
        mapView = (MapView) root.findViewById(R.id.mapView);

        //download paths from firebase and set map
        pathViewModel.downLoadMap();

        return root;
    }

    public void setMenu(ArrayList<PersonalMap> downloadedMaps){
        maps = downloadedMaps;
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                getActivity().getBaseContext(),
                android.R.layout.simple_spinner_dropdown_item,
                android.R.id.text1);
        for (PersonalMap map :
                maps) {
            spinnerAdapter.add(map.path);
        }
        paths.setAdapter(spinnerAdapter);
        paths.setSelection(0);

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
                paths.setSelection(0);
            }
        });

        paths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                map.clear();
                int positionSelected = paths.getSelectedItemPosition();

                //create first position (green)
                LatLng start = new LatLng(maps.get(positionSelected).latitudeFrom, maps.get(positionSelected).longitudeFrom);
                MarkerOptions markerOptionsStart = new MarkerOptions();
                markerOptionsStart.position(start);
                markerOptionsStart.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                map.addMarker(markerOptionsStart);

                //create second position (azure)
                LatLng destination = new LatLng(maps.get(positionSelected).latitudeTo, maps.get(positionSelected).longitudeTo);
                MarkerOptions markerOptionsDest = new MarkerOptions();
                markerOptionsDest.position(destination);
                markerOptionsDest.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                map.addMarker(markerOptionsDest);

                //Focus on the position
                CameraPosition cameraPosition = new CameraPosition.Builder().target(start).zoom(11).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
