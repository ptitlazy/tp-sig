package fr.ensimag.scip;

import database.Utils;
import geoexplorer.gui.GeoMainFrame;
import geoexplorer.gui.MapPanel;
import geoexplorer.gui.Polygon;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;

import java.sql.*;

/**
 * Created by Thomas on 15/01/2015.
 */
public class main {
    public static void main(String[] args) throws SQLException {
        Connection connection = Utils.getConnection();

        System.out.println(args[0]);

        //PreparedStatement stmt =  connection.prepareStatement();
        //ResultSet res = stmt.executeQuery();

//        MapPanel map = new MapPanel(0.0, 0.0, 100);
//        Polygon p = new Polygon();
//        p.addPoint(new geoexplorer.gui.Point(0,0));
//        p.addPoint(new geoexplorer.gui.Point(10,10));
//        p.addPoint(new geoexplorer.gui.Point(20,10));
//        p.addPoint(new geoexplorer.gui.Point(45, -10));
//        GeoMainFrame frame = new GeoMainFrame("SIG", map);
//        map.addPrimitive(p);

        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery("select tags->'name', geom from nodes where tags?'name' and tags->'name' like '"+args[0]+"'");
        while (res.next()) {
            Point geom = (Point) ((PGgeometry) res.getObject(2)).getGeometry();
            double x = geom.getX();
            double y = geom.getY();
            System.out.println("nom : " + res.getString(1) + " ;; X : " + x + " ;; Y : " + y);
        }


        Utils.closeConnection();
    }
}
