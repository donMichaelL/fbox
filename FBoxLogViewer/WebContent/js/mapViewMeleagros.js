var MELEAGROS_TEMPERATURE_STR = "Temp-Sensor";
var MELEAGROS_HUMIDITY_STR = "Hum-Sensor";
var MELEAGROS_WEATHER_STATION_STR = "DAVIS";


var map;
var fromProjection;
var toProjection;

/*function init() {
	
	var map;
	var fromProjection;
	var toProjection;

	//initMap();
	// loadMarkers();
}*/

function initMap() {
	
	// Map constructor
	map = new OpenLayers.Map("content-wrapper", {
		displayProjection: new OpenLayers.Projection("EPSG:4326")
	}); // map_container
	
	// Add OSM layer as base layer
	osmLayer =  new OpenLayers.Layer.OSM();
	map.addLayer(osmLayer);
	
	var googleMapsLayer_Hybrid = new OpenLayers.Layer.Google(
			'Google Maps Hybrid',
			{type: google.maps.MapTypeId.HYBRID} 
	);
	
	// Add Google Maps layer
	var googleMapsLayer_Roadmap = new OpenLayers.Layer.Google(
			'Google Maps Roadmap',					
			{type: google.maps.MapTypeId.ROADMAP} // The default
	);

	var googleMapsLayer_Satellite = new OpenLayers.Layer.Google(
			'Google Maps Satellite',
			{type: google.maps.MapTypeId.SATELLITE} 
	);
	
	var googleMapsLayer_Terrain = new OpenLayers.Layer.Google(
			'Google Maps Terrain',
			{type: google.maps.MapTypeId.TERRAIN} 
	);
	map.addLayers([googleMapsLayer_Roadmap, googleMapsLayer_Hybrid, googleMapsLayer_Satellite, googleMapsLayer_Terrain]);
	
	// Add Virtual Earth Layer
	/*var virtualEarth_Shaded = new OpenLayers.Layer.VirtualEarth(
			'Virtual Earth Shaded',
			{type: VEMapStyle.Shaded}
	);*/
	
	/*
	var ktimatologioLayer = new OpenLayers.Layer.WMS(
			'Ktimatologio A.E',
			'http://gis.ktimanet.gr/wms/wmsopen/wmsserver.aspx',
			{
				layers: "baisc",
				reaspect: "false",
				transparent: 'false'
				// srs: "EPSG:4326",
				//isBaseLayer: true,
			},
			{
				'buffer': 0
			}
	);
	
	ktimatologioLayer.isBaseLayer = true;
	ktimatologioLayer.setTileSize(new OpenLayers.Size(512, 512));
	map.addLayer(ktimatologioLayer);*/
	
	fromProjection = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
	// var toProjection   = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection
	toProjection = map.getProjectionObject(); // to Spherical Mercator Projection
	
	// Set center point and zoom
	var centerLonLat = new OpenLayers.LonLat( 23.68922, 38.09692).transform(fromProjection, toProjection);				
	var zoom = 10;
	map.setCenter (centerLonLat, zoom); 
	
	// Layer switcher
	map.addControl(new OpenLayers.Control.LayerSwitcher());
	map.addControl(new OpenLayers.Control.MousePosition());
	
	// map.addControl(new OpenLayers.Control.PanZoomBar());
	// map.addControl(new OpenLayers.Control.Permalink());
	// map.addControl(new OpenLayers.Control.OverviewMap());
	
	// map.zoomToMaxExtent();
}

