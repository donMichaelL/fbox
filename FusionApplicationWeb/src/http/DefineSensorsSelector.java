package http;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.fusion.application.communication.DataStreamConsumerBean;


/**
 * Servlet implementation class HelloWorldTest
 */
@WebServlet("/DefineSensors")
public class DefineSensorsSelector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	DataStreamConsumerBean selector;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DefineSensorsSelector() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String[] sensorIds=request.getParameterValues("id");
		
		String selectStr="";
		if (sensorIds!=null) {
			for (String sensorId : sensorIds) {
				if (!selectStr.equals(""))
					selectStr+=" OR ";
				selectStr+="sensorID='"+sensorId+"'";
			}
		} else
			selectStr=null;
		
		Set<String> selectorsSet = new HashSet<String>();
		selectorsSet.add(selectStr);
		try {
			selector.subscribeForData(selectorsSet);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		response.getWriter().write("<HTML><BODY>");
		response.getWriter().write("A new Selector has been defined as follows:</br>" + selectStr);
		response.getWriter().write("</BODY></HTML>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);

	}

}
