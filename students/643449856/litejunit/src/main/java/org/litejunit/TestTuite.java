package org.litejunit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestTuite extends Assert implements Test {
	List<Test> caselist = new ArrayList<>(10);
	List<String> namelist = new ArrayList<>();
	private String name;

	public TestTuite(String name) {
		this.name = name;
	}

	public  TestTuite(final Class<?> clazz) {
		if (!Modifier.isPublic(clazz.getModifiers())) {
			fail("the class " + clazz.getName() + " must be public");
		}
		Constructor<?> constructor = getConstructor(clazz);
		Method[] arrmethod = clazz.getDeclaredMethods();
		for (Method m : arrmethod) {
			addTest(m, constructor);
		}

		if (arrmethod.length == 0) {
			fail("there is no method to test!");
		}
	}

	private Constructor<?> getConstructor(Class<?> clazz) {
		Class<?>[] parameterTypes = new Class<?>[] { String.class };
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getConstructor(parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return constructor;
	}

	public void addTest(Method m, Constructor<?> constructor) {
		if (!isTestMethod(m)) {
			return;
		}
		String method_name = m.getName();
		if (namelist.contains(method_name)) {
			return;
		}
		namelist.add(method_name);
		Object[] initargs = new Object[] { method_name };
		try {
			Test t = (Test) constructor.newInstance(initargs);
			addTest(t);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void addTest(Test t) {
		caselist.add(t);
	}

	public void addTest(Class<?> clazz) {
		Object obj = null;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}

		Method method = null;
		try {
			Class<?>[] args = new Class<?>[0];
			method=clazz.getMethod("tuite", args);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		try {
			Test test = (Test) method.invoke(obj);
			addTest(test);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	private boolean isTestMethod(Method m) {

		return m.getName().startsWith("test") && Modifier.isPublic(m.getModifiers())
				&& m.getParameterTypes().length == 0 && m.getReturnType().equals(Void.TYPE);
	}

	@Override
	public void run(TestResult testResult) {
		for (Test tc : caselist) {
			tc.run(testResult);

		}
	}

	@Override
	public Integer getCaseCount() {
		int count = 0;
		for (Iterator<Test> iterator = caselist.iterator(); iterator.hasNext();) {
			Test test = iterator.next();
			count += test.getCaseCount();
		}
		return count;
	}

	public void addTestTuite(Class<?> clazz) {
		addTest(new TestTuite(clazz)); 
	}

}