function loadMarkers() {
	
	// Icons
	var size = new OpenLayers.Size(24,24);
	var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
	
	var defaultIcon = new OpenLayers.Icon('images/pois/information.png', size, offset);
	var campIcon = new OpenLayers.Icon('images/pois/camp.png', size, offset);
	var churchIcon = new OpenLayers.Icon('images/pois/icon-church.png', size, offset);
	var temperatureIcon = new OpenLayers.Icon('images/pois/thermometer.png', size, offset);
	var humidityIcon = new OpenLayers.Icon('images/pois/generic_water_64x64.png', size, offset);
	var weatherStationIcon = new OpenLayers.Icon('images/pois/iconSquareWeatherStation_64x64.png', size, offset);
	
	// Popup type
	var autoSizeFramedCloud = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
		'autoSize': true
	});
	
	// ~~~~~~ Fixed POIs ~~~~~~
	// ~~~~~ Camps ~~~~~ //
	var campLayer = new OpenLayers.Layer.Markers("Camps");
	map.addLayer(campLayer);
	
	var hamogeloLon = "23.77286";
	var hamogeloLat = "37.94773";
	var hamogeloLonLat = new OpenLayers.LonLat(hamogeloLon, hamogeloLat).transform(fromProjection, toProjection);
	var hamogeloMarker = new OpenLayers.Marker(hamogeloLonLat, campIcon);
	var hamogeloPopupClass = autoSizeFramedCloud;
	var hamogeloPopupContentHTML = "<strong>Description: </strong>" + "Xamogelo tou Paidiou" + "<br>";
	hamogeloPopupContentHTML += "<strong>Address: </strong>" + "Karea & Dafnis 2, Kareas, 16122" + "<br>";
	hamogeloPopupContentHTML += "<strong>Phone: </strong>" + "210-7609553" + "<br>";
	hamogeloPopupContentHTML += "<strong>Fax: </strong>" + "210-7661240" + "<br>";
	hamogeloPopupContentHTML += "<strong>Email: </strong>" + "kareas@hamogelo.gr" + "<br>";	
	addMarker(campLayer, hamogeloMarker, hamogeloLonLat, hamogeloPopupClass, hamogeloPopupContentHTML, true, true );
	
	var parnithaLon = "23.737593";
	var parnithaLat = "38.147936";
	var parnithaLonLat = new OpenLayers.LonLat(parnithaLon, parnithaLat).transform(fromProjection, toProjection);
	var parnithaMarker = new OpenLayers.Marker(parnithaLonLat, campIcon.clone());
	var parnithaPopupClass = autoSizeFramedCloud;
	var parnithaPopupContentHTML = "<strong>Description: </strong>" + "Paidikes Kataskinoseis Trapezas Elladas" + "<br>";
	parnithaPopupContentHTML += "<strong>Address: </strong>" + "Acharnes - Thesi Metoxi, 13601 " + "<br>";
	parnithaPopupContentHTML += "<strong>Phone: </strong>" + "210-2432775";
	addMarker(campLayer, parnithaMarker, parnithaLonLat, parnithaPopupClass, parnithaPopupContentHTML, true, true );
	
	// ~~~~~ Public Places ~~~~~ //
	// Iera Moni Kaisarianis 
	var publicBuildingsLayer = new OpenLayers.Layer.Markers("Public Places");
	map.addLayer(publicBuildingsLayer);
	
	var imkLon = "23.798216";
	var imkLat = "37.960635";
	var imkLonLat = new OpenLayers.LonLat(imkLon, imkLat).transform(fromProjection, toProjection);
	var imkMarker = new OpenLayers.Marker(imkLonLat, churchIcon);
	var imkPopupClass = autoSizeFramedCloud;
	var imkPopupContentHTML = "<strong>Description: </strong>" + "Iera Moni Kaisarianis" + "<br>";
	imkPopupContentHTML += "<strong>Address: </strong>" + "Kaisariani, 16122" + "<br>";
	imkPopupContentHTML += "<strong>Phone: </strong>" + "210-7236619";
	addMarker(publicBuildingsLayer, imkMarker, imkLonLat, imkPopupClass, imkPopupContentHTML, true, true );
	
	// Iera Moni Karea
	var imKareaLon = "23.778327";
	var imKareaLat = "37.942040";
	var imKareaLonLat = new OpenLayers.LonLat(imKareaLon, imKareaLat).transform(fromProjection, toProjection);
	var imKareaMarker = new OpenLayers.Marker(imKareaLonLat, churchIcon.clone());
	var imKareaPopupClass = autoSizeFramedCloud;
	var imKareaPopupContentHTML = "<strong>Description: </strong>" + "Iera Moni Timiou Prodromou Karea" + "<br>";
	imKareaPopupContentHTML += "<strong>Address: </strong>" + "Korwpi, 16342";
	addMarker(publicBuildingsLayer, imKareaMarker, imKareaLonLat, imKareaPopupClass, imKareaPopupContentHTML, true, true );
	
	// Iera Moni Agiou Asteriou - Taksiarxwn
	var imatLon = "23.810860";
	var imatLat = "37.969125";
	var imatLonLat = new OpenLayers.LonLat(imatLon, imatLat).transform(fromProjection, toProjection);
	var imatMarker = new OpenLayers.Marker(imatLonLat, churchIcon.clone());
	var imatPopupClass = autoSizeFramedCloud;
	var imatPopupContentHTML = "<strong>Description: </strong>" + "Iera Moni Asteriou - Taksiarxwn" + "<br>";
	imatPopupContentHTML += "<strong>Address: </strong>" + "Byrwnas, 16233";
	addMarker(publicBuildingsLayer, imatMarker, imatLonLat, imatPopupClass, imatPopupContentHTML, true, true );
	
	// ~~~~~~ Dynamic POIs ~~~~~~
	var temperatureLayer = new OpenLayers.Layer.Markers("Temperature Sensors");
	map.addLayer(temperatureLayer);
	
	var humidityLayer = new OpenLayers.Layer.Markers("Humidity Sensors");
	map.addLayer(humidityLayer);
	
	var weatherStationLayer = new OpenLayers.Layer.Markers("Weather Stations");
	map.addLayer(weatherStationLayer);
	
	var defaultLayer = new OpenLayers.Layer.Markers("Uncategorized");
	map.addLayer(defaultLayer);
	
	
	/*
	var hospitalLon = "23.74071";  // x
	var hospitalLat = "38.15094";   // y
	var hospitalLonLat = new OpenLayers.LonLat(hospitalLon, hospitalLat).transform(fromProjection, toProjection);
	var hospitalMarker = new OpenLayers.Marker(hospitalLonLat, buildingIcon);
	var hospitalPopupClass = autoSizeFramedCloud;
	var hospitalPopupContentHTML = "<strong>Description: </strong>" + "Klinikum Pirna" + "<br>";
	hospitalPopupContentHTML += "<strong>Address: </strong>" + "Struppener Strabe 13, 01796 Pirna, Germany" + "<br>";
	hospitalPopupContentHTML += "<strong>Phone: </strong>" + "03501 71180";
	
	addMarker(publicBuildingsLayer, hospitalMarker, hospitalLonLat, hospitalPopupClass, hospitalPopupContentHTML, true, true );
	*/
	


	// Get data through AJAX call
	$.getJSON('ProcedureMetadataServlet', function(response) {
		var sensorStreams = response;
		// alert("#SensorStreams: " + sensorStreams.length);
		
		var isDefaultIconSet = false;
		var isTemperatureIconSet = false;
		var isHumidityIconSet = false;
		var isWeatherStationIconSet = false;
		
		for(var i=0; i<sensorStreams.length; i++) {
			
			var longtitude = sensorStreams[i].longtitude; // x
			var latitude = sensorStreams[i].latitude;     // y
			var lonLat = new OpenLayers.LonLat(longtitude, latitude).transform(fromProjection, toProjection);
			
			var popupClass = autoSizeFramedCloud;
			var popupContentHTML = "";
			
			popupContentHTML = "<strong>id: </strong>" + sensorStreams[i].procedureId + "<br>";
			
			// Phenomena
			if(sensorStreams[i].phenomena.length == 1) {
				popupContentHTML += "<strong>phenomenon: </strong>" + sensorStreams[i].phenomena[0] + "<br>";
			} else {
				for(var j=0; j<sensorStreams[i].phenomena.length; j++) {
					popupContentHTML += "<strong>phenomenon_" + (j+1) + ": </strong>" + sensorStreams[i].phenomena[j] + "<br>";
				}
			}
			
			
			popupContentHTML += "<strong>isActive: </strong>" + sensorStreams[i].active.toString() + "<br>";
			popupContentHTML += "<strong>isMobile: </strong>" + sensorStreams[i].mobile.toString();
			
			var layer;
			
			var procedureId = sensorStreams[i].procedureId;
			
			// Check if it is a gauging station
			if(procedureId.indexOf(MELEAGROS_TEMPERATURE_STR) != -1) {
				if(isTemperatureIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, temperatureIcon);
					isTemperatureIconSet = true;
				}
				else {
					sensorMarker = new OpenLayers.Marker(lonLat, temperatureIcon.clone());
				}
				
				layer = temperatureLayer;
			} else if(procedureId.indexOf(MELEAGROS_HUMIDITY_STR) != -1) {
				if(isHumidityIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, humidityIcon);
					isHumidityIconSet = true;
				}
				else {
					sensorMarker = new OpenLayers.Marker(lonLat, humidityIcon.clone());
				}
				
				layer = humidityLayer;
			} else if(procedureId.indexOf(MELEAGROS_WEATHER_STATION_STR) != -1) {
				if(isWeatherStationIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, weatherStationIcon);
					isWeatherStationIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, weatherStationIcon.clone());
				} 
				
				layer = weatherStationLayer;
			} else {
				if(isDefaultIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon);
					isDefaultIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon.clone());
				}
				
				layer = defaultLayer;
			}
				
			// alert("Loading adding...");
			addMarker(layer, sensorMarker, lonLat, popupClass, popupContentHTML, true, true );
			// alert("Loading finished!");
		}

	});
}

