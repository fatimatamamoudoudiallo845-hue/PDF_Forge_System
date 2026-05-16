package PDFModule;

public abstract class PDFProcessorHelper {

    private static String _id = "IDL:PDFModule/PDFProcessor:1.0";

    public static void insert(org.omg.CORBA.Any a, PDFModule.PDFProcessor that) {
        org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }

    public static PDFModule.PDFProcessor extract(org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }

    private static org.omg.CORBA.TypeCode __typeCode = null;

    public static synchronized org.omg.CORBA.TypeCode type() {
        if (__typeCode == null) {
            __typeCode = org.omg.CORBA.ORB.init().create_interface_tc(_id, "PDFProcessor");
        }
        return __typeCode;
    }

    public static String id() {
        return _id;
    }

    public static PDFModule.PDFProcessor read(org.omg.CORBA.portable.InputStream istream) {
        return narrow(istream.read_Object());
    }

    public static void write(org.omg.CORBA.portable.OutputStream ostream, PDFModule.PDFProcessor value) {
        ostream.write_Object((org.omg.CORBA.Object) value);
    }

    public static PDFModule.PDFProcessor narrow(org.omg.CORBA.Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof PDFModule.PDFProcessor) {
            return (PDFModule.PDFProcessor) obj;
        } else if (!obj._is_a(id())) {
            throw new org.omg.CORBA.BAD_PARAM();
        } else {
            return createProxy(obj);
        }
    }

    public static PDFModule.PDFProcessor unchecked_narrow(org.omg.CORBA.Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof PDFModule.PDFProcessor) {
            return (PDFModule.PDFProcessor) obj;
        } else {
            return createProxy(obj);
        }
    }

    private static PDFModule.PDFProcessor createProxy(final org.omg.CORBA.Object obj) {
        return (PDFModule.PDFProcessor) java.lang.reflect.Proxy.newProxyInstance(
            PDFModule.PDFProcessor.class.getClassLoader(),
            new Class<?>[] { PDFModule.PDFProcessor.class, org.omg.CORBA.Object.class },
            new java.lang.reflect.InvocationHandler() {
                @Override
                public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
                    return method.invoke(obj, args);
                }
            }
        );
    }
}
