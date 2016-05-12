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

	@Pointcut("@annotation(requirePermissions) && execution(* *(..))")
	public void annotationPointCutDefinition(RequirePermissions requirePermissions){
	}

	@Around("annotationPointCutDefinition(requirePermissions)")
	public void around(ProceedingJoinPoint joinPoint, RequirePermissions requirePermissions) throws Throwable {
		PermissionGuardAware permissionGuardAware = (PermissionGuardAware) joinPoint.getTarget();
		/* In Aspect File, lambda expression fails. So 'new Runnable()' is used.  */
		permissionGuardAware.getPermissionGuard().requestPermission(new Runnable() {
			@Override
			public void run() {
				try {
					joinPoint.proceed();
				} catch (Throwable e) {
					Log.d(LOG_TAG, "joinPoint errror", e);
				}
			}
		}, requirePermissions.permissions());
	}

}
