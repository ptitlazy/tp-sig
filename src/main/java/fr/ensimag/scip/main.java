package fr.ensimag.scip;

import database.Utils;
import geoexplorer.gui.GeoMainFrame;
import geoexplorer.gui.LineString;
import geoexplorer.gui.MapPanel;
import geoexplorer.gui.Polygon;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 15/01/2015.
 */
public class main {
    public static void main(String[] args) throws SQLException {
        Connection connection = Utils.getConnection();

        MapPanel map = new MapPanel(917345, 6458708, 10000);

        //Affichage des quartiers de Grenoble,
        // couleur fonction de leur nombre d'école
        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery("select count(*) as n, st_transform(quartier.the_geom,2154) from quartier, ways where tags->'amenity'='school' and st_intersects(st_transform(quartier.the_geom,4326),ways.linestring)" +
                "group by quartier.quartier,quartier.the_geom order by n desc;");
        while (res.next()) {
            Color color = new Color(58,95,205,50 + (res.getInt(1))*(200-50)/15);
            Geometry geom = ((PGgeometry) res.getObject(2)).getGeometry();

            int numPoints = geom.numPoints();

            Polygon p = new Polygon(new Color(58,95,205), color);
            for (int i = 0 ; i<numPoints ; i++) {
                //System.out.println("new point -- X:"+geom.getPoint(i).getX()+" ; Y:"+geom.getPoint(i).getY());
                p.addPoint(new geoexplorer.gui.Point(geom.getPoint(i).getX(), geom.getPoint(i).getY()));
            }

            map.addPrimitive(p);
        }

        GeoMainFrame frame = new GeoMainFrame("SIG", map);

        System.out.println("quartiers done");

        //Requête pour trouver tous les bâtiments autour de Grenoble
        stmt = connection.createStatement();
        res = stmt.executeQuery("select id, st_transform(linestring,2154) "
                +"from ways where tags?'building' "
                +"and st_xmin(linestring) >= 5.7 and st_xmax(linestring) <= 5.8 "
                +"and st_ymin(linestring) >= 45.1 and st_ymax(linestring) <= 45.2"
        );

        System.out.println("buildings done");

        //Construction de la carte des bâtiments
        while (res.next()) {
            Geometry geom = ((PGgeometry) res.getObject(2)).getGeometry();

            int numPoints = geom.numPoints();

            Polygon p = new Polygon(new Color(250,200,0,200), new Color(250,200,0,150));
            for (int i = 0 ; i<numPoints ; i++) {
                //System.out.println("new point -- X:"+geom.getPoint(i).getX()+" ; Y:"+geom.getPoint(i).getY());
                p.addPoint(new geoexplorer.gui.Point(geom.getPoint(i).getX(), geom.getPoint(i).getY()));
            }

            map.addPrimitive(p);

            //System.out.println("bat id : " + res.getInt(1) + " done");
        }

        map.repaint();


        //Affichage des routes autour de Grenoble
        stmt = connection.createStatement();
        res = stmt.executeQuery("select st_transform(linestring,2154) "
                +"from ways where tags?'highway' "
                +"and st_xmin(linestring) >= 5.7 and st_xmax(linestring) <= 5.8 "
                +"and st_ymin(linestring) >= 45.1 and st_ymax(linestring) <= 45.2"
        );

        //Construction de la carte
        while (res.next()) {
            Geometry geom = ((PGgeometry) res.getObject(1)).getGeometry();

            int numPoints = geom.numPoints();

            LineString l = new LineString(new Color(130, 130, 130, 150));
            for (int i = 0 ; i<numPoints ; i++) {
                l.addPoint(new geoexplorer.gui.Point(geom.getPoint(i).getX(), geom.getPoint(i).getY()));
            }

            map.addPrimitive(l);

        }

        map.repaint();

        System.out.println("roads done");


        //Affichage des lignes de tram et train en vert
        stmt = connection.createStatement();
        res = stmt.executeQuery("select st_transform(linestring,2154) "
                        +"from ways where tags?'railway' "
                        +"and st_xmax(linestring) >= 5.7 and st_xmin(linestring) <= 5.8 "
                        +"and st_ymax(linestring) >= 45.1 and st_ymin(linestring) <= 45.2"
        );

        //Construction de la carte
        while (res.next()) {
            Geometry geom = ((PGgeometry) res.getObject(1)).getGeometry();

            int numPoints = geom.numPoints();

            LineString l = new LineString(new Color(46, 139, 87, 255));
            for (int i = 0 ; i<numPoints ; i++) {
                l.addPoint(new geoexplorer.gui.Point(geom.getPoint(i).getX(), geom.getPoint(i).getY()));
            }

            map.addPrimitive(l);

        }

        map.repaint();

        System.out.println("railways done");

        //Fermeture de la connexion
        Utils.closeConnection();
    }
}
