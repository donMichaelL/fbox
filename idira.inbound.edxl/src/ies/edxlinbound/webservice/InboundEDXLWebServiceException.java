
package ies.edxlinbound.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Inbound_EDXL_WebServiceException" type="{http://webservice.edxlinbound.ies}Exception" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "inboundEDXLWebServiceException"
})
@XmlRootElement(name = "Inbound_EDXL_WebServiceException")
public class InboundEDXLWebServiceException {

    @XmlElementRef(name = "Inbound_EDXL_WebServiceException", namespace = "http://webservice.edxlinbound.ies", type = JAXBElement.class, required = false)
    protected JAXBElement<Exception> inboundEDXLWebServiceException;

    /**
     * Gets the value of the inboundEDXLWebServiceException property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Exception }{@code >}
     *     
     */
    public JAXBElement<Exception> getInboundEDXLWebServiceException() {
        return inboundEDXLWebServiceException;
    }

    /**
     * Sets the value of the inboundEDXLWebServiceException property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Exception }{@code >}
     *     
     */
    public void setInboundEDXLWebServiceException(JAXBElement<Exception> value) {
        this.inboundEDXLWebServiceException = value;
    }

}
