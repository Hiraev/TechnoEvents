import java.util.List;

public interface DBConnection {
    boolean createConnection(String address, String username, String password);

    boolean addUser(int userId, long chatId);

    List<Long> getChatIds();
}
