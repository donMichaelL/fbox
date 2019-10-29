var map;
var fromProjection;
var toProjection;

function fnMapViewInitComponents() {
	
	// 1rst review - Dresden
	// fnInitMap(13.4788, 51.1473, 9);
	// fnLoadMarkersDresden(); 
	
	// EUAB Meeting - Vienna
	// fnInitMap(16.35241, 48.20115, 10);
	// fnLoadMarkersVienna();  
	
	// June Trainings - Athens
	// fnInitMap(23.75320, 38.12422, 10);
	// fnLoadMarkersAthens();
	
	// Helexpo
	// fnInitMap(23.75320, 38.12422, 10);
	// fnLoadMarkersHelexpo();
	
	// DISFER Demo - Athens
	// fnInitMap(23.79504, 37.95858, 12);
	// fnLoadMarkersAthens();
	
	// IDIRA
	//fnInitMap(23.79504, 37.95858, 11);
	//fnLoadMarkers();
	
	//SWeFS
	fnInitMap(23.79504, 37.95858, 11);
//	fnLoadMarkersSWeFS();
	fnLoadMarkersDresden();
	
	/**
	 * TODO
	 * Δημιουργία μιας fnLoadMarkers function.
	 */
}

function fnInitMap(longtitude, latitude, zoom) {
	
	// Map constructor
	map = new OpenLayers.Map("map-view-map-container", {
		displayProjection: new OpenLayers.Projection("EPSG:4326")
	}); // map_container
	
	// Add OSM layer as base layer
	osmLayer =  new OpenLayers.Layer.OSM();
	map.addLayer(osmLayer);
	
	var googleMapsLayer_Satellite = new OpenLayers.Layer.Google(
			'Google Maps Satellite',
			{type: google.maps.MapTypeId.SATELLITE} 
	);
	
	map.addLayers([googleMapsLayer_Satellite]);
	
	fromProjection = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
	// var toProjection   = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection
	toProjection = map.getProjectionObject(); // to Spherical Mercator Projection
	
	// Set center point and zoom
	var centerLonLat = new OpenLayers.LonLat( longtitude, latitude ).transform(fromProjection, toProjection);
	map.setCenter (centerLonLat, zoom);
	
	// Layer switcher
	// var layerSwitcherOptions = {
	//		'roundedCorner': true
	//};
	// map.addControl(new OpenLayers.Control.LayerSwitcher(layerSwitcherOptions));
	map.addControl(new OpenLayers.Control.LayerSwitcher());
	map.addControl(new OpenLayers.Control.MousePosition());
	
	// map.addControl(new OpenLayers.Control.PanZoomBar());
	// map.addControl(new OpenLayers.Control.Permalink());
	// map.addControl(new OpenLayers.Control.OverviewMap());
	
	// map.zoomToMaxExtent();
}


