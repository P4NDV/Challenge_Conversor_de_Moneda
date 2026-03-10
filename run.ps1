Set-Location $PSScriptRoot

# Compila fuentes Java con Gson en classpath
javac -cp "lib\gson-2.10.1.jar" -d out src\main\java\challengeconversordemonedas\*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error de compilacion." -ForegroundColor Red
    exit 1
}

# Ejecuta en modo interactivo (sin redireccionar input)
java -cp "out;lib\gson-2.10.1.jar" challengeconversordemonedas.ConversorApp
