
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ // 
//~~~~~~~~~~ Global variables ~~~~~~~~~ //
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ // 
var valuesTable;
var refreshPeriod = 20 * 1000; // In milli-seconds => 20s

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
//~~~~~~~~~~ FUNCTIONS ~~~~~~~~~~ //
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
function fnLogViewInitComponents() {
	fnInitLastValuesTable();
}

function fnInitLastValuesTable() {
	
	valuesTable = $('#sensors-values-table').dataTable({
		"sPaginationType": "full_numbers",
		"bFilter": true,
		"bJQueryUI": true,
		"iDisplayLength": 25,
		"sDom": '<"H"lfr>t<"F"ip>T',	// T: the table tools (buttons), H: header, F: footer
										// t: the data table (!!!) 
										// l: Length changing (Show...entries), f: filtering input, r: processing, i: information, p: pagination
		
		// "sDom": 'T<"clear">lfrtip<"clear spacer">T',  
		// "sDom": 'T<"clear">lfrtip',
		// "sDom": 'lfTrtip',
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
													"sTitle": "Log Viewer - Input Streams", // Title of file
													"sPdfMessage" : "Summary Info",			// Message inside pdf
													"sPdfOrientation": "landscape"
												}
								]
							}  
			],
			"sRowSelect": "single"
		},
		"bProcessing": true,
		"bServerSide": true,
		"sAjaxSource": "api/lastObservations",
		"sAjaxDataProp" : "data",
		"aoColumns": [
			{ "mData": "timeStamp" },
			{ "mData": "procedureId" },
			{ "mData": "phenomenonId" },
			{ "mData": "value" }
			
		],
		"aaSorting": [[0, "asc"]] // Predefined sorting
	}); // End of table.
	
	/*
	// $('.dataTable').dataTable(); // For multiples dataTables
	poisTable = $('#sensor-pois-table').dataTable({
		// "aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]], // Show entries - "All" needs special manipulation at server side.
		// "sScorollX": "500px", 
        // "sScrollY": "200px",
		// "bScrollInfinite": true 	// This disables pagination  
	}); // End of table 
	*/
	
}

function fnLogViewRefreshValuesTable() {
	// Redraw the table - Re-filter and resort (if enabled) the table before the draw.
	var pollingTimer = setInterval('valuesTable.fnDraw(true)', refreshPeriod);
}