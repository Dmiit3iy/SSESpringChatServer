package org.dmiit3iy.servise;



import org.dmiit3iy.event.MessageEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ApplicationEventPublisher eventPublisher;
    private ExecutorService singleThreadExecutor;

   // private BlockingQueue<Message> messageBlockingQueue = new LinkedBlockingQueue<>();

    public ChatService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void start() {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() -> {
            try {
                while (true) {
                    logger.info("Chat service started");
                    //Message message = messageBlockingQueue.take();
                  //  eventPublisher.publishEvent(new Event(this, message.getUser().getLogin(), message.getMessage()));
                    eventPublisher.publishEvent(new MessageEvent(this, "winner", "Victory"));
                    logger.info("Chat service finished");
                    Thread.sleep(5000);
                    logger.info("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
                }
            } catch (Exception e) {
                logger.error("Chat service failed", e);
            }
        });
    }

    @PreDestroy
    public void onDestroy() {
        singleThreadExecutor.shutdownNow();
    }
}
