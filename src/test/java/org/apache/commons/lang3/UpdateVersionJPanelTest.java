package org.apache.commons.lang3;

import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;

public class UpdateVersionJPanelTest {

	private static Method METHOD_CAST, METHOD_ADD, METHOD_GET_NAME, METHOD_GET_CLASS = null;

	@BeforeSuite
	void beforeSuite() throws NoSuchMethodException {
		//
		final Class<?> clz = UpdateVersionJPanel.class;
		//
		(METHOD_CAST = clz.getDeclaredMethod("cast", Class.class, Object.class)).setAccessible(true);
		//
		(METHOD_ADD = clz.getDeclaredMethod("add", Collection.class, Object.class)).setAccessible(true);
		//
		(METHOD_GET_NAME = clz.getDeclaredMethod("getName", Member.class)).setAccessible(true);
		//
		(METHOD_GET_CLASS = clz.getDeclaredMethod("getClass", Object.class)).setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private Boolean test, containsKey, add;

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			final String name = getName(method);
			//
			if (proxy instanceof Map) {
				//
				if (contains(Arrays.asList("put", "get"), name)) {
					//
					return null;
					//
				} else if (Objects.equals(name, "containsKey")) {
					//
					return containsKey;
					//
				} // if
					//
			} else if (proxy instanceof Member && Objects.equals(name, "getName")) {
				//
				return null;
				//
			} else if (proxy instanceof XPath && Objects.equals(name, "evaluate")) {
				//
				return null;
				//
			} else if (proxy instanceof Collection) {
				//
				if (Objects.equals(name, "add")) {
					//
					return add;
					//
				} else if (Objects.equals(name, "stream")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof Stream) {
				//
				if (Objects.equals(name, "toList")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "filter")) {
					//
					return proxy;
					//
				} // if
					//
			} else if (proxy instanceof Predicate && Objects.equals(name, "test")) {
				//
				return test;
				//
			} else if (proxy instanceof FailableFunction && Objects.equals(name, "apply")) {
				//
				return null;
				//
			} // if
				//
			throw new Throwable(name);
			//
		}

	}

	private static Class<?> getClass(final Object instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_CLASS != null ? METHOD_GET_CLASS.invoke(null, instance) : null;
			if (obj == null) {
				return null;
			} else if (obj instanceof Class) {
				return (Class<?>) obj;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static Class<?> getReturnType(final Method instance) {
		return instance != null ? instance.getReturnType() : null;
	}

	private UpdateVersionJPanel instance = null;

	@BeforeMethod
	void beforeMethod() throws Throwable {
		//
		instance = cast(UpdateVersionJPanel.class, Narcissus.allocateInstance(UpdateVersionJPanel.class));
		//
	}

	@Test
	public void testCast() throws Throwable {
		//
		Assert.assertNull(cast(Object.class, null));
		//
	}

	private static <T> T cast(final Class<T> clz, final Object instance) throws Throwable {
		try {
			return (T) (METHOD_CAST != null ? METHOD_CAST.invoke(null, clz, instance) : null);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testNull() throws Throwable {
		//
		final Method[] ms = UpdateVersionJPanel.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Object result = null;
		//
		String toString = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Collection<Object> collection = null;
		//
		Object[] os = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else {
					//
					add(collection, null);
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			toString = Objects.toString(m);
			//
			result = Modifier.isStatic(m.getModifiers()) ? Narcissus.invokeStaticMethod(m, os)
					: Narcissus.invokeMethod(instance, m, os);
			//
			if (contains(Arrays.asList(Integer.TYPE, Boolean.TYPE), getReturnType(m))) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	private static boolean contains(final Collection<?> items, final Object item) {
		return items != null && items.contains(item);
	}

	private static Object[] toArray(final Collection<?> instance) {
		return instance != null ? instance.toArray() : null;
	}

	@Test
	void testNotNull() throws Throwable {
		//
		final Method[] ms = UpdateVersionJPanel.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Object result, name = null;
		//
		String toString = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Collection<Object> collection = null;
		//
		Object[] os = null;
		//
		final IH ih = new IH();
		//
		ih.test = ih.containsKey = ih.add = Boolean.FALSE;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null
					|| Boolean.logicalAnd(Objects.equals(name = getName(m), "toMap"),
							Arrays.equals(parameterTypes, new Class[] { String[].class }))
					|| Boolean.logicalAnd(Objects.equals(name = getName(m), "indexOf"),
							Arrays.equals(parameterTypes, new Class[] { String.class, String.class, Integer.TYPE }))) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if ((parameterType = ArrayUtils.get(parameterTypes, j)) != null && parameterType.isInterface()) {
					//
					add(collection, Reflection.newProxy(parameterType, ih));
					//
				} else if (Objects.equals(parameterType, String[].class)) {
					//
					add(collection, new String[] {});
					//
				} else if (Objects.equals(parameterType, DocumentBuilderFactory.class)) {
					//
					add(collection, DocumentBuilderFactory.newDefaultInstance());
					//
				} else if (Objects.equals(parameterType, XPathFactory.class)) {
					//
					add(collection, XPathFactory.newDefaultInstance());
					//
				} else if (Objects.equals(parameterType, Class.class)) {
					//
					add(collection, Object.class);
					//
				} else if (Objects.equals(parameterType, JTextComponent.class)) {
					//
					add(collection, new JTextField());
					//
				} else if (parameterType != null && parameterType.isArray()) {
					//
					add(collection, Array.newInstance(parameterType, 0));
					//
				} else {
					//
					add(collection, Narcissus.allocateInstance(parameterType));
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			toString = Objects.toString(m);
			//
			result = Modifier.isStatic(m.getModifiers()) ? Narcissus.invokeStaticMethod(m, os)
					: Narcissus.invokeMethod(instance, m, os);
			//
			if (contains(Arrays.asList(Integer.TYPE, Boolean.TYPE), getReturnType(m))
					|| Boolean.logicalAnd(Objects.equals(name, "filter"),
							Arrays.equals(parameterTypes, new Class<?>[] { Stream.class, Predicate.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "cast"),
							Arrays.equals(parameterTypes, new Class<?>[] { Class.class, Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "newDocumentBuilder"),
							Arrays.equals(parameterTypes, new Class<?>[] { DocumentBuilderFactory.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getClass"),
							Arrays.equals(parameterTypes, new Class<?>[] { Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getText"),
							Arrays.equals(parameterTypes, new Class<?>[] { JTextComponent.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "newXPath"),
							Arrays.equals(parameterTypes, new Class<?>[] { XPathFactory.class }))) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	private static String getName(final Member instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_NAME != null ? METHOD_GET_NAME.invoke(null, instance) : null;
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static <E> void add(final Collection<E> instance, final E item) throws Throwable {
		try {
			if (METHOD_ADD != null) {
				METHOD_ADD.invoke(null, instance, item);
			}
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static void clear(final Collection<?> instance) {
		if (instance != null) {
			instance.clear();
		}
	}

	@Test
	public void testMain() throws Exception {
		//
		UpdateVersionJPanel.main(new String[] { "=", "= ", " =", "== " });
		//
		UpdateVersionJPanel
				.main(new String[] { "file=pom.xml", "groupId=org.testng", "artifactId=testng", "version=7.11.0" });
		//
	}

	@Test
	public void testActionPerformed() throws Exception {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final AbstractButton btnUpdate = new JButton();
		//
		FieldUtils.writeDeclaredField(instance, "btnUpdate", btnUpdate, true);
		//
		final ActionEvent actionEvent = new ActionEvent(btnUpdate, 0, null);
		//
		instance.actionPerformed(actionEvent);
		//
		FieldUtils.writeDeclaredField(instance, "tfFile", new JTextField(new File("pom.xml").getAbsolutePath()), true);
		//
		instance.actionPerformed(actionEvent);
		//
		FieldUtils.writeDeclaredField(instance, "tfGroupId", new JTextField("org.apache.commons"), true);
		//
		FieldUtils.writeDeclaredField(instance, "tfArtifactId", new JTextField("commons-lang3"), true);
		//
		instance.actionPerformed(actionEvent);
		//
	}

}