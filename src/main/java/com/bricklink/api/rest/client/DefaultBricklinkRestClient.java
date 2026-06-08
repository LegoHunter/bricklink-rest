package com.bricklink.api.rest.client;

import com.bricklink.api.rest.exception.BricklinkClientException;
import com.bricklink.api.rest.exception.BricklinkServerException;
import com.bricklink.api.rest.model.v1.*;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DefaultBricklinkRestClient implements BricklinkRestClient {
    private final BricklinkHttpClient bricklinkHttpClient;

    @Override
    public BricklinkResource<Item> getCatalogItem(String type, String no) {
        return call("getCatalogItem", () -> bricklinkHttpClient.getCatalogItem(type, no));
    }

    @Override
    public BricklinkResource<PriceGuide> getPriceGuide(String type, String no, Map<String, Object> params) {
        return call("getPriceGuide", () -> bricklinkHttpClient.getPriceGuide(type, no, nonNullParams(params)));
    }

    @Override
    public BricklinkResource<List<SubsetEntry>> getSubsets(String type, String no, Map<String, Object> params) {
        return call("getSubsets", () -> bricklinkHttpClient.getSubsets(type, no, nonNullParams(params)));
    }

    @Override
    public BricklinkResource<List<Inventory>> getInventories(Map<String, Object> params) {
        return call("getInventories", () -> bricklinkHttpClient.getInventories(nonNullParams(params)));
    }

    @Override
    public BricklinkResource<List<Order>> getOrders(Map<String, Object> params, Iterable status) {
        Map<String, Object> mergedParams = nonNullParams(params);
        if (status != null) {
            mergedParams.put("status", status);
        }
        return call("getOrders", () -> bricklinkHttpClient.getOrders(mergedParams));
    }

    @Override
    public BricklinkResource<List<Order>> getOrders(Map<String, Object> params) {
        return call("getOrders", () -> bricklinkHttpClient.getOrders(nonNullParams(params)));
    }

    @Override
    public BricklinkResource<Order> getOrder(String orderId) {
        return call("getOrder", () -> bricklinkHttpClient.getOrder(orderId));
    }

    @Override
    public BricklinkResource<List<List<OrderItem>>> getOrderItems(String orderId) {
        return call("getOrderItems", () -> bricklinkHttpClient.getOrderItems(orderId));
    }

    @Override
    public BricklinkResource<Inventory> getInventories(Long inventoryId) {
        return call("getInventory", () -> bricklinkHttpClient.getInventory(inventoryId));
    }

    @Override
    public BricklinkResource<Inventory> createInventory(Inventory inventory) {
        return call("createInventory", () -> bricklinkHttpClient.createInventory(inventory));
    }

    @Override
    public BricklinkResource<Inventory> updateInventory(Long inventoryId, Inventory inventory) {
        return call("updateInventory", () -> bricklinkHttpClient.updateInventory(inventoryId, inventory));
    }

    @Override
    public BricklinkResource<Order> updateOrder(String orderId, Order order) {
        return call("updateOrder", () -> bricklinkHttpClient.updateOrder(orderId, order));
    }

    @Override
    public BricklinkResource<Inventory> updateOrderStatus(String orderId, OrderStatus status) {
        Map<String, Object> statusUpdate = Map.of("field", "status", "value", status.name());
        return call("updateOrderStatus", () -> bricklinkHttpClient.updateOrderStatus(orderId, statusUpdate));
    }

    @Override
    public BricklinkResource<Void> sendDriveThru(String orderId, boolean mailMe) {
        return call("sendDriveThru", () -> bricklinkHttpClient.sendDriveThru(orderId, mailMe));
    }

    @Override
    public BricklinkResource<List<Category>> getCategories() {
        return call("getCategories", bricklinkHttpClient::getCategories);
    }

    @Override
    public BricklinkResource<Category> getCategory(Long categoryId) {
        return call("getCategory", () -> bricklinkHttpClient.getCategory(categoryId));
    }

    @Override
    public BricklinkResource<List<Color>> getColors() {
        return call("getColors", bricklinkHttpClient::getColors);
    }

    @Override
    public BricklinkResource<Color> getColor(Integer colorId) {
        return call("getColor", () -> bricklinkHttpClient.getColor(colorId));
    }

    @Override
    public BricklinkResource<List<ItemMapping>> getItemMapping(String no, Integer colorId) {
        return call("getItemMapping", () -> bricklinkHttpClient.getItemMapping(no, colorId));
    }

    private <T> BricklinkResource<T> call(String operation, Supplier<BricklinkResource<T>> supplier) {
        BricklinkResource<T> resource = supplier.get();
        if (resource != null && resource.getMeta() != null && resource.getMeta().getCode() != null) {
            Integer code = resource.getMeta().getCode();
            if (code >= 400 && code <= 499) {
                throw new BricklinkClientException(code, resource.getMeta().getMessage(), resource.getMeta().getDescription());
            }
            if (code >= 500 && code <= 599) {
                throw new BricklinkServerException(code, "Bricklink server error description: [%s] message: [%s] code: [%s]".formatted(
                        resource.getMeta().getDescription(),
                        resource.getMeta().getMessage(),
                        code));
            }
        }
        return resource;
    }

    private Map<String, Object> nonNullParams(Map<String, Object> params) {
        return params == null ? new HashMap<>() : new HashMap<>(params);
    }
}
