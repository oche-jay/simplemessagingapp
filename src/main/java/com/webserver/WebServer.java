package com.webserver;


import com.model.Message;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class WebServer {

    private final static Logger logger = Logger.getLogger(WebServer.class.getName());
    
    @Setter
    static int port = 8000;
    
    @Setter
    static int queue_capacity = 1000;
    
    @Getter
    static BlockingQueue<Message> queue;
    
    @Getter
    static SalesQueueConsumer consumer;

    public WebServer() {
        queue = new ArrayBlockingQueue<>(queue_capacity);
        consumer = new SalesQueueConsumer(queue);
    }

    public static void startServer() throws IOException {
        logger.info("Starting message server on port " + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new MessageHandler());
        server.createContext("/sales", new SaleMessageHandler(queue, consumer));
        server.setExecutor(null);
        server.start();
    }
    
    
    public static void main(String[] args) throws Exception {
        startServer();
    }

}
