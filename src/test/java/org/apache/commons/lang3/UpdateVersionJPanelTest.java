package org.apache.commons.lang3;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.function.FailableRunnable;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.w3c.dom.NodeList;

import com.google.common.base.Predicates;
import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;

public class UpdateVersionJPanelTest {

	private static Method METHOD_CAST, METHOD_ADD, METHOD_GET_NAME, METHOD_GET_CLASS, METHOD_TEST_AND_GET_AS_BOOLEAN,
			METHOD_IS_FILE, METHOD_NEW_DOCUMENT_BUILDER, METHOD_TEST_AND_RUN, METHOD_AND, METHOD_TEST_AND_ACCEPT,
			METHOD_PARSE, METHOD_REPLACE = null;

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
		(METHOD_TEST_AND_GET_AS_BOOLEAN = clz.getDeclaredMethod("testAndGetAsBoolean", Boolean.TYPE,
				BooleanSupplier.class)).setAccessible(true);
		//
		(METHOD_IS_FILE = clz.getDeclaredMethod("isFile", File.class)).setAccessible(true);
		//
		(METHOD_NEW_DOCUMENT_BUILDER = clz.getDeclaredMethod("newDocumentBuilder", DocumentBuilderFactory.class))
				.setAccessible(true);
		//
		(METHOD_TEST_AND_RUN = clz.getDeclaredMethod("testAndRun", Boolean.TYPE, FailableRunnable.class))
				.setAccessible(true);
		//
		(METHOD_AND = clz.getDeclaredMethod("and", Boolean.TYPE, Boolean.TYPE, boolean[].class)).setAccessible(true);
		//
		(METHOD_TEST_AND_ACCEPT = clz.getDeclaredMethod("testAndAccept", Predicate.class, Object.class,
				FailableConsumer.class)).setAccessible(true);
		//
		(METHOD_PARSE = clz.getDeclaredMethod("parse", DocumentBuilder.class, InputStream.class)).setAccessible(true);
		//
		(METHOD_REPLACE = clz.getDeclaredMethod("replace", String.class, Character.TYPE, Character.TYPE))
				.setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private Boolean test, containsKey, add, getAsBoolean;

