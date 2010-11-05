package model;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 257
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>description</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>type</td><td>0x1</td><td>0x0</td><td></td><td>byte</td><td>{@link UnionVisitor#type(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>Number</td><td>0xa</td><td>0x1</td><td></td><td>byte[]</td><td>{@link UnionVisitor#Number(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>PascalString</td><td>0x100</td><td>0x1</td><td></td><td>byte[]</td><td>{@link model.PascalString}</td></tr>
 * 
 * @see model.Union#type
 * @see model.Union#Number
 * @see model.Union#PascalString
 * </table>
 */
public enum Union { 
type(0x1),Number(0xa),PascalString(0x100)	{{
		___subrecord___=model.PascalString.class;
	}}
;
	/**
     * a hint class for docs.
     */
	public String ___doc___;
	
	
	
	
	
	/**
     * the length of one record
     */
	public static int ___recordlen___;
	/**
     * the offset from record-start of the field
     */
	public final int ___seek___;
	/**
     * the size per field, if any
     */
	public final int ___size___;
	/**
     * a hint for src.
     */
	public String ___src___;
	/**
     * a delegate class which will perform sub-indexing on behalf of a field once it has marked its initial starting
     * offset into the stack.
     */
	public Class<? extends Enum> ___subrecord___;
	/**
     * a hint class for bean-wrapper access to data contained.
     */
	public Class ___valueclass___;
	public static final boolean ___isHeader___=false;
	public static final boolean ___isInfo___=false;
	public static final boolean ___isRecord___=false;
	public static final boolean ___isRef___=false;
	public static final boolean ___isValue___=false;
    /** Union templated Byte Struct 
     * @param dimensions [0]=___size___,[1]= forced ___seek___
     */
	Union (int... dimensions) {
        int[] dim = init(dimensions);
        ___size___ = dim[0];
        ___seek___ = dim[1];

    }

    int[] init(int... dimensions) {
        int size = dimensions.length > 0 ? dimensions[0] : 0;
        if (___subrecord___ == null) {
            final String[] indexPrefixes = {"", "s", "_", "Index", "Length", "Ref", "Header", "Info", "Table"};
            for (String indexPrefix : indexPrefixes) {
                try {
                    ___subrecord___ = (Class<? extends Enum>) Class.forName(getClass().getPackage().getName() + '.' + name() + indexPrefix);
                    try {
                        size = ___subrecord___.getField("___recordlen___").getInt(null);
                    } catch (Exception e) {
                    }
                    break;
                } catch (ClassNotFoundException e) {
                }
            }
        }

        for (String vPrefixe1 : new String[]{"_", "", "$", "Value",}) {
            if (___valueclass___ != null) break;
            String suffix = vPrefixe1;
            for (String name1 : new String[]{name().toLowerCase(), name(),}) {
                if (___valueclass___ != null) break;
                final String trailName = name1;
                if (trailName.endsWith(suffix)) {
                    for (String aPackage1 : new String[]{"",
                            getClass().getPackage().getName() + ".",
                            "java.lang.",
                            "java.util.",
                    })
                        if (___valueclass___ == null) break;
                        else
                            try {
                                ___valueclass___ = Class.forName(aPackage1 + name().replace(suffix, ""));
                            } catch (ClassNotFoundException e) {
                            }
                }
            }
        }

        int seek = dimensions.length > 1 ? dimensions[1] : ___recordlen___;
        ___recordlen___ = Math.max(___recordlen___,seek+size);
        return new int[]{size, seek};
    }
    /**
     * The struct's top level method for indexing 1 record. Each Enum field will call SubIndex
     *
     * @param src      the ByteBuffer of the input file
     * @param register array holding values pointing to Stack offsets
     * @param stack    A stack of 32-bit pointers only to src positions
     */
    static void index
            (ByteBuffer src, int[] register, IntBuffer stack) {
        for (Union Union_ : values()) {
            String hdr = Union_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            Union_.subIndex(src, register, stack);
        }
    }

    /**
     * Each of the Enums can override thier deault behavior of "___seek___-past"
     *
     * @param src      the ByteBuffer of the input file
     * @param register array holding values pointing to Stack offsets
     * @param stack    A stack of 32-bit pointers only to src positions
     */
    private void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        System.err.println(name() + ":subIndex src:stack" + src.position() + ':' + stack.position());
        int begin = src.position();
        int stackPtr = stack.position();
        stack.put(begin);
        if (___isRecord___ && ___subrecord___ != null) { 
            /*                 try {
                final model.UUID table = model.UUID.valueOf(___subrecord___.getSimpleName());
                if (table != null) {
                    //stow the original location
                    int mark = stack.position();
                    stack.position((register[TopLevelRecord.TableRecord.ordinal()] + table.___seek___) / 4);
                    ___subrecord___.getMethod("index", ByteBuffer.class, int[].class, IntBuffer.class).invoke(null);
                    //resume the lower stack activities
                    stack.position(mark);
                }
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
*/        }
    }}
//@@ #endUnion