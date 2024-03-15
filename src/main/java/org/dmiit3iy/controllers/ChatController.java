package org.dmiit3iy.controllers;


import org.dmiit3iy.dto.ResponseResult;
import org.dmiit3iy.event.MessageEvent;
import org.dmiit3iy.model.Message;
import org.dmiit3iy.model.User;
import org.dmiit3iy.repository.SseEmitters;
import org.dmiit3iy.servise.ChatService;
import org.dmiit3iy.servise.MessageService;
import org.dmiit3iy.servise.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/sse2")
public class ChatController implements ApplicationListener<MessageEvent> {

    @Value("${user.id}")
    private long id;
    final private ChatService chatService;
    @Autowired
    //без нее ошибка Null Caused by: java.lang.NullPointerException: Cannot invoke "org.dmiit3iy.servise.UserService.get(long)" because "this.userService" is null
    private SseEmitters emitters;

    @Autowired
    public void setEmitters(SseEmitters emitters) {
        this.emitters = emitters;
    }

    @Autowired //без нее ошибка Null Caused by:...
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired //без нее ошибка Null Caused by:...
    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }


    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostConstruct
    void init() {
        chatService.start();
    }


    @GetMapping(path = "/chat/${user.id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getChatUsers(@RequestParam long idUser) {

       User user= userService.get(id);
        System.out.println(user);
        return emitters.add(new SseEmitter(60000L), idUser);
    }

    @Value("${user.id}")
    @PostMapping(path = "message/${user.id}")
    public ResponseEntity<ResponseResult<Message>> sendMessage(@RequestParam long idUser, @RequestParam String message) {
        try {
            User user = userService.get(idUser);
            if (user == null) {
                throw new IllegalArgumentException();
            }
            Message message1 = new Message(message);

            message1.setUser(user);
            messageService.add(message1);
            chatService.addMessage(message1);
            return new ResponseEntity<>(new ResponseResult<>(null, message1), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void onApplicationEvent(MessageEvent event) {
        emitters.send(event.getEvent());
    }
}
