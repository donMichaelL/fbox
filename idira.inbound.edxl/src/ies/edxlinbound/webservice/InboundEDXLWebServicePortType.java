package ies.edxlinbound.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.5
 * 2013-05-23T16:35:00.058+03:00
 * Generated source version: 2.7.5
 * 
 */
@WebService(targetNamespace = "http://webservice.edxlinbound.ies", name = "Inbound_EDXL_WebServicePortType")
@XmlSeeAlso({ObjectFactory.class})
public interface InboundEDXLWebServicePortType {

    @WebResult(name = "return", targetNamespace = "http://webservice.edxlinbound.ies")
    @Action(input = "urn:checkStatus", output = "urn:checkStatusResponse")
    @RequestWrapper(localName = "checkStatus", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.CheckStatus")
    @WebMethod(action = "urn:checkStatus")
    @ResponseWrapper(localName = "checkStatusResponse", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.CheckStatusResponse")
    public java.lang.Boolean checkStatus();

    @WebResult(name = "return", targetNamespace = "http://webservice.edxlinbound.ies")
    @Action(input = "urn:receiveEDXL", output = "urn:receiveEDXLResponse", fault = {@FaultAction(className = InboundEDXLWebServiceException_Exception.class, value = "urn:receiveEDXLInbound_EDXL_WebServiceException")})
    @RequestWrapper(localName = "receiveEDXL", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.ReceiveEDXL")
    @WebMethod(action = "urn:receiveEDXL")
    @ResponseWrapper(localName = "receiveEDXLResponse", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.ReceiveEDXLResponse")
    public java.lang.Boolean receiveEDXL(
        @WebParam(name = "edxl", targetNamespace = "http://webservice.edxlinbound.ies")
        java.lang.String edxl
    ) throws InboundEDXLWebServiceException_Exception;

    @WebResult(name = "return", targetNamespace = "http://webservice.edxlinbound.ies")
    @Action(input = "urn:isValidRM", output = "urn:isValidRMResponse")
    @RequestWrapper(localName = "isValidRM", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.IsValidRM")
    @WebMethod(action = "urn:isValidRM")
    @ResponseWrapper(localName = "isValidRMResponse", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.IsValidRMResponse")
    public java.lang.Boolean isValidRM(
        @WebParam(name = "RM", targetNamespace = "http://webservice.edxlinbound.ies")
        java.lang.String rm
    );

    @WebResult(name = "return", targetNamespace = "http://webservice.edxlinbound.ies")
    @Action(input = "urn:receiveCAP", output = "urn:receiveCAPResponse", fault = {@FaultAction(className = InboundEDXLWebServiceException_Exception.class, value = "urn:receiveCAPInbound_EDXL_WebServiceException")})
    @RequestWrapper(localName = "receiveCAP", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.ReceiveCAP")
    @WebMethod(action = "urn:receiveCAP")
    @ResponseWrapper(localName = "receiveCAPResponse", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.ReceiveCAPResponse")
    public java.lang.Boolean receiveCAP(
        @WebParam(name = "CAPmessage", targetNamespace = "http://webservice.edxlinbound.ies")
        java.lang.String caPmessage
    ) throws InboundEDXLWebServiceException_Exception;

    @WebResult(name = "return", targetNamespace = "http://webservice.edxlinbound.ies")
    @Action(input = "urn:isValidCAP", output = "urn:isValidCAPResponse")
    @RequestWrapper(localName = "isValidCAP", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.IsValidCAP")
    @WebMethod(action = "urn:isValidCAP")
    @ResponseWrapper(localName = "isValidCAPResponse", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.IsValidCAPResponse")
    public java.lang.Boolean isValidCAP(
        @WebParam(name = "CAP", targetNamespace = "http://webservice.edxlinbound.ies")
        java.lang.String cap
    );

    @WebResult(name = "return", targetNamespace = "http://webservice.edxlinbound.ies")
    @Action(input = "urn:validateCAP", output = "urn:validateCAPResponse")
    @RequestWrapper(localName = "validateCAP", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.ValidateCAP")
    @WebMethod(action = "urn:validateCAP")
    @ResponseWrapper(localName = "validateCAPResponse", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.ValidateCAPResponse")
    public java.lang.String validateCAP(
        @WebParam(name = "CAP", targetNamespace = "http://webservice.edxlinbound.ies")
        java.lang.String cap
    );

    @WebResult(name = "return", targetNamespace = "http://webservice.edxlinbound.ies")
    @Action(input = "urn:validateRM", output = "urn:validateRMResponse")
    @RequestWrapper(localName = "validateRM", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.ValidateRM")
    @WebMethod(action = "urn:validateRM")
    @ResponseWrapper(localName = "validateRMResponse", targetNamespace = "http://webservice.edxlinbound.ies", className = "ies.edxlinbound.webservice.ValidateRMResponse")
    public java.lang.String validateRM(
        @WebParam(name = "RM", targetNamespace = "http://webservice.edxlinbound.ies")
        java.lang.String rm
    );
}
