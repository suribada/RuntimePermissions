package com.naver.sample.runtimepermissions;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

/**
 * Created by nhn on 16. 5. 10..
 */
public class Permissions {

	public static String getText(@NonNull Context context, @NonNull String[] permissions) {
		if (permissions.length == 1) {
			Pair<String, String> permissionLabel = getPermissionLabel(context, permissions[0]);
			return context.getString(R.string.permission_rationale, permissionLabel.first, permissionLabel.second);
		}
		ArrayList<String> groupLabels = new ArrayList<>();
		for (String permission : permissions) {
			Pair<String, String> permissionLabel = getPermissionLabel(context, permission);
			groupLabels.add(permissionLabel.first);
		}
		return context.getString(R.string.permission_rationale_multiple, TextUtils.join(", ", groupLabels.toArray(new String[0])));
	}

	private static Pair<String, String> getPermissionLabel(@NonNull Context context, @NonNull String permissionName) {
		try {
			PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(permissionName, 0);
			CharSequence permissionLabel = permissionInfo.loadLabel(context.getPackageManager());

			PackageItemInfo groupInfo = context.getPackageManager().getPermissionGroupInfo(permissionInfo.group, 0);
			CharSequence permissionGroupLabel = groupInfo.loadLabel(context.getPackageManager());
			return new Pair<>(permissionGroupLabel.toString(), permissionLabel.toString());
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
