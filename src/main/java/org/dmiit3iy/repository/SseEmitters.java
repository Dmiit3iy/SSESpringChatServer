package org.dmiit3iy.repository;

import org.dmiit3iy.model.User;
import org.dmiit3iy.servise.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Repository
public class SseEmitters {


    private SseOnlineEmitters sseOnlineEmitters;

    @Autowired
    public void setSseOnlineEmitters(SseOnlineEmitters sseOnlineEmitters) {
        this.sseOnlineEmitters = sseOnlineEmitters;
    }

    private static final Logger logger = LoggerFactory.getLogger(SseEmitters.class);

    private static ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> map = new ConcurrentHashMap<>();


    public SseEmitter add(SseEmitter emitter, long idUser) {
        addInMap(emitter, idUser);
        sseOnlineEmitters.send(sseOnlineEmitters.getOnlineUsers());
        emitter.onCompletion(() -> {
            logger.info("Emitter completed: {}", emitter);
            remove(emitter, idUser);

            sseOnlineEmitters.send(sseOnlineEmitters.getOnlineUsers());
        });
        emitter.onTimeout(() -> {
            logger.info("Emitter timed out: {}", emitter);
            emitter.complete();
            remove(emitter, idUser);
            sseOnlineEmitters.send(sseOnlineEmitters.getOnlineUsers());
        });

        emitter.onError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                logger.info("Emitter error: {}", emitter);
                remove(emitter, idUser);
                sseOnlineEmitters.send(sseOnlineEmitters.getOnlineUsers());
            }
        });

        return emitter;
    }

    public void send(Object obj) {
        logger.info("Emitters current before deleting: {}", getOnlineEmitters());
        List<SseEmitter> failedEmitters = new ArrayList<>();
        CopyOnWriteArrayList<SseEmitter> emittersOnLine = getOnlineEmitters();
        emittersOnLine.forEach(emitter -> {

            Thread thread = new Thread(() -> {
                try {
                    emitter.send(obj);
                } catch (Exception e) {
                    emitter.completeWithError(e);
                    failedEmitters.add(emitter);
                    logger.error("Emitter failed: {}", emitter, e);
                }
            });
            thread.start();
        });


        for (SseEmitter s : failedEmitters) {
            removeByEmitter(s);
        }
        logger.info("Emitters current: {}", getOnlineEmitters());
        logger.info("Removed Emitters current: {}", failedEmitters);
    }


    public ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> getMap() {
        return map;
    }

    public static void addInMap(SseEmitter sseEmitter, long userId) {
        CopyOnWriteArrayList<SseEmitter> list = map.getOrDefault(userId, new CopyOnWriteArrayList<>());
        list.add(sseEmitter);
        map.put(userId, list);
    }

    public static void remove(SseEmitter emitter, long userId) {
        if (userId > 0) {
            CopyOnWriteArrayList<SseEmitter> list = map.getOrDefault(userId, new CopyOnWriteArrayList<>());
            list.remove(emitter);
            map.put(userId, list);
        }
    }


    public static void removeByEmitter(SseEmitter emitter) {
        for (var pair : map.entrySet()) {
            CopyOnWriteArrayList<SseEmitter> list = pair.getValue();
            list.remove(emitter);
            map.put(pair.getKey(), list);
        }
    }


    public static CopyOnWriteArrayList<Long> getOnlineUsers() {
        CopyOnWriteArrayList<Long> list1 = new CopyOnWriteArrayList<>();
        for (var pair : map.entrySet()) {
            if (!pair.getValue().isEmpty()) {
                list1.add(pair.getKey());
            }
        }
        return list1;
    }

    public static CopyOnWriteArrayList<SseEmitter> getOnlineEmitters() {
        CopyOnWriteArrayList<SseEmitter> list1 = new CopyOnWriteArrayList<>();
        for (var pair : map.entrySet()) {
            if (!pair.getValue().isEmpty()) {
                list1.addAll(pair.getValue());
            }
        }
        return list1;
    }
}