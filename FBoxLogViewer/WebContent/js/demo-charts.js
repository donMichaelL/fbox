/* 
data: [22.2, 22.3, 23.0, 26.2, 29.6, 27.1, 27.3, 26.5, 26.1, 26.0, 26.3, 27.3]
data: [[0, 22.2], [1, 22.3], [2, 23.0], [3, 26.2], [4, 29.6], [5, 27.1], [6, 27.3], [7, 26.5], [8, 26.1], [9, 26.0], [10, 26.3], [11, 27.3]]

// in JavaScript, months start at 0 for January, 1 for February etc.
// Date.UTC(year,month,day,hours,minutes,seconds,millisec) 
data: [[Date.UTC(2013,8,30,10,20), 22.2], [Date.UTC(2013,8,30,10,30), 22.3], [Date.UTC(2013,8,30,10,40), 23.0], [Date.UTC(2013,8,30,10,50), 26.2], 
	   [Date.UTC(2013,8,30,11,20), 29.6], [Date.UTC(2013,8,30,11,25), 27.1], [Date.UTC(2013,8,30,12,20), 27.3], [Date.UTC(2013,8,30,12,50), 26.5], 
	   [Date.UTC(2013,8,30,13,20), 26.1], [Date.UTC(2013,8,30,14,20), 26.0], [Date.UTC(2013,8,30,15,20), 26.3], [Date.UTC(2013,9,1,10,20), 27.3]]

data: [[new Date("2013-09-17 16:14:43+03").getTime(), 22.2],[new Date("2013-09-17 17:14:43+03").getTime(), 27.3]]
*/

/*
 * ########################## EXAMPLES ##################################  
 * */

function fnInitCharts () {
	
	Highcharts.setOptions({
	    global: {
	        useUTC: false
	    }
	});
	
	$('#content-wrapper-1').highcharts({
		chart: {
            type: 'spline',
            marginRight: 130,
        },
        title: {
        	text: "Static Chart",
            x: -20 //center
        },
        subtitle: {
            x: -20 // center
        },
        xAxis: {
        	type: 'datetime'
        },
        yAxis: {
            title: {
               text: null
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: null
        },
        legend: {
            layout: 'horizontal',
            align: 'center',
            verticalAlign: 'bottom',
        },
        series: [{
	        name: 'Sensor data',
	        data: [[0, 22.2], [1, 22.3], [2, 23.0], [3, 26.2], [4, 29.6], [5, 27.1], [6, 27.3], [7, 26.5], [8, 26.1], [9, 26.0], [10, 26.3], [11, 27.3]]
        }],
        credits: {
            enabled: false // Remove credits (Highcharts.com)
        }
    });
}

function fnPlotDynamically (procId, phenId, limit) {
	$.ajax({
	    type: "GET",
	    url: "/FBoxLogViewer/api/observations",
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
	    	
	    	// alert("Array: " + values.length);
	    	// alert("Stringify: " + JSON.stringify(values));
	    	
	    	var chart = $('#chart-wrapper-1').highcharts();
	    	chart.setTitle({text: procId}, {text: phenId});
	    	
	    	// Update dynamically axis title
	    	/*
	    	chart.yAxis[0].update({
                title:{
                	text: phenId
                }
            });*/
	    	
			chart.addSeries({
				name: procId,
				data: values
			});
			
			/*success: function(data) {
		    	chart.series[0].data = data;
			 },*/
		
		},
		error: function(response) {
			// $("#undeployment-dialog").text("Undeployment error!");
    	},
    	cache: false
	});  
}