function fnLoadMarkers() {
	var PROCEDURE_ID_IDIRA = "IDIRA";
	var PROCEDURE_ID_VIRTUAL = "VIRTUAL";
	
	// Icons
	var size = new OpenLayers.Size(24,24);
	var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
	
	var realSensorIcon = new OpenLayers.Icon('images/pois/cop/sensor_blue.png', size, offset);
	var virtualSensorIcon = new OpenLayers.Icon('images/pois/cop/sensor_red.png', size, offset);
	var defaultIcon = new OpenLayers.Icon('images/pois/information.png', size, offset);
	
	// Popup type
	var autoSizeFramedCloud = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
		'autoSize': true
	});
	
	
	// ~~~~~~ Dynamic POIs ~~~~~~
	var realSensorLayer = new OpenLayers.Layer.Markers("IDIRA Sensors");
	map.addLayer(realSensorLayer);
	
	var virtualSensorLayer = new OpenLayers.Layer.Markers("Virtual Sensors");
	map.addLayer(virtualSensorLayer);
	
	var defaultLayer = new OpenLayers.Layer.Markers("Uncategorized");
	map.addLayer(defaultLayer);

	// Get data through AJAX call
	$.getJSON('api/procedureMetadata', function(response) {
		var sensorStreams = response;
		// alert("#SensorStreams: " + sensorStreams.length);
		
		var isRealSensorIconSet = false;
		var isVirtualSensorIconSet = false;
		var isDefaultIconSet = false;
		
		var toSkip = false;
		
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
			
			popupContentHTML += "<strong>latitude: </strong>" + sensorStreams[i].latitude.toString() + "<br>";
			popupContentHTML += "<strong>longtitude: </strong>" + sensorStreams[i].longtitude.toString() + "<br>";
			popupContentHTML += "<strong>isActive: </strong>" + sensorStreams[i].active.toString() + "<br>";
			popupContentHTML += "<strong>isMobile: </strong>" + sensorStreams[i].mobile.toString();
			
			var layer;
			
			var procedureId = sensorStreams[i].procedureId;
			
			// Check if it is a Building sensor
			if((procedureId.indexOf(PROCEDURE_ID_IDIRA) != -1)) {
				if(isRealSensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, realSensorIcon);
					isRealSensorIconSet = true;
				}
				else {
					sensorMarker = new OpenLayers.Marker(lonLat, realSensorIcon.clone());
				}
				
				layer = realSensorLayer;
			}  else if(procedureId.indexOf(PROCEDURE_ID_VIRTUAL) != -1) { 
				if(isVirtualSensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, virtualSensorIcon);
					isVirtualSensorIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, virtualSensorIcon.clone());
				} 
				
				layer = virtualSensorLayer;
			} else { // Uncategorized
				if(isDefaultIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon);
					isDefaultIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon.clone());
				}
				
				layer = defaultLayer;
			}
				
			// alert("Loading adding...");
			if(!toSkip) {
				addMarker(layer, sensorMarker, lonLat, popupClass, popupContentHTML, true, true );
			} else {
				toSkip = false;
			}
			// alert("Loading finished!");
		}

	});

}

