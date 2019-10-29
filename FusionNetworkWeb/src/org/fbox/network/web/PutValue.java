package org.fbox.network.web;


import java.io.IOException;
import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.data.IDataElement;
import org.fbox.common.network.IDataProvider;
import org.fbox.common.network.data.MeasuredDataElement;



/**
 * Servlet implementation class HelloWorldTest
 */
@WebServlet("/PutValue")
public class PutValue extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@EJB
	IDataProvider mprovider;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PutValue() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id=request.getParameter("id");
		IDataElement data=new MeasuredDataElement(id);
		data.setTimestamp(new Date());
		
		data.setValue( Double.parseDouble(request.getParameter("value")));
		mprovider.addDataInQueue(id, data);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
