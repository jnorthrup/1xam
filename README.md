1xam
====

 java metaprogramming ISAM  "1x Access Method"



the whole motivation for this project is to codify 'grammars' for 'structs' in legal java, using partly convention, partly reflection, and partly code generation.

this project consists of a simple utility which post-processes a legal package full of java enum's and generates decorated versions of these classes for ease of maintaining and javadoc usefulness.

at runtime these generated enum classes will scan the package via reflection and assemble a visitor pattern scanner for binary memory representation, otherwise the equivalence of C language structs using java NIO.

finally, this visitor scanner and the generative utility form the basis for specialized and optimized ISAM record access, for large, memory mapped contiguous bytes, suitable for graph stores and data warehousing.

an example of this project in action is below:

```
 
package model;
import java.nio.*;
import java.lang.reflect.*;
/* extruded from the source "grammar"

package model;

public enum UUID { data ; public final int ___size___=14 ;}
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
 
/**
 * <p>recordSize: 286
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>description</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>Id</td><td>0xe</td><td>0x0</td><td></td><td>byte[]</td><td>{@link model.Id}</td></tr>
 * <tr><td>Name</td><td>0x80</td><td>0xe</td><td></td><td>byte[]</td><td>{@link model.Name}</td></tr>
 * <tr><td>Email</td><td>0x80</td><td>0x8e</td><td></td><td>byte[]</td><td>{@link model.Email}</td></tr>
 * <tr><td>Password</td><td>0x10</td><td>0x10e</td><td></td><td>byte[]</td><td>{@link model.Password}</td></tr>
 * 
 * @see model.Account#Id
 * @see model.Account#Name
 * @see model.Account#Email
 * @see model.Account#Password
 * </table>
 */
public enum Account { 
Id(0xe)	{{
		___subrecord___=model.Id.class;
	}}
,Name(0x80)	{{
		___subrecord___=model.Name.class;
	}}
,Email(0x80)	{{
		___subrecord___=model.Email.class;
	}}
,Password(0x10)	{{
		___subrecord___=model.Password.class;
	}}
;
	public static final boolean ___isValue___=false;
	
	public static final boolean ___isRef___=false;
	public static final boolean ___isRecord___=false;
	
	
	/**
     * the length of one record
     */
	public static int ___recordlen___;
	/**
     * a hint for src.
     */
	public String ___src___;
	/**
     * the size per field, if any
     */
	public final int ___size___;
	public static final boolean ___isInfo___=false;
	/**
     * a hint class for docs.
     */
	public String ___doc___;
	public static final boolean ___isHeader___=false;
	/**
     * a delegate class which will perform sub-indexing on behalf of a field once it has marked its initial starting
     * offset into the stack.
     */
	public Class<? extends Enum> ___subrecord___;
	/**
     * a hint class for bean-wrapper access to data contained.
     */
	public Class ___valueclass___;
	
	
	/**
     * the offset from record-start of the field
     */
	public final int ___seek___;
    /** Account templated Byte Struct 
     * @param dimensions [0]=___size___,[1]= forced ___seek___
     */
	Account (int... dimensions) {
        int[] dim = init(dimensions);
        ___size___ = dim[0];
        ___seek___ = dim[1];

    }
```
 [..] // boilerplate generated beyond this point

 