function fnLoadMarkersSWeFS() {
	var PROCEDURE_ID_SunSPOT = "SunSPOT";
	var PROCEDURE_ID_DAVIS = "DAVIS";
	var PROCEDURE_ID_SATELLITE = "sat_sensor";
	var PROCEDURE_ID_CAMERA = "cam";
	
	// Icons
	var size = new OpenLayers.Size(24,24);
	var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
	
	var defaultIcon = new OpenLayers.Icon('images/pois/information.png', size, offset);
	var sunspotIcon = new OpenLayers.Icon('images/pois/swefs/sunspot.png', size, offset);
	var weatherStationIcon = new OpenLayers.Icon('images/pois/swefs/davis.png', size, offset);
	var cameraIcon = new OpenLayers.Icon('images/pois/swefs/camera.png', size, offset);
	var satelliteIcon = new OpenLayers.Icon('images/pois/swefs/satellite.png', size, offset);

	// Popup type
	var autoSizeFramedCloud = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
		'autoSize': true
	});
	
	
	// ~~~~~~ Dynamic POIs ~~~~~~
	var sunSpotLayer = new OpenLayers.Layer.Markers("SunSPOT Sensors");
	map.addLayer(sunSpotLayer);
	
	var weatherStationLayer = new OpenLayers.Layer.Markers("DAVIS Weather Stations");
	map.addLayer(weatherStationLayer);
	
	var satelliteLayer = new OpenLayers.Layer.Markers("Satellite Virtual Sensors");
	map.addLayer(satelliteLayer);
	
	var cameraLayer = new OpenLayers.Layer.Markers("Camera Virtual Sensors");
	map.addLayer(cameraLayer);
	
	var defaultLayer = new OpenLayers.Layer.Markers("Uncategorized");
	map.addLayer(defaultLayer);

	// Get data through AJAX call
	$.getJSON('api/procedureMetadata', function(response) {
		var sensorStreams = response;
		// alert("#SensorStreams: " + sensorStreams.length);
		
		var isSunSPOT = false;
		var isDAVIS = false;
		var isSatellite = false;
		var isCamera = false;
		var isDefaultIconSet = false;
		
		var toSkip = false;
		
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
			
			popupContentHTML += "<strong>latitude: </strong>" + sensorStreams[i].latitude.toString() + "<br>";
			popupContentHTML += "<strong>longtitude: </strong>" + sensorStreams[i].longtitude.toString() + "<br>";
			popupContentHTML += "<strong>isActive: </strong>" + sensorStreams[i].active.toString() + "<br>";
			popupContentHTML += "<strong>isMobile: </strong>" + sensorStreams[i].mobile.toString();
			
			var layer;
			
			var procedureId = sensorStreams[i].procedureId;
			
			// Check the sensor type
			if((procedureId.indexOf(PROCEDURE_ID_SunSPOT) != -1)) { //SunSPOT sensor
				if(isSunSPOT) {
					sensorMarker = new OpenLayers.Marker(lonLat, sunspotIcon);
					isSunSPOT = true;
				}
				else {
					sensorMarker = new OpenLayers.Marker(lonLat, sunspotIcon.clone());
				}
				
				layer = sunSpotLayer;
			}  else if(procedureId.indexOf(PROCEDURE_ID_DAVIS) != -1) { //DAVIS sensor
				if(isDAVIS) {
					sensorMarker = new OpenLayers.Marker(lonLat, weatherStationIcon);
					isDAVIS = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, weatherStationIcon.clone());
				} 
				
				layer = weatherStationLayer;
			} else if(procedureId.indexOf(PROCEDURE_ID_SATELLITE) != -1) { //Satellite sensor
				if(isSatellite) {
					sensorMarker = new OpenLayers.Marker(lonLat, satelliteIcon);
					isSatellite = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, satelliteIcon.clone());
				} 
				
				layer = satelliteLayer;
			} else if(procedureId.indexOf(PROCEDURE_ID_CAMERA) != -1) { //Camera sensor
				if(isCamera) {
					sensorMarker = new OpenLayers.Marker(lonLat, cameraIcon);
					isCamera = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, cameraIcon.clone());
				} 
				
				layer = cameraLayer;
			} else { // Uncategorized
				if(isDefaultIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon);
					isDefaultIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon.clone());
				}
				
				layer = defaultLayer;
			}
				
			// alert("Loading adding...");
			if(!toSkip) {
				addMarker(layer, sensorMarker, lonLat, popupClass, popupContentHTML, true, true );
			} else {
				toSkip = false;
			}
			// alert("Loading finished!");
		}

	});

}

