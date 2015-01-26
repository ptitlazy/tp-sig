package fr.ensimag.scip;

import database.Utils;
import geoexplorer.gui.GeoMainFrame;
import geoexplorer.gui.MapPanel;
import geoexplorer.gui.Polygon;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 15/01/2015.
 */
public class main {
    public static void main(String[] args) throws SQLException {
        Connection connection = Utils.getConnection();

        //PreparedStatement stmt =  connection.prepareStatement();
        //ResultSet res = stmt.executeQuery();

        MapPanel map = new MapPanel(5.7682866458, 45.193390625, 0.01);

        Statement stmt = connection.createStatement();
//        ResultSet res = stmt.executeQuery("select id, st_transform(linestring,2154) "
        ResultSet res = stmt.executeQuery("select id, linestring "
                +"from ways where tags?'building' and tags->'name' like 'Ensimag%' "
                +"and st_xmin(linestring) >= 5.7 and st_xmax(linestring) <= 5.8 "
                +"and st_ymin(linestring) >= 45.1 and st_ymax(linestring) <= 45.2"
        );

//        GeoMainFrame frame = new GeoMainFrame("SIG", map);

        List<Polygon> polygons = new ArrayList<>();

        System.out.println("requete done");
        while (res.next()) {
            Geometry geom = ((PGgeometry) res.getObject(2)).getGeometry();

            int numPoints = geom.numPoints();

            Polygon p = new Polygon();
            for (int i = 0 ; i<numPoints ; i++) {
                System.out.println("X:"+geom.getPoint(i).getX()+" ; Y:"+geom.getPoint(i).getY());
                p.addPoint(new geoexplorer.gui.Point(geom.getPoint(i).getX(), geom.getPoint(i).getY()));
            }

            p.addPoint(new geoexplorer.gui.Point(20,10));
//            p.addPoint(new geoexplorer.gui.Point(45, -10));

            map.addPrimitive(p);

            System.out.println("id : " + res.getInt(1) + " Construit");
//            frame.repaint();
        }


        GeoMainFrame frame = new GeoMainFrame("SIG", map);

        /*MapPanel map = new MapPanel(5.768, 45.19, 100);
        Polygon p = new Polygon();
        p.addPoint(new geoexplorer.gui.Point(5.768,45.19));
        p.addPoint(new geoexplorer.gui.Point(5.7681,45.18));
        p.addPoint(new geoexplorer.gui.Point(20,10));
        p.addPoint(new geoexplorer.gui.Point(45, -10));
        map.addPrimitive(p);
        GeoMainFrame frame = new GeoMainFrame("SIG", map);*/

        Utils.closeConnection();
    }
}
