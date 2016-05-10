package com.naver.sample.runtimepermissions;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nhn on 16. 5. 4..
 */
public class PermissionGuard {

	private static final AtomicInteger requestId = new AtomicInteger(0);
	private AppCompatActivity thisActivity;

	public PermissionGuard(@NonNull AppCompatActivity activity) {
		this.thisActivity = activity;
	}

	public void requestPermission(@NonNull Runnable runnable, @NonNull  String... permissions) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M  || isPermissionsGranted(permissions)) {
			runnable.run();
			return;
		}
		String[] rationalePermissions = shouldShowRequestPermissionRationale(permissions);
		if (rationalePermissions.length >= 0) {
			new AlertDialog.Builder(thisActivity).setMessage(Permissions.getText(thisActivity, rationalePermissions))
				.setPositiveButton(R.string.confirm, (dialog, which) -> {
					requestPermission(permissions);
				})
				.setNegativeButton(R.string.cancel, null)
				.show();
		} else {
			requestPermission(permissions);
		}
	}

	private boolean isPermissionsGranted(String[] permissions) {
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(thisActivity, permission) == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}

	/**
	 *  Denied case
	 */
	private String[] shouldShowRequestPermissionRationale(String[] permissions) {
		ArrayList<String> rationalePermissions = new ArrayList<>();
		for (String permission : permissions) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, permission)) {
				rationalePermissions.add(permission);
			}
		}
		return rationalePermissions.toArray(new String[0]);
	}

	private void requestPermission(String[] permission) {
		ActivityCompat.requestPermissions(thisActivity, permission, requestId.incrementAndGet());
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

	}

}
