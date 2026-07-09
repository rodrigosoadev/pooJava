@echo off
chcp 65001 >nul
echo Compilando projeto...
if not exist bin mkdir bin

powershell -NoProfile -Command "$files = Get-ChildItem -Path 'src' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }; javac -encoding UTF-8 -d 'bin' $files; exit $LASTEXITCODE"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERRO na compilacao. Verifique as mensagens acima.
    exit /b 1
)
echo.
echo Compilacao concluida com sucesso!
exit /b 0
