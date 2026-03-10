package challengeconversordemonedas;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeRateService {

    private static final String API_BASE = "https://v6.exchangerate-api.com/v6/";
    // Fallback para uso local: evita depender de variables de entorno en cada ejecución.
    private static final String API_KEY_FALLBACK = "TU_API_KEY_AQUI";
    private static final Map<String, Double> CACHE_TASAS = new ConcurrentHashMap<>();
    private static volatile String apiKeyRuntime;

    public static void setApiKeyRuntime(String apiKey) {
        apiKeyRuntime = (apiKey == null || apiKey.isBlank()) ? null : apiKey.trim();
    }

    public static boolean isApiKeyConfigurada() {
        String apiKey = resolverApiKey();
        return apiKey != null && !apiKey.isBlank();
    }

    public static String getMensajeApiKey() {
        return "Configura EXCHANGE_API_KEY o ingrésala al iniciar el programa.";
    }

    public static double obtenerTasa(String from, String to) throws Exception {
        String keyPar = from + "->" + to;
        Double tasaEnCache = CACHE_TASAS.get(keyPar);
        if (tasaEnCache != null) {
            return tasaEnCache;
        }

        String apiKey = resolverApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("La variable de entorno EXCHANGE_API_KEY no está configurada.");
        }

        String url = API_BASE + apiKey + "/pair/" + from + "/" + to;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error HTTP: " + response.statusCode() + " - " + response.body());
        }

        Gson gson = new Gson();
        JsonObject json = gson.fromJson(response.body(), JsonObject.class);

        // Dependiendo de la API, el campo puede llamarse conversion_rate
        if (json.has("conversion_rate")) {
            double tasa = json.get("conversion_rate").getAsDouble();
            CACHE_TASAS.put(keyPar, tasa);
            return tasa;
        }

        // Fallback: intentar buscar 'conversion_rates' o 'rate'
        if (json.has("conversion_rates")) {
            JsonObject rates = json.getAsJsonObject("conversion_rates");
            if (rates.has(to)) {
                double tasa = rates.get(to).getAsDouble();
                CACHE_TASAS.put(keyPar, tasa);
                return tasa;
            }
        }

        throw new RuntimeException("No se encontró la tasa de conversión en la respuesta: " + response.body());
    }

    private static String resolverApiKey() {
        if (apiKeyRuntime != null && !apiKeyRuntime.isBlank()) {
            return apiKeyRuntime;
        }
        String apiKeyEnv = System.getenv("EXCHANGE_API_KEY");
        if (apiKeyEnv != null && !apiKeyEnv.isBlank()) {
            return apiKeyEnv;
        }
        return API_KEY_FALLBACK;
    }
}
