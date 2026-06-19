package org.apache.commons.lang3;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.github.toolfactory.narcissus.Narcissus;
import net.miginfocom.swing.MigLayout;

public class UpdateVersionJPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2170500482514889468L;

	private static class Dependency {

		private String groupId;

		private String artifactId;

	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Note {
		String value();
	}

	@Note("File")
	private JTextComponent tfFile = null;

	private JTextComponent tfGroupId, tfArtifactId, tfVersion = null;

	@Note("File")
	private AbstractButton btnFile = null;

	private AbstractButton btnUpdate = null;

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
		add(new JLabel("File"));
		//
		add(tfFile = new JTextField(), "growx");
		//
		tfFile.setEditable(false);
		//
		add(btnFile = new JButton("File"), "wrap");
		//
		btnFile.addActionListener(this);
		//
		add(new JLabel("Group ID"));
		//
		add(tfGroupId = new JTextField(), "growx,wrap");
		//
		add(new JLabel("Artifact ID"));
		//
		add(tfArtifactId = new JTextField(), "growx,wrap");
		//
		add(new JLabel("Version"));
		//
		add(tfVersion = new JTextField(), "growx,wrap");
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
			if (!GraphicsEnvironment.isHeadless() && !isTestMode()
					&& jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				//
				final File selectedFile = jfc.getSelectedFile();
				//
				setText(tfFile, isXml(selectedFile) ? getAbsolutePath(selectedFile) : null);
				//
			} // if
				//
		} else if (Objects.equals(source, btnUpdate)) {
			//
			final File file = testAndApply(Objects::nonNull, getText(tfFile), File::new, null);
			//
			if (file == null || !file.isFile()) {
				//
				if (!GraphicsEnvironment.isHeadless() && !isTestMode()) {
					//
					JOptionPane.showMessageDialog(null, "Please select a file");
					//
				} // if
					//
				return;
				//
			} // if
				//
			try {
				//
				final DocumentBuilder db = newDocumentBuilder(DocumentBuilderFactory.newDefaultInstance());
				//
				final XPath xp = newXPath(XPathFactory.newDefaultInstance());
				//
				final NodeList nodeList = cast(NodeList.class, evaluate(xp,
						"/*[local-name()=\"project\"]/*[local-name()=\"dependencies\"]/*[local-name()=\"dependency\"]",
						db != null ? db.parse(file) : null, XPathConstants.NODESET));
				//
				Dependency dependency = null;
				//
				Node node = null;
				//
				Collection<Dependency> dependencies = null;
				//
				for (int i = 0; nodeList != null && i < nodeList.getLength(); i++) {
					//
					(dependency = new Dependency()).groupId = Objects.toString(evaluate(xp,
							"*[local-name()=\"groupId\"]", node = nodeList.item(i), XPathConstants.STRING));
					//
					dependency.artifactId = Objects
							.toString(evaluate(xp, "*[local-name()=\"artifactId\"]", node, XPathConstants.STRING));
					//
					add(dependencies = ObjectUtils.getIfNull(dependencies, ArrayList::new), dependency);
					//
				} // for
					//
				if (IterableUtils.isEmpty(dependencies = toList(
						filter(stream(dependencies), x -> x != null && Objects.equals(x.groupId, getText(tfGroupId))
								&& Objects.equals(x.artifactId, getText(tfArtifactId)))))) {
					//
					if (!GraphicsEnvironment.isHeadless() && !isTestMode()) {
						//
						JOptionPane.showMessageDialog(null, "No dependency found");
						//
					} // if
						//
				} else if (IterableUtils.size(dependencies) > 1) {
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
							String.format("<%1$s>%2$s</%1$s>", "groupId", getText(tfGroupId)));
					//
					final int index2 = indexOf(string,
							String.format("<%1$s>%2$s</%1$s>", "artifactId", getText(tfArtifactId)));
					//
					final int index3 = indexOf(string, "<version>", Math.max(index1, index2)) + 9;
					//
					final int index4 = indexOf(string, "</version>", Math.max(index1, index2));
					//
					final String versionOld = StringUtils.substring(string, index3, index4);
					//
					final String versionNew = getText(tfVersion);
					//
					if (!Objects.equals(versionOld, versionNew) && !GraphicsEnvironment.isHeadless() && !isTestMode()
							&& JOptionPane.showConfirmDialog(null,
									String.format("Update version number from \"%1$s\" to \"%2$s\"?", versionOld,
											versionNew)) == JOptionPane.YES_OPTION) {
						//
						final StringBuilder sb = new StringBuilder(ObjectUtils.getIfNull(string, ""));
						//
						sb.delete(index3, index4);
						//
						Files.writeString(path, sb.insert(index3, versionNew));
						//
					} // if
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
				filter(stream(FieldUtils.getAllFieldsList(getClass(a))), f -> Objects.equals(getName(f), "value"))),
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
		return instance != null ? instance.evaluate(expression, item, returnType) : null;
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
		final Field value = testAndApply(x -> IterableUtils.size(x) == 1, toList(
				filter(stream(FieldUtils.getAllFieldsList(getClass(text))), f -> Objects.equals(getName(f), "value"))),
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
			final DocumentBuilder db = newDocumentBuilder(DocumentBuilderFactory.newDefaultInstance());
			//
			return db != null && file != null && file.getPath() != null && db.parse(file) != null;
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
		if (containsKey(map, "groupId")) {
			//
			setText(instance.tfGroupId, get(map, "groupId"));
			//
		} // if
			//
		if (containsKey(map, "artifactId")) {
			//
			setText(instance.tfArtifactId, get(map, "artifactId"));
			//
		} // if
			//
		if (containsKey(map, "version")) {
			//
			setText(instance.tfVersion, get(map, "version"));
			//
		} // if
			//
		final JFrame jFrame = !GraphicsEnvironment.isHeadless() ? new JFrame() : null;
		//
		if (jFrame != null) {
			//
			jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			//
			jFrame.add(instance);
			//
			jFrame.pack();
			//
			if (!isTestMode()) {
				//
				jFrame.setVisible(true);
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