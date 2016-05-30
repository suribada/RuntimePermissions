package com.naver.runtimepermissions;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by Noh.Jaechun on 16. 5. 11..
 */
@Aspect
public class PermissionAspect {

	public static final String LOG_TAG = "PermissionAspect";

	@Pointcut("@annotation(askPermission) && execution(* *(..))")
	public void annotationPointCutDefinition(AskPermission askPermission){
	}

	@Around("annotationPointCutDefinition(askPermission)")
	public void around(ProceedingJoinPoint joinPoint, AskPermission askPermission) throws Throwable {
		PermissionGuardAware permissionGuardAware = (PermissionGuardAware) joinPoint.getTarget();
		PermissionGuard permissionGuard = permissionGuardAware.getPermissionGuard();
		if (permissionGuard.isPermissionResult()) {
			return;
		}
		/* In Aspect File, lambda expression fails. So 'new Runnable()' is used.  */
		permissionGuard.requestPermission(new Runnable() {
			@Override
			public void run() {
				try {
					joinPoint.proceed();
				} catch (Throwable e) {
					Log.d(LOG_TAG, "joinPoint errror", e);
				}
			}
		}, askPermission.value());
	}

}