function fnLoadMarkersDresden() {
	
	var IDIRA_GAUGING_STR = "streamGauge";
	var IDIRA_WEATHER_STATION_STR = "WEATHER";
	var IDIRA_BUILDING_SENSOR_STR = "FLEXIT";
	var IDIRA_DAVIS_STR = "DAVIS";
	var IDIRA_SENSOR_FUSION_ENGINE_STR = "SensorFusionEngine";
	var GAUGING_STR = "GAUGING_STATION";
	
	// Icons
	var size = new OpenLayers.Size(24,24);
	var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
	
	var gaugingIcon = new OpenLayers.Icon('images/pois/generic_water_64x64.png', size, offset);
	var weatherStationIcon = new OpenLayers.Icon('images/pois/iconSquareWeatherStation_64x64.png', size, offset);
	var buildingSensorIcon = new OpenLayers.Icon('images/pois/agency_72x72.png', size, offset);
	var mobileDeviceIcon = new OpenLayers.Icon('images/pois/mobile_128x128.png', size, offset);
	var hospitalIcon = new OpenLayers.Icon('images/pois/red_cross.png', size, offset);
	var sensorFusionIcon = new OpenLayers.Icon('images/pois/temperature_64x64.png', size, offset);
	var defaultIcon = new OpenLayers.Icon('images/pois/information.png', size, offset);
	
	// Popup type
	var autoSizeFramedCloud = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
		'autoSize': true
	});
	
	// ~~~~~~ Fixed POIs ~~~~~~
	var staticPoiLayer = new OpenLayers.Layer.Markers("Public Buildings");
	map.addLayer(staticPoiLayer);
	
	var hospitalLon = "13.95307";  // x
	var hospitalLat = "50.95860";   // y
	var hospitalLonLat = new OpenLayers.LonLat(hospitalLon, hospitalLat).transform(fromProjection, toProjection);
	var hospitalMarker = new OpenLayers.Marker(hospitalLonLat, hospitalIcon);
	var hospitalPopupClass = autoSizeFramedCloud;
	var hospitalPopupContentHTML = "<strong>Description: </strong>" + "Klinikum Pirna" + "<br>";
	hospitalPopupContentHTML += "<strong>Address: </strong>" + "Struppener Strabe 13, 01796 Pirna, Germany" + "<br>";
	hospitalPopupContentHTML += "<strong>Phone: </strong>" + "03501 71180";
	
	addMarker(staticPoiLayer, hospitalMarker, hospitalLonLat, hospitalPopupClass, hospitalPopupContentHTML, true, true );
	
	// alert("Loading...");
	// ~~~~~~ Dynamic POIs ~~~~~~
	var gaugingStationLayer = new OpenLayers.Layer.Markers("GaugingStations");
	map.addLayer(gaugingStationLayer);
	
	var weatherStationLayer = new OpenLayers.Layer.Markers("WeatherStations");
	map.addLayer(weatherStationLayer);
	
	var davisLayer = new OpenLayers.Layer.Markers("Davis Weather Station");
	map.addLayer(davisLayer);
	
	var buildingSensorLayer = new OpenLayers.Layer.Markers("Building Sensors");
	map.addLayer(buildingSensorLayer);
	
	var mobileDevicesLayer = new OpenLayers.Layer.Markers("Mobile Devices");
	map.addLayer(mobileDevicesLayer);
	
	var sensorFusionLayerOptions = {
			'displayInLayerSwitcher': false
	};
	var sensorFusionLayer = new OpenLayers.Layer.Markers("Sensor Fusion Output", sensorFusionLayerOptions);
	map.addLayer(sensorFusionLayer);
	
	var defaultLayer = new OpenLayers.Layer.Markers("Uncategorized");
	map.addLayer(defaultLayer);

	// Get data through AJAX call
	$.getJSON('api/procedureMetadata', function(response) {
		var sensorStreams = response;
		// alert("#SensorStreams: " + sensorStreams.length);
		
		var isGaugingIconSet = false;
		var isWeatherStationIconSet = false;
		var isBuildingSensorIconSet = false;
		var isMobileDeviceIconSet = false;
		var isSensorFusionIconSet = false;
		var isDefaultIconSet = false;
		var toSkip = false;
		
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
			
			popupContentHTML += "<strong>latitude: </strong>" + sensorStreams[i].latitude.toString() + "<br>";
			popupContentHTML += "<strong>longtitude: </strong>" + sensorStreams[i].longtitude.toString() + "<br>";
			popupContentHTML += "<strong>isActive: </strong>" + sensorStreams[i].active.toString() + "<br>";
			popupContentHTML += "<strong>isMobile: </strong>" + sensorStreams[i].mobile.toString();
			
			var layer;
			
			var procedureId = sensorStreams[i].procedureId;
			
			// Check if it is a Gauging Station
			if((procedureId.indexOf(IDIRA_GAUGING_STR) != -1) || (procedureId.indexOf(GAUGING_STR) != -1)) {
				if(isGaugingIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, gaugingIcon);
					isGaugingIconSet = true;
				}
				else {
					sensorMarker = new OpenLayers.Marker(lonLat, gaugingIcon.clone());
				}
				
				layer = gaugingStationLayer;
			} else if(procedureId.indexOf(IDIRA_WEATHER_STATION_STR) != -1) { // Weather Stations
				if(isWeatherStationIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, weatherStationIcon);
					isWeatherStationIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, weatherStationIcon.clone());
				} 
				
				layer = weatherStationLayer;
			} else if(procedureId.indexOf(IDIRA_DAVIS_STR) != -1) {
				if(isWeatherStationIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, weatherStationIcon);
					isWeatherStationIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, weatherStationIcon.clone());
				} 
				
				layer = davisLayer;
			} else if(procedureId.indexOf(IDIRA_BUILDING_SENSOR_STR) != -1) { // Building Sensors
				if(isBuildingSensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, buildingSensorIcon);
					isBuildingSensorIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, buildingSensorIcon.clone());
				} 
				
				layer = buildingSensorLayer;
			} else if(sensorStreams[i].mobile.toString().toLowerCase() == "true") { // Mobile device
				if(isMobileDeviceIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, mobileDeviceIcon);
					isMobileDeviceIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, mobileDeviceIcon.clone());
				} 
				
				layer = mobileDevicesLayer;
			} else if(procedureId.indexOf(IDIRA_SENSOR_FUSION_ENGINE_STR) != -1) { // Sensor Fusion Engine
				if(isSensorFusionIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, sensorFusionIcon);
					isSensorFusionIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, sensorFusionIcon.clone());
				} 
				
				layer = sensorFusionLayer;
				toSkip = true;
			} else { // Uncategorized
				if(isDefaultIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon);
					isDefaultIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon.clone());
				}
				
				layer = defaultLayer;
			}
				
			// alert("Loading adding...");
			if(!toSkip) {
				addMarker(layer, sensorMarker, lonLat, popupClass, popupContentHTML, true, true );
			} else {
				toSkip = false;
			}
			// alert("Loading finished!");
		}

	});
}

