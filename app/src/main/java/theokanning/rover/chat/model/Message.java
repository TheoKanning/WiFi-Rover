package theokanning.rover.chat.model;

/**
 * Message sent between chat clients, each client is responsible for serializing tag and contents
 *
 * @author Theo Kanning
 */
public final class Message {
    public enum Tag{
        DISPLAY,
        ROBOT,
        TEST
    }

    private Tag tag;
    private String contents;

    private Message(){
        //block instantiation without setting parameters
    }

    public Message(Tag tag, String contents) {
        this.tag = tag;
        this.contents = contents;
    }

    public Tag getTag() {
        return tag;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "Message{" +
                "tag=" + tag +
                ", contents='" + contents + '\'' +
                '}';
    }
}
