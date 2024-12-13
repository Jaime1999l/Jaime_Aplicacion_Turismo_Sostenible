package com.example.jaime_aplicacion_turismo_sostenible.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.jaime_aplicacion_turismo_sostenible.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private List<MarkerData> allLocations = new ArrayList<>();
    private List<Marker> allMarkers = new ArrayList<>();

    private AutoCompleteTextView searchBar;
    private Spinner spinnerFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        initializeUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Habilitar controles de zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Cargar ubicaciones de la API
        loadMarkersFromOverpass();
    }

    private void initializeUI() {
        searchBar = findViewById(R.id.search_bar);
        spinnerFilter = findViewById(R.id.spinner_filter);

        // Configurar opciones del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.location_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                filterMarkers(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                filterMarkersBySearch(query.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadMarkersFromOverpass() {
        new Thread(() -> {
            try {
                String overpassQuery = "[out:json];("
                        + "node[leisure=park](40.4,-3.8,40.6,-3.6);"
                        + "node[amenity=clinic](40.4,-3.8,40.6,-3.6);"
                        + "node[amenity=hospital](40.4,-3.8,40.6,-3.6);"
                        + "node[amenity=police](40.4,-3.8,40.6,-3.6);"
                        + "node[amenity=recycling](40.4,-3.8,40.6,-3.6);"
                        + ");out;";
                String url = "https://overpass-api.de/api/interpreter?data=" + overpassQuery;

                String json = readUrl(url);
                parseOverpassJson(json);

                runOnUiThread(() -> Toast.makeText(this, "Datos cargados", Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    private void parseOverpassJson(String json) {
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(json, Map.class);
        List<Map<String, Object>> elements = (List<Map<String, Object>>) map.get("elements");

        for (Map<String, Object> element : elements) {
            if (element.containsKey("lat") && element.containsKey("lon")) {
                double lat = Double.parseDouble(element.get("lat").toString());
                double lon = Double.parseDouble(element.get("lon").toString());

                String tipo = "Ubicación";
                if (element.containsKey("tags")) {
                    Map<String, String> tags = (Map<String, String>) element.get("tags");
                    if (tags.containsKey("leisure") && tags.get("leisure").equals("park")) {
                        tipo = "Parque";
                    } else if (tags.containsKey("amenity")) {
                        switch (tags.get("amenity")) {
                            case "clinic":
                            case "hospital":
                                tipo = "Centro de Salud";
                                break;
                            case "police":
                                tipo = "Policía";
                                break;
                            case "recycling":
                                tipo = "Reciclaje";
                                break;
                        }
                    }
                }

                allLocations.add(new MarkerData(tipo, new LatLng(lat, lon)));
            }
        }
    }

    private void filterMarkers(String type) {
        mMap.clear();
        for (MarkerData data : allLocations) {
            if (type.equals("Todos") || data.type.equals(type)) {
                mMap.addMarker(new MarkerOptions().position(data.position).title(data.type));
            }
        }
    }

    private void filterMarkersBySearch(String query) {
        mMap.clear();
        for (MarkerData data : allLocations) {
            if (data.type.toLowerCase().contains(query)) {
                mMap.addMarker(new MarkerOptions().position(data.position).title(data.type));
            }
        }
    }

    private String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } finally {
            if (reader != null) reader.close();
        }
    }

    static class MarkerData {
        String type;
        LatLng position;

        MarkerData(String type, LatLng position) {
            this.type = type;
            this.position = position;
        }
    }
}