/**
 * Function: addMarker
 * Add a new marker to the markers layer given the following lonlat, 
 *     popupClass, and popup contents HTML. Also allow specifying 
 *     whether or not to give the popup a close box.
 * 
 * Parameters:
 * lonLat - {<OpenLayers.LonLat>} Where to place the marker
 * popupClass - {<OpenLayers.Class>} Which class of popup to bring up 
 *     when the marker is clicked.
 * popupContentHTML - {String} What to put in the popup
 * closeBox - {Boolean} Should popup have a close box?
 * overflow - {Boolean} Let the popup overflow scrollbars?
 */
function addMarker(poisLayer, poiMarker, lonLat, popupClass, popupContentHTML, closeBox, overflow) {
	var feature = new OpenLayers.Feature(poisLayer, lonLat); 
	feature.closeBox = closeBox;
	feature.popupClass = popupClass;
	feature.data.popupContentHTML = popupContentHTML;
	feature.data.overflow = (overflow) ? "auto" : "hidden";
	
	// var marker = feature.createMarker();
	
	var markerClick = function (evt) {
		if (this.popup == null) {
			this.popup = this.createPopup(this.closeBox);
			map.addPopup(this.popup);
			this.popup.show();
		} else {
			this.popup.toggle();
		}
		currentPopup = this.popup;
		OpenLayers.Event.stop(evt);
	};
	
	poiMarker.events.register("mousedown", feature, markerClick);
	poisLayer.addMarker(poiMarker);
}