function fnLoadMarkersVienna() {
	var IDIRA_PROCEDURE_ID_HUMIDITY = "Hum";
	var IDIRA_PROCEDURE_ID_TEMPERATURE = "Temp";
	var IDIRA_PROCEDURE_ID_PERSON_COUNT = "person-count";
	var IDIRA_PROCEDURE_ID_WAPMERR = "WAPMERR";
	var IDIRA_PROCEDURE_ID_BUILDING = "building";
	
	
	// Icons
	var size = new OpenLayers.Size(24,24);
	var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
	
	var humiditySensorIcon = new OpenLayers.Icon('images/pois/generic_water_64x64.png', size, offset);
	var temperatureSensorIcon = new OpenLayers.Icon('images/pois/thermometer.png', size, offset);
	var buildingSensorIcon = new OpenLayers.Icon('images/pois/agency_72x72.png', size, offset);
	
	var defaultIcon = new OpenLayers.Icon('images/pois/information.png', size, offset);
	
	// Popup type
	var autoSizeFramedCloud = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
		'autoSize': true
	});
	
	// ~~~~~~ Fixed POIs ~~~~~~
	
	
	// ~~~~~~ Dynamic POIs ~~~~~~
	
	var humiditySensorLayer = new OpenLayers.Layer.Markers("Humidity Sensors");
	map.addLayer(humiditySensorLayer);
	
	var temperatureSensorLayer = new OpenLayers.Layer.Markers("Temperature Sensors");
	map.addLayer(temperatureSensorLayer);
	
	var buildingSensorLayer = new OpenLayers.Layer.Markers("Building Sensors");
	map.addLayer(buildingSensorLayer);
	

	var wapmerrLayerOptions = {
			'displayInLayerSwitcher': false
	}; 
	
	var wapmerrLayer = new OpenLayers.Layer.Markers("WAPMERR", wapmerrLayerOptions);
	map.addLayer(wapmerrLayer); 
	
	var defaultLayer = new OpenLayers.Layer.Markers("Uncategorized");
	map.addLayer(defaultLayer);

	// Get data through AJAX call
	$.getJSON('api/procedureMetadata', function(response) {
		var sensorStreams = response;
		// alert("#SensorStreams: " + sensorStreams.length);
		
		var isHumiditySensorIconSet = false;
		var isTemperatureSensorIconSet = false;
		var isBuildingSensorIconSet = false;
		var isWAPMERRSensorIconSet = false;
		var isDefaultIconSet = false;
		
		var toSkip = false;
		
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
			
			popupContentHTML += "<strong>latitude: </strong>" + sensorStreams[i].latitude.toString() + "<br>";
			popupContentHTML += "<strong>longtitude: </strong>" + sensorStreams[i].longtitude.toString() + "<br>";
			popupContentHTML += "<strong>isActive: </strong>" + sensorStreams[i].active.toString() + "<br>";
			popupContentHTML += "<strong>isMobile: </strong>" + sensorStreams[i].mobile.toString();
			
			var layer;
			
			var procedureId = sensorStreams[i].procedureId;
			
			// Check if it is a Building sensor
			if((procedureId.indexOf(IDIRA_PROCEDURE_ID_PERSON_COUNT) != -1)) {
				if(isBuildingSensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, buildingSensorIcon);
					isBuildingSensorIconSet = true;
				}
				else {
					sensorMarker = new OpenLayers.Marker(lonLat, buildingSensorIcon.clone());
				}
				
				layer = buildingSensorLayer;
			}  else if(procedureId.indexOf(IDIRA_PROCEDURE_ID_HUMIDITY) != -1) { 
				if(isHumiditySensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, humiditySensorIcon);
					isHumiditySensorIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, humiditySensorIcon.clone());
				} 
				
				layer = humiditySensorLayer;
			} else if(procedureId.indexOf(IDIRA_PROCEDURE_ID_TEMPERATURE) != -1) { 
				if(isTemperatureSensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, temperatureSensorIcon);
					isTemperatureSensorIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, temperatureSensorIcon.clone());
				} 
				
				layer = temperatureSensorLayer;
			}  else if((procedureId.indexOf(IDIRA_PROCEDURE_ID_WAPMERR) != -1) || (procedureId.indexOf(IDIRA_PROCEDURE_ID_BUILDING) != -1)) { 
				if(isWAPMERRSensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon);
					isWAPMERRSensorIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon.clone());
				} 
				
				layer = wapmerrLayer;
				toSkip = true;
			} else { // Uncategorized
				if(isDefaultIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon);
					isDefaultIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon.clone());
				}
				
				layer = defaultLayer;
			}
				
			// alert("Loading adding...");
			if(!toSkip) {
				addMarker(layer, sensorMarker, lonLat, popupClass, popupContentHTML, true, true );
			} else {
				toSkip = false;
			}
			// alert("Loading finished!");
		}

	});
}


