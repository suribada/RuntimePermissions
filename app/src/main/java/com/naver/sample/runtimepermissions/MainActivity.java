package com.naver.sample.runtimepermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.naver.runtimepermissions.AskPermission;
import com.naver.runtimepermissions.PermissionGuard;
import com.naver.runtimepermissions.PermissionGuardAware;

/**
 * Sample Activity
 */
public class MainActivity extends FragmentActivity implements PermissionGuardAware {

	private static final String LOG_TAG = "MainActivity";

	private PermissionGuard permissionGuard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		permissionGuard = new PermissionGuard(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getMyPhoneNumber();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		 /* In case that onResume() method requires permission, permission dialog appears continuously when deny permissions.
		  This is an obligate choice.
		  */
		permissionGuard.resetPermissionsResult();
	}

	@AskPermission(value = Manifest.permission.READ_PHONE_STATE, resumed = true)
	private void getMyPhoneNumber() {
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		String phoneNumber = telephonyManager.getLine1Number();
		Log.d(LOG_TAG, "phoneNumber=" + phoneNumber);
	}

	/**
	 * For using at AOP pointcut
	 */
	@Override
	public PermissionGuard getPermissionGuard() {
		return permissionGuard;
	}

	/**
	 * This will throw a SecurityException at the version >= Mashmallow
	 */
	public void onClickLocationNoPermissionCheck(View view) {
		requestLocationUpdate();
	}

	public void onClickSinglePermission(View view) {
		permissionGuard.requestPermission(this::requestLocationUpdate, Manifest.permission.ACCESS_FINE_LOCATION);
	}

	public void onClickMultiPermissions(View view) {
		permissionGuard.requestPermission(this::writeMyPhoneNumber, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);
	}

	public void onClickSinglePermissionAnnotated(View view) {
		requestLocationUpdateWithAnnotatated();
	}

	public void onClickMultiPermissionsAnnotated(View view) {
		writeMyPhoneNumberWithAnnotatated();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
		@NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		permissionGuard.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	/**
	 * Sample method #1
	 * neeeds permission ACCESS_FINE_LOCATION
	 */
	@SuppressWarnings({"MissingPermission"})
	private void requestLocationUpdate() {
		Log.d(LOG_TAG, "requestLocationUpdate");
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 60000, new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				Toast.makeText(MainActivity.this, "Location=" + location.toString(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}
		});
	}

	/**
	 * Sample method #2
	 * neeeds permission WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE
	 */
	private void writeMyPhoneNumber() {
		Log.d(LOG_TAG, "writeMyPhoneNumber");
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String phoneNumber = telephonyManager.getLine1Number();
		File phoneFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "phone.info");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(phoneFile);
			fos.write(phoneNumber.getBytes());
		} catch (IOException ioe) {
			Log.e(LOG_TAG, "Could not write file", ioe);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}

	}

	@AskPermission(Manifest.permission.ACCESS_FINE_LOCATION)
	private void requestLocationUpdateWithAnnotatated() {
		Log.d(LOG_TAG, "requestLocationUpdateWithAnnotatated");
		requestLocationUpdate();
	}

	@AskPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE})
	private void writeMyPhoneNumberWithAnnotatated() {
		Log.d(LOG_TAG, "writeMyPhoneNumberWithAnnotatated");
		writeMyPhoneNumber();
	}

}
