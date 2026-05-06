# Rezervare bilete avion (Zboruri, Pasageri, Bilete)

### Compile the program
```bash
javac --module-path "d:\Practica-2\javafx-sdk-17.0.19\lib" --add-modules javafx.controls Main.java
```

### Run the program
```bash
java --module-path "d:\Practica-2\javafx-sdk-17.0.19\lib" --add-modules javafx.controls -cp ".;lib\\mssql-jdbc-11.2.3.jre11.jar" Main
```

### Required runtime files
- `lib\mssql-jdbc-11.2.3.jre11.jar` must exist before running the app.
- For Windows Authentication, the Microsoft SQL Server native authentication DLL must also be available on the machine and reachable by the JVM.
- The SQL script in `BazaDeDate.sql` must already be executed in SQL Server so the database exists before connecting.