function fnLoadMarkersAthens() {
	
	var PROCEDURE_ID_IDIRA = "IDIRA";
	var PROCEDURE_ID_VIRTUAL = "VIRTUAL";
	
	// Icons
	var size = new OpenLayers.Size(24,24);
	var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
	
	var realSensorIcon = new OpenLayers.Icon('images/pois/cop/sensor_blue.png', size, offset);
	var virtualSensorIcon = new OpenLayers.Icon('images/pois/cop/sensor_red.png', size, offset);
	var defaultIcon = new OpenLayers.Icon('images/pois/information.png', size, offset);
	
	// Popup type
	var autoSizeFramedCloud = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
		'autoSize': true
	});
	
	
	// ~~~~~~ Dynamic POIs ~~~~~~
	var realSensorLayer = new OpenLayers.Layer.Markers("Real Sensors");
	map.addLayer(realSensorLayer);
	
	var virtualSensorLayer = new OpenLayers.Layer.Markers("Virtual Sensors");
	map.addLayer(virtualSensorLayer);
	
	var defaultLayer = new OpenLayers.Layer.Markers("Uncategorized");
	map.addLayer(defaultLayer);

	// Get data through AJAX call
	$.getJSON('api/procedureMetadata', function(response) {
		var sensorStreams = response;
		// alert("#SensorStreams: " + sensorStreams.length);
		
		var isRealSensorIconSet = false;
		var isVirtualSensorIconSet = false;
		var isDefaultIconSet = false;
		
		var toSkip = false;
		
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
			
			popupContentHTML += "<strong>latitude: </strong>" + sensorStreams[i].latitude.toString() + "<br>";
			popupContentHTML += "<strong>longtitude: </strong>" + sensorStreams[i].longtitude.toString() + "<br>";
			popupContentHTML += "<strong>isActive: </strong>" + sensorStreams[i].active.toString() + "<br>";
			popupContentHTML += "<strong>isMobile: </strong>" + sensorStreams[i].mobile.toString();
			
			var layer;
			
			var procedureId = sensorStreams[i].procedureId;
			
			// Check if it is a Building sensor
			if((procedureId.indexOf(PROCEDURE_ID_IDIRA) != -1)) {
				if(isRealSensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, realSensorIcon);
					isRealSensorIconSet = true;
				}
				else {
					sensorMarker = new OpenLayers.Marker(lonLat, realSensorIcon.clone());
				}
				
				layer = realSensorLayer;
			}  else if(procedureId.indexOf(PROCEDURE_ID_VIRTUAL) != -1) { 
				if(isVirtualSensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, virtualSensorIcon);
					isVirtualSensorIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, virtualSensorIcon.clone());
				} 
				
				layer = virtualSensorLayer;
			} else { // Uncategorized
				if(isDefaultIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon);
					isDefaultIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon.clone());
				}
				
				layer = defaultLayer;
			}
				
			// alert("Loading adding...");
			if(!toSkip) {
				addMarker(layer, sensorMarker, lonLat, popupClass, popupContentHTML, true, true );
			} else {
				toSkip = false;
			}
			// alert("Loading finished!");
		}

	});
}

