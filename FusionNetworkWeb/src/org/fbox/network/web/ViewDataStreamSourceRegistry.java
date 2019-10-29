package org.fbox.network.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.data.DataStream;
import org.fbox.network.impl.DataStreamSourceRegistry;

@WebServlet("/ViewDataStreamSourceRegistry")
public class ViewDataStreamSourceRegistry extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	@EJB
	DataStreamSourceRegistry dataStreamSourceRegistry; 
	
//    public ViewDataStreamSourceRegistry() {
//        super();
//   }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.write("Called!");
		
		Set<DataStream> dataStreams = dataStreamSourceRegistry.getDataStreams();
		if(dataStreams.size() > 0) {
			out.write(dataStreams.toString());
		}
			
	}

}
