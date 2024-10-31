package app.Formats;

public interface FormattingInstance {
    /**
     * Accept method for visitor
     * @param visitor the visitor
     * @param format  the string format for the specific entity
     */
    void accept(FormatVisitor visitor, StringBuilder format);
}
