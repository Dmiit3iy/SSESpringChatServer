package org.dmiit3iy.servise;

import org.dmiit3iy.event.MessageEvent;
import org.dmiit3iy.model.Message;
import org.dmiit3iy.model.User;
import org.dmiit3iy.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;
    private ApplicationEventPublisher eventPublisher;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void add(Message message, long idUser) {
        User user = userService.get(idUser);
        message.setUser(user);
        messageRepository.save(message);
        eventPublisher.publishEvent(new MessageEvent(this, message.getUser().getLogin(), message));
    }

    @Override
    public List<Message> get() {
        return messageRepository.findAll();
    }

    @Override
    public Message get(long id) {
        return messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Message does not exists!"));
    }
}



