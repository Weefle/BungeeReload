/*
 * Copyright (c) 2016 Jofkos. All rights reserved.
 */

package com.jofkos.breload;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public class Reflect {

	private static final FieldAccessor modifiers = getField(Field.class, "modifiers");

	public static FieldAccessor getField(Field field) {
		return new FieldAccessor(setAccessible(field));
	}

	public static FieldAccessor getField(Class<?> clazz, String fieldName) {
		Field field;
		try {
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				field = clazz.getField(fieldName);
			}
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("No field '" + fieldName + "' found in '" + clazz.getCanonicalName() + "'", e);
		}

		return new FieldAccessor(setAccessible(field));
	}

	public static Field setAccessible(Field field) {
		try {
			field.setAccessible(true);
			
			if (Modifier.isFinal(field.getModifiers())) {
				modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);
			}
			
			return field;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MethodAccessor getMethod(Class<?> clazz, String methodName, Class<?>... args) {
		Method method;
		try {
			try {
				method = clazz.getDeclaredMethod(methodName, args);
			} catch (NoSuchMethodException e) {
				method = clazz.getMethod(methodName, args);
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No method '" + methodName + "' found in '" + clazz.getCanonicalName() + "'", e);
		}
		method.setAccessible(true);

		return new MethodAccessor(method);
	}

	public static <T> Class<T> getClass(String className) {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class '" + className + "' not found", e);
		}
	}

	public static class FieldAccessor {

		protected Field field;

		FieldAccessor(Field field) {
			this.field = field;
		}


		public <T> T get(Object instance) {
			try {
				return (T) field.get(instance);
			} catch (Exception ex) {
				throw new RuntimeException(String.format("Error while reading field '%s'", field), ex);
			}
		}

		public <T> T getStatic() {
			return get(null);
		}


		public void set(Object instance, Object value) {
			try {
				field.set(instance, value);
			} catch (Exception ex) {
				throw new RuntimeException(String.format("Error while setting field '%s'", field), ex);
			}
		}

		public void setStatic(Object value) {
			set(null, value);
		}

		public Field getField() {
			return field;
		}


	}


	public static class MethodAccessor {

		private Method method;

		MethodAccessor(Method method) {
			this.method = method;
		}

		public <T> T invoke(Object instance, Object... args) {
			try {
				return (T) method.invoke(instance, args);
			} catch (Exception ex) {
				throw new RuntimeException(String.format("Error while invoking method '%s'", method), ex);
			}
		}

		public Method getMethod() {
			return method;
		}
	}

}
