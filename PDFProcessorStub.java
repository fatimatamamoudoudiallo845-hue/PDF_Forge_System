
package PDFModule;

public class _PDFProcessorStub extends org.omg.CORBA.portable.ObjectImpl
        implements PDFModule.PDFProcessor {

    private static final String[] __ids = {
        "IDL:PDFModule/PDFProcessor:1.0"
    };

    public String[] _ids() { return __ids; }

    // 1. mergePDFs
    public byte[] mergePDFs(byte[][] pdfFiles) throws PDFModule.PDFException {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("mergePDFs", true);
            PDFModule.PDFListHelper.write(_out, pdfFiles);
            _in = _invoke(_out);
            return PDFModule.PDFDataHelper.read(_in);
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            _in = _ex.getInputStream();
            String _id = _ex.getId();
            if (_id.equals(PDFModule.PDFExceptionHelper.id()))
                throw PDFModule.PDFExceptionHelper.read(_in);
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return mergePDFs(pdfFiles);
        } finally {
            _releaseReply(_in);
        }
    }

    // 2. splitPDF
    public byte[][] splitPDF(byte[] pdfFile, int pagesPerChunk)
            throws PDFModule.PDFException {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("splitPDF", true);
            PDFModule.PDFDataHelper.write(_out, pdfFile);
            _out.write_long(pagesPerChunk);
            _in = _invoke(_out);
            return PDFModule.PDFListHelper.read(_in);
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            _in = _ex.getInputStream();
            String _id = _ex.getId();
            if (_id.equals(PDFModule.PDFExceptionHelper.id()))
                throw PDFModule.PDFExceptionHelper.read(_in);
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return splitPDF(pdfFile, pagesPerChunk);
        } finally {
            _releaseReply(_in);
        }
    }

    // 3. extractPages
    public byte[] extractPages(byte[] pdfFile, int[] pages)
            throws PDFModule.PDFException, PDFModule.PageNotFoundException {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("extractPages", true);
            PDFModule.PDFDataHelper.write(_out, pdfFile);
            PDFModule.PageListHelper.write(_out, pages);
            _in = _invoke(_out);
            return PDFModule.PDFDataHelper.read(_in);
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            _in = _ex.getInputStream();
            String _id = _ex.getId();
            if (_id.equals(PDFModule.PDFExceptionHelper.id()))
                throw PDFModule.PDFExceptionHelper.read(_in);
            if (_id.equals(PDFModule.PageNotFoundExceptionHelper.id()))
                throw PDFModule.PageNotFoundExceptionHelper.read(_in);
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return extractPages(pdfFile, pages);
        } finally {
            _releaseReply(_in);
        }
    }

    // 4. deletePages
    public byte[] deletePages(byte[] pdfFile, int[] pages)
            throws PDFModule.PDFException, PDFModule.PageNotFoundException {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("deletePages", true);
            PDFModule.PDFDataHelper.write(_out, pdfFile);
            PDFModule.PageListHelper.write(_out, pages);
            _in = _invoke(_out);
            return PDFModule.PDFDataHelper.read(_in);
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            _in = _ex.getInputStream();
            String _id = _ex.getId();
            if (_id.equals(PDFModule.PDFExceptionHelper.id()))
                throw PDFModule.PDFExceptionHelper.read(_in);
            if (_id.equals(PDFModule.PageNotFoundExceptionHelper.id()))
                throw PDFModule.PageNotFoundExceptionHelper.read(_in);
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return deletePages(pdfFile, pages);
        } finally {
            _releaseReply(_in);
        }
    }

    // 5. addPassword
    public byte[] addPassword(byte[] pdfFile, String ownerPwd, String userPwd)
            throws PDFModule.PDFException, PDFModule.InvalidPasswordException {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("addPassword", true);
            PDFModule.PDFDataHelper.write(_out, pdfFile);
            _out.write_string(ownerPwd);
            _out.write_string(userPwd);
            _in = _invoke(_out);
            return PDFModule.PDFDataHelper.read(_in);
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            _in = _ex.getInputStream();
            String _id = _ex.getId();
            if (_id.equals(PDFModule.PDFExceptionHelper.id()))
                throw PDFModule.PDFExceptionHelper.read(_in);
            if (_id.equals(PDFModule.InvalidPasswordExceptionHelper.id()))
                throw PDFModule.InvalidPasswordExceptionHelper.read(_in);
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return addPassword(pdfFile, ownerPwd, userPwd);
        } finally {
            _releaseReply(_in);
        }
    }

    // 6. convertToImages
    public String[] convertToImages(byte[] pdfFile, String format, int dpi)
            throws PDFModule.PDFException, PDFModule.ConversionException {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("convertToImages", true);
            PDFModule.PDFDataHelper.write(_out, pdfFile);
            _out.write_string(format);
            _out.write_long(dpi);
            _in = _invoke(_out);
            return PDFModule.ImageListHelper.read(_in);
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            _in = _ex.getInputStream();
            String _id = _ex.getId();
            if (_id.equals(PDFModule.PDFExceptionHelper.id()))
                throw PDFModule.PDFExceptionHelper.read(_in);
            if (_id.equals(PDFModule.ConversionExceptionHelper.id()))
                throw PDFModule.ConversionExceptionHelper.read(_in);
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return convertToImages(pdfFile, format, dpi);
        } finally {
            _releaseReply(_in);
        }
    }

    // 7. extractText
    public String extractText(byte[] pdfFile) throws PDFModule.PDFException {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("extractText", true);
            PDFModule.PDFDataHelper.write(_out, pdfFile);
            _in = _invoke(_out);
            return _in.read_string();
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            _in = _ex.getInputStream();
            String _id = _ex.getId();
            if (_id.equals(PDFModule.PDFExceptionHelper.id()))
                throw PDFModule.PDFExceptionHelper.read(_in);
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return extractText(pdfFile);
        } finally {
            _releaseReply(_in);
        }
    }

    // 8. createPDF
    public byte[] createPDF(String title, String content, String author)
            throws PDFModule.PDFException {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("createPDF", true);
            _out.write_string(title);
            _out.write_string(content);
            _out.write_string(author);
            _in = _invoke(_out);
            return PDFModule.PDFDataHelper.read(_in);
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            _in = _ex.getInputStream();
            String _id = _ex.getId();
            if (_id.equals(PDFModule.PDFExceptionHelper.id()))
                throw PDFModule.PDFExceptionHelper.read(_in);
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return createPDF(title, content, author);
        } finally {
            _releaseReply(_in);
        }
    }

    // getPageCount
    public int getPageCount(byte[] pdfFile) throws PDFModule.PDFException {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("getPageCount", true);
            PDFModule.PDFDataHelper.write(_out, pdfFile);
            _in = _invoke(_out);
            return _in.read_long();
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            _in = _ex.getInputStream();
            String _id = _ex.getId();
            if (_id.equals(PDFModule.PDFExceptionHelper.id()))
                throw PDFModule.PDFExceptionHelper.read(_in);
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return getPageCount(pdfFile);
        } finally {
            _releaseReply(_in);
        }
    }

    // ping
    public boolean ping() {
        org.omg.CORBA.portable.InputStream _in = null;
        try {
            org.omg.CORBA.portable.OutputStream _out = _request("ping", true);
            _in = _invoke(_out);
            return _in.read_boolean();
        } catch (org.omg.CORBA.portable.ApplicationException _ex) {
            throw new org.omg.CORBA.MARSHAL(_ex.getId());
        } catch (org.omg.CORBA.portable.RemarshalException _rm) {
            return ping();
        } finally {
            _releaseReply(_in);
        }
    }
}
EOF
