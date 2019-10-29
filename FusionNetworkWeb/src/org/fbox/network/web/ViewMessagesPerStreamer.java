package org.fbox.network.web;

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

import org.fbox.common.data.IDataElement;
import org.fbox.network.INetworkModule;


/**
 * Servlet implementation class HelloWorldTest
 */
@WebServlet("/ViewMessagesPerStreamer")
public class ViewMessagesPerStreamer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	INetworkModule network;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewMessagesPerStreamer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id=request.getParameter("id");
		response.getWriter().write("<HTML><BODY>");
		if (id!=null) {
			ArrayList<IDataElement> messages=network.getMessagesPerDataStream().get(id);
			if (messages!=null) {
				for (int i=0; i<messages.size();i++) {
					response.getWriter().write(i + ")"+ messages.get(i) +"</br>");
				}
			} else
				response.getWriter().write("No datastreamer with id="+id+" exists");
		} else
			response.getWriter().write("No id="+id+" specified");
		
			response.getWriter().write("</BODY></HTML>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);

	}

}
