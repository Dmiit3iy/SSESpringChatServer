package org.dmiit3iy.controllers;


import org.dmiit3iy.event.OnlineEvent;
import org.dmiit3iy.model.User;
import org.dmiit3iy.repository.SseOnlineEmitters;
import org.dmiit3iy.servise.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/sse/online")
public class OnlineController implements ApplicationListener<OnlineEvent> {
    private SseOnlineEmitters sseOnlineEmitters;
    private UserService userService;

    @Autowired
    public void setSseOnlineEmitters(SseOnlineEmitters sseOnlineEmitters) {
        this.sseOnlineEmitters = sseOnlineEmitters;
    }


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/{idUser}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getChatUsers(@PathVariable long idUser) {
        User user = userService.get(idUser);
        return sseOnlineEmitters.add(new SseEmitter(10000L), idUser);
    }

    @Override
    public void onApplicationEvent(OnlineEvent event) {
        sseOnlineEmitters.send(event.getEvent());
    }
}
