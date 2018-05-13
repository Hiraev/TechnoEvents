import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class TestVkConnection implements Publisher {
    private Post LAST = new Post("", "");

    private Boolean compare(Post last, Post recent) {
        return last.getLink().equals(recent.getLink());
    }

    @Override
    public void start(final Publish publish) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Post newPost;
                /* Получаем appId и accessToken из конфигурационного файла */
                String accessToken = null;
                try {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream("resources/vk.cfg"));
                    accessToken = properties.getProperty("access_token");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /* ПЕРВЫЙ СПОСОБ */
                final String url = "https://api.vk.com/method/wall.get?access_token=" + accessToken + "&owner_id=-52224211&count=1&v=5.26";
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
                        line = bufferedReader.readLine();
                        //   System.out.println(line);
                        newPost = parse(line);
                        if (!compare(LAST, newPost)) {
                            LAST = newPost; // newPost надо вернуть
                            publish.publish(newPost);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }, 0, 10800000);

    }

    private Post parse(String response) {
        StringBuilder link = new StringBuilder("https://vk.com/wall-52224211_");
        StringBuilder headLine = new StringBuilder();
        int indexId = response.indexOf("id");
        for (int i = 0; i < 4; i++) {
            link.append(response.charAt(indexId + 4 + i));
        }
        int indexHL = response.indexOf("text");
        for (int i = 0; i < 100; i++) {
            headLine.append(response.charAt(indexHL + 7 + i));
        }
        headLine = new StringBuilder(headLine.toString().replace("\\n", "\n"));
        headLine.append("...");
        System.out.println(link + "\n");
        System.out.println(headLine);
        return new Post(headLine.toString(), link.toString());
    }
}
