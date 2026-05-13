@echo off
REM Launcher for Rezervare Bilete Avion Application

set JAVA_HOME=C:\Users\L213_PC07\.jdk\jdk-17.0.16
set FX_PATH=C:\Practica-2\JAVA\javafx-sdk-17.0.19\lib
set APP_PATH=C:\Practica-2\JAVA

cd %APP_PATH%

%JAVA_HOME%\bin\java.exe ^
  --module-path %FX_PATH% ^
  --add-modules javafx.controls,javafx.fxml ^
  -cp "out;utils\mssql-jdbc-13.4.0.jre11.jar" ^
  ui.RezervareBileteApp

pause
