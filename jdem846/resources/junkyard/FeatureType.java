/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.shapefile;

import java.util.Map;

public enum FeatureType {
    NullType(FeatureTypeGroup.NullType, "Null", FeatureTypeStroke.BasicFeature),
    
    SeaOcean(FeatureTypeGroup.Waterbodies, "SeaOcean", FeatureTypeStroke.BasicWater),
    Foreshore(FeatureTypeGroup.Waterbodies, "Foreshore", FeatureTypeStroke.BasicWater),
    BayOrEstuaryOrOcean(FeatureTypeGroup.Waterbodies, "Bay or Estuary or Ocean", FeatureTypeStroke.BasicWater),           // A body of salt water.
    Canal(FeatureTypeGroup.Streams, "Canal", FeatureTypeStroke.BasicWater),                        	 					// An artificial open waterway constructed to transport water, to irrigate or drain land, to connect two or more bodies of water, or to serve as a waterway for water craft.
    CanalDitch(FeatureTypeGroup.Streams, "CanalDitch", FeatureTypeStroke.BasicWater),  
    ArtificialPath(FeatureTypeGroup.Streams, "ArtificialPath", FeatureTypeStroke.BasicWater),                        		// An artificial open waterway constructed to transport water, to irrigate or drain land, to connect two or more bodies of water, or to serve as a waterway for water craft.
    Connector(FeatureTypeGroup.Streams, "Connector", FeatureTypeStroke.BasicWater),                        	 			// An artificial open waterway constructed to transport water, to irrigate or drain land, to connect two or more bodies of water, or to serve as a waterway for water craft.
    Glacier(FeatureTypeGroup.Waterbodies, "Glacier", FeatureTypeStroke.BasicWater),                       				// A field of ice.
    Lake(FeatureTypeGroup.Waterbodies, "Lake", FeatureTypeStroke.BasicWater),                          					// A standing body of water with a predominately natural shoreline surrounded by land.
    LakePond(FeatureTypeGroup.Waterbodies, "LakePond", FeatureTypeStroke.BasicWater), 
    LakeDry(FeatureTypeGroup.Waterbodies, "Lake Dry", FeatureTypeStroke.BasicWater),                       				// An area that at one time contained a standing body of water.
    LakeIntermittent(FeatureTypeGroup.Waterbodies, "Lake Intermittent", FeatureTypeStroke.BasicWater),              		// A standing body of water that contains water for only part of the year.
    NonWaterFeature(FeatureTypeGroup.Waterbodies, "Null", FeatureTypeStroke.BasicWater),               					// Not a water feature.
    Reservoir(FeatureTypeGroup.Waterbodies, "Reservoir", FeatureTypeStroke.BasicWater),                     				// A constructed basin formed to contain water.
    ReservoirIntermittent(FeatureTypeGroup.Waterbodies, "Reservoir Intermittent", FeatureTypeStroke.BasicWater),         	// A constructed basin formed to contain water for only part of the year.
    Stream(FeatureTypeGroup.Streams, "Stream", FeatureTypeStroke.BasicWater),                        	 					// A body of flowing water.
    StreamRiver(FeatureTypeGroup.Streams, "StreamRiver", FeatureTypeStroke.BasicWater),
    SwampOrMarsh(FeatureTypeGroup.Waterbodies, "Swamp Or Marsh", FeatureTypeStroke.BasicWater),                  			// A noncultivated, vegetated area that is inundated or saturated for a significant part of the year.
    SwampMarsh(FeatureTypeGroup.Waterbodies, "SwampMarsh", FeatureTypeStroke.BasicWater),
    ClosureLine(FeatureTypeGroup.Streams, "Closure Line", FeatureTypeStroke.BasicWater),
    Shoreline(FeatureTypeGroup.Streams, "Shoreline", FeatureTypeStroke.BasicWater),
    Coastline(FeatureTypeGroup.Streams, "Coastline", FeatureTypeStroke.BasicWater),
    Pipeline(FeatureTypeGroup.Streams, "Pipeline", FeatureTypeStroke.BasicWater),
    UndergroundConduit(FeatureTypeGroup.Streams, "Underground Conduit", FeatureTypeStroke.BasicWater),
    Dam(FeatureTypeGroup.Waterbodies, "Dam", FeatureTypeStroke.BasicWater),
    LeftBank(FeatureTypeGroup.Streams, "Left Bank", FeatureTypeStroke.BasicWater),
    RightBank(FeatureTypeGroup.Streams, "Right Bank", FeatureTypeStroke.BasicWater),
    Estuary(FeatureTypeGroup.Waterbodies, "Estuary", FeatureTypeStroke.BasicWater),
    
