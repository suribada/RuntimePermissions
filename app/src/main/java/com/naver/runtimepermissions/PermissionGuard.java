package com.naver.runtimepermissions;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.naver.sample.runtimepermissions.R;
import rx.Subscription;
import rx.subjects.PublishSubject;

/**
 * Created by Noh.jaechun on 16. 5. 4..
 */
public class PermissionGuard {

	private static int requestId = 0;
	private FragmentActivity thisActivity;
	private PublishSubject<Integer> publishSubject;
	private boolean onPermissonsResult = false;

	public PermissionGuard(@NonNull FragmentActivity activity) {
		this.thisActivity = activity;
		publishSubject = PublishSubject.create();
	}

	@UiThread
	public void requestPermission(@NonNull Runnable runnable, @NonNull  String... permissions) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M  || isPermissionsGranted(permissions)) {
			runnable.run();
			return;
		}
		ArrayList<String> rationalePermissions = shouldShowRequestPermissionRationale(permissions);
		if (rationalePermissions.size() > 0) {
			new AlertDialog.Builder(thisActivity).setMessage(Permissions.getText(thisActivity, rationalePermissions))
				.setPositiveButton(R.string.confirm, (dialog, which) -> {
					requestPermission(permissions, runnable);
				})
				.setNegativeButton(R.string.cancel, null)
				.show();
		} else {
			requestPermission(permissions, runnable);
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
	private ArrayList<String> shouldShowRequestPermissionRationale(String[] permissions) {
		ArrayList<String> rationalePermissions = new ArrayList<>();
		for (String permission : permissions) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, permission)) {
				rationalePermissions.add(permission);
			}
		}
		return rationalePermissions;
	}

	private Subscription subscription;

	private void requestPermission(@NonNull String[] permissions, @NonNull Runnable runnable) {
		ActivityCompat.requestPermissions(thisActivity, permissions, ++requestId);
		subscription = publishSubject.subscribe(v -> runnable.run());
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		onPermissonsResult = true;
		if (requestCode == requestId) {
			boolean allAgreed = true;
			ArrayList<String> revokedPermissions = new ArrayList<>();
			for (int i = 0, len = permissions.length;  i < len ; i++) {
				if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
					/* http://stackoverflow.com/questions/30719047/android-m-check-runtime-permission-how-to-determine-if-the-user-checked-nev */
					if (!ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, permissions[i])) {
						/* user denied flagging NEVER ASK AGAIN */
						revokedPermissions.add(permissions[i]);
					}
					allAgreed = false;
					break;
				}
			}
			if (revokedPermissions.size() > 0) {
				Snackbar.make(thisActivity.findViewById(android.R.id.content), R.string.permissions_do_not_ask_again,
					Snackbar.LENGTH_LONG)
					.setAction(R.string.ok, view -> {
						Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
							Uri.fromParts("package", thisActivity.getPackageName(), null));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						thisActivity.startActivity(intent);
					})
					.show();

			}
			if (allAgreed) {
				publishSubject.onNext(PackageManager.PERMISSION_GRANTED);
			}
			subscription.unsubscribe();
		}
	}

	public void resetPermissionsResult() {
		onPermissonsResult = false;
	}

	public boolean isPermissionResult() {
		return onPermissonsResult;
	}

}
