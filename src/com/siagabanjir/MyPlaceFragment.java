package com.siagabanjir;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MyPlaceFragment extends Fragment implements OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	public GoogleMap peta = null;
	private SupportMapFragment mapFragment;
	private Context context;
	private LocationClient locationClient;
	private LocationRequest locationRequest;
	private boolean locationEnabled;
	
	public MyPlaceFragment(Context context) {
		this.context = context;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_myplace, container, false);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    FragmentManager fm = getChildFragmentManager();
	    mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
	    if (mapFragment == null) {
	        mapFragment = SupportMapFragment.newInstance();
	        fm.beginTransaction().replace(R.id.map, mapFragment).commit();
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    initializeMap();
	    setupUserLocation();
	}
	
	private void setupUserLocation() {
		locationClient = new LocationClient(context, this, this);
		locationRequest = new LocationRequest();
		
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(5);
		locationRequest.setFastestInterval(1);
		
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationEnabled = false;
			Toast.makeText(context, "Enable location services for accurate data", Toast.LENGTH_SHORT).show();
		}
		
		else  {
			locationEnabled = true;
		}
		
		locationClient.connect();
		
	}
	
	private void initializeMap() {
        if (peta == null) {
        	peta = mapFragment.getMap();
 
            // check if map is created successfully or not
            if (peta == null) {
                Toast.makeText(this.getActivity().getApplicationContext(),
                        "Error showing map", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		//locationClient.setMockMode(true);
		Location location = locationClient.getLastLocation();
		if (location != null) {
			Geocoder geocoder = new Geocoder(context);
			try {
				List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				address.get(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Toast.makeText(context, "Initial location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
			LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
			refreshMap(currentLoc);
			
		}
		else if (location == null && locationEnabled) {
			locationClient.requestLocationUpdates(locationRequest, this);
		} 
	}
	
	public void refreshMap(LatLng currentLoc) {
		DataPintuAir.initLocation();
		
		peta.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
		
		MarkerOptions marker = new MarkerOptions();
		marker.position(currentLoc);
		Address addr;
		try {
			addr = new Geocoder(context).getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1).get(0);
			System.out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		marker.draggable(true);
		marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
		
		peta.setOnMarkerDragListener(this);
		peta.addMarker(marker);
		
		for (LatLng loc : DataPintuAir.locationPintuAir.values()) {
			MarkerOptions markerPintuAir = new MarkerOptions().position(loc);
			
			peta.addMarker(markerPintuAir);
			CircleOptions circle = new CircleOptions();
			
			int strokeColor = 0xffff0000; //red outline
		    int shadeColor = 0x44ff0000;
		    
			circle.center(loc);
			circle.radius(4000.0f);
			circle.strokeColor(strokeColor);
			circle.fillColor(shadeColor);
			
			peta.addCircle(circle);
		}
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		locationClient.removeLocationUpdates(this);
		Toast.makeText(context, "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
		
		
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		checkLocation(marker);
		
	}
	
	public void checkLocation(Marker marker) {
		HashMap<String, LatLng> inArea = DataPintuAir.checkLocation(marker.getPosition());
		
		
		String pintuAir = "";
		for (String locName : inArea.keySet()) {
			pintuAir += locName + ", ";
		}
		
		
		Toast.makeText(context, "Selected location: " + marker.getPosition().latitude + ", " + marker.getPosition().longitude + "\nNearest floodgates: " + pintuAir, Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}