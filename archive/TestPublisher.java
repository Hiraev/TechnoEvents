import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.WallPostFull;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestPublisher implements Publisher {
    private static TestPublisher instance;
    private VkApiClient vk;
    private ServiceActor actor;
    private List<Integer> posted;

    private TestPublisher() {
        try {
            posted = new ArrayList<>();
            //Загружаем последние отправленные записи
            Files
                    .lines(Paths.get("resources/posted.txt"), StandardCharsets.UTF_8)
                    .map(Integer::valueOf)
                    .forEach(posted::add);

            Properties properties = new Properties();
            properties.load(new FileInputStream("resources/vk.cfg"));
            String accessToken = properties.getProperty("access_token");
            int appId = Integer.valueOf(properties.getProperty("app_id"));
            TransportClient transportClient = HttpTransportClient.getInstance();
            vk = new VkApiClient(transportClient);
            actor = new ServiceActor(appId, accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TestPublisher getInstance() {
        if (instance == null) {
            instance = new TestPublisher();
        }
        return instance;
    }

    @Override
    public void start(Publish publish) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    //Получаем последние 4 поста. Нулевым всегда идет закрепленный
                    List<WallPostFull> posts = vk.wall().get(actor).ownerId(-52224211).count(4).execute().getItems();
                    for (WallPostFull post : posts) {
                        if (!posted.contains(post.getId())) {
                            posted.add(post.getId());
                            String link = "https://vk.com/wall-52224211_" + String.valueOf(post.getId());
                            publish.publish(new Post(post.getText(), link));

                            //Если созранено уже больше 40 номеров записей, удаляем лишние
                            if (posted.size() > 40) {
                                posted = posted.subList(posted.size() - 11, posted.size() - 1);
                            }
                            write();
                        }
                    }
                } catch (ApiException | ClientException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 6L * 1000);
        System.out.println("Process launched");
    }

    private void write() {
        try {
            FileWriter fileWriter = new FileWriter("resources/posted.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            posted.forEach(printWriter::println);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}