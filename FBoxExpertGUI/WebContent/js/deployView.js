var scriptEditor;

function fnDeployViewInitComponents() {
	
	scriptEditor = CodeMirror.fromTextArea(document.getElementById("script-text-area"), {
         mode: "xml",
		 lineNumbers: true,
		 lineWrapping: true,
		 tabMode: "indent",
		 value: ""
    });
	
	scriptEditor.setSize(null, 500); // Width , Height
	 
	$("input[type=submit], input[type=reset], button").button();
	
	// Deployment status dialog    		    
    $("#deployment-dialog").dialog({
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
    
    // ajaxSend => Show spinner
    // ajaxStop | ajaxError => Hide spinner
    
    $("#deployment-loading-spinner").bind("ajaxSend", function() {
				$(this).show();
		}).bind("ajaxStop", function() {					
			    $(this).hide();		
		}).bind("ajaxError", function() {
			    $(this).hide();
		});
		
}

function fnDeployViewEnableEventListeners() {
	
	 // Load button
    $("#load-button").click(function(event) {	
	   // var scriptToSend = $("#script-text-area").val();

	//   $("#deployment-dialog").dialog("open");
	   
	   var data = new FormData();
	   jQuery.each($('#browse')[0].files, function(i, file) {
		   data.append('file-'+i, file);
	   });
	   
    	$.ajax({
		    type: "POST",
		    url: "UploadServlet",
		    dataType: "text",
		    data: data,
		    cache: false,
		    contentType: false,
		    processData: false,
		    success: function(response) {
		    	 // $("#script-text-area").val(response);
		    	scriptEditor.setValue(response);
    		},
    		error: function(response) {
    			alert("Loading error!");
        	}
		});  		    	
    });
    
    
	// Load script 1 button
    $("#load-template1-button").click(function(event) {
	    // Read XML file...
	    $.ajax({
		    type: "GET",
		    url: "xml/Template_Basic.xml",
		    dataType: "text",
		    success: function(xml) {
    		    // $("#script-text-area").val(xml);
    		    scriptEditor.setValue(xml);
    		    // $("#script1-label").val("Flooding Spots!");
    		},
    		error: function(xhr) {
        		alert("Error loading script 1.");
        	}
		
		});
    });

    /*
    // Load script 2 button
    $("#load-template2-button").click(function(event) {
	    // Read XML file...
	    $.ajax({
		    type: "GET",
		    url: "xml/Template_CAP_Spatial.xml",
		    dataType: "text",
		    success: function(xml) {
    		    // alert("Script 2 loading...");
    		    $("#script-text-area").val(xml);
    		},
    		error: function(xhr) {
        		alert("Error loading script 2.");
        	}
		
		});
    });

    // Load script 3 button
    $("#load-template3-button").click(function(event) {
	    // Read XML file...
	    $.ajax({
		    type: "GET",
		    url: "xml/Template_Email_Id.xml",
		    dataType: "text",
		    success: function(xml) {
    		    // alert("Script 3 loading...");
    		    $("#script-text-area").val(xml);
    		},
    		error: function(xhr) {
        		alert("Error loading script 3.");
        	}
		
		});
    });
     */
    /*
    // Load script 4 button
    $("#load-template4-button").click(function(event) {
	    // Read XML file...
	    $.ajax({
		    type: "GET",
		    url: "xml/test/Simple_Spatial.xml",
		    dataType: "text",
		    success: function(xml) {
    		    // alert("Script 4 loading...");
    		    $("#script-text-area").val(xml);
    		},
    		error: function(xhr) {
        		alert("Error loading script 4.");
        	}
		
		});
    });
    */
    
    /*
    // Load script 5 button
    $("#load-template5-button").click(function(event) {
	    // Read XML file...
	    $.ajax({
		    type: "GET",
		    url: "xml/EUAB_earthquake.xml",
		    dataType: "text",
		    success: function(xml) {
    		    // alert("Script 4 loading...");
    		    $("#script-text-area").val(xml);
    		},
    		error: function(xhr) {
        		alert("Error loading script 5.");
        	}
		
		});
    });*/
    
    /*
    // Load script 6 button
    $("#load-template6-button").click(function(event) {
	    // Read XML file...
	    $.ajax({
		    type: "GET",
		    url: "xml/trainings/Trainings_Athens_Fire.xml",
		    dataType: "text",
		    success: function(xml) {
    		    // alert("Script 4 loading...");
    		    $("#script-text-area").val(xml);
    		},
    		error: function(xhr) {
        		alert("Error loading script 5.");
        	}
		
		});
    }); */
    
    /*
    // Load script 7 button
    $("#load-template7-button").click(function(event) {
	    // Read XML file...
	    $.ajax({
		    type: "GET",
		    url: "xml/trainings/Trainings_Salzburg_Flood.xml",
		    dataType: "text",
		    success: function(xml) {
    		    // $("#script-text-area").val(xml);
		    	scriptEditor.setValue(xml);
    		},
    		error: function(xhr) {
        		alert("Error loading script 7.");
        	}
		
		});
    });
    */
    
    $("#load-template8-button").click(function(event) {
	    // Read XML file...
	    $.ajax({
		    type: "GET",
		    url: "xml/DISFER/DISFER_Athens_Fire.xml",
		    dataType: "text",
		    success: function(xml) {
    		    // $("#script-text-area").val(xml);
		    	scriptEditor.setValue(xml);
    		},
    		error: function(xhr) {
        		alert("Error loading script 7.");
        	}
		
		});
    });
    
    
    // Deployment button
    $("#deploy-button").click(function(event) {	
	    // var scriptToSend = $("#script-text-area").val();
	    var scriptToSend = scriptEditor.getValue();
	    console.log(scriptToSend);
	    /*
	    alert("Deploy");
	   
	    if($("#deployment-dialog").is(':visible'))
	    	alert("true");
	    else
	    	alert("false");
	    */
	    $("#deployment-dialog").dialog("open");
	    $("#deployment-dialog-feedback").text("Deploying fusion script...");
	    
	    if(scriptToSend != '') {
	    	// $("#deployment-dialog-feedback").text("Sending something");
	    	//alert(scriptToSend);

	    	$.ajax({
    		    type: "POST",
    		    url: "/FusionCore/DeployApplication",
    		    // url: "http://localhost:9080/FusionCore/DeployApplication",
    		    // url: "http://satia.di.uoa.gr:8080/FusionCore/DeployApplication",
    		    // url: "http://wand.di.uoa.gr:9080/FusionCore/DeployApplication",
    		    dataType: "text",
    		    data: "source=" + scriptToSend,
    		    success: function(response) {
        		    //alert(response);
        		    var textToDisplay;
        		    
        		    if (response == 0) {
        		    	textToDisplay= "Deployment Successful!";
        		    } else if (response == -2) {
        		    	textToDisplay= "Invalid fusion script!";
        		    } else {
        		    	textToDisplay = "Deployment Failed!";
        		    }
        		    
    		    	// $("#deployment-dialog").text(textToDisplay);
        		    $("#deployment-dialog-feedback").text(textToDisplay);
        		},
        		error: function(response) {
            		$("#deployment-dialog").text("Deployment Error!");
            		//$("#deployment-dialog").dialog({
                    	// title: "Error in deployment!"	
            		//}).dialog('open');
            	}
    		});      	
	    } else {
	    	$("#deployment-dialog").text("No data to send! Please select fusion script!");
		}	 
		    
    	  		    	
    });

    // Clear button
    $("#clear-button").click(function(event) {	
    	// $("#script-text-area").val('');
    	scriptEditor.setValue("");
    	scriptEditor.clearHistory();
    });
}