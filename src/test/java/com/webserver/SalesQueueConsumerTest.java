package com.webserver;

import com.model.Instruction;
import com.model.Message;
import com.model.Sale;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.model.Instruction.*;

public class SalesQueueConsumerTest {

    public static Object[][] multipleItemsWithDifferentInstructions = new Object[][]{
            //product | value | instuction | total | count
            {"pear", 5.00, null, 5.00, 1},
            {"orange", 5.00, null, 5.00, 1},
            {"orange", 4.00, null, 9.00, 2},
            {"orange", 3.00, null, 12.00, 3},
            {"orange", 8.00, null, 20.00, 4},
            {"banana", 5.00, null, 5.00, 1},
            {"banana", 4.00, null, 9.00, 2},
            {"banana", 3.00, null, 12.00, 3},
            {"banana", 8.00, null, 20.00, 4},
            {"apple", 5.00, null, 5.00, 1},
            {"apple", 4.00, null, 9.00, 2},
            {"apple", 3.00, null, 12.00, 3},
            {"apple", 8.00, null, 20.00, 4},
            {"apple", 1.50, ADD, 26.00, 4},
            {"apple", 1.50, ADD, 32.00, 4},
            {"orange", 1.50, ADD, 26.00, 4},
            {"orange", 1.50, ADD, 32.00, 4},
            {"orange", 5.00, null, 37.00, 5},
            {"pear", 4.00, null, 9.00, 2},
            {"pear", 3.00, null, 12.00, 3},
            {"pear", 8.00, null, 20.00, 4},
            {"pear", 1.50, ADD, 26.00, 4},
            {"pear", 1.50, ADD, 32.00, 4},
            {"orange", 8.00, null, 45.00, 6},
            {"orange", 1.50, SUBTRACT, 36.00, 6},
            {"orange", 3.00, SUBTRACT, 18.00, 6},
            {"orange", 2.00, MULTIPLY, 36.00, 6},
            {"orange", 2.00, MULTIPLY, 72.00, 6},
            {"orange", 8.00, null, 80.00, 7},
            {"orange", 8.00, null, 88.00, 8},
            {"banana", 1.50, ADD, 26.00, 4},
            {"banana", 1.50, ADD, 32.00, 4},
            {"banana", 5.00, null, 37.00, 5},
            {"banana", 8.00, null, 45.00, 6},
            {"banana", 1.50, SUBTRACT, 36.00, 6},
            {"banana", 3.00, SUBTRACT, 18.00, 6},
            {"banana", 2.00, MULTIPLY, 36.00, 6},
            {"banana", 2.00, MULTIPLY, 72.00, 6},
            {"banana", 8.00, null, 80.00, 7},
            {"banana", 8.00, null, 88.00, 8},
            {"pear", 5.00, null, 37.00, 5},
            {"pear", 8.00, null, 45.00, 6},
            {"pear", 1.50, SUBTRACT, 36.00, 6},
            {"pear", 3.00, SUBTRACT, 18.00, 6},
            {"pear", 2.00, MULTIPLY, 36.00, 6},
            {"pear", 2.00, MULTIPLY, 72.00, 6},
            {"pear", 8.00, null, 80.00, 7},
            {"pear", 8.00, null, 88.00, 8},
            {"apple", 5.00, null, 37.00, 5},
            {"apple", 8.00, null, 45.00, 6},
    };
    ;
    BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1000);
    SalesQueueConsumer salesQueueConsumer = new SalesQueueConsumer();

    public void refresh() {
        queue = new ArrayBlockingQueue<>(1000);
        salesQueueConsumer = new SalesQueueConsumer();
        salesQueueConsumer.setSLEEP_DURATION(10);
    }

    public Sale processMessage(Message message) throws InterruptedException {
        String product = message.getProduct();
        queue.add(message);
        salesQueueConsumer.consume(queue);
        Map<String, Sale> mp = salesQueueConsumer.getMap();
        return mp.get(product);
    }

    @Test(dataProvider = "sameItems")
    public void canProcessSeveralOfTheSameProduct(String product, double value, double expectedTotal) throws Exception {
        Message message = TestHelper.createMessage(product, value);
        Sale saleInfo = processMessage(message);
        Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
    }

    @Test(dataProvider = "sameItemMultipleOccurrences")
    public void canProcessSeveralOfTheSameProductWithMultipleOccurences(String product, double value, int occurrences, double expectedTotal) throws Exception {
        Message message = TestHelper.createMessage(product, value, occurrences);
        Sale saleInfo = processMessage(message);
        Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
    }

    @Test(dataProvider = "twoDifferentItems")
    public void canProcessTwoDifferentProducts(String product, double value, double expectedTotal) throws Exception {
        Message message = TestHelper.createMessage(product, value);
        Sale saleInfo = processMessage(message);
        Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
    }

    @Test(dataProvider = "severalDifferentItems")
    public void canProcessSeveralDifferentItems(String product, double value, double expectedTotal) throws Exception {
        Message message = TestHelper.createMessage(product, value);
        Sale saleInfo = processMessage(message);
        Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
    }

    @Test(dataProvider = "sameItemsWithAddInstruction")
    public void canProcessSameItemsWithAddition(String product, double value, Instruction instruction, double expectedTotal) throws Exception {
        Message message = TestHelper.createMessage(product, value, instruction);
        Sale saleInfo = processMessage(message);
        Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
    }

    @Test(dataProvider = "sameItemsWithSubtractInstruction")
    public void canProcessSameItemsWithSubtraction(String product, double value, Instruction instruction, double expectedTotal) throws Exception {
        Message message = TestHelper.createMessage(product, value, instruction);
        Sale saleInfo = processMessage(message);
        Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
    }

    @Test(dataProvider = "sameItemsWithMultiplicationInstruction")
    public void canProcessSameItemsWithMultiplication(String product, double value, Instruction instruction, double expectedTotal) throws Exception {
        Message message = TestHelper.createMessage(product, value, instruction);
        Sale saleInfo = processMessage(message);
        Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
    }

    @Test(dataProvider = "singleItemsWithDifferentInstructions")
    public void canProcessSingleItemWithDifferentInstructions(String product, double value, Instruction instruction, double expectedTotal, int count) throws Exception {
        Message message = TestHelper.createMessage(product, value, instruction);
        Sale saleInfo = processMessage(message);

        Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
        Assert.assertEquals(saleInfo.getTotalItemsSold().intValueExact(), count);
    }

    @Test(dataProvider = "multipleItemsWithDifferentInstructions")
    public void canProcessManyItemsWithDifferentInstructions(String product, double value, Instruction instruction, double expectedTotal, int count) throws Exception {
        Message message = TestHelper.createMessage(product, value, instruction);
        Sale saleInfo = processMessage(message);
        Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
        Assert.assertEquals(saleInfo.getTotalItemsSold().intValueExact(), count);
    }

    @Test(dataProvider = "badData", expectedExceptions = ArithmeticException.class)
    public void canProcessBadData(String product, double value, Instruction instruction, double expectedTotal, int count) throws Exception {
        Message message = TestHelper.createMessage(product, value, instruction);
        Sale saleInfo = processMessage(message);
        if (saleInfo != null) {
            Assert.assertEquals(saleInfo.getRunningTotal().doubleValue(), expectedTotal);
            Assert.assertEquals(saleInfo.getTotalItemsSold().intValueExact(), count);
        }
    }

    @Test(dataProvider = "lotsOfRandomMessages")
    public void canProcessRandomDataWithoutCrashing(String product, double value, Instruction instruction) throws Exception {
        Message message = TestHelper.createMessage(product, value, instruction);
        try {
            processMessage(message);
        } catch (ArithmeticException ae) {
            ae.printStackTrace();
        }

    }

    @DataProvider
    public Object[][] lotsOfRandomMessages() {
        refresh();
        int noOfMessages = 500;
        String[] products = {"orange", "apple", "banana", "grapes"};
        return TestHelper.createRandomMessages(noOfMessages, products);
    }

    @DataProvider
    public Object[][] badData() {
        refresh();
        return new Object[][]{
                {"orange", 5.00, ADD, 5.00, 1},
        };
    }

    @DataProvider
    public Object[] sameItems() {
        refresh();
        return new Object[][]{
                //product | value | total |
                {"orange", 5.00, 5.00},
                {"orange", 5.00, 10.00},
                {"orange", 5.00, 15.00},
                {"orange", 5.00, 20.00},
                {"orange", 5.00, 25.00},
                {"orange", 5.00, 30.00}
        };
    }

    @DataProvider
    public Object[] sameItemsWithAddInstruction() {
        refresh();
        return new Object[][]{
                //product | value | instruction, expectedTotal |
                {"orange", 5.00, null, 5.00},
                {"orange", 5.00, null, 10.00},
                {"orange", 5.00, null, 15.00},
                {"orange", 5.00, ADD, 30.00}, //assume we sold 3 at (5 + 5)
                {"orange", 2.00, ADD, 36.00}, //assume we sold 3 at (5 + 5 + 2)
                {"orange", 7.00, ADD, 57.00}, //assume we sold 3 at (5 + 5 + 2 + 7)
                {"orange", 4.00, null, 61.00}, //assume we sold 1 at 4.00 plus 3 at (3 * (5 + 5 + 2 + 7))
        };
    }

    @DataProvider
    public Object[] sameItemsWithSubtractInstruction() {
        refresh();
        return new Object[][]{
                //product | value | instruction, expectedTotal |
                {"orange", 5.00, null, 5.00},
                {"orange", 5.00, null, 10.00},
                {"orange", 5.00, null, 15.00},
                {"orange", 1.00, SUBTRACT, 12.00}, //assume we sold 3 at (5 - 1)
                {"orange", 1.00, SUBTRACT, 9.00}, //assume we sold 3 at (5 - 1 - 1)
                {"orange", 0.01, SUBTRACT, 8.97}, //assume we sold 3 at (5 - 1 - 1 - 0.01)
                {"orange", 4.00, null, 12.97}, //assume we sold 3 at (5 + 5 + 2 + 7)) AND 1 at 4.00
        };
    }

    @DataProvider
    public Object[] sameItemsWithMultiplicationInstruction() {
        refresh();
        return new Object[][]{
                //product | value | instruction, expectedTotal |
                {"orange", 5.00, null, 5.00},
                {"orange", 5.00, null, 10.00},
                {"orange", 5.00, null, 15.00},
                {"orange", 1.00, MULTIPLY, 15.00}, //assume we sold 3 at (5 - 1)
                {"orange", 0.50, MULTIPLY, 7.5}, //assume we sold 3 at (5 - 1 - 1)
                {"orange", 10.00, MULTIPLY, 75.00}, //assume we sold 3 at (5 - 1 - 1 - 0.01)
                {"orange", 4.00, null, 79.00}, //assume we sold 3 at (5 + 5 + 2 + 7)) AND 1 at 4.00
        };
    }

    @DataProvider
    public Object[] sameItemSixTimesWithAddition() {
        refresh();
        return new Object[][]{
                //product | value | |total |
                {"orange", 5.00, 5.00},
                {"orange", 5.00, 10.00},
                {"orange", 5.00, 15.00},
                {"orange", 5.00, 20.00},
                {"orange", 5.00, 25.00},
                {"orange", 5.00, 30.00}
        };
    }

    @DataProvider
    public Object[] sameItemMultipleOccurrences() {
        refresh();
        return new Object[][]{
                //product | value | freq | total
                {"orange", 5.00, 2, 10.00},
                {"orange", 5.00, 3, 25.00},
                {"orange", 5.00, 3, 40.00},
                {"orange", 5.00, 7, 75.00},
                {"orange", 5.00, 1, 80.00},
        };
    }

    @DataProvider
    public Object[] twoDifferentItems() {
        refresh();
        return new Object[][]{
                //product | value | total
                {"orange", 5.00, 5.00},
                {"orange", 5.00, 10.00},
                {"orange", 5.00, 15.00},
                {"apple", 5.00, 5.00},
                {"apple", 5.00, 10.00},
                {"apple", 5.00, 15.00}
        };
    }

    @DataProvider
    public Object[] severalDifferentItems() {
        refresh();
        return new Object[][]{
                //product | value | total
                {"orange", 5.00, 5.00},
                {"banana", 5.00, 5.00},
                {"banana", 5.00, 10.00},
                {"apple", 2.00, 2.00},
                {"apple", 4.00, 6.00},
                {"grapes", 5.00, 5.00},
                {"grapes", 5.00, 10.00},
                {"orange", 5.00, 10.00},
                {"orange", 5.00, 15.00},
                {"silver", 7.00, 7.00},
                {"gold", 9.00, 9.00},
                {"silver", 6.00, 13.00},
        };
    }

    @DataProvider
    public Object[] singleItemsWithDifferentInstructions() {
        refresh();
        return new Object[][]{
                //product | value | instuction | total | count
                {"orange", 5.00, null, 5.00, 1},
                {"orange", 4.00, null, 9.00, 2},
                {"orange", 3.00, null, 12.00, 3},
                {"orange", 8.00, null, 20.00, 4},
                {"orange", 1.50, ADD, 26.00, 4},
                {"orange", 1.50, ADD, 32.00, 4},
                {"orange", 5.00, null, 37.00, 5},
                {"orange", 8.00, null, 45.00, 6},
                {"orange", 1.50, SUBTRACT, 36.00, 6},
                {"orange", 3.00, SUBTRACT, 18.00, 6},
                {"orange", 2.00, MULTIPLY, 36.00, 6},
                {"orange", 2.00, MULTIPLY, 72.00, 6},
                {"orange", 8.00, null, 80.00, 7},
                {"orange", 8.00, null, 88.00, 8},
        };
    }

    @DataProvider
    public Object[] multipleItemsWithDifferentInstructions() {
        refresh();
        return multipleItemsWithDifferentInstructions;
    }


}