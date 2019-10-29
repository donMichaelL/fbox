package fbox.fusion.core.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.application.configuration.ApplicationInfo;
import org.fbox.common.registry.IRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ApplicationEventParser
 */
@WebServlet("/GetApplicationRegistry")
public class GetApplicationRegistryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	IRegistry<ApplicationInfo> registry;
	
	 /**
     * @see HttpServlet#HttpServlet()
     */
    public GetApplicationRegistryServlet() {
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
	
		//Retrieve the list with the deployed applications
		HashMap<String, ApplicationInfo> appsMap = registry.getRegistryEntries();
		List<ApplicationInfo> appsList = new LinkedList<ApplicationInfo>(appsMap.values());
		
		//Shorten the results!!
		for(ApplicationInfo app : appsList) {
			app.setModel(null);
		}
		
		ObjectMapper mapper = new ObjectMapper(); 
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(appsList);
		
		// System.out.println(json);
		
		// Send response
		PrintWriter out = response.getWriter();
		out.write(json);
	}
}