function fnLoadMarkersHelexpo() {
	
	var PROCEDURE_ID_IDIRA = "SunSPOT";
	
	// Icons
	var size = new OpenLayers.Size(24,24);
	var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
	
	var liveSensorIcon = new OpenLayers.Icon('images/pois/cop/sensor_blue.png', size, offset);
	var defaultIcon = new OpenLayers.Icon('images/pois/information.png', size, offset);
	
	// Popup type
	var autoSizeFramedCloud = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {
		'autoSize': true
	});
	
	// ~~~~~~ Fixed POIs ~~~~~~
	
	
	// ~~~~~~ Dynamic POIs ~~~~~~
	var liveSensorLayer = new OpenLayers.Layer.Markers("SunSPOTs");
	map.addLayer(liveSensorLayer);
	
	var defaultLayer = new OpenLayers.Layer.Markers("Uncategorized");
	map.addLayer(defaultLayer);

	// Get data through AJAX call
	$.getJSON('api/procedureMetadata', function(response) {
		var sensorStreams = response;
		// alert("#SensorStreams: " + sensorStreams.length);
		
		var isLiveSensorIconSet = false;
		var isDefaultIconSet = false;
		
		var toSkip = false;
		
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
			
			popupContentHTML += "<strong>latitude: </strong>" + sensorStreams[i].latitude.toString() + "<br>";
			popupContentHTML += "<strong>longtitude: </strong>" + sensorStreams[i].longtitude.toString() + "<br>";
			popupContentHTML += "<strong>isActive: </strong>" + sensorStreams[i].active.toString() + "<br>";
			popupContentHTML += "<strong>isMobile: </strong>" + sensorStreams[i].mobile.toString();
			
			var layer;
			
			var procedureId = sensorStreams[i].procedureId;
			
			// Check if it is a Building sensor
			if((procedureId.indexOf(PROCEDURE_ID_IDIRA) != -1)) {
				if(isLiveSensorIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, liveSensorIcon);
					isLiveSensorIconSet = true;
				}
				else {
					sensorMarker = new OpenLayers.Marker(lonLat, liveSensorIcon.clone());
				}
				
				layer = liveSensorLayer;
			} else { // Uncategorized
				if(isDefaultIconSet) {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon);
					isDefaultIconSet = true;
				} else {
					sensorMarker = new OpenLayers.Marker(lonLat, defaultIcon.clone());
				}
				
				layer = defaultLayer;
			}
				
			// alert("Loading adding...");
			if(!toSkip) {
				addMarker(layer, sensorMarker, lonLat, popupClass, popupContentHTML, true, true );
			} else {
				toSkip = false;
			}
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