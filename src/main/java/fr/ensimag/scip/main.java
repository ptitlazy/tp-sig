package fr.ensimag.scip;

import database.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Thomas on 15/01/2015.
 */
public class main {
    public static void main(String[] args) throws SQLException {
        Connection connection = Utils.getConnection();

        PreparedStatement stmt =  connection.prepareStatement("select count(*) from users");
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            System.out.println("colonne 1 = " + res.getString(1));
        }
    }
}
