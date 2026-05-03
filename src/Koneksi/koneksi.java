package Koneksi;
import java.sql.*;
 
public class koneksi {
    private Connection koneksi;
    public Connection connect(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("berhasil connect");
        }
        catch(ClassNotFoundException ex){
            System.out.println("gagal koneksi"+ex);
        }
        String url = "jdbc:mysql://localhost:3307/tmasch_db";
        try{
            koneksi = DriverManager.getConnection(url,"root","");
            System.out.println("berhasil koneksi database");
        }
        catch (SQLException ex){
            System.out.println("gagal koneksi database"+ex);
        }
        return koneksi;
    }
}