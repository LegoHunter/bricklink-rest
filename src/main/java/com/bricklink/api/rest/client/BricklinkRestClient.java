package com.bricklink.api.rest.client;

import com.bricklink.api.rest.model.v1.*;

import java.util.List;
import java.util.Map;

public interface BricklinkRestClient {
    BricklinkResource<Item> getCatalogItem(String type, String no);

    BricklinkResource<PriceGuide> getPriceGuide(String type, String no, Map<String, Object> params);

    BricklinkResource<List<SubsetEntry>> getSubsets(String type, String no, Map<String, Object> params);

    BricklinkResource<List<Inventory>> getInventories(Map<String, Object> params);

    BricklinkResource<List<Order>> getOrders(Map<String, Object> params, Iterable status);

    BricklinkResource<List<Order>> getOrders(Map<String, Object> params);

    BricklinkResource<Order> getOrder(String orderId);

    BricklinkResource<List<List<OrderItem>>> getOrderItems(String orderId);

    BricklinkResource<Inventory> getInventories(Long inventoryId);

    BricklinkResource<Inventory> createInventory(Inventory inventory);

    BricklinkResource<Inventory> updateInventory(Long inventoryId, Inventory inventory);

    BricklinkResource<Order> updateOrder(String orderId, Order order);

    BricklinkResource<Inventory> updateOrderStatus(String orderId, OrderStatus status);

    BricklinkResource<Void> sendDriveThru(String orderId, boolean mailMe);

    BricklinkResource<List<Category>> getCategories();

    BricklinkResource<Category> getCategory(Long categoryId);

    BricklinkResource<List<Color>> getColors();

    BricklinkResource<Color> getColor(Integer colorId);

    BricklinkResource<List<ItemMapping>> getItemMapping(String no, Integer colorId);
}
