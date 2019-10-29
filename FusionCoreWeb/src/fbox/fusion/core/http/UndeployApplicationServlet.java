package fbox.fusion.core.http;


import java.io.IOException;


import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.fbox.common.exception.EventSourceParserException;
import org.fbox.configurator.ejb.impl.ApplicationBuilderBean;
import org.fbox.configurator.exceptions.ApplicationBuilderException;
import org.fbox.configurator.exceptions.ApplicationDeployerException;

/**
 * Servlet implementation class ApplicationEventParser
 */
@WebServlet("/UndeployApplication")
public class UndeployApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB
	ApplicationBuilderBean builder;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UndeployApplicationServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String applicationId=request.getParameter("applicationId");
		if (applicationId!=null) {
			try {
				response.getWriter().write(""+builder.destroyApplication(applicationId, true));
			} catch (ApplicationBuilderException | EventSourceParserException | ApplicationDeployerException e) {
				e.printStackTrace();
				response.getWriter().write("false");
			}
		}
	}

}
