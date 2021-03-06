package org.sos.ds.ui.table.model;

import javax.servlet.http.HttpServletRequest;

public class DataTablesParamUtility {
	public static DataTablesParamModel getParam(HttpServletRequest request)
	{
		if(request.getParameter("sEcho")!=null && request.getParameter("sEcho")!= "")
		{
			DataTablesParamModel param = new DataTablesParamModel();
			param.sEcho = request.getParameter("sEcho");
			param.sSearch = request.getParameter("sSearch");
			param.sColumns = request.getParameter("sColumns");
			param.iDisplayStart = Integer.parseInt( request.getParameter("iDisplayStart") );
			param.iDisplayLength = Integer.parseInt( request.getParameter("iDisplayLength") );
			param.iColumns = Integer.parseInt( request.getParameter("iColumns") );
			param.iSortingCols = Integer.parseInt( request.getParameter("iSortingCols") );
			param.iSortColumnIndex = Integer.parseInt(request.getParameter("iSortCol_0"));
			param.sSortDirection = request.getParameter("sSortDir_0");
			return param;
		}else
			return null;
	}

}
