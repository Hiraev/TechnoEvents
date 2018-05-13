public class Post {

    private String headLine;
    private String link;

    public Post(String headLine, String link) {
        this.headLine = headLine;
        this.link = link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getLink() {
        return link;
    }

    public String getHeadLine() {
        return headLine;
    }

    @Override
    public String toString() {
        return headLine + "\n" + link;
    }
}
