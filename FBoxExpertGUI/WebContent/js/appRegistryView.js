// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ // 
// ~~~~~~~~~~ Global variables ~~~~~~~~~ //
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ // 
var deploymentsTables;
var giRedraw = false;
var giCount = 1;

var refreshPeriod = 20 * 1000; // in milliseconds => 20s

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
//~~~~~~~~~~ FUNCTIONS ~~~~~~~~~~ //
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

function fnAppRegistryViewInitComponents() {
	
	$("input[type=submit], input[type=reset], button").button();
	
	fnInitDeploymentsTable();
	
	// Undeployment  status dialog    		    
    $("#undeployment-dialog").dialog({
	    autoOpen: false,
	    show: "blind",
	    modal: true
	});
	
    
    $("#undeployment-loading-spinner").bind("ajaxSend", function() {
			$(this).show();
	}).bind("ajaxStop", function() {					
		    $(this).hide();		
	}).bind("ajaxError", function() {
		    $(this).hide();
	});
}


function fnInitDeploymentsTable () {
	deploymentsTables = $('#deploy-registry-table').dataTable({
		 "sPaginationType": "full_numbers",
		 "bFilter": true,
		 "bJQueryUI": true,
		 "iDisplayLength": 25,
		 
		 "bProcessing": true,
		 "bServerSide": true,
		 "sAjaxSource": "/FusionCore/ViewApplicationRegistry",
		 // "sAjaxSource": "http://localhost:9080/FusionCore/ViewApplicationRegistry",
		 // "sAjaxSource": "http://satia.di.uoa.gr:8080/FusionCore/ViewApplicationRegistry",
		 // "sAjaxSource": "http://wand.di.uoa.gr:9080/FusionCore/ViewApplicationRegistry",
		 "sAjaxDataProp" : "data",
		 "aoColumns": [
			{ "mData": "deploymentTime" },
			{ "mData": "id" },
			{ "mData": "status" },
			{ "mData": "deployedModuleName" }
			
		 ],
		 "sDom": '<"H"lfr>t<"F"ip>T',	// T: the table tools (buttons), H: header, F: footer
		 								// t: the data table (!!!) 
		 								// l: Length changing (Show...entries), f: filtering input, r: processing, i: information, p: pagination
		 "oTableTools": {
		 	"sSwfPath": "libs/DataTables-1.9.3/extras/TableTools/media/swf/copy_csv_xls_pdf.swf",
		 	"aButtons": [
					"copy", 
					"print",
					{
						"sExtends": "collection",
						"sButtonText": "Save",
						"aButtons": [
										"csv",
										{
											"sExtends": "pdf",
											"sTitle": "SFE_Deployments_Report", // Title of file
											"sPdfMessage" : "Deployments Summary Info",			// Message inside pdf
											"sPdfOrientation": "landscape"
										}
						]
					}  
		 	 ],
		 	 "sRowSelect": "single"
		  }
	    });
}

function fnAppRegistryViewEnableEventListeners () {
	
	// Click on "tbody" html element of the table
	// Remove class 'rows_selected' from all rows  
	$("#deploy-registry-table tbody").click(function(event) {
	    
		$(deploymentsTables.fnSettings().aoData).each(function (){
	        $(this.nTr).removeClass('row_selected');
	    });
	    
	    $(event.target.parentNode).addClass('row_selected');
	});

	// Click undeploy button 
	$('#undeploy-button').click( function() {
		
		// Step 1: Get selected rows 
	    var anSelected = fnGetSelected(deploymentsTables);

		// Step 2: Get the data of selected row
		var rowData = deploymentsTables.fnGetData( anSelected[0]);
		var runTimeName = rowData.id; // Get runtime name
		// alert("#rows selected: " + anSelected.length);
	    // alert(anSelected[0]);
		// alert('rowData: ' + rowData);
		
	    // Step 3: Undeploy application (i.e., call servlet, update table)
	    // alert('Undeploying application...' + runTimeName);
	    fnUndeployApplication(runTimeName);
	});

	// $('#add-button').click(fnClickAddRow);

	// $('#iterate-button').click(fnClickIterateDeploymentRows);

	$(window).bind('resize', function () {
		deploymentsTables.fnAdjustColumnSizing();
    } );

}

function fnUndeployApplication(appId) {
	 // alert('Opening...');
	 $("#undeployment-dialog").dialog("open");
	 $("#undeployment-dialog-feedback").text("Undeploying '" + appId + "' fusion script...");
	 
	$.ajax({
	    type: "POST",
	    url: "/FusionCore/UndeployApplication",
	    // url: "http://localhost:9080/FusionCore/UndeployApplication",
	    // url: "http://satia.di.uoa.gr:8080/FusionCore/UndeployApplication",
	    // url: "http://wand.di.uoa.gr:9080/FusionCore/UndeployApplication",
	    dataType: "text",
	    data: "applicationId=" + appId,
	    success: function(response) {
		    // alert('OK ' + response);
		    
		    // ReDraw table. 
		    // fnDeleteRow can be used only on client side
		    // deploymentsTables.fnDeleteRow(anSelected[0]);
		    deploymentsTables.fnDraw(true); 
		    
		    var textToDisplay;
		    
		    if (response == "true")
		    	textToDisplay= "Undeployment Successful!";
		    else
		    	textToDisplay = "Undeployment Failed!";
		    
		    $("#undeployment-dialog-feedback").text(textToDisplay);
		},
		error: function(response) {
			$("#undeployment-dialog").text("Undeployment error!");
    	}
	});  
}

function fnClickIterateDeploymentRows() {
	var tmpTable = $('#deploy-registry-table').dataTable();
	var tmpNodes = $('#deploy-registry-table tbody tr');

	for (var i = 0; i < tmpNodes.length; ++i)
	{
	    var tmpRowData = tmpTable.fnGetData( tmpNodes[i] ).id;
	 
	    //some stuff with the obtained data
	    alert(tmpRowData);
	}
}


function initTimer() {
	alert("Init...");
	setInterval(fnInitDeploymentsTable, 3000);
}

function fnAppRegistryViewRefreshDeployemntsTable() {
	// Redraw the table - Re-filter and resort (if enabled) the table before the draw.
	setInterval('deploymentsTables.fnDraw(true)', refreshPeriod);
}