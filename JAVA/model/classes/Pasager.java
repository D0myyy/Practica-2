class Pasageri extends Persoana(
    int IdPasager;
    String Email;
    String Telefon;

    Pasageri(String Nume, String Prenume, int IdPasager, String Email, String Telefon){
        super(Nume, Prenume);
        this.IdPasager = IdPasager;
        this.Email = Email;
        this.Telefon = Telefon;
    }
)