    // Roads
    FerryCrossing(FeatureTypeGroup.Roads, "Ferry Crossing", FeatureTypeStroke.WaterCrossing),
    FerryCrossingTollRoad(FeatureTypeGroup.Roads, "Ferry Crossing  Toll Road", FeatureTypeStroke.WaterCrossing),
    LimitedAccessHighway(FeatureTypeGroup.Roads, "Limited Access Highway", FeatureTypeStroke.BasicHighway),
    LimitedAccessHighwayAlternateRoute(FeatureTypeGroup.Roads, "Limited Access Highway  Alternate Route", FeatureTypeStroke.BasicHighway),
    LimitedAccessHighwayBusinessRoute(FeatureTypeGroup.Roads, "Limited Access Highway  Business Route", FeatureTypeStroke.BasicHighway),
    LimitedAccessHighwayBypassRoute(FeatureTypeGroup.Roads, "Limited Access Highway  Bypass Route", FeatureTypeStroke.BasicHighway),
    LimitedAccessHighwayInTunnel(FeatureTypeGroup.Roads, "Limited Access Highway  In Tunnel", FeatureTypeStroke.BasicHighwayInTunnel),
    LimitedAccessHighwayInTunnelTollRoad(FeatureTypeGroup.Roads, "Limited Access Highway  In Tunnel  Toll Road", FeatureTypeStroke.BasicHighwayInTunnel),
    LimitedAccessHighwayTollRoad(FeatureTypeGroup.Roads, "Limited Access Highway  Toll Road", FeatureTypeStroke.BasicHighway),
    LimitedAccessHighwayTruckRoute(FeatureTypeGroup.Roads, "Limited Access Highway  Truck Route", FeatureTypeStroke.BasicHighway),
    OtherHighway(FeatureTypeGroup.Roads, "Other Highway", FeatureTypeStroke.BasicHighway),
    OtherHighwayAlternateRoute(FeatureTypeGroup.Roads, "Other Highway  Alternate Route", FeatureTypeStroke.BasicHighway),
    OtherHighwayBusinessRoute(FeatureTypeGroup.Roads, "Other Highway  Business Route", FeatureTypeStroke.BasicHighway),
    OtherHighwayBypassRoute(FeatureTypeGroup.Roads, "Other Highway  Bypass Route", FeatureTypeStroke.BasicHighway),
    OtherHighwayTollRoad(FeatureTypeGroup.Roads, "Other Highway  Toll Road", FeatureTypeStroke.BasicHighway),
    OtherThroughHighway(FeatureTypeGroup.Roads, "Other Through Highway", FeatureTypeStroke.BasicHighway),
    OtherThroughHighwayAlternateRoute(FeatureTypeGroup.Roads, "Other Through Highway  Alternate Route", FeatureTypeStroke.BasicHighway),
    OtherThroughHighwayAlternateRouteBypassRoute(FeatureTypeGroup.Roads, "Other Through Highway  Alternate Route  Bypass Route", FeatureTypeStroke.BasicHighway),
    OtherThroughHighwayBusinessRoute(FeatureTypeGroup.Roads, "Other Through Highway  Business Route", FeatureTypeStroke.BasicHighway),
    OtherThroughHighwayBusinessRouteAlternateRoute(FeatureTypeGroup.Roads, "Other Through Highway  Business Route  Alternate Route", FeatureTypeStroke.BasicHighway),
    OtherThroughHighwayBypassRoute(FeatureTypeGroup.Roads, "Other Through Highway  Bypass Route", FeatureTypeStroke.BasicHighway),
    OtherThroughHighwayInTunnelAlternateRoute(FeatureTypeGroup.Roads, "Other Through Highway  In Tunnel  Alternate Route", FeatureTypeStroke.BasicHighway),
    OtherThroughHighwayTollRoad(FeatureTypeGroup.Roads, "Other Through Highway  Toll Road", FeatureTypeStroke.BasicHighway),
    OtherThroughHighwayTruckRoute(FeatureTypeGroup.Roads, "Other Through Highway  Truck Route", FeatureTypeStroke.BasicHighway),
    PrincipalHighway(FeatureTypeGroup.Roads, "Principal Highway", FeatureTypeStroke.BasicHighway),
    PrincipalHighwayAlternateRoute(FeatureTypeGroup.Roads, "Principal Highway  Alternate Route", FeatureTypeStroke.BasicHighway),
    PrincipalHighwayAlternateRouteBypassRoute(FeatureTypeGroup.Roads, "Principal Highway  Alternate Route  Bypass Route", FeatureTypeStroke.BasicHighway),
    PrincipalHighwayBusinessRoute(FeatureTypeGroup.Roads, "Principal Highway  Business Route", FeatureTypeStroke.BasicHighway),
    PrincipalHighwayBusinessRouteAlternateRoute(FeatureTypeGroup.Roads, "Principal Highway  Business Route  Alternate Route", FeatureTypeStroke.BasicHighway),
    PrincipalHighwayBypassRoute(FeatureTypeGroup.Roads, "Principal Highway  Bypass Route", FeatureTypeStroke.BasicHighway),
    PrincipalHighwayInTunnel(FeatureTypeGroup.Roads, "Principal Highway  In Tunnel", FeatureTypeStroke.BasicHighwayInTunnel),
    PrincipalHighwayInTunnelTollRoad(FeatureTypeGroup.Roads, "Principal Highway  In Tunnel  Toll Road", FeatureTypeStroke.BasicHighwayInTunnel),
    PrincipalHighwayTollRoad(FeatureTypeGroup.Roads, "Principal Highway  Toll Road", FeatureTypeStroke.BasicHighway),
    PrincipalHighwayTruckRoute(FeatureTypeGroup.Roads, "Principal Highway  Truck Route", FeatureTypeStroke.BasicHighway),

