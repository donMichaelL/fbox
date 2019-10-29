package org.fbox.network.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.network.IDataProvider;

/**
 * Servlet implementation class HelloWorldTest
 */
@WebServlet("/ViewMessagesToQueuePerStreamer")
public class ViewMessagesToQueuePerStreamer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	IDataProvider dataProvider;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewMessagesToQueuePerStreamer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("<HTML><BODY>");
		HashMap<String, Integer> messages=dataProvider.getMessagesPerDataStream();
		for (Entry<String, Integer> message : messages.entrySet()) {
			response.getWriter().write(message.getKey() + "--->"+ message.getValue() +"</br>");
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
