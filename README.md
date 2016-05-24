# RuntimePermissions

## 시작
1. https://github.com/googlesamples의 Permission 관련 샘플과, 송지철님의 https://oss.navercorp.com/g-chul-song/AndroidPermission 코드 참고
2. 기존 코드 수정을 최소한으로 할 방법을 생각=>애너테이션을 사용하고 AOP에서 코드를 변경하면 어떨까?

## 진행
1. Activity와 UI 의존성이 많으므로 라이브러리로 따로 만들지는 않는다.
2. 실제에서는 권한을 Deny하는 경우 `다시 묻지 않기`에 대해서도 나중에 다시 UI를 보여주는 경우가 있는데 이에 대한 처리를 하지 않았다. 
SharedPreferences로 하는 방법도 있으나 '데이터 삭제'같은 경우도 있기 때문에 어차피 완벽할 수 없다.
3. 권한이 모두 동의되었을 때 하려는 작업을 진행하고, 아니면 아무 것도 하지 않는 간단한 시나리오에만 대응하였다.

## 샘플 1
AOP를 사용하지 않는 예. 반드시 AOP를 사용할 필요가 없다.
```
public void onClickSinglePermission(View view) {
		permissionGuard.requestPermission(this::requestLocationUpdate, Manifest.permission.ACCESS_FINE_LOCATION);
	}

	public void onClickMultiPermissions(View view) {
		permissionGuard.requestPermission(this::writeMyPhoneNumber, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);
	}
```

## 샘플 2
AOP를 사용한 예. 샘플을 보면 코드에서 할 게 있긴 하다.
```
	@RequirePermissions(permissions = {Manifest.permission.ACCESS_FINE_LOCATION})
	private void requestLocationUpdateWithAnnotatated() {
		Log.d(LOG_TAG, "requestLocationUpdateWithAnnotatated");
		requestLocationUpdate();
	}

	@RequirePermissions(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE})
	private void writeMyPhoneNumberWithAnnotatated() {
		Log.d(LOG_TAG, "writeMyPhoneNumberWithAnnotatated");
		writeMyPhoneNumber();
	}
```

