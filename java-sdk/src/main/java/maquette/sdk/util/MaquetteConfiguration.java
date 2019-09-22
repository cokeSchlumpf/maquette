package maquette.sdk.util;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import okhttp3.Request;

@Value
@Wither
@AllArgsConstructor(staticName = "apply")
public class MaquetteConfiguration {

    private String baseUrl;

    private String user;

    private String token;

    public static MaquetteConfiguration apply() {
        return apply("http://localhost:8080/api/v1", "hippo", null);
    }

    public Request.Builder createRequestFor(String url, Object ...args) {
        Request.Builder builder = new Request.Builder()
            .url(String.format("%s%s", baseUrl, String.format(url, args)))
            .header("x-user-id", user);

        if (token != null) {
            builder = builder.header("x-user-token", token);
        }

        return builder;

    }

}
