package org.dmiit3iy.controllers;


import org.dmiit3iy.event.MessageEvent;
import org.dmiit3iy.repository.SseEmitters;
import org.dmiit3iy.servise.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/sse2")
public class ChatController implements ApplicationListener<MessageEvent> {
    final private ChatService chatService;

    private SseEmitters emitters;

    @Autowired
    public void setEmitters(SseEmitters emitters) {
        this.emitters = emitters;
    }


    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostConstruct
    void init() {
        chatService.start();
    }

    @Value("${user.id}")
    @GetMapping(path = "/chat/${user.id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getChatUsers() {
        return emitters.add(new SseEmitter(60000L), 2);
    }

    @Override
    public void onApplicationEvent(MessageEvent event) {
        emitters.send(event.getEvent());
    }
}