function fnInitChart3 () {

	$('#chart-wrapper-3').highcharts({
	    chart: {
	        type: 'spline',
	        animation: Highcharts.svg, // don't animate in old IE
	        marginRight: 10,
	        events: {
	            load: function() { // TODO: Servlet CALL.
	
	                // set up the updating of the chart each second
	                var series = this.series[0];
	                
	                // Execute the function every 1000 milliseconds
	                setInterval(function() {
	        			var x = (new Date()).getTime(); // current time
	        			var	y = Math.random();
	        			series.addPoint([x, y], true, true);
	                }, 5000);
	            }
	        }
	    },
	    title: {
	        text: 'Live Sensor Data'
	    },
	    
	    
	    xAxis: {
	        type: 'datetime',
	        tickPixelInterval: 150
	    },
	    
	    yAxis: {
	        title: {
	            text: 'Value'
	        },
	        plotLines: [{
	            value: 0,
	            width: 1,
	            color: '#808080'
	        }]
	    },
	    tooltip: {
	        formatter: function() {
	                return '<b>'+ this.series.name +'</b><br/>'+
	                Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+
	                Highcharts.numberFormat(this.y, 2);
	        }
	    },
	    legend: {
	        enabled: false
	    },
	    exporting: {
	        enabled: false
	    },
	    series: [{
	        name: 'Sensor data',
	        data: (function() {
	            // generate an array of random data
	                var data = [],
	                    time = (new Date()).getTime(),
	                    i;
	
	                for (i = -19; i <= 0; i++) {
	                    data.push({
	                        x: time + i * 1000,
	                        y: Math.random()
	                    });
	                }
	                return data;
	            })()
	        }]
	    });			
};

function fnInitChart4 () {	
	// Create the chart
	$('#chart-wrapper-4').highcharts('StockChart', {
		chart : {
			/* events : {
				load : function() {
					// set up the updating of the chart each second
					var series = this.series[0];
					setInterval(function() {
						var x = (new Date()).getTime(), // current time
						y = Math.round(Math.random() * 100);
						series.addPoint([x, y], true, true);
					}, 5000);
				}
			} */
		},
		
		rangeSelector: {
			buttons: [{
				count: 1,
				type: 'minute',
				text: '1M'
			}, {
				count: 5,
				type: 'minute',
				text: '5M'
			}, {
				type: 'all',
				text: 'All'
			}],
			inputEnabled: false,
			selected: 0
		},
		
		title : {
			text : 'Live random data'
		},
		
		exporting: {
			enabled: false
		},
		
		series : [{
			name : 'Random data',
			data : (function() {
				// generate an array of random data
				var data = [], time = (new Date()).getTime(), i;

				for( i = -999; i <= 0; i++) {
					data.push([
						time + i * 1000,
						Math.round(Math.random() * 100)
					]);
				}
				return data;
			})()
		}]
	});
};


function generateArrayOfRandomData () {
		// generate an array of random data
		var data = []; 
		var time = (new Date()).getTime(), i;

		for( i = -999; i <= 0; i++) {
			data.push([
				time + i * 1000,
				Math.round(Math.random() * 100)
			]);
		}
		return data;
}



function fnPlotValuesFromTxt() {
    $.ajax({
	    type: "GET",
	    url: "data/test.txt",
	    dataType: "text",
	    success: function(txtdata) {
		    // alert(typeof(txtdata));
	    	// alert (txtdata);
		    var chart = $('#content-wrapper-1').highcharts();
		    chart.addSeries({
	              name: "Test Series",
	              data: JSON.parse(txtdata) // Array literal notation is still valid JSON
	            });
		},
		error: function(xhr) {
    		alert("Error loading values from txt.");
    	},
    	cache: false
	});
}

function fnPlotStreamValues (procId, phenId, limit) {
	$.ajax({
	    type: "GET",
	    url: "/FBoxExpertGUI/api/sos/getObservations",
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
	    	
	    	// alert("Array: " + values.length);
	    	// alert("Stringify: " + JSON.stringify(values));
	    	
	    	var chart = $('#chart-wrapper-1').highcharts();
	    	chart.setTitle({text: procId}, {text: phenId});
	    	
	    	// Update dynamically axis title
	    	/*
	    	chart.yAxis[0].update({
                title:{
                	text: phenId
                }
            });*/
	    	
			chart.addSeries({
				name: procId,
				data: values
			});
			
			/*success: function(data) {
		    	chart.series[0].data = data;
			 },*/
		
		},
		error: function(response) {
			// $("#undeployment-dialog").text("Undeployment error!");
    	},
    	cache: false
	});  
}

function fnInitProceduresCombo() {
	$.ajax({
	    type: "GET",
	    url: "/FBoxExpertGUI/api/sos/getProceduresMetadata",
	    dataType: "json",
	    success: function(response) {
	    	var combo = document.getElementById("procedures-combo");
	    	
	    	// parse JSON response
	    	for(var i=response.length-1; i>=0; i--) {
	    		
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
	    url: "/FBoxExpertGUI/api/sos/getProceduresMetadata",
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