package org.dmiit3iy.servise;

import org.dmiit3iy.model.Message;
import org.dmiit3iy.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;

    @Autowired
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void add(Message message) {
        try {
            messageRepository.save(message);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("This message is already added");
        }
    }

    @Override
    public List<Message> get() {
        return messageRepository.findAll();
    }

    @Override
    public Message get(long id) {
        return messageRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Message does not exists!"));
    }
}
