package com.naqelexpress.naqelpointer.Activity.routeMap;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anupamchugh on 27/11/15.
 */

public class DirectionsJSONParserWayPoints {

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject, String position, Context conext, int division, int preposition,
                                                     ArrayList<MyRouteShipments> myRouteShipmentList, ArrayList<Location> places) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONArray waypointOrder = null;
        String overview_polyline = "";
        JSONArray ovpolyline = null;
        JSONObject ovpoints = null;
        int legsCount = 0;
        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                int waypointsJsonArray = 0;
                waypointsJsonArray = waypointsJsonArray + preposition;
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                legsCount = jLegs.length();
                String pline = (String) ((JSONObject) jRoutes.get(i)).getJSONObject("overview_polyline").get("points");
                for (int m = 0; m < jLegs.length(); m++) {

                    List path = new ArrayList<HashMap<String, String>>();
                    HashMap<String, String> hm_ = new HashMap<String, String>();
                    String km = "";
                    km = (String) ((JSONObject) ((JSONObject) jLegs.get(m)).get("distance")).get("text");
                    String duration = "";
                    duration = (String) ((JSONObject) ((JSONObject) jLegs.get(m)).get("duration")).get("text");
                    int durationvalue = (int) ((JSONObject) ((JSONObject) jLegs.get(m)).get("duration")).get("value");
                    hm_.put("km", km);
                    hm_.put("time", duration);
                    hm_.put("address", ((JSONObject) jLegs.get(m)).getString("end_address"));
                    RouteMap.distance_time.add(hm_);

                    if (division == 0) {

                        if (waypointOrder == null) {
                            waypointOrder = ((JSONObject) jRoutes.get(0)).getJSONArray("waypoint_order");
//                            overview_polyline = ((JSONObject) jRoutes.get(0)).getJSONObject("overview_polyline").getString("points");


                        }
                        String wno = "";
                        try {
                            if (jLegs.length() - 1 == m)
                                wno = myRouteShipmentList.get((int) places.get(places.size() - 1).getSpeed() - 1).ItemNo;
                            else
                                wno = myRouteShipmentList.get((int) places.get((int) waypointOrder.get(m + preposition) + 1).getSpeed() - 1).ItemNo;
                        } catch (Exception e) {
                            System.out.println(e);
                        }
//
                        DBConnections dbConnections = new DBConnections(conext, null);

                        JSONObject jrsonObject = jObject;
                        JSONObject ov = new JSONObject();
                        ov.put("points", pline);
                        JSONArray rjRoutes = jrsonObject.getJSONArray("routes");

                        ((JSONObject) rjRoutes.get(0)).remove("legs");// getJSONArray("legs").ge    t(m);
                        JSONArray asd = new JSONArray();
                        asd.put(jLegs.get(m));
                        JSONObject jo = new JSONObject();
                        jo.put("legs", asd);

                        //JSONObject jj = new JSONObject();
                        //jj.put("summary", ((JSONObject) jLegs.get(m)).getString("end_address"));

                        jo.put("overview_polyline", ov);
                        jo.put("summary", ((JSONObject) jLegs.get(m)).getString("end_address"));
                        rjRoutes.put(0, jo);


                        String PolyLine = "";
                        for (int j = 0; j < jLegs.length(); j++) {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for (int k = 0; k < jSteps.length(); k++) {
                                String polyline = "";
                                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
//                                if (k == 0)
//                                    PolyLine = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
//                                else
//                                    PolyLine = PolyLine + (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                List list = decodePoly(polyline);

                                /** Traversing all points */
                                for (int l = 0; l < list.size(); l++) {
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                    hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                    path.add(hm);
                                }
                            }
                            routes.add(path);
                        }

                        dbConnections.InsertPlannedLocationWayPoints(conext, jrsonObject.toString(), m, Integer.parseInt(wno), km, duration,
                                ((JSONObject) jLegs.get(m)).getString("start_address"),
                                ((JSONObject) jLegs.get(m)).getString("end_address"), durationvalue);
//                        }
                        dbConnections.close();
                    }

                }
            }


            ArrayList<Location> tplaces = new ArrayList<>();
            if (waypointOrder != null && waypointOrder.length() > 0) {
                for (int i = 0; i < waypointOrder.length() + 1; i++) {
                    if (i == 0) {
                        tplaces.add(places.get(i));
//                        places.remove(0);
                        continue;
                    }
                    int wayposition = (int) waypointOrder.get(i - 1);
                    tplaces.add(places.get(wayposition + 1));
//                    if (waypointOrder.length() - 1 == i)
//                        tplaces.add(places.get(places.size() - 1));
                }
                RouteMap.places.clear();
                RouteMap.places.addAll(tplaces);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBConnections dbConnections = new DBConnections(conext, null);
        dbConnections.InsertSeqtime(conext, legsCount);
        dbConnections.close();

        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}