		private Integer length, size;

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			if (Objects.equals(method != null ? method.getReturnType() : null, Void.TYPE)) {
				//
				return null;
				//
			} // if
				//
			final String name = getName(method);
			//
			if (proxy instanceof ListModel) {
				//
				if (Objects.equals(name, "getSize")) {
					//
					return size;
					//
				} else if (Objects.equals(name, "getElementAt")) {
					//
					return null;
					//
				} // if
					//
			} // if
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
				if (contains(Arrays.asList("toList", "map"), name)) {
					//
					return null;
					//
				} else if (contains(Arrays.asList("filter", "distinct", "sorted"), name)) {
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
			} else if (proxy instanceof ComboBoxModel && Objects.equals(name, "getSelectedItem")) {
				//
				return null;
				//
			} else if (proxy instanceof BooleanSupplier && Objects.equals(name, "getAsBoolean")) {
				//
				return getAsBoolean;
				//
			} else if (proxy instanceof NodeList) {
				//
				if (Objects.equals(name, "getLength")) {
					//
					return length;
					//
				} else if (Objects.equals(name, "item")) {
					//
					return null;
					//
				} // if
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

	private IH ih = null;

	@BeforeMethod
	void beforeMethod() throws Throwable {
		//
		instance = cast(UpdateVersionJPanel.class, Narcissus.allocateInstance(UpdateVersionJPanel.class));
		//
		ih = new IH();
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
		Class<?> parameterType = null;
		//
		Collection<Object> collection = null;
		//
		Object[] os = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null
					|| Boolean.logicalAnd(Objects.equals(getName(m), "and"), Arrays.equals(parameterTypes,
							new Class[] { Boolean.TYPE, Boolean.TYPE, boolean[].class }))) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (Objects.equals(parameterType, Boolean.TYPE)) {
					//
					add(collection, Boolean.valueOf(false));
					//
				} else if (Objects.equals(parameterType, Character.TYPE)) {
					//
					add(collection, Character.valueOf(' '));
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
		if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
			//
			ih.test = ih.containsKey = ih.add = Boolean.FALSE;
			//
			ih.length = ih.size = Integer.valueOf(0);
			//
		} // if
			//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null
					|| Boolean.logicalAnd(Objects.equals(name = getName(m), "toMap"),
							Arrays.equals(parameterTypes, new Class[] { String[].class }))
					|| Boolean.logicalAnd(Objects.equals(name, "indexOf"),
							Arrays.equals(parameterTypes, new Class[] { String.class, String.class, Integer.TYPE }))
					|| Boolean.logicalAnd(Objects.equals(name, "replace"),
							Arrays.equals(parameterTypes, new Class[] { String.class, Character.TYPE, Character.TYPE }))
					|| Boolean.logicalAnd(Objects.equals(name, "and"), Arrays.equals(parameterTypes,
							new Class[] { Boolean.TYPE, Boolean.TYPE, boolean[].class }))) {
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
				} else if (Objects.equals(parameterType, DocumentBuilder.class)) {
					//
					add(collection, newDocumentBuilder(DocumentBuilderFactory.newDefaultInstance()));
					//
				} else if (Objects.equals(parameterType, Class.class)) {
					//
					add(collection, Object.class);
					//
				} else if (Objects.equals(parameterType, JTextComponent.class)) {
					//
					add(collection, new JTextField());
					//
				} else if (Objects.equals(parameterType, Boolean.TYPE)) {
					//
					add(collection, Boolean.valueOf(false));
					//
				} else if (Objects.equals(parameterType, Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (Objects.equals(parameterType, InputStream.class)) {
					//
					add(collection, new ByteArrayInputStream(new byte[] {}));
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
					|| Boolean.logicalAnd(Objects.equals(name, "distinct"),
							Arrays.equals(parameterTypes, new Class<?>[] { Stream.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "sorted"),
							Arrays.equals(parameterTypes, new Class<?>[] { Stream.class }))
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

	private static DocumentBuilder newDocumentBuilder(final DocumentBuilderFactory instance) throws Throwable {
		try {
			final Object obj = METHOD_NEW_DOCUMENT_BUILDER != null ? METHOD_NEW_DOCUMENT_BUILDER.invoke(null, instance)
					: null;
			if (obj == null) {
				return null;
			} else if (obj instanceof DocumentBuilder) {
				return (DocumentBuilder) obj;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
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
		instance.actionPerformed(new ActionEvent("", 0, null));
		//
		// btnUpdate
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
		FieldUtils.writeDeclaredField(instance, "dcbmGroupId", new DefaultComboBoxModel<>(), true);
		//
		FieldUtils.writeDeclaredField(instance, "dcbmArtifactId", new DefaultComboBoxModel<>(), true);
		//
		instance.actionPerformed(actionEvent);
		//
		// jcbGroupId
		//
		final JComboBox<?> jcbGroupId = new JComboBox<>();
		//
		FieldUtils.writeDeclaredField(instance, "jcbGroupId", jcbGroupId, true);
		//
		FieldUtils.writeDeclaredField(instance, "dependencies", Collections.singleton(null), true);
		//
		instance.actionPerformed(new ActionEvent(jcbGroupId, 0, null));
		//
		// jcbArtifactId
		//
		final JComboBox<?> jcbArtifactId = new JComboBox<>();
		//
		FieldUtils.writeDeclaredField(instance, "jcbArtifactId", jcbArtifactId, true);
		//
		instance.actionPerformed(new ActionEvent(jcbArtifactId, 0, null));
		//
		// btnCheckVersion
		//
		final AbstractButton btnCheckVersion = new JButton();
		//
		FieldUtils.writeDeclaredField(instance, "btnCheckVersion", btnCheckVersion, true);
		//
		instance.actionPerformed(new ActionEvent(btnCheckVersion, 0, null));
		//
	}

	@Test
	public void testTestAndGetAsBoolean() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(
				METHOD_TEST_AND_GET_AS_BOOLEAN != null ? METHOD_TEST_AND_GET_AS_BOOLEAN.invoke(null, Boolean.TRUE, null)
						: null,
				Boolean.FALSE);
		//
		if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
			//
			ih.getAsBoolean = Boolean.FALSE;
			//
		} // if
			//
		final BooleanSupplier booleanSupplier = Reflection.newProxy(BooleanSupplier.class, ih);
		//
		Assert.assertEquals(METHOD_TEST_AND_GET_AS_BOOLEAN != null
				? METHOD_TEST_AND_GET_AS_BOOLEAN.invoke(null, Boolean.TRUE, booleanSupplier)
				: null, ih != null ? ih.getAsBoolean : null);
		//
		if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
			//
			ih.getAsBoolean = Boolean.TRUE;
			//
		} // if
			//
		Assert.assertEquals(METHOD_TEST_AND_GET_AS_BOOLEAN != null
				? METHOD_TEST_AND_GET_AS_BOOLEAN.invoke(null, Boolean.TRUE, booleanSupplier)
				: null, ih != null ? ih.getAsBoolean : null);
		//
	}

	@Test
	public void testIsFile() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(METHOD_IS_FILE != null ? METHOD_IS_FILE.invoke(null, new File(".")) : null, Boolean.FALSE);
		//
	}

	@Test
	public void testTestAndRun() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(METHOD_TEST_AND_RUN != null ? METHOD_TEST_AND_RUN.invoke(null, Boolean.TRUE, null) : null);
		//
		Assert.assertNull(
				METHOD_TEST_AND_RUN != null
						? METHOD_TEST_AND_RUN.invoke(null, Boolean.TRUE,
								Reflection.newProxy(FailableRunnable.class, ObjectUtils.getIfNull(ih, IH::new)))
						: null);
		//
	}

	@Test
	public void testAnd() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(METHOD_AND != null ? METHOD_AND.invoke(null, Boolean.FALSE, Boolean.TRUE, null) : null,
				Boolean.FALSE);
		//
		Assert.assertEquals(METHOD_AND != null ? METHOD_AND.invoke(null, Boolean.TRUE, Boolean.FALSE, null) : null,
				Boolean.FALSE);
		//
		Assert.assertEquals(METHOD_AND != null ? METHOD_AND.invoke(null, Boolean.TRUE, Boolean.TRUE, null) : null,
				Boolean.TRUE);
		//
		Assert.assertEquals(
				METHOD_AND != null ? METHOD_AND.invoke(null, Boolean.TRUE, Boolean.TRUE, new boolean[] { true }) : null,
				Boolean.TRUE);
		//
		Assert.assertEquals(
				METHOD_AND != null ? METHOD_AND.invoke(null, Boolean.TRUE, Boolean.TRUE, new boolean[] { false })
						: null,
				Boolean.FALSE);
		//
	}

