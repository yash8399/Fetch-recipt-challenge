package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class ReceiptProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReceiptProcessorApplication.class, args);
    }
}

@RestController
class ReceiptController {

    private final ReceiptStore receiptStore = new ReceiptStore();

    @PostMapping("/receipts/process")
    public ReceiptIdResponse processReceipt(@RequestBody Receipt receipt) {
        String receiptId = UUID.randomUUID().toString();
        receipt.setId(receiptId);
        receiptStore.saveReceipt(receipt);

        return new ReceiptIdResponse(receiptId);
    }

    @GetMapping("/receipts/{id}/points")
    public PointsResponse getPoints(@PathVariable String id) {
        Receipt receipt = receiptStore.getReceiptById(id);
        if (receipt == null) {
            return new PointsResponse(0);
        }

        int points = calculatePoints(receipt);

        return new PointsResponse(points);
    }

    private int calculatePoints(Receipt receipt) {
        int points = receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        double total = Double.parseDouble(receipt.getTotal());
        if (total == Math.floor(total) && total % 1 == 0) {
            points += 50;
        }
        if (total % 0.25 == 0) {
            points += 25;
        }

        points += receipt.getItems().size() / 2;

        for (Item item : receipt.getItems()) {
            if (item.getShortDescription().trim().length() % 3 == 0) {
                double price = Double.parseDouble(item.getPrice());
                points += (int) Math.ceil(price * 0.2);
            }
        }

        int day = Integer.parseInt(receipt.getPurchaseDate().split("-")[2]);
        if (day % 2 == 1) {
            points += 6;
        }

        String[] timeParts = receipt.getPurchaseTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        if (hour >= 14 && hour < 16) {
            points += 10;
        }

        return points;
    }
}

class Receipt {
    private String id;
    private String retailer;
    private String purchaseDate;
    private String purchaseTime;
    private List<Item> items;
    private String total;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRetailer() {
        return retailer;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }
    public List<Item> getItems() {
        return items;
    }
    public String getTotal() {
        return total;
    }
}

class Item {
    private String shortDescription;
    private String price;

    public String getShortDescription() {
        return shortDescription;
    }

    public String getPrice() {
        return price;
    }
}

class ReceiptIdResponse {
    private String id;

    public ReceiptIdResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

class PointsResponse {
    private int points;

    public PointsResponse(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}

class ReceiptStore {
    private Map<String, Receipt> receiptMap = new HashMap<>();

    public void saveReceipt(Receipt receipt) {
        receiptMap.put(receipt.getId(), receipt);
    }

    public Receipt getReceiptById(String id) {
        return receiptMap.get(id);
    }
}
