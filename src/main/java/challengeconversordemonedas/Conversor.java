package challengeconversordemonedas;

import java.io.IOException;
import java.util.Scanner;

public class Conversor {

    public static void eleccionUserMenu() throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        if (!ExchangeRateService.isApiKeyConfigurada()) {
            System.out.println("No se detectó la API key de ExchangeRate.");
            System.out.println(ExchangeRateService.getMensajeApiKey());
            System.out.print("Ingrese su API key (o escriba SALIR): ");
            String apiKeyIngresada = sc.nextLine().trim();
            if (apiKeyIngresada.equalsIgnoreCase("SALIR") || apiKeyIngresada.isBlank()) {
                System.out.println("Saliendo...");
                return;
            }
            ExchangeRateService.setApiKeyRuntime(apiKeyIngresada);
        }

        while (true) {
            mostrarMenu();
            System.out.print("Ingrese una opción: ");
            int opcion = leerEntero(sc);

            if (opcion == 9) {
                System.out.println("Saliendo...");
                break;
            }
            String[] par = obtenerParDivisasPorOpcion(opcion);
            if (par == null) {
                System.out.println("Elija una opción válida.");
                continue;
            }

            Double cantidad = leerDecimal(sc);
            if (cantidad == null) {
                System.out.println("Cantidad inválida.");
                continue;
            }

            String from = par[0];
            String to = par[1];

            try {
                double tasa = ExchangeRateService.obtenerTasa(from, to);
                double resultado = ConversionUtils.convert(cantidad, tasa);
                System.out.printf("El valor %.2f [%s] corresponde al valor final de =>>> %.2f [%s]\n",
                        cantidad, from, resultado, to);
            } catch (Exception e) {
                System.out.println("Error obteniendo la tasa: " + e.getMessage());
            }

            System.out.println();
        }

        // No cerramos System.in para evitar problemas de entrada en algunas consolas/IDE.
    }

    private static void mostrarMenu() {
        System.out.println("*********************************************");
        System.out.println("Sea bienvenido/a al Conversor de Moneda =]");
        System.out.println();
        System.out.println("1) Dólar =>> Peso argentino");
        System.out.println("2) Peso argentino =>> Dólar");
        System.out.println("3) Dólar =>> Real brasileño");
        System.out.println("4) Real brasileño =>> Dólar");
        System.out.println("5) Dólar =>> Peso colombiano");
        System.out.println("6) Peso colombiano =>> Dólar");
        System.out.println("7) Dólar =>> Peso mexicano");
        System.out.println("8) Peso mexicano =>> Dólar");
        System.out.println("9) Salir");
        System.out.println("Elija una opción válida:");
        System.out.println("*********************************************");
    }

    static String[] obtenerParDivisasPorOpcion(int opcion) {
        return switch (opcion) {
            case 1 -> new String[]{"USD", "ARS"};
            case 2 -> new String[]{"ARS", "USD"};
            case 3 -> new String[]{"USD", "BRL"};
            case 4 -> new String[]{"BRL", "USD"};
            case 5 -> new String[]{"USD", "COP"};
            case 6 -> new String[]{"COP", "USD"};
            case 7 -> new String[]{"USD", "MXN"};
            case 8 -> new String[]{"MXN", "USD"};
            default -> null;
        };
    }

    private static int leerEntero(Scanner sc) {
        try {
            String valor = sc.nextLine().trim();
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return -1;
        }
    }

    private static Double leerDecimal(Scanner sc) {
        try {
            System.out.print("Ingrese cuánto desea convertir: ");
            String valor = sc.nextLine().trim().replace(',', '.');
            return Double.parseDouble(valor);
        } catch (Exception e) {
            return null;
        }
    }
}
