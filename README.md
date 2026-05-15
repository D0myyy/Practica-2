# Rezervare bilete avion (Zboruri, Pasageri, Bilete)

## Build and run
```bash
javac -cp ".;..\utils\mssql-jdbc-13.4.0.jre11.jar" Main.java && java -cp ".;..\utils\mssql-jdbc-13.4.0.jre11.jar" Main
```

### Compile the program
```bash
cd JAVA
javac -cp ".;utils\mssql-jdbc-13.4.0.jre11.jar" app\Main.java
```

### Run the program
```bash
cd JAVA
java -cp ".;utils\mssql-jdbc-13.4.0.jre11.jar;app" Main
```

# Steps to make SSMS work properly
* Run BazaDeDate.sql in SSMS
* Configuration for SSMS make sure TCP/IP is Enabled (port 1433)
* ```SQL/mssql-jdbc_auth-13.4.0.x64.dll```  put in ```C:\Program Files\Microsoft\jdk-11.0.12.7-hotspot\bin```
