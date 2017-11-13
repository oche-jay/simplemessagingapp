package com.webserver;

import com.google.gson.Gson;
import com.model.Instruction;
import com.model.Message;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;

public class WebServerTest {


    WebServer webServer = new WebServer();
    int port = 8001;
    int test_sleep_duration = 100;

    @BeforeTest
    public void startServer() throws IOException {
        webServer.setPort(port);
        webServer.getConsumer().setSLEEP_DURATION(test_sleep_duration);
        webServer.startServer();
    }

    @Test(dataProvider = "multipleItemsWithDifferentInstructions")
    public void canSendALotOfMessagesToWebServer(String product, double value, Instruction instruction, double expectedTotal, int coun) throws IOException {
        Message message = TestHelper.createMessage(product, value, instruction);
        given()
                .body(new Gson().toJson(message)).
                when()
                .post("http://localhost:" + port + "/sales")
                .then()
                .statusCode(HTTP_OK);

    }

    @Test(dataProvider = "lotsOfRandomlyGeneratedMessages")
    public void canHandleALotOfRandomMessagesToWebServer(String product, double value, Instruction instruction) throws IOException {
        Message message = TestHelper.createMessage(product, value, instruction);
        given()
                .body(new Gson().toJson(message)).
                when()
                .post("http://localhost:" + port + "/sales");

    }

    @DataProvider
    public Object[][] multipleItemsWithDifferentInstructions() {
        return SalesQueueConsumerTest.multipleItemsWithDifferentInstructions;
    }

    @DataProvider
    public Object[][] lotsOfRandomlyGeneratedMessages() {
        return TestHelper.createRandomMessages(500, new String[]{"crude", "gold", "silver", "platinum"});
    }

}