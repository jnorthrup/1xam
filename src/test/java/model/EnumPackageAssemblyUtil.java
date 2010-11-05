package model;


import java.io.*;

import static java.lang.Package.*;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.UUID;
import java.util.Map.*;
import java.util.jar.*;
import java.util.logging.*;

public class EnumPackageAssemblyUtil {
    private static final String EOL = "\n";
    private static final Map<CharSequence, String> INTRINSICS = new TreeMap<CharSequence, String>();
    private static final String[] ISAREFS = new String[]{"Record", "Value", "Header", "Ref", "Info"};
    private static final String ISA_MODS = Modifier.toString(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC);
    static final Map<Class<?>, Pair<String, Pair<String, String>>> bBufWrap = new LinkedHashMap<Class<?>, Pair<String, Pair<String, String>>>();

    static {
        bBufWrap.put(char.class, new Pair<String, Pair<String, String>>("Char", new Pair<String, String>("char", "")));
        bBufWrap.put(int.class, new Pair<String, Pair<String, String>>("Int", new Pair<String, String>("int", "")));
        bBufWrap.put(long.class, new Pair<String, Pair<String, String>>("Long", new Pair<String, String>("long", "")));
        bBufWrap.put(short.class, new Pair<String, Pair<String, String>>("Short", new Pair<String, String>("short", " & 0xffff")));
        bBufWrap.put(double.class, new Pair<String, Pair<String, String>>("Double", new Pair<String, String>("double", "")));
        bBufWrap.put(float.class, new Pair<String, Pair<String, String>>("Float", new Pair<String, String>("float", "")));
        bBufWrap.put(byte[].class, new Pair<String, Pair<String, String>>("", new Pair<String, String>("byte", " & 0xff")));
        bBufWrap.put(byte.class, new Pair<String, Pair<String, String>>("", new Pair<String, String>("byte", " & 0xff")));
        INTRINSICS.put("___recordlen___",
                "/**\n" +
                        "     * the length of one record\n" +
                        "     */\n\t" +
                        Modifier.toString(Modifier.STATIC | Modifier.PUBLIC) + " int ___recordlen___;");
        INTRINSICS.put("___size___",
                "/**\n" +
                        "     * the size per field, if any\n" +
                        "     */\n\t" +
                        Modifier.toString(Modifier.FINAL | Modifier.PUBLIC) + " int ___size___;");
        INTRINSICS.put("___seek___",
                "/**\n" +
                        "     * the offset from record-start of the field\n" +
                        "     */\n\t" +
                        Modifier.toString(Modifier.FINAL | Modifier.PUBLIC) + " int ___seek___;");
        INTRINSICS.put("___subrecord___",
                "/**\n" +
                        "     * a delegate class which will perform sub-indexing on behalf of a field once it has marked its initial starting\n" +
                        "     * offset into the stack.\n" +
                        "     */\n" +
                        "\tpublic Class<? extends Enum> ___subrecord___;");
        INTRINSICS.put("___valueclass___",
                "/**\n" +
                        "     * a hint class for bean-wrapper access to data contained.\n" +
                        "     */\n" +
                        "\tpublic Class ___valueclass___;");
        INTRINSICS.put("___doc___",
                "/**\n" +
                        "     * a hint class for docs.\n" +
                        "     */\n" +
                        "\tpublic String ___doc___;");
        INTRINSICS.put("___src___",
                "/**\n" +
                        "     * a hint for src.\n" +
                        "     */\n" +
                        "\tpublic String ___src___;");
        for (String isaref : ISAREFS)
            INTRINSICS.put("___is" + isaref + "___", "");
    }

    public String getEnumsStructsForPackage(Class<?> tableRecordClass) throws Exception {
        return createEnumStructSourceFiles(tableRecordClass);
    }

    public static String createEnumStructSourceFiles(final Class tableRecordClass) throws Exception {

        Map<Class<? extends Enum>, Iterable<? extends Enum>> map = PackageAssembly.getEnumsStructsForPackage(tableRecordClass.getPackage());
        Set<Entry<Class<? extends Enum>, Iterable<? extends Enum>>> entries = map.entrySet();

        String display = "";
        String enumName = "";
        for (Entry<Class<? extends Enum>, Iterable<? extends Enum>> entry : entries)
            display += createEnumMiddle(tableRecordClass, entry);
        return display;
    }


