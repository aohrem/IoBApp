package de.ifgi.iobapp.model;


import java.util.Comparator;

public class MessageComparator implements Comparator<Message> {
    @Override
    public int compare(Message message1, Message message2) {
        return message1.getTimestamp().compareTo(message2.getTimestamp());
    }
}