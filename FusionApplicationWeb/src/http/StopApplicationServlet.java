package http;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.application.configuration.IConfigure;
import org.fbox.common.exception.ApplicationConfigurationException;

/**
 * Servlet implementation class StartApplicationServlet
 */
@WebServlet("/StopApplication")
public class StopApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB
	IConfigure configStub;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StopApplicationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			configStub.unconfigure();
		} catch (ApplicationConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
