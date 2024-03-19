package org.dmiit3iy.controllers;


import org.dmiit3iy.dto.ResponseResult;
import org.dmiit3iy.event.MessageEvent;
import org.dmiit3iy.model.Message;
import org.dmiit3iy.model.User;
import org.dmiit3iy.repository.SseEmitters;

import org.dmiit3iy.servise.MessageService;
import org.dmiit3iy.servise.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/sse/chat")
public class ChatController implements ApplicationListener<MessageEvent> {

    private SseEmitters emitters;
    private UserService userService;
    private MessageService messageService;

    @Autowired
    public void setEmitters(SseEmitters emitters) {
        this.emitters = emitters;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping(path = "/{idUser}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getChatUsers(@PathVariable long idUser) {
        User user = userService.get(idUser);
        return emitters.add(new SseEmitter(10000L), idUser);
    }

    @PostMapping(path = "/message/{idUser}")
    public ResponseEntity<ResponseResult<Message>> get(@PathVariable long idUser,
                                                       @RequestBody Message message) {
        try {
            messageService.add(message, idUser);
            return new ResponseEntity<>(new ResponseResult<>(null, message), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseResult<List<Message>>> get() {
        try {
            List<Message> messageList = messageService.get();
            return new ResponseEntity<>(new ResponseResult<>(null, messageList), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void onApplicationEvent(MessageEvent event) {
        emitters.send(event.getEvent());
    }
}
