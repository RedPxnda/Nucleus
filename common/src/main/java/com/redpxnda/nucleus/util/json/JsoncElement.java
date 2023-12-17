package com.redpxnda.nucleus.util.json;

public abstract class JsoncElement {
    public static final String INDENT = " ".repeat(4);
    protected String comment;

    public void writeComments(StringBuilder builder, int depth) {
        String[] comments = getCommentLines();
        if (comments.length > 0 && !comment.isBlank()) {
            String currentIndent = INDENT.repeat(depth);
            if (comments.length == 1) builder.append(currentIndent).append("// ").append(comment).append('\n');
            else {
                builder.append(currentIndent).append("/*\n");
                for (String comment : comments)
                    builder.append(currentIndent).append(comment).append('\n');
                builder.append(currentIndent).append("*/\n");
            }
        }
    }

    public abstract JsoncElement copy();

    public abstract String toString(int depth);

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String[] getCommentLines() {
        return comment.split("\\R");
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
