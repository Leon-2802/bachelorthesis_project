package org.implementation;

import org.implementation.trias.TriasRequests;
import org.implementation.trias.request_templates.RequestTargets;

public class Main {
    public static void main(String[] args) {
        TriasRequests tr = new TriasRequests();
        //String res = tr.locInfoReq("Hochschule Offenburg", RequestTargets.VRN_TRIAS);
        // String res = tr.coordToCoordTripReq(new Coord(8.38755106233198f, 49.01261399475719f), new Coord(8.41064385412417f, 49.00505228988602f), HelperFunctions.getCurrentTime (), (short)1, true, RequestTargets.VRN_TRIAS);
        String res = tr.stationToStationTripRequest("de:08317:14501", "fr:24067:1296", HelperFunctions.getCurrentTime(), (short)1, true, RequestTargets.VRN_TRIAS);
        System.out.println("Response Body:\n " + res);
    }
}