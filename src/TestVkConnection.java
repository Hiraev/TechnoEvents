import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.responses.GetResponse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class TestVkConnection {
    public static void main(String[] args) {

        /* Получаем appId и accessToken из конфигурационного файла */
        String accessToken = "";
        int appId = 0;
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("resources/vk.cfg"));
            appId = Integer.valueOf(properties.getProperty("app_id"));
            accessToken = properties.getProperty("access_token");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* ПЕРВЫЙ СПОСОБ */

        String url = "https://api.vk.com/method/wall.get?access_token=" + accessToken + "&owner_id=-52224211&count=10&v=5.26";
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(1200);
            connection.setReadTimeout(1000);
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }


        /* ВТОРОЙ СПОСОБ ПОЛУЧЕНИЯ, С ИСПОЛЬЗОВАНИЕМ VK SDK */

        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        ServiceActor serviceActor = new ServiceActor(appId, accessToken);
        GetResponse response = null;
        try {
            response = vk
                    .wall()
                    .get(serviceActor)
                    .ownerId(-52224211)
                    .count(1)
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
        System.out.println(response.getItems().get(0).getText());
    }
}
