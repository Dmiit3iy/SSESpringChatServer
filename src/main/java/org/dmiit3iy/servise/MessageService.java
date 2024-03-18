package org.dmiit3iy.servise;

import org.dmiit3iy.model.Message;

import java.util.List;

public interface MessageService {
    void add(Message message, long idUser);
    List<Message> get();
    Message get(long id);
}
