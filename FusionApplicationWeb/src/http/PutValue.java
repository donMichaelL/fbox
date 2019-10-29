package http;


import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.application.data.DataElement;
import org.fbox.common.data.IDataElement;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.fusion.application.algorithms.invoker.IContextorExecute;
import org.fbox.fusion.application.communication.DataElementProviderBean;
import org.fbox.fusion.application.exception.ContextNotFoundException;



/**
 * Servlet implementation class HelloWorldTest
 */
@WebServlet("/PutValue")
public class PutValue extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@EJB
	DataElementProviderBean mprovider;
	
	@EJB
	IContextorExecute executor;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PutValue() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String srcId=request.getParameter("id");
		String value=request.getParameter("value");
		
		if (srcId!=null && value!=null) { 
			IDataElement data=new DataElement(srcId);
			data.setValue(Double.parseDouble(value));
			try {
				executor.update(srcId, data);
			} catch (AlgorithmExecutionException | ContextNotFoundException	| OutputAdapterException | FormatterException | RegistryInsertionError e) {
				e.printStackTrace();
			}
			response.getWriter().write("Value (" + value +") coming from source with GUID:" +srcId + " was sent to queue"); 
		} else {
			response.getWriter().write("Non valid data provided");
		}
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
