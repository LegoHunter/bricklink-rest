package com.bricklink.api.rest.client;

import com.bricklink.api.rest.model.v1.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;
import java.util.Map;

public interface BricklinkHttpClient {
    @GetExchange("/items/{type}/{no}")
    BricklinkResource<Item> getCatalogItem(@PathVariable("type") String type, @PathVariable("no") String no);

    @GetExchange("/items/{type}/{no}/price")
    BricklinkResource<PriceGuide> getPriceGuide(@PathVariable("type") String type, @PathVariable("no") String no, @RequestParam Map<String, Object> params);

    @GetExchange("/items/{type}/{no}/subsets")
    BricklinkResource<List<SubsetEntry>> getSubsets(@PathVariable("type") String type, @PathVariable("no") String no, @RequestParam Map<String, Object> params);

    @GetExchange("/inventories")
    BricklinkResource<List<Inventory>> getInventories(@RequestParam Map<String, Object> params);

    @GetExchange("/orders")
    BricklinkResource<List<Order>> getOrders(@RequestParam Map<String, Object> params);

    @GetExchange("/orders/{order_id}")
    BricklinkResource<Order> getOrder(@PathVariable("order_id") String orderId);

    @GetExchange("/orders/{order_id}/items")
    BricklinkResource<List<List<OrderItem>>> getOrderItems(@PathVariable("order_id") String orderId);

    @GetExchange("/inventories/{inventory_id}")
    BricklinkResource<Inventory> getInventory(@PathVariable("inventory_id") Long inventoryId);

    @PostExchange(value = "/inventories", contentType = MediaType.APPLICATION_JSON_VALUE)
    BricklinkResource<Inventory> createInventory(@RequestBody Inventory inventory);

    @PutExchange(value = "/inventories/{inventory_id}", contentType = MediaType.APPLICATION_JSON_VALUE)
    BricklinkResource<Inventory> updateInventory(@PathVariable("inventory_id") Long inventoryId, @RequestBody Inventory inventory);

    @PutExchange(value = "/orders/{order_id}", contentType = MediaType.APPLICATION_JSON_VALUE)
    BricklinkResource<Order> updateOrder(@PathVariable("order_id") String orderId, @RequestBody Order order);

    @PutExchange(value = "/orders/{order_id}/status", contentType = MediaType.APPLICATION_JSON_VALUE)
    BricklinkResource<Inventory> updateOrderStatus(@PathVariable("order_id") String orderId, @RequestBody Map<String, Object> statusUpdate);

    @PostExchange("/orders/{order_id}/drive_thru")
    BricklinkResource<Void> sendDriveThru(@PathVariable("order_id") String orderId, @RequestParam("mail_me") boolean mailMe);

    @GetExchange("/categories")
    BricklinkResource<List<Category>> getCategories();

    @GetExchange("/categories/{category_id}")
    BricklinkResource<Category> getCategory(@PathVariable("category_id") Long categoryId);

    @GetExchange("/colors")
    BricklinkResource<List<Color>> getColors();

    @GetExchange("/colors/{color_id}")
    BricklinkResource<Color> getColor(@PathVariable("color_id") Integer colorId);

    @GetExchange("/item_mapping/part/{no}")
    BricklinkResource<List<ItemMapping>> getItemMapping(@PathVariable("no") String no, @RequestParam("color_id") Integer colorId);
}
