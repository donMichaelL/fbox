function fnInitComponents () {
	fnInitButtons();
	fnInitCombos();
	fnInitCharts();
};

function fnInitButtons () {
	$("input[type=submit], input[type=reset], button").button();
		
	// Load values dynamically
	$("#plot-button").click(function(event) {
		var procId = $("#procedures-combo").find('option:selected').text();
		var phenId = $("#phenomena-combo").find('option:selected').text();
		var limit = 1000;
		
		fnPlotStreamValues (procId, phenId, limit);
		// fnPlotStaticValues();
	});
	
	// $("#clear-button").prop('disabled', true);
	
	$("#clear-button").click(function(event) {
		$("#chart-div").highcharts().destroy();
	});
	
	
	// Deployment status dialog    		    
    $("#load-data-dialog").dialog({
	    autoOpen: false,
	    show: "blind",
	    modal: true
	    //buttons: {
	    //	Ok: function() {
	    //		$(this).dialog("close");
	    //	}
	   // }
	    // hide: "explode"
	});
};

function fnInitCombos() {
	
	fnInitProceduresCombo();
	
	$("#procedures-combo").change(function() {
		$("#phenomena-combo").empty();
		fnInitPhenomenaCombo(this.value);
	});	
}

function fnInitProceduresCombo() {
	$.ajax({
	    type: "GET",
	    url: "api/procedureMetadata",
	    dataType: "json",
	    success: function(response) {
	    	var combo = document.getElementById("procedures-combo");
	    	
	    	// parse JSON response
	    	for(var i=0; i<response.length; i++) {
	    		
	    		 var option = document.createElement("option");
	    		 option.text = response[i].procedureId;
	    		 option.value = response[i].procedureId;
	    		 
	    		 try {
	    			 combo.add(option, null); //Standard 
	    		 } catch(error) {
	    			 combo.add(option); // IE only
	    		 }
	    	}
		},
		error: function(response) {
			// $("#undeployment-dialog").text("Undeployment error!");
    	},
    	cache: false
	});
}

function fnInitPhenomenaCombo(procId) {
	$.ajax({
	    type: "GET",
	    url: "api/procedureMetadata",
	    dataType: "json",
	    success: function(response) {
	    	
	    	// parse JSON response
	    	for(var i=response.length-1; i>=0; i--) {
	    		
	    		if(response[i].procedureId == procId) {
	    			//alert(procId + " - " + response[i].phenomena);
	    			for(var j=0; j<response[i].phenomena.length; j++) {
	    				$('#phenomena-combo').append(new Option(response[i].phenomena[j], response[i].phenomena[j]));
	    			}
	    			break;
	    			
	    		}
	    	}
		},
		error: function(response) {
			// $("#undeployment-dialog").text("Undeployment error!");
    	},
    	cache: false
	});
}


function fnInitCharts () {
	Highcharts.setOptions({
	    global: {
	        useUTC: false
	    }
	});
};


function fnPlotStreamValues (procId, phenId, limit) {
	$.ajax({
	    type: "GET",
	    url: "api/observations",
	    dataType: "json",
	    data: {
	    	"procId": procId,
	    	"phenId": phenId,
	    	"limit": limit
	    },
	    success: function(response) {
	    	
	    	// parse JSON response
	    	var values = [];
	    	for(var i=response.length-1; i>=0; i--) {
	    		values.push([(new Date(response[i].timeStamp)).getTime(), parseFloat(response[i].value)]);
	    	}
	    	
	    	if(values.length == 0) {
	    		 $("#load-data-dialog").dialog("open");
	    		 $("#load-data-dialog-feedback").text("No Data Available!");
	    		// $("#rawdata-textarea").val("No Data Available!");
	    	} else {
		    	// alert("Stringify: " + JSON.stringify(values));
		    	// Create the chart
		    	$("#chart-div").highcharts('StockChart', {
		    	    chart: {
		    	    	 type: 'line'
		    	    },
		    	    rangeSelector: {
		    	    	buttons: [{
		    				count: 1,
		    				type: 'day',
		    				text: '1D'
		    			}, {
		    				count: 1,
		    				type: 'week',
		    				text: '1W'
		    			}, {
		    				count: 1,
		    				type: 'month',
		    				text: '1M'
		    			}, {
		    				type: 'all',
		    				text: 'All'
		    			}],
		    			
		    	    	inputEnabled: true,
		    	        selected: 3
		    	    },
		    	    title: {
		    	        text: procId
		    	    },
		    	    subtitle: {
		                text: phenId
		            },
		            xAxis: {
		            	type: 'datetime',
		            	ordinal: false
		            },
		    	    series:  [{
		    	    	name: procId,
		    	    	data: values         
		    	    }],
		    	    exporting: {
		                buttons: { 
		                    exportButton: {
		                        enabled:false
		                    },
		                    printButton: {
		                        enabled:false
		                    }
		                }
		            },
		    	    credits: {
		                enabled: false // Remove credits (Highcharts.com)
		            }
		    	}); // End of highcharts
	    	}
	    },
		error: function(response) {
			$("#load-data-dialog").dialog("open");
   		 	$("#load-data-dialog-feedback").text("Error Loading Data!");
    	},
    	cache: false
	});  
}