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

        MapPanel map = new MapPanel(917345, 6458708, 1000);

        //Requête pour trouver tous les bâtiments autour de Grenoble
        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery("select id, st_transform(linestring,2154), st_transform(st_centroid(linestring),2154) "
                +"from ways where tags?'building' and tags->'name' like 'Ensimag%' "
                +"and st_xmin(linestring) >= 5.7 and st_xmax(linestring) <= 5.8 "
                +"and st_ymin(linestring) >= 45.1 and st_ymax(linestring) <= 45.2"
        );

        System.out.println("request done");

        //Construction de la carte
        while (res.next()) {
            Geometry geom = ((PGgeometry) res.getObject(2)).getGeometry();

            int numPoints = geom.numPoints();

            Polygon p = new Polygon();
            for (int i = 0 ; i<numPoints ; i++) {
                System.out.println("new point -- X:"+geom.getPoint(i).getX()+" ; Y:"+geom.getPoint(i).getY());
                p.addPoint(new geoexplorer.gui.Point(geom.getPoint(i).getX(), geom.getPoint(i).getY()));
            }

            map.addPrimitive(p);

            System.out.println("bat id : " + res.getInt(1) + " done");
        }

        GeoMainFrame frame = new GeoMainFrame("SIG", map);

        //Fermeture de la connexion
        Utils.closeConnection();
    }
}
