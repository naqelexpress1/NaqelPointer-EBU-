package com.naqelexpress.naqelpointer.NCLBulk;

import java.util.List;

public interface INclShipmentActivity {
    void onNCLGenerated(String NCLNo , int NCLDestStationID , List<Integer> allowedDestStations);
}
