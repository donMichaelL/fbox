package http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.application.data.ContextorSource;
import org.fbox.fusion.application.registry.InputOutputMapRegistry;

/**
 * Servlet implementation class HelloWorldTest
 */
@WebServlet("/ViewRegistry")
public class ViewRegistry extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	InputOutputMapRegistry registry;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewRegistry() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("<HTML><BODY>");
		HashMap<String, ArrayList<ContextorSource>> sourceDestinationMappings=registry.getOutputInputMap();
		System.out.println(sourceDestinationMappings);
		for (Entry<String, ArrayList<ContextorSource>> dst : sourceDestinationMappings.entrySet()) {
			response.getWriter().write(dst.getKey()+ " ---------> {");
			ArrayList<ContextorSource> sources=dst.getValue();
			int length=sources.size();
			for (int i=0;i<length;i++) {
				response.getWriter().write(sources.get(i).toString());
				if (i<(length-1))
					response.getWriter().write(", ");
			}
			response.getWriter().write("}</br>");
		}
		response.getWriter().write("</BODY></HTML>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);

	}

}
