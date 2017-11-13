package com.webserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.model.Message;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.webserver.util.AnnotatedDeserializer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.*;

public class SaleMessageHandler implements HttpHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    protected BlockingQueue<Message> queue;
    protected SalesQueueConsumer consumer;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Message.class, new AnnotatedDeserializer<Message>())
            .create();


    public SaleMessageHandler(BlockingQueue queue, SalesQueueConsumer consumer) {
        this.queue = queue;
        this.consumer = consumer;
    }

    public void handle(HttpExchange exchange) throws IOException {
        Message message = null;
        String msg = "POST message recieved";
        int httpResponseCode = HTTP_OK;

        String httpMethod = exchange.getRequestMethod().toUpperCase();

        switch (httpMethod) {
            case "POST":
                logger.info("received POST message from " + exchange.getRemoteAddress());
                Reader reader = new InputStreamReader(exchange.getRequestBody());
//                TODO: also log raw messages received by server
                try {
                    message = gson.fromJson(reader, Message.class);

                } catch (Exception e) {
                    e.printStackTrace();
                    msg = e.getClass().getSimpleName() + ": " + e.getMessage();
                    httpResponseCode = HTTP_BAD_REQUEST;
                    logger.severe(msg);
                }

                if (message != null) {
                    logger.info("message rcvd: " + ": " + message);
                    queue.add(message);
                    try {
                        httpResponseCode = consumer.consume(queue);
                        msg = "message succesfully consumed";
                    } catch (Exception e) {
                        httpResponseCode = HTTP_BAD_REQUEST;
                        msg = e.getClass().getSimpleName() + ": " + e.getMessage();
                    }

                }

                exchange.sendResponseHeaders(httpResponseCode, msg.length());
                break;

            default:
                msg = "only POST messages allowed at this endpoint";
                httpResponseCode = HTTP_BAD_METHOD;
                exchange.sendResponseHeaders(httpResponseCode, msg.length());
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(msg.getBytes());
        responseBody.close();
    }
}

