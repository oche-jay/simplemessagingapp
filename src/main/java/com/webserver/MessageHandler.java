package com.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_OK;

public class MessageHandler implements HttpHandler {


    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void handle(HttpExchange exchange) throws IOException {
        logger.info("received message");
        String msg = "Simple sales processor v 1.0";
        exchange.sendResponseHeaders(HTTP_OK, msg.length());
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(msg.getBytes());
        responseBody.close();
    }
}

