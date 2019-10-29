package http;

import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.application.data.ContextorSource;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.fusion.application.registry.InputOutputMapRegistry;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;

/**
 * Servlet implementation class HelloWorldTest
 */
@WebServlet("/UpdateRegistry")
public class UpdateRegistry extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	InputOutputMapRegistry registry;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateRegistry() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String source=request.getParameter("src");
		String destination=request.getParameter("dst");
		String timeLimit=request.getParameter("timeLimit");
		
		response.getWriter().write("<HTML><BODY>");
		if (source!=null && destination!=null) {
			try {
				registry.addMapping(new ContextorSource(source, Long.parseLong(timeLimit)), destination);
			} catch (RegistryInsertionError e) {
				e.printStackTrace();
			}
			response.getWriter().write("Mapping "+ source +" ------> " + destination +" stored sucessfully");
		} else
			response.getWriter().write("No valid data specified!. Nothing is stored");
		response.getWriter().write("</BODY></HTML>");		 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);

	}

}
