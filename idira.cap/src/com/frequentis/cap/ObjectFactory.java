
package com.frequentis.cap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.frequentis.cap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SaveCAPCapMessage_QNAME = new QName("http://cap.frequentis.com/", "CapMessage");
    private final static QName _ValidateCAPResponseValidateCAPResult_QNAME = new QName("http://cap.frequentis.com/", "ValidateCAPResult");
    private final static QName _SaveCAPResponseSaveCAPResult_QNAME = new QName("http://cap.frequentis.com/", "SaveCAPResult");
    private final static QName _ReadCAPReceiver_QNAME = new QName("http://cap.frequentis.com/", "Receiver");
    private final static QName _ReadCAPArea_QNAME = new QName("http://cap.frequentis.com/", "Area");
    private final static QName _ReadCAPPassword_QNAME = new QName("http://cap.frequentis.com/", "Password");
    private final static QName _ReadCAPResponseReadCAPResult_QNAME = new QName("http://cap.frequentis.com/", "ReadCAPResult");
    private final static QName _SupportedCAPSchemaResponseSupportedCAPSchemaResult_QNAME = new QName("http://cap.frequentis.com/", "SupportedCAPSchemaResult");
    private final static QName _SupportedCAPVersionResponseSupportedCAPVersionResult_QNAME = new QName("http://cap.frequentis.com/", "SupportedCAPVersionResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.frequentis.cap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ValidateCAPResponse }
     * 
     */
    public ValidateCAPResponse createValidateCAPResponse() {
        return new ValidateCAPResponse();
    }

    /**
     * Create an instance of {@link SaveCAP }
     * 
     */
    public SaveCAP createSaveCAP() {
        return new SaveCAP();
    }

    /**
     * Create an instance of {@link SupportedCAPSchema }
     * 
     */
    public SupportedCAPSchema createSupportedCAPSchema() {
        return new SupportedCAPSchema();
    }

    /**
     * Create an instance of {@link ValidateCAP }
     * 
     */
    public ValidateCAP createValidateCAP() {
        return new ValidateCAP();
    }

    /**
     * Create an instance of {@link ReadCAP }
     * 
     */
    public ReadCAP createReadCAP() {
        return new ReadCAP();
    }

    /**
     * Create an instance of {@link SupportedCAPVersionResponse }
     * 
     */
    public SupportedCAPVersionResponse createSupportedCAPVersionResponse() {
        return new SupportedCAPVersionResponse();
    }

    /**
     * Create an instance of {@link SupportedCAPVersion }
     * 
     */
    public SupportedCAPVersion createSupportedCAPVersion() {
        return new SupportedCAPVersion();
    }

    /**
     * Create an instance of {@link SaveCAPResponse }
     * 
     */
    public SaveCAPResponse createSaveCAPResponse() {
        return new SaveCAPResponse();
    }

    /**
     * Create an instance of {@link SupportedCAPSchemaResponse }
     * 
     */
    public SupportedCAPSchemaResponse createSupportedCAPSchemaResponse() {
        return new SupportedCAPSchemaResponse();
    }

    /**
     * Create an instance of {@link ReadCAPResponse }
     * 
     */
    public ReadCAPResponse createReadCAPResponse() {
        return new ReadCAPResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "CapMessage", scope = SaveCAP.class)
    public JAXBElement<String> createSaveCAPCapMessage(String value) {
        return new JAXBElement<String>(_SaveCAPCapMessage_QNAME, String.class, SaveCAP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "ValidateCAPResult", scope = ValidateCAPResponse.class)
    public JAXBElement<String> createValidateCAPResponseValidateCAPResult(String value) {
        return new JAXBElement<String>(_ValidateCAPResponseValidateCAPResult_QNAME, String.class, ValidateCAPResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "SaveCAPResult", scope = SaveCAPResponse.class)
    public JAXBElement<String> createSaveCAPResponseSaveCAPResult(String value) {
        return new JAXBElement<String>(_SaveCAPResponseSaveCAPResult_QNAME, String.class, SaveCAPResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "Receiver", scope = ReadCAP.class)
    public JAXBElement<String> createReadCAPReceiver(String value) {
        return new JAXBElement<String>(_ReadCAPReceiver_QNAME, String.class, ReadCAP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "Area", scope = ReadCAP.class)
    public JAXBElement<String> createReadCAPArea(String value) {
        return new JAXBElement<String>(_ReadCAPArea_QNAME, String.class, ReadCAP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "Password", scope = ReadCAP.class)
    public JAXBElement<String> createReadCAPPassword(String value) {
        return new JAXBElement<String>(_ReadCAPPassword_QNAME, String.class, ReadCAP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "ReadCAPResult", scope = ReadCAPResponse.class)
    public JAXBElement<ArrayOfstring> createReadCAPResponseReadCAPResult(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_ReadCAPResponseReadCAPResult_QNAME, ArrayOfstring.class, ReadCAPResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "SupportedCAPSchemaResult", scope = SupportedCAPSchemaResponse.class)
    public JAXBElement<String> createSupportedCAPSchemaResponseSupportedCAPSchemaResult(String value) {
        return new JAXBElement<String>(_SupportedCAPSchemaResponseSupportedCAPSchemaResult_QNAME, String.class, SupportedCAPSchemaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "CapMessage", scope = ValidateCAP.class)
    public JAXBElement<String> createValidateCAPCapMessage(String value) {
        return new JAXBElement<String>(_SaveCAPCapMessage_QNAME, String.class, ValidateCAP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cap.frequentis.com/", name = "SupportedCAPVersionResult", scope = SupportedCAPVersionResponse.class)
    public JAXBElement<String> createSupportedCAPVersionResponseSupportedCAPVersionResult(String value) {
        return new JAXBElement<String>(_SupportedCAPVersionResponseSupportedCAPVersionResult_QNAME, String.class, SupportedCAPVersionResponse.class, value);
    }

}
