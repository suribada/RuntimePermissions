package com.naver.runtimepermissions;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.naver.sample.runtimepermissions.R;

/**
 * Created by Noh.jaechun on 16. 5. 10..
 */
public class Permissions {

	public static String getText(@NonNull Context context, @NonNull ArrayList<String> permissions) {
		HashSet<String> groupLabels = new HashSet<>();
		for (String permission : permissions) {
			groupLabels.add(getPermissionLabel(context, permission));
		}
		return context.getResources().getQuantityString(R.plurals.permission_rationale, groupLabels.size(),
			TextUtils.join(", ", groupLabels.toArray(new String[0])));
	}

	private static String getPermissionLabel(@NonNull Context context, @NonNull String permissionName) {
		try {
			PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(permissionName, 0);
			PackageItemInfo groupInfo = context.getPackageManager().getPermissionGroupInfo(permissionInfo.group, 0);
			CharSequence permissionGroupLabel = groupInfo.loadLabel(context.getPackageManager());
			return permissionGroupLabel.toString();
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
