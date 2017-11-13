package com.webserver;

import com.model.HistoricalValue;
import com.model.Message;
import com.model.Sale;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_OK;

class SalesQueueConsumer implements Runnable {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    @Getter
    protected BlockingQueue<Message> queue;
    @Getter
    protected Map<String, Sale> map = new HashMap<>();
    @Getter
    protected int count;
    @Getter
    @Setter
    private int SLEEP_DURATION = 10000;

    public SalesQueueConsumer() {
    }

    public SalesQueueConsumer(BlockingQueue<Message> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        logger.info("Sales Queue Consumer started");
        while (true) {
            try {
                consume(this.queue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int consume(BlockingQueue<Message> queue) throws InterruptedException {
        try {
            Message message = queue.take();

            Sale salesInfo = map.getOrDefault(message.getProduct(), new Sale());
            salesInfo.setProductType(message.getProduct());

            if (message.getInstruction() == null) {
                processMessageWithoutInstruction(message, salesInfo);
            } else if (message.getInstruction() != null) {
                BigDecimal newPrice;
                BigDecimal averagePrice = salesInfo.getRunningTotal().divide(salesInfo.getTotalItemsSold(), RoundingMode.HALF_UP);

                switch (message.getInstruction()) {
                    case ADD:
                        salesInfo.setRunningTotal(averagePrice.add(message.getValue()).multiply(salesInfo.getTotalItemsSold()));
                        newPrice = salesInfo.getCurrentValue().add(message.getValue());
                        break;
                    case SUBTRACT:
                        salesInfo.setRunningTotal(averagePrice.subtract(message.getValue()).multiply(salesInfo.getTotalItemsSold()));
                        newPrice = salesInfo.getCurrentValue().subtract(message.getValue());
                        break;
                    case MULTIPLY:
                        salesInfo.setRunningTotal(averagePrice.multiply(message.getValue()).multiply(salesInfo.getTotalItemsSold()));
                        newPrice = salesInfo.getCurrentValue().multiply(message.getValue());
                        break;
                    default:
                        logger.severe("Unknown instruction");
                        throw new RuntimeException("Unknown instruction");
                }

                salesInfo.setCurrentValue(newPrice);
                salesInfo.getHistoricalValues().add(new HistoricalValue(System.currentTimeMillis(), message.getValue(), message.getInstruction()));
            }

            logger.info("Succesfully processed message: " + message);
            map.put(message.getProduct(), salesInfo);
            count++;

//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                logger.severe("Unexpected Error");
//                e.printStackTrace();
        } finally {
            if (count > 0 && count % 10 == 0) {
                printReport();
            }
            if (count > 0 && count % 50 == 0) {
                printAdjustmentReport();
                logger.warning(String.format("Pausing execution for  %s seconds", SLEEP_DURATION / 1000));
                Thread.sleep(SLEEP_DURATION);
            }
        }
        return HTTP_OK;
    }

    private void processMessageWithoutInstruction(Message message, Sale salesInfo) {
        BigDecimal occurrence = new BigDecimal(message.getOccurrence());
        salesInfo.setCurrentValue(message.getValue());
        salesInfo.setTotalItemsSold(salesInfo.getTotalItemsSold().add(occurrence));
        salesInfo.setRunningTotal(salesInfo.getRunningTotal().add(message.getValue().multiply(occurrence)));
        salesInfo.getHistoricalValues().add(new HistoricalValue(System.currentTimeMillis(), message.getValue()));
    }

    private void printAdjustmentReport() {


        System.out.println("====================================================================");
        System.out.println("Sales Adjustment Report:");
        System.out.println("====================================================================");
        BigDecimal grandTotal = new BigDecimal(0);

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Sale> pair = (Map.Entry) it.next();
            String item = pair.getKey();
            Sale sales = pair.getValue();

            Set<HistoricalValue> adjustments = sales.getHistoricalValues().stream().filter(k -> k.getInstruction() != null).collect(Collectors.toSet());

            System.out.println("--------------------------------------------------------------------");
            System.out.printf("%-6s%-20s %-15s%-4.0f %-16s%-4.2f\n", "Item:", item, "Number sold:", sales.getTotalItemsSold(), "Total value:", sales.getRunningTotal());
            System.out.println("--------------------------------------------------------------------");

            for (HistoricalValue historicalValue : adjustments) {
                System.out.printf("timestamp: %s Value: %s Instruction: %s \n", historicalValue.getTimestamp(), historicalValue.getValue(), historicalValue.getInstruction());
            }

            grandTotal = grandTotal.add(sales.getRunningTotal());
        }

        System.out.println("--------------------------------------------------------------------");
        System.out.printf("Grand Total: %54.2f\n", grandTotal);
        System.out.println("--------------------------------------------------------------------");
    }


    private void printReport() {
        System.out.println("====================================================================");
        System.out.println("Sales Report:");
        System.out.println("====================================================================");
        BigDecimal grandTotal = new BigDecimal(0);

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Sale> pair = (Map.Entry) it.next();
            String item = pair.getKey();
            Sale sales = pair.getValue();
            System.out.printf("%-6s%-20s %-15s%-4.0f %-16s%-4.2f\n", "Item:", item, "Number sold:", sales.getTotalItemsSold(), "Total value:", sales.getRunningTotal());
            grandTotal = grandTotal.add(sales.getRunningTotal());
        }
        System.out.println("--------------------------------------------------------------------");
        System.out.printf("Grand Total: %54.2f\n", grandTotal);
        System.out.println("--------------------------------------------------------------------");
    }
}
