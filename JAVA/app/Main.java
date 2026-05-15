import java.sql.*;

public class Main {

    public static void main(String[] args) {

        String url =
            "jdbc:sqlserver://localhost:1433;" +
            "databaseName=Rezervare_bilete_avion;" +
            "integratedSecurity=true;" +
            "encrypt=true;" +
            "trustServerCertificate=true";

        try {

            Connection con =
                DriverManager.getConnection(url);

            System.out.println("Conectat!");
            
            // SELECT
            Statement st = con.createStatement();

            ResultSet rs =
                st.executeQuery("SELECT * FROM dbo.Pasageri");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                    rs.getInt("IdPasager") + " " +
                    rs.getString("Nume") + " " +
                    rs.getString("Prenume")
                );
            }

            if (!found) {
                System.out.println("No rows found in Pasageri!");
            }

            // INSERT
            // String sql =
            //     "INSERT INTO Pasageri (Nume, Prenume, Email, Telefon) " +
            //     "VALUES (?, ?, ?, ?)";

            // PreparedStatement ps = con.prepareStatement(sql);

            // ps.setString(1, "Eminescu");
            // ps.setString(2, "Mihai");
            // ps.setString(3, "mihai.eminescu@mail.com");
            // ps.setString(4, "060000999");

            // int rows = ps.executeUpdate();

            // System.out.println("Inserted rows: " + rows);

            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}