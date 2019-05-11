import com.univocity.parsers.csv.CsvWriter;

import java.io.FileWriter;
import java.sql.*;

class JDBCExample {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/companydb";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "password";

    //   public ResultSet updatedset(String sql,Statement stmt){
    //     ResultSet rs = stmt.executeQuery(sql);
    //     while(rs.next()){
    //        //Retrieve by column name
    //        String temp = "xxxx";
    //        rs.updateString("Salary",temp);
    //        rs.updateRow();
    //   }
    //   return rs;
    // }

    public ResultSet getResultSet(String query) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String sql = query;
            //exp
           /* CsvWriter writer= new CsvWriter(new FileWriter("new1.csv"),'\t');
            Boolean includeHeaders= ture ;
            */

            //exp
            rs = stmt.executeQuery(sql);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    return rs;
    }

}