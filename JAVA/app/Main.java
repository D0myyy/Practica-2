import database.DatabaseInitializer;
import dao.PasagerDAO;
import model.Pasager;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseInitializer.initialize();
            PasagerDAO pasagerDAO = new PasagerDAO();
            System.out.println("Conectat la baza de date.");
            System.out.println("Pasageri gasiti: " + pasagerDAO.findAll().size());
            Pasager first = pasagerDAO.findAll().stream().findFirst().orElse(null);
            if (first != null) {
                System.out.println(first.getDescrierePersoana());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}