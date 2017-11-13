package com.webserver;

import com.model.Instruction;
import com.model.Message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

import static com.model.Instruction.*;

public class TestHelper {
    public static Message createMessage(String product, double value) {
        Message message = new Message();
        message.setProduct(product);
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        message.setValue(bd);
        return message;
    }

    public static Message createMessage(String product, double value, int occurrence) {
        Message message = createMessage(product, value);
        message.setOccurrence(occurrence);
        return message;
    }

    public static Message createMessage(String product, double value, Instruction instruction) {
        Message message = createMessage(product, value);
        message.setInstruction(instruction);
        return message;
    }

    public static Object[][] createRandomMessages(int noOfMessages, String[] products) {
        Instruction[] instructions = {ADD, SUBTRACT, MULTIPLY, null, null, null}; //heuristic to make null instruction msgs appear twice as often
        Object[][] data = new Object[noOfMessages][3];

        for (int i = 0; i < noOfMessages; i++) {
            int rp = ThreadLocalRandom.current().nextInt(0, products.length);
            int ri = ThreadLocalRandom.current().nextInt(0, instructions.length);
            double value = ThreadLocalRandom.current().nextDouble(10.00);

            data[i] = new Object[]{products[rp], value, instructions[ri]};

        }
        return data;
    }
}
