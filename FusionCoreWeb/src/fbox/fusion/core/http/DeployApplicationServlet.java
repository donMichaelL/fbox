package fbox.fusion.core.http;


import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.fbox.common.application.configuration.ApplicationInfo;
import org.fbox.common.exception.ApplicationAlreadyExistsException;
import org.fbox.common.exception.EventSourceParserException;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.common.xml.data.ApplicationDAO;
import org.fbox.configurator.ejb.impl.ApplicationBuilderBean;
import org.fbox.configurator.exceptions.ApplicationBuilderException;
import org.fbox.configurator.exceptions.ApplicationDeployerException;
import org.fbox.configurator.exceptions.CLIManagerException;

/**
 * Servlet implementation class ApplicationEventParser
 */
@WebServlet("/DeployApplication")
public class DeployApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB
	ApplicationBuilderBean builder;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeployApplicationServlet() {
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
		String appSource=request.getParameter("source");
		if (appSource!=null) {
			try {
				ApplicationInfo application=builder.buildApplication(appSource,true);
				response.getWriter().write("0");
			} catch (ApplicationBuilderException | EventSourceParserException | ApplicationDeployerException | RegistryInsertionError e ) {
				// e.printStackTrace();
				response.getWriter().write("-1");
				// response.getWriter().write(e.getMessage());
			} catch (ParserConfigurationException pce) {
				response.getWriter().write("-2");
			} catch (ApplicationAlreadyExistsException e) {
				response.getWriter().write("-3");
				//e.printStackTrace();
			}
		}
	}

}
