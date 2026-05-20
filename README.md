# Rezervare bilete avion (Zboruri, Pasageri, Bilete)

## Build and run JavaFX

### Compile the program
```bash
javac --module-path "..\javafx-sdk-17.0.19\lib" --add-modules javafx.controls -cp ".;utils\mssql-jdbc-13.4.0.jre11.jar" app\Main.java ui\AppUI.java dao\*.java database\*.java model\*.java model\enums\*.java model\interfaces\*.java
```

### Run the program
```bash
java --module-path "..\javafx-sdk-17.0.19\lib" --add-modules javafx.controls -cp ".;utils\mssql-jdbc-13.4.0.jre11.jar;app" Main
```

# Steps to make SSMS work properly
* Run BazaDeDate.sql in SSMS
* Configuration for SSMS make sure TCP/IP is Enabled (port 1433)
* ```SQL/mssql-jdbc_auth-13.4.0.x64.dll```  put in ```C:\Program Files\Microsoft\jdk-11.0.12.7-hotspot\bin```
