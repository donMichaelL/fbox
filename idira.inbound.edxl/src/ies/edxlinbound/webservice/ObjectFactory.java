
package ies.edxlinbound.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ies.edxlinbound.webservice package. 
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

    private final static QName _ExceptionMessage_QNAME = new QName("http://webservice.edxlinbound.ies", "Message");
    private final static QName _ReceiveEDXLEdxl_QNAME = new QName("http://webservice.edxlinbound.ies", "edxl");
    private final static QName _IsValidRMRM_QNAME = new QName("http://webservice.edxlinbound.ies", "RM");
    private final static QName _InboundEDXLWebServiceExceptionInboundEDXLWebServiceException_QNAME = new QName("http://webservice.edxlinbound.ies", "Inbound_EDXL_WebServiceException");
    private final static QName _IsValidCAPCAP_QNAME = new QName("http://webservice.edxlinbound.ies", "CAP");
    private final static QName _ValidateCAPResponseReturn_QNAME = new QName("http://webservice.edxlinbound.ies", "return");
    private final static QName _ReceiveCAPCAPmessage_QNAME = new QName("http://webservice.edxlinbound.ies", "CAPmessage");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ies.edxlinbound.webservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IsValidCAPResponse }
     * 
     */
    public IsValidCAPResponse createIsValidCAPResponse() {
        return new IsValidCAPResponse();
    }

    /**
     * Create an instance of {@link IsValidRM }
     * 
     */
    public IsValidRM createIsValidRM() {
        return new IsValidRM();
    }

    /**
     * Create an instance of {@link ReceiveCAP }
     * 
     */
    public ReceiveCAP createReceiveCAP() {
        return new ReceiveCAP();
    }

    /**
     * Create an instance of {@link IsValidRMResponse }
     * 
     */
    public IsValidRMResponse createIsValidRMResponse() {
        return new IsValidRMResponse();
    }

    /**
     * Create an instance of {@link ReceiveEDXLResponse }
     * 
     */
    public ReceiveEDXLResponse createReceiveEDXLResponse() {
        return new ReceiveEDXLResponse();
    }

    /**
     * Create an instance of {@link ValidateRM }
     * 
     */
    public ValidateRM createValidateRM() {
        return new ValidateRM();
    }

    /**
     * Create an instance of {@link ValidateCAP }
     * 
     */
    public ValidateCAP createValidateCAP() {
        return new ValidateCAP();
    }

    /**
     * Create an instance of {@link ValidateRMResponse }
     * 
     */
    public ValidateRMResponse createValidateRMResponse() {
        return new ValidateRMResponse();
    }

    /**
     * Create an instance of {@link InboundEDXLWebServiceException }
     * 
     */
    public InboundEDXLWebServiceException createInboundEDXLWebServiceException() {
        return new InboundEDXLWebServiceException();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link CheckStatus }
     * 
     */
    public CheckStatus createCheckStatus() {
        return new CheckStatus();
    }

    /**
     * Create an instance of {@link ReceiveCAPResponse }
     * 
     */
    public ReceiveCAPResponse createReceiveCAPResponse() {
        return new ReceiveCAPResponse();
    }

    /**
     * Create an instance of {@link ValidateCAPResponse }
     * 
     */
    public ValidateCAPResponse createValidateCAPResponse() {
        return new ValidateCAPResponse();
    }

    /**
     * Create an instance of {@link CheckStatusResponse }
     * 
     */
    public CheckStatusResponse createCheckStatusResponse() {
        return new CheckStatusResponse();
    }

    /**
     * Create an instance of {@link IsValidCAP }
     * 
     */
    public IsValidCAP createIsValidCAP() {
        return new IsValidCAP();
    }

    /**
     * Create an instance of {@link ReceiveEDXL }
     * 
     */
    public ReceiveEDXL createReceiveEDXL() {
        return new ReceiveEDXL();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "Message", scope = Exception.class)
    public JAXBElement<String> createExceptionMessage(String value) {
        return new JAXBElement<String>(_ExceptionMessage_QNAME, String.class, Exception.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "edxl", scope = ReceiveEDXL.class)
    public JAXBElement<String> createReceiveEDXLEdxl(String value) {
        return new JAXBElement<String>(_ReceiveEDXLEdxl_QNAME, String.class, ReceiveEDXL.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "RM", scope = IsValidRM.class)
    public JAXBElement<String> createIsValidRMRM(String value) {
        return new JAXBElement<String>(_IsValidRMRM_QNAME, String.class, IsValidRM.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "Inbound_EDXL_WebServiceException", scope = InboundEDXLWebServiceException.class)
    public JAXBElement<Exception> createInboundEDXLWebServiceExceptionInboundEDXLWebServiceException(Exception value) {
        return new JAXBElement<Exception>(_InboundEDXLWebServiceExceptionInboundEDXLWebServiceException_QNAME, Exception.class, InboundEDXLWebServiceException.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "CAP", scope = IsValidCAP.class)
    public JAXBElement<String> createIsValidCAPCAP(String value) {
        return new JAXBElement<String>(_IsValidCAPCAP_QNAME, String.class, IsValidCAP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "return", scope = ValidateCAPResponse.class)
    public JAXBElement<String> createValidateCAPResponseReturn(String value) {
        return new JAXBElement<String>(_ValidateCAPResponseReturn_QNAME, String.class, ValidateCAPResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "CAP", scope = ValidateCAP.class)
    public JAXBElement<String> createValidateCAPCAP(String value) {
        return new JAXBElement<String>(_IsValidCAPCAP_QNAME, String.class, ValidateCAP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "RM", scope = ValidateRM.class)
    public JAXBElement<String> createValidateRMRM(String value) {
        return new JAXBElement<String>(_IsValidRMRM_QNAME, String.class, ValidateRM.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "CAPmessage", scope = ReceiveCAP.class)
    public JAXBElement<String> createReceiveCAPCAPmessage(String value) {
        return new JAXBElement<String>(_ReceiveCAPCAPmessage_QNAME, String.class, ReceiveCAP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.edxlinbound.ies", name = "return", scope = ValidateRMResponse.class)
    public JAXBElement<String> createValidateRMResponseReturn(String value) {
        return new JAXBElement<String>(_ValidateCAPResponseReturn_QNAME, String.class, ValidateRMResponse.class, value);
    }

}
