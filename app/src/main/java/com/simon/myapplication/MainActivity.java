package com.simon.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private GraphicsOverlay mGraphicsOverlay;
    private LocationDisplay mLocationDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        setupMap();
        setupLocationDisplay();
        createGraphics();
    }

    private void setupLocationDisplay() {
        mLocationDisplay = mMapView.getLocationDisplay();
        mLocationDisplay = mMapView.getLocationDisplay();
        /* ** ADD ** */
        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                return;
            }

            int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            if (!(ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
            } else {
                String message = String.format("Error in DataSourceStatusChangedListener: %s",
                        dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                LocationDataSource.Location location = locationChangedEvent.getLocation();
                Point position = location.getPosition();
                Log.e("simonfong", "onLocationChanged:" + position.toString());

            }
        });
        mLocationDisplay.startAsync();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void createGraphics() {
        createGraphicsOverlay();
//        createPointGraphics();
    }

    private void createGraphicsOverlay() {
        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
    }

    private void setupMap() {
        if (mMapView != null) {
            String url = "http://202.104.29.138:1027/arcgis/rest/services/ShunDe/shunde11month20191205/MapServer";
            ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(url);
            Basemap basemap = new Basemap(arcGISTiledLayer);
            ArcGISMap arcGISMap = new ArcGISMap(basemap);
            mMapView.setMap(arcGISMap);
            mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    android.graphics.Point screenPoint = new android.graphics.Point(
                            Math.round(e.getX()),
                            Math.round(e.getY()));
                    Point mapPoint = mMapView.screenToLocation(screenPoint);
                    Log.e("simonfong", "onSingleTapUp----x=" + mapPoint.getX() + "---y=" + mapPoint.getY());
                    createPointGraphics(mapPoint);
                    return super.onSingleTapUp(e);
                }
            });
        }
    }

    private void createPointGraphics(Point point) {
//        Point point = new Point(113.264569, 22.817918, SpatialReferences.getWgs84());
        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f);
        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f));
        Graphic pointGraphic = new Graphic(point, pointSymbol);
        mGraphicsOverlay.getGraphics().add(pointGraphic);
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.dispose();
        }
        super.onDestroy();
    }
}