function alert_sensorMarker(sid, longtitude, latitude) {
	alert(sid + " , " + longtitude + " , " + latitude);
}


function loadVectorLayers() {
	/*var boxExtents = [
	           [-75, 41, -71, 44]
	]; */
	
	// alert("Transforming...");
	var proj_4326 = new OpenLayers.Projection('EPSG:4326');
	var proj_900913 = new OpenLayers.Projection('EPSG:900913');
	
	var point_to_transform = new OpenLayers.LonLat(-79, 42);
	point_to_transform.transform(proj_4326, proj_900913);
	
	console.log(point_to_transform);
	console.log("---> " + point_to_transform.lon, "---> " + point_to_transform.lat);
	
	alert("Done");
	
	
	var vectorLayer = new OpenLayers.Layer.Vector( "Fire Events" );
	map.addControl(new OpenLayers.Control.EditingToolbar(vectorLayer));
	map.addLayer(vectorLayer);
	
	
	
	var point1 = new OpenLayers.Geometry.Point(13.4, 51.02);
	var featurePoint1 = new OpenLayers.Feature.Vector(
								point1, 
								{
									'location': "Athens",
									'description': "We are on fire!"
								}
	);
	// vectorLayer.addFeatures([featurePoint1]);
	
	
	alert("Creating Polygon...");
	// Create geometry
	var points = new Array(); 
	points.push(new OpenLayers.Geometry.Point(13.95307, 40.95860));
	points.push(new OpenLayers.Geometry.Point(16.1, 52.1));
	points.push(new OpenLayers.Geometry.Point(20.5, 52.9));
	
	alert(points);
	
	// add it to the vector layer
	var linearRing = new OpenLayers.Geometry.LinearRing(points);
	var featurePolygon =  new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]));
	
	alert("Adding Polygon...");
	vectorLayer.addFeatures([featurePoint1, featurePolygon]);
	
	alert("Polygon added...");
	
	var selectFeatureControl = new OpenLayers.Control.SelectFeature(
									vectorLayer,
									{
										multiple: false,
										toggle: true,
										multipleKey: 'shiftKey'
									}
								);
	map.addControl(selectFeatureControl);
	
	
	/*
	
	var boxesLayer = new OpenLayers.Layer.Vector("Boxes");
	for(var i=0; i<boxExtents.length; i++) {
		ext = boxExtents[i];
		bounds = OpenLayers.Bounds.fromArray(ext);
		
		box = new OpenLayers.Feature.Vector(bounds.toGeometry());
		boxesLayer.addFeatures(box);
		// box = new OpenLayers.Marker.Box(bounds);
		// box.events.register("click", box, function (e) {
		//	this.setBorder("yellow");
	}
		
	// boxesLayer.addMarker(box);
	map.addLayer(boxesLayer);
	var sf = new OpenLayers.Control.SelectFeature(boxesLayer);
	map.addControl(sf);
	sf.activate();
	map.zoomToMaxExtent();
	*/
	
	
	
}