	@Test
	public void testDependency() throws Throwable {
		//
		final Class<?> clz = Class.forName("org.apache.commons.lang3.UpdateVersionJPanel$Dependency");
		//
		final Method[] ms = clz != null ? clz.getDeclaredMethods() : null;
		//
		Method m = null;
		//
		Object[] os = null;
		//
		Object dependency = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Collection<Object> collection = null;
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
			os = toArray(Collections.nCopies(m.getParameterCount(), null));
			//
			Assert.assertNull(Modifier.isStatic(m.getModifiers()) ? Narcissus.invokeStaticMethod(m, os)
					: Narcissus.invokeMethod(
							dependency = ObjectUtils.getIfNull(dependency, () -> Narcissus.allocateInstance(clz)), m,
							os),
					Objects.toString(m));
			//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				add(collection, Narcissus.allocateInstance(ArrayUtils.get(parameterTypes, j)));
				//
			} // for
				//
			os = toArray(collection);
			//
			Assert.assertNull(Modifier.isStatic(m.getModifiers()) ? Narcissus.invokeStaticMethod(m, os)
					: Narcissus.invokeMethod(
							dependency = ObjectUtils.getIfNull(dependency, () -> Narcissus.allocateInstance(clz)), m,
							os),
					Objects.toString(m));
			//
		} // for
			//
	}

	@Test
	public void testTestAndAccept() throws IllegalAccessException, InvocationTargetException {
		//
		final Predicate<?> alwaysTrue = Predicates.alwaysTrue();
		//
		Assert.assertNull(
				METHOD_TEST_AND_ACCEPT != null ? METHOD_TEST_AND_ACCEPT.invoke(null, alwaysTrue, null, null) : null);
		//
		Assert.assertNull(
				METHOD_TEST_AND_ACCEPT != null
						? METHOD_TEST_AND_ACCEPT.invoke(null, alwaysTrue, null,
								Reflection.newProxy(FailableConsumer.class, ObjectUtils.getIfNull(ih, IH::new)))
						: null);
		//
	}

	@Test
	public void testParse() throws Throwable {
		//
		final DocumentBuilder db = newDocumentBuilder(DocumentBuilderFactory.newDefaultInstance());
		//
		Assert.assertNull(METHOD_PARSE != null ? METHOD_PARSE.invoke(null, db, null) : null);
		//
		Assert.assertNotNull(
				METHOD_PARSE != null ? METHOD_PARSE.invoke(null, db, new ByteArrayInputStream("<a/>".getBytes()))
						: null);
		//
	}

	@Test
	public void testReplace() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(METHOD_REPLACE != null ? METHOD_REPLACE.invoke(null, ".", '.', '/') : null, "/");
		//
	}

}