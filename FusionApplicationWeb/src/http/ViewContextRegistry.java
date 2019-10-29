package http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.data.IContextorContext;
import org.fbox.fusion.application.registry.ContextorRegistry;

/**
 * Servlet implementation class HelloWorldTest
 */
@WebServlet("/ViewContextRegistry")
public class ViewContextRegistry extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	ContextorRegistry registry;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewContextRegistry() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("<HTML><BODY>");
		HashMap<String, IContextorContext> contextEntries=registry.getRegistryEntries();
		System.out.println(contextEntries);
		for (Entry<String, IContextorContext> dst : contextEntries.entrySet()) {
			response.getWriter().write(dst.getKey()+ " ---------> {");
			IContextorContext ctx=dst.getValue();
			response.getWriter().write(ctx.toString());
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
