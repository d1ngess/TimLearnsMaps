package com.timdingess.timlearnsmaps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tim on 12/18/2016.
 */

public class MapMaker
{
    public String Name = "";
    public LatLng Coordinates;
    public MapMaker(LatLng coords, String name)
    {
        Coordinates = coords;
        Name = name;
    }
}
