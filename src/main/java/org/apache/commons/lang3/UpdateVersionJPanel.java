package org.apache.commons.lang3;

import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.function.FailableRunnable;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.github.toolfactory.narcissus.Narcissus;
import net.miginfocom.swing.MigLayout;

public class UpdateVersionJPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2170500482514889468L;

	private static final String GROUP_ID = "groupId";

	private static final String ARTIFACT_ID = "artifactId";

	private static final String VALUE = "value";

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Note {
		String value();
	}

	private static class Dependency {

		@Note("Group ID")
		private String groupId;

		@Note("Artifact ID")
		private String artifactId;

		private String version;

		private String getGroupId() {
			return groupId;
		}

		private static String getGroupId(final Dependency instance) {
			return instance != null ? instance.getGroupId() : null;
		}

		private String getArtifactId() {
			return artifactId;
		}

		private static String getArtifactId(final Dependency instance) {
			return instance != null ? instance.getArtifactId() : null;
		}

		private String getVersion() {
			return version;
		}

		private static String getVersion(final Dependency instance) {
			return instance != null ? instance.getVersion() : null;
		}

	}

	@Note("File")
	private JTextComponent tfFile = null;

	private JTextComponent tfVersion = null;

	@Note("File")
	private AbstractButton btnFile = null;

	private AbstractButton btnUpdate, btnCheckVersion = null;

	@Note("Group ID")
	private JComboBox<String> jcbGroupId = null;

	private JComboBox<String> jcbArtifactId = null;

	@Note("Group ID")
	private DefaultComboBoxModel<String> dcbmGroupId = null;

	private DefaultComboBoxModel<String> dcbmArtifactId = null;

	private JFrame jFrame = null;

	private transient Collection<Dependency> dependencies = null;

	@Note("Group ID")
	private String groupId = null;

	private String artifactId = null;

	private UpdateVersionJPanel() {
		//
		init();
		//
	}

	private void init() {
		//
		setLayout(new MigLayout());
		//
		final Field component = testAndApply(x -> IterableUtils.size(x) == 1, toList(
				filter(stream(FieldUtils.getAllFieldsList(getClass())), f -> Objects.equals(getName(f), "component"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (component != null && Narcissus.getField(this, component) == null) {
			//
			return;
			//
		} // if
			//
		add(new JLabel("Select"));
		//
		final String growx = "growx";
		//
		add(tfFile = new JTextField(), growx);
		//
		tfFile.setEditable(false);
		//
		final String wrap = "wrap";
		//
		add(btnFile = new JButton("File"), String.join(",", growx, wrap));
		//
		btnFile.addActionListener(this);
		//
		add(new JLabel("Group ID"));
		//
		add(jcbGroupId = new JComboBox<>(dcbmGroupId = new DefaultComboBoxModel<>()), String.join(",", growx, wrap));
		//
		jcbGroupId.addActionListener(this);
		//
		add(new JLabel("Artifact ID"));
		//
		add(jcbArtifactId = new JComboBox<>(dcbmArtifactId = new DefaultComboBoxModel<>()),
				String.join(",", growx, wrap));
		//
		jcbArtifactId.addActionListener(this);
		//
		add(new JLabel("Version"));
		//
		add(tfVersion = new JTextField(), growx);
		//
		add(btnCheckVersion = new JButton("Check"), wrap);
		//
		btnCheckVersion.addActionListener(this);
		//
		add(new JLabel());
		//
		add(btnUpdate = new JButton("Update"));
		//
		btnUpdate.addActionListener(this);
		//
	}

	private static <T> Stream<T> stream(final Collection<T> instance) {
		return instance != null ? instance.stream() : null;
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static <T, R, E extends Throwable> R testAndApply(final Predicate<T> predicate, final T value,
			final FailableFunction<T, R, E> functionTrue, final FailableFunction<T, R, E> functionFalse) throws E {
		return test(predicate, value) ? apply(functionTrue, value) : apply(functionFalse, value);
	}

	private static <T> boolean test(final Predicate<T> instance, final T value) {
		return instance != null && instance.test(value);
	}

	private static <T, R, E extends Throwable> R apply(final FailableFunction<T, R, E> instance, final T value)
			throws E {
		return instance != null ? instance.apply(value) : null;
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		//
		final Object source = getSource(evt);
		//
		if (Objects.equals(source, btnFile)) {
			//
			final JFileChooser jfc = new JFileChooser(".");
			//
			testAndRun(
					testAndGetAsBoolean(Boolean.logicalAnd(!GraphicsEnvironment.isHeadless(), !isTestMode()),
							() -> jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION),
					() -> setText(tfFile, testAndApply(UpdateVersionJPanel::isXml, jfc.getSelectedFile(),
							UpdateVersionJPanel::getAbsolutePath, null)));
			//
			try {
				//
				testAndAccept(x -> isFile(x), testAndApply(Objects::nonNull, getText(tfFile), File::new, null), x -> {
					//
					removeAllElements(dcbmGroupId);
					//
					forEach(sorted(
							distinct(map(stream(dependencies = getDependencies(x)), y -> Dependency.getGroupId(y)))),
							y -> addElement(dcbmGroupId, y));
					//
					pack(jFrame);
					//
				});
				//
			} catch (final Exception e) {
				//
				throw e instanceof RuntimeException re ? re : new RuntimeException(e);
				//
			} // try
				//
			forEach(IntStream.range(0, getSize(dcbmGroupId)), i -> {
				//
				if (Objects.equals(groupId, getElementAt(dcbmGroupId, i))) {
					//
					setSelectedItem(dcbmGroupId, groupId);
					//
					groupId = null;
					//
				} // if
					//
			});
			//
			return;
			//
		} else if (Objects.equals(source, jcbGroupId)) {
			//
			removeAllElements(dcbmArtifactId);
			//
			forEach(sorted(distinct(map(
					filter(stream(dependencies),
							x -> Objects.equals(Dependency.getGroupId(x), getSelectedItem(dcbmGroupId))),
					x -> Dependency.getArtifactId(x)))), x -> addElement(dcbmArtifactId, x));
			//
			forEach(IntStream.range(0, getSize(dcbmArtifactId)), i -> {
				//
				if (Objects.equals(artifactId, getElementAt(dcbmArtifactId, i))) {
					//
					setSelectedItem(dcbmArtifactId, artifactId);
					//
					artifactId = null;
					//
				} // if
					//
			});
			//
			return;
			//
		} else if (Objects.equals(source, jcbArtifactId)) {
			//
			setText(tfVersion, Dependency.getVersion(testAndApply(x -> IterableUtils.size(x) == 1, toList(filter(
					stream(dependencies),
					x -> Boolean.logicalAnd(Objects.equals(Dependency.getGroupId(x), getSelectedItem(dcbmGroupId)),
							Objects.equals(Dependency.getArtifactId(x), getSelectedItem(dcbmArtifactId))))),
					x -> IterableUtils.get(x, 0), null)));
			//
			return;
			//
		} else if (Objects.equals(source, btnCheckVersion)) {
			//
			final Dependency dependency = testAndApply(x -> IterableUtils.size(x) == 1, toList(filter(
					stream(dependencies),
					x -> Boolean.logicalAnd(Objects.equals(Dependency.getGroupId(x), getSelectedItem(dcbmGroupId)),
							Objects.equals(Dependency.getArtifactId(x), getSelectedItem(dcbmArtifactId))))),
					x -> IterableUtils.get(x, 0), null);
			//
			if (dependency != null) {
				//
				try (final InputStream is = new URL(
						String.format("https://repo1.maven.org/maven2/%1$s/%2$s/maven-metadata.xml",
								replace(dependency.groupId, '.', '/'), replace(dependency.artifactId, '.', '/')))
						.openStream()) {
					//
					setText(tfVersion,
							Objects.toString(evaluate(newXPath(XPathFactory.newDefaultInstance()),
									"/*/versioning/release/text()",
									parse(newDocumentBuilder(DocumentBuilderFactory.newDefaultInstance()), is),
									XPathConstants.STRING)));
					//
				} catch (final IOException | ParserConfigurationException | XPathExpressionException | SAXException e) {
					//
					throw new RuntimeException(e);
					//
				} // try
					//
			} // if
				//
		} // if
			//
		actionPerformed(this, source);
		//
	}

	private static String replace(final String instance, final char oldChar, final char newChar) {
		//
		if (instance == null) {
			//
			return instance;
			//
		} // if
			//
		final Field value = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), VALUE))),
				x -> IterableUtils.get(x, 0), null);
		//
		return value == null || Narcissus.getField(instance, value) != null ? instance.replace(oldChar, newChar)
				: instance;
		//
	}

	private static void forEach(final IntStream instance, final IntConsumer action) {
		if (instance != null) {
			instance.forEach(action);
		}
	}

	private static void setSelectedItem(final ComboBoxModel<?> instance, final Object item) {
		if (instance != null) {
			instance.setSelectedItem(item);
		}
	}

	private static <E> E getElementAt(final ListModel<E> instance, final int index) {
		return instance != null ? instance.getElementAt(index) : null;
	}

	private static int getSize(final ListModel<?> instance) {
		return instance != null ? instance.getSize() : 0;
	}

	private static void actionPerformed(final UpdateVersionJPanel instance, final Object source) {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		if (Objects.equals(source, instance.btnUpdate)) {
			//
			final File file = testAndApply(Objects::nonNull, getText(instance.tfFile), File::new, null);
			//
			if (!isFile(file)) {
				//
				testAndRun(Boolean.logicalAnd(!GraphicsEnvironment.isHeadless(), !isTestMode()),
						() -> JOptionPane.showMessageDialog(null, "Please select a file"));
				//
				return;
				//
			} // if
				//
			try {
				//
				Collection<Dependency> ds = getDependencies(file);
				//
				if (IterableUtils.isEmpty(ds = toList(filter(stream(ds), x -> Boolean.logicalAnd(
						Objects.equals(Dependency.getGroupId(x), getSelectedItem(instance.dcbmGroupId)),
						Objects.equals(Dependency.getArtifactId(x), getSelectedItem(instance.dcbmArtifactId))))))) {
					//
					testAndRun(Boolean.logicalAnd(!GraphicsEnvironment.isHeadless(), !isTestMode()),
							() -> JOptionPane.showMessageDialog(null, "No dependency found"));
					//
				} else if (IterableUtils.size(ds) > 1) {
					//
					JOptionPane.showMessageDialog(null, "More than one dependency definition found");
					//
				} else {
					//
					final Path path = toPath(file);
					//
					final String string = Files.readString(path);
					//
					final int index1 = indexOf(string,
							String.format("<%1$s>%2$s</%1$s>", GROUP_ID, getSelectedItem(instance.dcbmGroupId)));
					//
					final int index2 = indexOf(string,
							String.format("<%1$s>%2$s</%1$s>", ARTIFACT_ID, getSelectedItem(instance.dcbmArtifactId)));
					//
					final int index3 = indexOf(string, "<version>", Math.max(index1, index2)) + 9;
					//
					final int index4 = indexOf(string, "</version>", Math.max(index1, index2));
					//
					final String versionOld = StringUtils.substring(string, index3, index4);
					//
					final String versionNew = getText(instance.tfVersion);
					//
					testAndRun(testAndGetAsBoolean(
							and(!Objects.equals(versionOld, versionNew), !GraphicsEnvironment.isHeadless(),
									!isTestMode()),
							() -> JOptionPane.showConfirmDialog(null,
									String.format("Update version number from \"%1$s\" to \"%2$s\"?", versionOld,
											versionNew)) == JOptionPane.YES_OPTION),
							() -> {
								//
								final StringBuilder sb = new StringBuilder(ObjectUtils.getIfNull(string, ""));
								//
								sb.delete(index3, index4);
								//
								Files.writeString(path, sb.insert(index3, versionNew));
								//
							});
					//
				} // if
					//
			} catch (final ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
				//
				throw new RuntimeException(e);
				//
			} // try
				//
		} // if
			//
	}

	private static <T, E extends Throwable> void testAndAccept(final Predicate<T> predicate, final T value,
			final FailableConsumer<T, E> consumer) throws E {
		if (test(predicate, value) && consumer != null) {
			consumer.accept(value);
		}
	}

	private static void pack(final Window instance) {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final Field objectLock = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), "objectLock"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (objectLock == null || Narcissus.getField(instance, objectLock) != null) {
			//
			instance.pack();
			//
		} // if
			//
	}

	private static void removeAllElements(final DefaultComboBoxModel<?> instance) {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final Field objects = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), "objects"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (objects == null || Narcissus.getField(instance, objects) != null) {
			//
			instance.removeAllElements();
			//
		} // if
			//
	}

	private static <E> void addElement(final MutableComboBoxModel<E> instance, final E item) {
		if (instance != null) {
			instance.addElement(item);
		}
	}

	private static Object getSelectedItem(final ComboBoxModel<?> instance) {
		return instance != null ? instance.getSelectedItem() : null;
	}

	private static <T> void forEach(final Stream<T> instance, final Consumer<? super T> action) {
		if (instance != null) {
			instance.forEach(action);
		}
	}

	private static <T> Stream<T> sorted(final Stream<T> instance) {
		return instance != null ? instance.sorted() : instance;
	}

	private static <T> Stream<T> distinct(final Stream<T> instance) {
		return instance != null ? instance.distinct() : instance;
	}

	private static <T, R> Stream<R> map(final Stream<T> instance, final Function<? super T, ? extends R> mapper) {
		return instance != null ? instance.map(mapper) : null;
	}

	private static Collection<Dependency> getDependencies(final File file)
			throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		//
		final XPath xp = newXPath(XPathFactory.newDefaultInstance());
		//
		final NodeList nodeList = cast(NodeList.class, evaluate(xp,
				"/*[local-name()=\"project\"]/*[local-name()=\"dependencies\"]/*[local-name()=\"dependency\"]",
				parse(newDocumentBuilder(DocumentBuilderFactory.newDefaultInstance()), file), XPathConstants.NODESET));
		//
		Dependency dependency = null;
		//
		Node node = null;
		//
		Collection<Dependency> dependencies = null;
		//
		for (int i = 0; i < getLength(nodeList); i++) {
			//
			(dependency = new Dependency()).groupId = Objects.toString(
					evaluate(xp, "*[local-name()=\"groupId\"]", node = item(nodeList, i), XPathConstants.STRING));
			//
			dependency.artifactId = Objects
					.toString(evaluate(xp, "*[local-name()=\"artifactId\"]", node, XPathConstants.STRING));
			//
			dependency.version = Objects
					.toString(evaluate(xp, "*[local-name()=\"version\"]", node, XPathConstants.STRING));
			//
			add(dependencies = ObjectUtils.getIfNull(dependencies, ArrayList::new), dependency);
			//
		} // for
			//
		return dependencies;
		//
	}

	private static boolean and(final boolean a, final boolean b, final boolean... bs) {
		//
		boolean result = a && b;
		//
		if (!result) {
			//
			return result;
			//
		} // if
			//
		for (int i = 0; bs != null && i < bs.length; i++) {
			//
			if (!(result &= bs[i])) {
				//
				return result;
				//
			} // if
				//
		} // for
			//
		return result;
		//
	}

	private static Node item(final NodeList instance, final int index) {
		return instance != null ? instance.item(index) : null;
	}

	private static int getLength(final NodeList instance) {
		return instance != null ? instance.getLength() : 0;
	}

	private static <E extends Throwable> void testAndRun(final boolean condition, final FailableRunnable<E> runnable)
			throws E {
		//
		if (condition && runnable != null) {
			//
			runnable.run();
			//
		} // if
			//
	}

	private static Document parse(final DocumentBuilder instance, final File file) throws SAXException, IOException {
		return instance != null && file != null && file.getPath() != null ? instance.parse(file) : null;
	}

	private static Document parse(final DocumentBuilder instance, final InputStream is)
			throws SAXException, IOException {
		//
		if (instance == null || is == null) {
			//
			return null;
			//
		} // if
			//
		if (is.markSupported()) {
			//
			final byte[] bs = is.readAllBytes();
			//
			if (bs == null || bs.length == 0) {
				//
				return null;
				//
			} // if
				//
			is.reset();
			//
		} // if
			//
		return instance.parse(is);
		//
	}

	private static boolean isFile(final File instance) {
		return instance != null && instance.getPath() != null && instance.isFile();
	}

	private static boolean testAndGetAsBoolean(final boolean condition, final BooleanSupplier booleanSupplier) {
		//
		return condition && booleanSupplier != null && booleanSupplier.getAsBoolean();
		//
	}

	private static Object getSource(final EventObject instance) {
		return instance != null ? instance.getSource() : null;
	}

	private static int indexOf(final String a, final String b, final int fromIndex) {
		return a != null ? a.indexOf(b, fromIndex) : -1;
	}

	private static int indexOf(final String a, final String b) {
		//
		if (a == null) {
			//
			return -1;
			//
		} // if
			//
		final Field value = testAndApply(x -> IterableUtils.size(x) == 1, toList(
				filter(stream(FieldUtils.getAllFieldsList(getClass(a))), f -> Objects.equals(getName(f), VALUE))),
				x -> IterableUtils.get(x, 0), null);
		//
		return value == null || Narcissus.getField(a, value) != null ? a.indexOf(b) : -1;
		//
	}

	private static Path toPath(final File instance) {
		return instance != null && instance.getPath() != null ? instance.toPath() : null;
	}

	private static Object evaluate(final XPath instance, final String expression, final Object item,
			final QName returnType) throws XPathExpressionException {
		return instance != null && item != null ? instance.evaluate(expression, item, returnType) : null;
	}

	private static XPath newXPath(final XPathFactory instance) {
		return instance != null ? instance.newXPath() : null;
	}

	private static String getText(final JTextComponent instance) {
		return instance != null ? instance.getText() : null;
	}

	private static void setText(final JTextComponent instance, final String text) {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final Field value = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(
						stream(testAndApply(Objects::nonNull, getClass(text), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), VALUE))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (value == null || Narcissus.getField(text, value) != null) {
			//
			instance.setText(text);
			//
		} // if
			//
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

	private static <E> void add(final Collection<E> instance, final E item) {
		if (instance != null) {
			instance.add(item);
		}
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

	private static boolean isXml(final File file) {
		//
		try {
			//
			return parse(newDocumentBuilder(DocumentBuilderFactory.newDefaultInstance()), file) != null;
			//
		} catch (final ParserConfigurationException | SAXException | IOException e) {
			//
			return false;
			//
		} // try
			//
	}

	private static DocumentBuilder newDocumentBuilder(final DocumentBuilderFactory instance)
			throws ParserConfigurationException {
		return instance != null ? instance.newDocumentBuilder() : null;
	}

	private static String getAbsolutePath(final File instance) {
		return instance != null && instance.getPath() != null ? instance.getAbsolutePath() : null;
	}

	public static void main(final String[] args) throws Exception {
		//
		final UpdateVersionJPanel instance = new UpdateVersionJPanel();
		//
		final Map<String, String> map = toMap(args);
		//
		if (containsKey(map, "file")) {
			//
			final File file = new File(get(map, "file"));
			//
			if (isXml(file)) {
				//
				setText(instance.tfFile, getAbsolutePath(file));
				//
			} // if
				//
		} // if
			//
		if (containsKey(map, GROUP_ID)) {
			//
			instance.groupId = get(map, GROUP_ID);
			//
		} // if
			//
		if (containsKey(map, ARTIFACT_ID)) {
			//
			instance.artifactId = get(map, ARTIFACT_ID);
			//
		} // if
			//
		instance.jFrame = !GraphicsEnvironment.isHeadless() ? new JFrame() : null;
		//
		if (instance.jFrame != null) {
			//
			instance.jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			//
			instance.jFrame.add(instance);
			//
			pack(instance.jFrame);
			//
			if (!isTestMode()) {
				//
				instance.jFrame.setVisible(true);
				//
			} // if
				//
		} // if
			//
	}

	private static boolean isTestMode() {
		try {
			return Class.forName("org.testng.annotations.Test") != null;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	private static boolean containsKey(final Map<?, ?> instance, final Object key) {
		return instance != null && instance.containsKey(key);
	}

	private static <T> List<T> toList(final Stream<T> instance) {
		return instance != null ? instance.toList() : null;
	}

	private static <T> Stream<T> filter(final Stream<T> instance, final Predicate<? super T> predicate) {
		return instance != null ? instance.filter(predicate) : instance;
	}

	private static <V> V get(final Map<?, V> instance, final Object key) {
		return instance != null ? instance.get(key) : null;
	}

	private static Map<String, String> toMap(final String... ss) {
		//
		String s = null;
		//
		Map<String, String> map = null;
		//
		for (int i = 0; i < length(ss); i++) {
			//
			if (Objects.equals(s = ArrayUtils.get(ss, i), "=")) {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), "", "");
				//
			} else if (s != null && s.length() == 2 && s.charAt(0) == '=') {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), "", s.substring(1, s.length()));
				//
			} else if (s != null && s.length() == 2 && s.charAt(s.length() - 1) == '=') {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), s.substring(0, s.length() - 1), "");
				//
			} else if (s != null && s.indexOf('=') >= 0 && s.indexOf('=') == s.lastIndexOf('=')) {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), StringUtils.substringBefore(s, '='),
						StringUtils.substringAfter(s, '='));
				//
			} else if (s != null && s.length() > 2 && s.indexOf('=') != s.lastIndexOf('=')) {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), StringUtils.substring(s, 0, s.indexOf('=')),
						StringUtils.substring(s, s.indexOf('=') + 1));
				//
			} // if
				//
		} // for
			//
		return map;
		//
	}

	private static <K, V> void put(final Map<K, V> instance, final K key, final V value) {
		if (instance != null) {
			instance.put(key, value);
		}
	}

	private static int length(final Object[] instance) {
		return instance != null ? instance.length : 0;
	}

}