@FunctionalInterface
public interface Publish {
    void publish(Post post, boolean isEvent);
}