    static String createEnumMiddle(Class<?> tableRecordClass, Entry<Class<? extends Enum>, Iterable<? extends Enum>> entry) throws IOException {

        String display = "";
        String enumName;
        Class<? extends Enum> enumClazz = entry.getKey();
        Iterable<? extends Enum> parentEnum = entry.getValue();
        enumName = enumClazz.getSimpleName();
        String fn = ("src/main/hybrid/" + tableRecordClass.getPackage().getName() + "/" + enumName).replace(".", "/") + ".java";
        System.err.println("attempting to open " + fn);
        final File file = new File(fn);
        file.getParentFile().mkdirs();
        file.createNewFile();
        OutputStreamWriter ostream = new FileWriter(file);
        System.err.println("*** Dumping " + file.getCanonicalPath() + "\t" + file.toURI().toASCIIString());

        display += "public enum " + enumName + " { " + EOL;


        display += renderConstantFields(enumClazz) + ";\n";
        String result = renderBaseEnumFields(enumClazz);

        final String trClass = tableRecordClass.getCanonicalName();
        display += result + "    /** " + enumName + " templated Byte Struct \n" +
                "     * @param dimensions [0]=___size___,[1]= forced ___seek___\n" +
                "     */\n";


        display += "\t" + enumName + " ";

        display += "(int... dimensions) {\n" +
                "        int[] dim = init(dimensions);\n" +
                "        ___size___ = dim[0];\n" +
                "        ___seek___ = dim[1];\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    int[] init(int... dimensions) {\n" +
                "        int size = dimensions.length > 0 ? dimensions[0] : 0;" +
                "\n" +
                "        if (___subrecord___ == null) {\n" +
                "            final String[] indexPrefixes = {\"\", \"s\", \"_\", \"Index\", \"Length\", \"Ref\", \"Header\", \"Info\", \"Table\"};\n" +
                "            for (String indexPrefix : indexPrefixes) {\n" +
                "                try {\n" +
                "                    ___subrecord___ = (Class<? extends Enum>) Class.forName(getClass().getPackage().getName() + '.' + name() + indexPrefix);\n" +
                "                    try {\n" +
                "                        size = ___subrecord___.getField(\"___recordlen___\").getInt(null);\n" +
                "                    } catch (Exception e) {\n" +
                "                    }\n" +
                "                    break;\n" +
                "                } catch (ClassNotFoundException e) {\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        for (String vPrefixe1 : new String[]{\"_\", \"\", \"$\", \"Value\",}) {\n" +
                "            if (___valueclass___ != null) break;\n" +
                "            String suffix = vPrefixe1;\n" +
                "            for (String name1 : new String[]{name().toLowerCase(), name(),}) {\n" +
                "                if (___valueclass___ != null) break;\n" +
                "                final String trailName = name1;\n" +
                "                if (trailName.endsWith(suffix)) {\n" +
                "                    for (String aPackage1 : new String[]{\"\",\n" +
                "                            getClass().getPackage().getName() + \".\",\n" +
                "                            \"java.lang.\",\n" +
                "                            \"java.util.\",\n" +
                "                    })\n" +
                "                        if (___valueclass___ == null) break;\n" +
                "                        else\n" +
                "                            try {\n" +
                "                                ___valueclass___ = Class.forName(aPackage1 + name().replace(suffix, \"\"));\n" +
                "                            } catch (ClassNotFoundException e) {\n" +
                "                            }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        int seek = dimensions.length > 1 ? dimensions[1] : ___recordlen___;\n" +
                "        ___recordlen___ = Math.max(___recordlen___,seek+size);" +
                "\n" +
                "        return new int[]{size, seek};\n" +
                "    }" +
                "\n" +
                "    /**\n" +
                "     * The struct's top level method for indexing 1 record. Each Enum field will call SubIndex\n" +
                "     *\n" +
                "     * @param src      the ByteBuffer of the input file\n" +
                "     * @param register array holding values pointing to Stack offsets\n" +
                "     * @param stack    A stack of 32-bit pointers only to src positions\n" +
                "     */\n" +
                "    static void index\n" +
                "            (ByteBuffer src, int[] register, IntBuffer stack) {\n" +
                "        for (" + enumName + " " + enumName + "_ : values()) {\n" +
                "            String hdr = " + enumName + "_.name();\n" +
                "            System.err.println(\"hdr:pos \" + hdr + ':' + stack.position());\n" +
                "            " + enumName + "_.subIndex(src, register, stack);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Each of the Enums can override thier deault behavior of \"___seek___-past\"\n" +
                "     *\n" +
                "     * @param src      the ByteBuffer of the input file\n" +
                "     * @param register array holding values pointing to Stack offsets\n" +
                "     * @param stack    A stack of 32-bit pointers only to src positions\n" +
                "     */\n" +
                "    private void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {\n" +
                "        System.err.println(name() + \":subIndex src:stack\" + src.position() + ':' + stack.position());\n" +
                "        int begin = src.position();\n" +
                "        int stackPtr = stack.position();\n" +
                "        stack.put(begin);\n" +
                "        if (___isRecord___ && ___subrecord___ != null) { \n" +
                "            /* " +
                "                try {\n" +
                "                final " + tableRecordClass.getCanonicalName() + " table = " + tableRecordClass.getCanonicalName() + ".valueOf(___subrecord___.getSimpleName());\n" +
                "                if (table != null) {\n" +
                "                    //stow the original location\n" +
                "                    int mark = stack.position();\n" +
                "                    stack.position((register[TopLevelRecord.TableRecord.ordinal()] + table.___seek___) / 4);\n" +
                "                    ___subrecord___.getMethod(\"index\", ByteBuffer.class, int[].class, IntBuffer.class).invoke(null);\n" +
                "                    //resume the lower stack activities\n" +
                "                    stack.position(mark);\n" +
                "                }\n" +
                "            } catch (Exception e) {\n" +
                "                throw new Error(e.getMessage());\n" +
                "            }\n*/" +
                "        }\n" +
                "    }";

        final String postScript = display += "}\n" +
                "//@@ #end" + enumName + "";

        try {

            String t = "";
            t += "package " + "" + enumClazz.getPackage().getName() + ";";
            t += "\n" + "import java.nio.*;";
            t += "\n" + "import java.lang.reflect.*;";


            String eclazz = genHeader(enumClazz);
            display = t + eclazz + display;
//        } catch (NoSuchFieldException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        ostream.write(display);
        ostream.close();
        display = "";
        return display;
    }

    private static String renderBaseEnumFields(Class<? extends Enum> enumClazz) {
        String result = "";

        try {

            final Field[] fields = enumClazz.getFields();

            String s1 = "";
            for (Field field : fields) {
                String z = field.toGenericString().replaceAll(enumClazz.getCanonicalName() + ".", "");
                if (field.getType() != enumClazz && !INTRINSICS.containsKey(field.getName()))
                    s1 += "\t" + z + ";" + EOL;
            }

            if (s1.length() > 4)
                result += s1 + EOL;

            for (String isaref : ISAREFS) {
                INTRINSICS.put("is" + isaref, ISA_MODS + " boolean " + "___is" + isaref + "___=" + enumClazz.getSimpleName().endsWith(isaref) + ';');
            }


            for (String field : INTRINSICS.values()) {

                result += "\t" + field + EOL;
            }

        } catch (SecurityException e) {
            e.printStackTrace();  //todo: verify for a purpose
        } catch (Exception e) {
        }
        return result;
    }

    static String renderConstantFields(Class<? extends Enum> enumClazz) {
        boolean first = true;

        String result = "";
        String pname = enumClazz.getPackage().getName();
        for (Enum instance : enumClazz.getEnumConstants()) {
            try {
                String symbol = instance.name();


                result += (first ? "" : ",") + symbol.replaceAll(pname + ".", "(");
                first = false;
                try {
                    final Field[] fields = enumClazz.getFields();
                    String tmpString = "";
                    try {
                        Field doc = enumClazz.getField("___doc___");
                        Object o1 = doc.get(enumClazz);
                        if (null != o1) {
                            tmpString += o1.toString();
                        }
                    } catch (Exception e) {
                    }
                    for (Field field : fields) {


                        String attrName = field.getName().replaceAll(enumClazz.getCanonicalName(), "");
                        if (attrName.equals("___size___")) {
                            final Integer integer = (Integer) field.get(instance);
                            if (integer != 0)
                                result = result + "(0x" + Integer.toHexString(integer) + ")";
                        } else {
                            if (field.getType() != enumClazz && (field.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == 0) {
                                final Object o = field.get(instance);
                                if (o != null && !o.equals(0))
                                    tmpString += "\n\t\t" + attrName + "=" + (field.getType() == Class.class
                                            ? ((Class) o).getCanonicalName() + ".class" :
                                            field.getType() == String.class
                                                    ? '"' + String.valueOf(o).trim() + '"' :
                                                    String.valueOf(o)) + ";";
                            }
                        }
                    }
                    if (tmpString.length() > 4)
                        result += "\t{{" + tmpString + "\n\t}}" + EOL;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String genHeader(Class<? extends Enum> docEnum) throws NoSuchFieldException {

        String display = "";
        final Enum[] enums = docEnum.getEnumConstants();

        int recordLen = 0;
        try {
            recordLen = (Integer) docEnum.getDeclaredField("___recordlen___").get(null);
        } catch (Exception e) {
            recordLen = 0;
        }
        display += "\n\n/**\n * <p>recordSize: " + recordLen + "\n * <table><tr> " +
                "<th>name</th>" +
                "<th>size</th>" +
                "<th>seek</th>" +
                "<th>description</th>" +
                "<th>Value Class</th>" +
                "<th>Sub-Index</th>" +
                "</tr>\n";

        String name = "";
        for (Enum theSlot : enums) {
            int size = 0, seek = 0;
            name = theSlot.name();
            Class subRecord = null;
            Class valClazz = null;

            final String[] strings = {"___subrecord___", "___valueclass___", "___size___", "___seek___", "___doc___", "___src___",};

            final Object[] objects = new Object[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String string = strings[i];
                try {
                    objects[i] = theSlot.getDeclaringClass().getDeclaredField(string).get(theSlot);
                } catch (Exception e) {
                }
            }

            int j = 0;
            subRecord = (Class) objects[j++];
            valClazz = (Class) objects[j++];
            try {
                size = (Integer) objects[j++];
            } catch (Exception e) {
                size = 4;
            }
            try {
                seek = (Integer) objects[j++];
            } catch (Exception e) {
                seek = 0;
            }

            String docString = "";
            try {
                docString = (String) objects[j++];
            } catch (Exception e) {
            }

            if (valClazz == null) {
                valClazz = guessIntTypes(size);
            }


            final Pair<String, Pair<String, String>> pair = bBufWrap.get(valClazz);
            display += " * <tr>" +
                    "<td>" + name + "</td>" +
                    "<td>0x" + Integer.toHexString(size) + "</td>" +
                    "<td>0x" + Integer.toHexString(seek) + "</td>" +
                    "<td>" + (docString == null ? "" : docString) + "</td>" +
                    "<td>" + ((valClazz == null) ? (" (" + pair.getSecond().getFirst() + ") " +
                    name + "=src.get" + pair.getFirst()
                    + "(0x" + Integer.toHexString(seek) + ")"
                    + pair.getSecond().getSecond()) : (valClazz.getCanonicalName())) + "</td>" +
                    "<td>{@link "
                    + (subRecord == null ? theSlot.getDeclaringClass().getSimpleName()
                    + "Visitor#" + name + "(ByteBuffer, int[], IntBuffer)" : subRecord.getCanonicalName()) + "}</td>" +
                    "</tr>\n";
        }
        display += " * \n";

        for (Enum theSlot : enums) {
            display += " * @see " + docEnum.getCanonicalName() + "#" + theSlot.name() + '\n';
        }
        display += " * </table>\n";

        display += " */\n";


        return display;
    }


    private Object[] getSubRecord(Enum enum_) {
        final String[] suffixes = {"", "s", "_", "Index", "Value", "Ref", "Header", "Info"};
        for (String indexPrefix : suffixes) {
            try {
                final String p = enum_.getDeclaringClass().getPackage().getName();
                final String name = p + '.' + enum_.name() + indexPrefix;
                final Class<?> aClass = Class.forName(name);
                final int anInt = aClass.getField("___recordlen___").getInt(null);
                if (aClass != null)
                    return new Object[]{aClass, anInt};

            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Class guessIntTypes(int size, Class... clazz) {
        Class layout_clazz;
        if (clazz.length == 0) {
            switch (size) {
                case 1:
                    layout_clazz = byte.class;
                    break;
                case 2:
                    layout_clazz = short.class;
                    break;
                case 4:
                    layout_clazz = int.class;
                    break;
                case 8:
                    layout_clazz = long.class;
                    break;
                default:
                    layout_clazz = byte[].class;
                    break;
            }
        } else {
            layout_clazz = clazz[0];
        }
        return layout_clazz;
    }

    public void testPackage(Package... p) throws Exception {
        String packageName = (p.length > 0 ? p[0] : getClass().getPackage()).getName();
        for (Class<? extends Enum> aClass : PackageAssembly.getEnumsForPackage(getPackage(packageName))) {

            Field[] fields = aClass.getFields();
            String[] fn = new String[fields.length];

            for (int i = 0; i < fn.length; i++)
                fn[i] = fields[i].toGenericString();

            System.err.println(aClass.getSimpleName() + Arrays.toString(fn).replaceAll(",", ",\n\t").replaceAll(packageName + ".", ""));
        }
    }


    /**
     * make a best-attempt at creating or opening an index file for later sizing
     *
     * @param indexName -
     * @return a file for index writings/reads
     * @throws FileNotFoundException
     */
    static File getIndexFile(String indexName) throws FileNotFoundException {
        for (int i = 0; i < 2; i++)
            try {
                File raf = new File(indexName, "rw");
                if (!raf.isFile()) {
                    raf.getParentFile().mkdirs();
                } else return raf;
            } catch (Exception e) {
                System.err.println("");
            }
        return null;
    }

    public static class PackageAssembly {

        public static Map<Class<? extends Enum>, Iterable<? extends Enum>> getEnumsStructsForPackage(final Package package_) throws Exception {
            Map<Class<? extends Enum>, Iterable<? extends Enum>> map = new HashMap<Class<? extends Enum>, Iterable<? extends Enum>>();
            for (Class<? extends Enum> aClass : getClassessOfParent(package_, Enum.class)) {
                Enum[] constants = aClass.getEnumConstants();
                map.put(aClass, Arrays.asList(constants));
            }
            return map;
        }

        public static List<Class<Enum>> getEnumsForPackage(Package package_)
                throws ClassNotFoundException {
            // This will hold a list of directories matching the pckgname.
            //There may be more than one if a package is split over multiple jars/paths
            List<Class<Enum>> classes = new ArrayList<Class<Enum>>();
            ArrayList<File> directories = new ArrayList<File>();
            String pckgname = package_.getName();
            try {

                ClassLoader cld = Thread.currentThread().getContextClassLoader();
                if (cld == null) {
                    throw new ClassNotFoundException("Can't get class loader.");
                }
                // Ask for all resources for the path
                final String resName = pckgname.replace('.', '/');
                Enumeration<URL> resources = cld.getResources(resName);
                while (resources.hasMoreElements()) {
                    URL res = resources.nextElement();
                    if (res.getProtocol().equalsIgnoreCase("jar") || res.getProtocol().equalsIgnoreCase("zip")) {
                        JarURLConnection conn = (JarURLConnection) res.openConnection();
                        JarFile jar = conn.getJarFile();

                        for (JarEntry e : Collections.list(jar.entries())) {
                            if (e.getName().startsWith(resName) && e.getName().endsWith(".class") && !e.getName().contains("$")) {
                                String className = e.getName().replace("/", ".").substring(0, e.getName().length() - 6);
                                System.out.println(className);
                                classes.add((Class<Enum>) Class.forName(className));
                            }
                        }
                    } else
                        directories.add(new File(URLDecoder.decode(res.getPath(), "UTF-8")));
                }
            } catch (NullPointerException x) {
                throw new ClassNotFoundException(pckgname + " does not appear to be " +
                        "a valid package (Null pointer exception)");
            } catch (UnsupportedEncodingException encex) {
                throw new ClassNotFoundException(pckgname + " does not appear to be " +
                        "a valid package (Unsupported encoding)");
            } catch (IOException ioex) {
                throw new ClassNotFoundException("IOException was thrown when trying " +
                        "to get all resources for " + pckgname);
            }

            // For every directory identified capture all the .class files
            for (File directory : directories) {
                if (directory.exists()) {
                    // Get the list of the files contained in the package
                    String[] files = directory.list();
                    for (String file : files) {
                        // we are only interested in .class files
                        if (file.endsWith(".class")) {
                            // removes the .class extension
                            classes.add(
                                    (Class<Enum>) Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
                        }
                    }
                } else {
                    throw new ClassNotFoundException(pckgname + " (" + directory.getPath() +
                            ") does not appear to be a valid package");
                }
            }
            return classes;
        }


        public List<Class<Enum>> getClassessOfInterface(Package thePackage, Class theInterface) {
            List<Class<Enum>> classList = new ArrayList<Class<Enum>>();
            try {
                for (Class<Enum> discovered : getEnumsForPackage(thePackage)) {
                    if (Arrays.asList(discovered.getInterfaces()).contains(theInterface)) {
                        classList.add(discovered);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, null, ex);
            }

            return classList;
        }

        public static List<Class<? extends Enum>> getClassessOfParent(Package thePackage, Class<? extends Enum> theParent) {
            List<Class<? extends Enum>> classList = new ArrayList<Class<? extends Enum>>();

            try {
                for (Class discovered : getEnumsForPackage(thePackage)) {

                    do {
                        Class parent = discovered.getSuperclass();
                        if (parent != theParent) {
                            discovered = parent;
                        } else {
                            classList.add(discovered);
                            break;
                        }
                    } while (discovered != null);
                }
            } catch (Exception ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);
            }
            return classList;
        }


    }

    /**
     * A Hackish tuple class.  Acts as Map Entry as well as attempting some level of jit trickery by state as obj[] as
     * external from-stack.
     */
    public static class Pair<K, V> implements Entry<K, V> {
        public Object[] kv;

        public Pair(K k, V v) {
            kv = new Object[]{k, v};
        }

        public Pair(Object... two) {
            kv = two;
        }

        public K getFirst() {
            return (K) kv[0];
        }

        public V getSecond() {
            return (V) kv[1];
        }

        /**
         * Returns the key corresponding to this entry.
         *
         * @return the key corresponding to this entry
         * @throws IllegalStateException implementations may, but are not required to, throw this exception if the entry has
         *                               been removed from the backing map.
         */
        public K getKey() {
            return (K) kv[0];
        }

        /**
         * Returns the value corresponding to this entry.  If the mapping has been removed from the backing map (by the
         * iterator's <tt>remove</tt> operation), the results of this call are undefined.
         *
         * @return the value corresponding to this entry
         * @throws IllegalStateException implementations may, but are not required to, throw this exception if the entry has
         *                               been removed from the backing map.
         */
        public V getValue() {
            return (V) kv[1];//To change body of implemented methods use File | Settings | File Templates.
        }


        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }

        public int hashCode() {
            return getKey().hashCode() << 16 + getValue().hashCode() & 0xffff;

        }

    }

    public static void main(String... args) throws Exception {
//        final String dirName = args.length > 0 ? "target/classes" : args[0];
//
//
////        final String indexName = (String)   ((args.length < 1) ?
////                new File(createTempFile("__BC__" + currentTimeMillis(), "rw"), "model").getAbsolutePath() : args[1]);
////
////        File index = getIndexFile(indexName);

        new EnumPackageAssemblyUtil().getEnumsStructsForPackage(model.
                UUID.class);
    }
}


/*
package model;



public enum UUID { data ; public final int ___size___=20 ;}
enum Id{UUID}
enum Utf8$16{bytes; public final int ___size___=16; public String ___doc___="remove trailing 0's";}
enum Utf8$64{bytes; public final int ___size___=64 ;public String ___doc___="remove trailing 0's";}
enum Utf8$128{bytes; public final int ___size___=128; public String ___doc___="remove trailing 0's";}
enum Utf8$256{bytes; public final int ___size___=256 ;public String ___doc___="remove trailing 0's";}
enum Utf8$1024{bytes; public final int ___size___=1024; public String ___doc___="remove trailing 0's";}
enum Utf8$8192{bytes; public final int ___size___=8192;public String ___doc___="remove trailing 0's";}
enum HostName{Utf8$128}
enum Name{Utf8$128}
enum Email{Utf8$128}
enum Password{Utf8$16}
enum Account{Id,Name,Email,Password}
*/
