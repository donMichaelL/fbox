package org.fbox.fusion.algorithms.miso.ejb.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.AlgorithmInitializationException;
import org.fbox.fusion.algorithms.miso.AbstractMISOAlgorithm;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

@Stateless(name="EuclideanDistance")
@Remote( { IAlgorithm.class, IStructure.class })
public class EuclideanDistance extends AbstractMISOAlgorithm {
	
	/**
     * Default constructor. 
     */
    public EuclideanDistance() {
    }
    
    /**
     * @see IDetect#getRequiredParameters()
     */
    public String[] getRequiredParameters() {
		String[] requiredParams={"unit"}; //a Single initialization parameter("units") is needed
		return requiredParams;
	}
    
    @Override
    public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {

    	Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
		
		//intialize execution params
		Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			switch (param.getName()) { 
				case "unit":
					
					String unit = (String)param.getValue();
					
					// Check if we have a valid input for "unit" parameter
					if(!unit.equalsIgnoreCase("km") && !unit.equalsIgnoreCase("m") && !unit.equalsIgnoreCase("cm")) {
							throw new AlgorithmInitializationException("A non valid type(" + unit + ") was specified as unit. Unit can take one from the following values: {'km','m','cm'}");	
					} else {
						state.setContextParameter("unit", unit);  //set the units type for the calculation of Euclidean Distance
						requiredParameters.remove(param.getName());
					}
					
					break;
				default:
					System.out.println("[EuclDist] WARNING --> Input Parameter '"+ param.getName() + "' not applicable for Detection Algorithm " + getType() +". Will be Ignored.");
			}
		}
		
		for (String s : requiredParameters) {
			System.out.println("[EuclDist] ERROR --> Input Parameter '"+ s + "' is needed but has not been specified.");
		}		
    }
    
    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement[] measurementList) throws AlgorithmExecutionException {
    	
    	int i=0;
    	double distance=0.0;
    	Geometry [] geometries = new Geometry[2];
    	double [] point1 = new double[2];
    	double [] point2 = new double[2];
    	String unit = (String)state.getContextParameter("unit");
    	
    	/*
    	//For DEBUG
    	for(IDataElement geom : measurementList) {
    		System.out.println("[EuclDist] INPUT["+i+"]:  "+geom.toString());
    		i++;
    	}
    	
    	System.out.println("[EuclDist] Length: " + measurementList.length);
    	*/
    	
    	//Check if we have two streams as input:
    	if(measurementList.length != 2) {
    		System.out.println("[EuclDist] WARNING: The number of input streams allowed is 2 and we currently have ("+measurementList.length+") streams as input!");
    		return null;
    		//throw new AlgorithmExecutionException("The number of input streams allowed is 2 and we currently have ("+measurementList.length+") streams as input!");
    	}
    	i=0;
    	//The input streams must be geometry objects
    	for (IDataElement geom : measurementList) {
    		if( (geom.getValue() != null) && (geom.getValue() instanceof Geometry) ) {
    			geometries[i] = (Geometry)geom.getValue();
    			//System.out.println("[EuclDist] INPUT["+i+"]:  "+geometries[i].toString());
    			i++;
    		} else {
    			System.out.println("[EuclDist] WARNING: Both input streams must be of \"Geometry\" type and not \"null\" in order to be taken into account!");
    			return null;
    			//throw new AlgorithmExecutionException("Both input streams must be of \"Geometry\" type and not \"null\" in order to be taken into account!");
    		}
    	}
    	
    	//Now that we have the two geometries we must check if they are of type "Point" 
    	if(!geometries[0].getGeometryType().equalsIgnoreCase("point") || !geometries[1].getGeometryType().equalsIgnoreCase("point")) {
    		System.out.println("[EuclDist] WARNING: Both input geometries must be of type \"Point\" !");
    		return null;
    		//throw new AlgorithmExecutionException("Both input geometries must be of type \"Point\" !");
    	}
    	
    	//Also, check if they do have the same SRID
    	if(geometries[0].getSRID() != geometries[1].getSRID()) {
    		System.out.println("[EuclDist] WARNING: Both input geometries must have the same SRID !");
    		return null;
    		//throw new AlgorithmExecutionException("Both input geometries must have the same SRID!");
    	}
    	
    	//Get the coordinates of the two points
    	Coordinate coord = null;
    	
    	//1st point
    	coord = new Coordinate(geometries[0].getCoordinate());
    	point1[0] = new Double(coord.x);
    	point1[1] = new Double(coord.y);
    	coord = null;
    	
    	//2nd point
    	coord = new Coordinate(geometries[1].getCoordinate());
    	point2[0] = new Double(coord.x);
    	point2[1] = new Double(coord.y);
    	
    	//Transform the output according to the "unit" type
    	switch (unit) {
    		case "m":
    			distance = haversineFormula(point1, point2) * 1000.0;
    			break;
    		case "cm":
    			distance = haversineFormula(point1, point2) * 1000.0 * 100.0;
    			break;
    		default:  //"km"
    			distance = haversineFormula(point1, point2);
    	}
    	
    	//System.out.println("[ED] Result = "+distance+unit);
    	return distance;
    }
    
	//Calculate Euclidean Distance (in "km") between two points by taking into account the spheroid shape of Earth
	public static double haversineFormula(double [] point1, double [] point2) {

		double distance=0;
		double DEG_TO_RAD = 0.017453293; //The known pi/180
		double EARTH_RADIUS_IN_METERS = 6371.0; //Earth radius in km
		double dlon,dlat,a,c;

		dlat = (point2[0] - point1[0]) * DEG_TO_RAD;
		dlon = (point2[1] - point1[1]) * DEG_TO_RAD;
		a = Math.pow(Math.sin(dlat * 0.5),2) + Math.cos(point1[0] * DEG_TO_RAD)* Math.cos(point2[0] * DEG_TO_RAD) * Math.pow(Math.sin(dlon * 0.5),2);
		c = 2.0 * Math.asin(Math.min(1.0 , Math.sqrt(a)));
		distance = EARTH_RADIUS_IN_METERS * c;

		return distance; //Distance in km 
	}
	
	@Override
    public String getType() {
      return "EuclideanDistance";
    }
	
	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}

}