    LocalRoad(FeatureTypeGroup.Roads, "Local Road", FeatureTypeStroke.BasicRoad),
    Ramp(FeatureTypeGroup.Roads, "Ramp", FeatureTypeStroke.Ramp),
    PrivateRoad(FeatureTypeGroup.Roads, "Private Road", FeatureTypeStroke.BasicRoad),
    SecandaryRoad(FeatureTypeGroup.Roads, "Secandary Road", FeatureTypeStroke.SecandaryRoad),
    FourWDRoad(FeatureTypeGroup.Roads, "4WD Road", FeatureTypeStroke.DirtRoad),
    PrimaryRoad(FeatureTypeGroup.Roads, "Primary Road", FeatureTypeStroke.PrimaryRoad),
    PrivateDriveway(FeatureTypeGroup.Roads, "Private Driveway", FeatureTypeStroke.BasicRoad),
    ServiceRoad(FeatureTypeGroup.Roads, "Service Road", FeatureTypeStroke.DirtRoad),
    ParkingLotRoad(FeatureTypeGroup.Roads, "Parking Lot Road", FeatureTypeStroke.BasicRoad),
    ParklingLotRoad(FeatureTypeGroup.Roads, "Parkling Lot Road", FeatureTypeStroke.BasicRoad),
    Alley(FeatureTypeGroup.Roads, "Alley", FeatureTypeStroke.BasicRoad),
    UnknownRoad(FeatureTypeGroup.Roads, "Unknown", FeatureTypeStroke.BasicRoad),
    
    Rail(FeatureTypeGroup.Rail, "Rail", FeatureTypeStroke.Rail),
    
    Runway(FeatureTypeGroup.Airports, "Runway", FeatureTypeStroke.Runway);
    
    private final FeatureTypeGroup featureTypeGroup;
	private final String featureName;
    private final FeatureTypeStroke featureStroke;
    
    FeatureType(FeatureTypeGroup featureTypeGroup, String featureName, FeatureTypeStroke featureStroke)
	{
		this.featureTypeGroup = featureTypeGroup;
		this.featureName = featureName;
		this.featureStroke = featureStroke;
	}
	
    public FeatureTypeGroup getFeatureTypeGroup() { return featureTypeGroup; }
    public String featureName() { return featureName; }
    public FeatureTypeStroke featureStroke() { return featureStroke; }
    
    public static FeatureType getFeatureTypeFromString(String value)
    {
    	for (FeatureType featureType : FeatureType.values()) {
    		if (featureType.featureName() != null && featureType.featureName().equalsIgnoreCase(value))
    			return featureType;
    	}
    	return null;
    }
    
    public static FeatureType getFeatureTypeFromMap(Map<String, Object> infoMap)
    {
    	
    	for (String name : infoMap.keySet()) {
    		Object value = infoMap.get(name);
    		
    		if (!(value instanceof String))
    			continue;
    		
    		FeatureType featureType = FeatureType.getFeatureTypeFromString((String)value);
    		if (featureType != null)
    			return featureType;
    		
    	}
    	
    	return null;
    }

}
