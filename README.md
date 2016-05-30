# RuntimePermissions

## 시작
1. https://github.com/googlesamples의 Permission 관련 샘플과, 송지철님의 https://oss.navercorp.com/g-chul-song/AndroidPermission 코드 참고
2. 기존 코드 수정을 최소한으로 할 방법을 생각=>애너테이션을 사용하고 AOP에서 코드를 변경하면 어떨까?

## 진행
1. Activity와 UI 의존성이 많으므로 라이브러리로 따로 만들지는 않는다.
2. `다시 묻지 않기`를 선택하고 권한을 Deny하는 경우, SnackBar에 메시지를 보여주도록 한다.
3. 권한이 모두 동의되었을 때 하려는 작업을 진행하고, 아니면 아무 것도 하지 않는 간단한 시나리오에만 대응하였다.
4. onResume() 메서드에서 권한을 필요로 하는 경우, Deny하고서 돌아오면 자꾸 권한을 또 요청하는 케이스에 대응하였다.

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
```

