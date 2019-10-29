package fbox.fusion.core.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fbox.fusion.core.http.datatables.DataTablesParamUtility;
import fbox.fusion.core.http.datatables.JQueryDataTableParamModel;
import fbox.fusion.core.http.datatables.DataTablesViewApplicationRegistryModel;

import org.fbox.common.application.configuration.ApplicationInfo;
import org.fbox.common.registry.IRegistry;
import org.fbox.core.registry.ApplicationInfoRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class HelloWorldTest
 */
@WebServlet("/ViewApplicationRegistry")
public class ViewApplicationRegistry extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	IRegistry<ApplicationInfo> registry;
	
	private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ssZ";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewApplicationRegistry() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// response.getWriter().write("<HTML><BODY>");
		
		System.out.println("[VAR] ViewApplicationRegistry called...");
		
		HashMap<String, ApplicationInfo> appsMap = registry.getRegistryEntries();
		
		/*
		System.out.println(request.getQueryString());
		System.out.println(request.getRequestURI());
		Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()) {
			System.out.println(request.getParameterValues(params.nextElement()));
		}
		*/
		/*
		System.out.println(applications);
		for (Entry<String, ApplicationInfo> app : applications.entrySet()) {
			response.getWriter().write(app.getKey()+ " ---------> "+ app.getValue().getStatus());
			response.getWriter().write("}</br>");
		}
		response.getWriter().write("</BODY></HTML>");
		*/
		
		// Datatables code
		// System.out.println("[VAR] Starting dataTables code...");
		JQueryDataTableParamModel param = DataTablesParamUtility.getParam(request);
		int sEcho = Integer.parseInt(param.sEcho);
		int iTotalRecords;
		int iTotalDisplayRecords;
		
		// System.out.println("[VAR] Before converting to list...");
		List<ApplicationInfo> appsList = new LinkedList<ApplicationInfo>(appsMap.values()); 
		iTotalRecords = appsList.size();	
		
		List<ApplicationInfo> appsToSentList = new LinkedList<ApplicationInfo>();
		
		// System.out.println("[VAR] Before searching...");
		
		// ~~~~~ Searching capabilities ~~~~~ //
		if(param.sSearch != null && param.sSearch != "") {
			for(ApplicationInfo appInfo : appsList) {
				if( appInfo.getId().toLowerCase().contains(param.sSearch.toLowerCase()) ||
					appInfo.getStatus().toString().toLowerCase().contains(param.sSearch.toLowerCase())) {
					
					// add ... that matches given search criterion
					appsToSentList.add(appInfo);
				}
			}
		} else {
			appsToSentList = appsList;
		}
		
		iTotalDisplayRecords = appsToSentList.size();
		
		// ~~~~~ Filtering Capabilities ~~~~~ //
		final int sortColumnIndex = param.iSortColumnIndex;
		final int sortDirection = param.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(appsToSentList, new Comparator<ApplicationInfo>() {
			@Override
			public int compare(ApplicationInfo appInfo1, ApplicationInfo appInfo2) {
				switch(sortColumnIndex) {
					case 1:
						return appInfo1.getId().compareTo(appInfo2.getId()) * sortDirection;
					case 2:
						return appInfo1.getStatus().compareTo(appInfo2.getStatus()) * sortDirection;
				}
				
				return 0;
			}
			
		});
		
		// ~~~~~ Paging Capabilities ~~~~~ //
		if(appsToSentList.size() < param.iDisplayStart + param.iDisplayLength) {
			appsToSentList = appsToSentList.subList(param.iDisplayStart, appsToSentList.size());
		} else {
			appsToSentList = appsToSentList.subList(param.iDisplayStart, param.iDisplayStart + param.iDisplayLength);
		}
		
		// System.out.println("[VAR] Before making response...");
		// ~~~~~ Response ~~~~~ //
		DataTablesViewApplicationRegistryModel dtAppRegModel = new DataTablesViewApplicationRegistryModel();
		dtAppRegModel.setsEcho(sEcho);
		dtAppRegModel.setiTotalRecords(iTotalRecords);
		dtAppRegModel.setiTotalDisplayRecords(iTotalDisplayRecords);
		dtAppRegModel.setaaData(appsToSentList);

		// System.out.println("[VAR] Before formatting response...");
		// Format response
		ObjectMapper mapper = new ObjectMapper(); 
		mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT)); 
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dtAppRegModel);
		
		//System.out.println(json);
		
		// Send response
		PrintWriter out = response.getWriter();
		out.write(json);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);

	}

}
