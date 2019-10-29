
package com.frequentis.cap;

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
 *         &lt;element name="SupportedCAPSchemaResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "supportedCAPSchemaResult"
})
@XmlRootElement(name = "SupportedCAPSchemaResponse")
public class SupportedCAPSchemaResponse {

    @XmlElementRef(name = "SupportedCAPSchemaResult", namespace = "http://cap.frequentis.com/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> supportedCAPSchemaResult;

    /**
     * Gets the value of the supportedCAPSchemaResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSupportedCAPSchemaResult() {
        return supportedCAPSchemaResult;
    }

    /**
     * Sets the value of the supportedCAPSchemaResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSupportedCAPSchemaResult(JAXBElement<String> value) {
        this.supportedCAPSchemaResult = value;
    }

}
