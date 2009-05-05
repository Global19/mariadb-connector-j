package org.drizzle.jdbc;

import static junit.framework.Assert.assertEquals;

import java.sql.*;

import org.junit.Test;

/**
 * User: marcuse
 * Date: Feb 26, 2009
 * Time: 10:12:52 PM
 */
public class DatabaseMetadataTest {
    private Connection connection;
    public DatabaseMetadataTest() throws ClassNotFoundException, SQLException {
        Class.forName("org.drizzle.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:drizzle://"+DriverTest.host+":4427/test_units_jdbc");

    }
    @Test
    public void primaryKeysTest() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("drop table if exists pk_test");
        stmt.execute("create table pk_test (id1 int not null, id2 int not null, val varchar(20), primary key(id1, id2))");
        DatabaseMetaData dbmd = connection.getMetaData();
        ResultSet rs = dbmd.getPrimaryKeys(null,"test_units_jdbc","pk_test");
        int i=0;
        while(rs.next()) {
            i++;
            assertEquals("NULL",rs.getString("table_cat"));
            assertEquals("test_units_jdbc",rs.getString("table_schem"));
            assertEquals("pk_test",rs.getString("table_name"));
            assertEquals("id"+i,rs.getString("column_name"));
            assertEquals(i,rs.getShort("key_seq"));
        }
        assertEquals(2,i);
    }
    @Test
    public void exportedKeysTest() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("drop table if exists fore_key0");
        stmt.execute("drop table if exists fore_key1");
        stmt.execute("drop table if exists prim_key");


        stmt.execute("create table prim_key (id int not null primary key, " +
                                            "val varchar(20))");
        stmt.execute("create table fore_key0 (id int not null primary key, " +
                                            "id_ref0 int, foreign key (id_ref0) references prim_key(id))");
        stmt.execute("create table fore_key1 (id int not null primary key, " +
                                            "id_ref1 int, foreign key (id_ref1) references prim_key(id) on update cascade)");


        DatabaseMetaData dbmd = connection.getMetaData();
        ResultSet rs = dbmd.getExportedKeys("","test_units_jdbc","prim_key");
        int i =0 ;
        while(rs.next()) {
            assertEquals("id",rs.getString("pkcolumn_name"));
            assertEquals("fore_key"+i,rs.getString("fktable_name"));
            assertEquals("id_ref"+i,rs.getString("fkcolumn_name"));
            i++;

        }
        assertEquals(2,i);
    }
}