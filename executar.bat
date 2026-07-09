@echo off
chcp 65001 >nul
if not exist bin\com\estoque\Main.class (
    echo Classes nao encontradas. Compilando...
    call compilar.bat
    if %ERRORLEVEL% NEQ 0 exit /b 1
)
java -Dfile.encoding=UTF-8 -cp bin com.estoque.Main
