package com.bricklink.api.rest.client;

import com.bricklink.api.rest.model.v1.Category;
import com.bricklink.api.rest.model.v1.Inventory;
import com.bricklink.api.rest.model.v1.Item;
import com.bricklink.api.rest.model.v1.PriceGuide;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

class BricklinkRestClientEndpointTest extends BricklistRestClientTest {

    @Test
    void getCatalogItem_returnsItem() {
        stubFor(get(urlEqualTo("/items/PART/3001"))
                .willReturn(okJson("""
                        {
                          "meta": {"code": 200, "message": "OK", "description": "OK"},
                          "data": {
                            "no": "3001",
                            "name": "Brick 2 x 4",
                            "type": "PART",
                            "category_id": 5,
                            "year_released": 1958
                          }
                        }
                        """)));

        Item item = bricklinkRestClient.getCatalogItem("PART", "3001").getData();

        assertThat(item.getNo()).isEqualTo("3001");
        assertThat(item.getName()).isEqualTo("Brick 2 x 4");
        assertThat(item.getType()).isEqualTo("PART");
        assertThat(item.getCategory_id()).isEqualTo(5);
    }

    @Test
    void getInventory_returnsInventory() {
        stubFor(get(urlEqualTo("/inventories/12345"))
                .willReturn(okJson("""
                        {
                          "meta": {"code": 200, "message": "OK", "description": "OK"},
                          "data": {
                            "inventory_id": 12345,
                            "item": {
                              "no": "3001",
                              "name": "Brick 2 x 4",
                              "type": "PART",
                              "category_id": 5
                            },
                            "color_id": 1,
                            "color_name": "White",
                            "quantity": 10,
                            "new_or_used": "U",
                            "unit_price": 0.25
                          }
                        }
                        """)));

        Inventory inventory = bricklinkRestClient.getInventories(12345L).getData();

        assertThat(inventory.getInventory_id()).isEqualTo(12345L);
        assertThat(inventory.getItem().getNo()).isEqualTo("3001");
        assertThat(inventory.getQuantity()).isEqualTo(10);
    }

    @Test
    void getInventories_appliesQueryParameters() {
        stubFor(get(urlPathEqualTo("/inventories"))
                .withQueryParam("item_type", equalTo("PART"))
                .withQueryParam("color_id", equalTo("1"))
                .willReturn(okJson("""
                        {
                          "meta": {"code": 200, "message": "OK", "description": "OK"},
                          "data": [
                            {
                              "inventory_id": 12345,
                              "item": {"no": "3001", "name": "Brick 2 x 4", "type": "PART", "category_id": 5},
                              "color_id": 1,
                              "quantity": 10
                            }
                          ]
                        }
                        """)));

        List<Inventory> inventories = bricklinkRestClient.getInventories(Map.of("item_type", "PART", "color_id", 1)).getData();

        assertThat(inventories).hasSize(1);
        assertThat(inventories.getFirst().getInventory_id()).isEqualTo(12345L);
    }

    @Test
    void getCategory_returnsCategory() {
        stubFor(get(urlEqualTo("/categories/5"))
                .willReturn(okJson("""
                        {
                          "meta": {"code": 200, "message": "OK", "description": "OK"},
                          "data": {
                            "category_id": 5,
                            "category_name": "Bricks",
                            "parent_id": 0
                          }
                        }
                        """)));

        Category category = bricklinkRestClient.getCategory(5L).getData();

        assertThat(category.getCategory_id()).isEqualTo(5);
        assertThat(category.getCategory_name()).isEqualTo("Bricks");
    }

    @Test
    void getPriceGuide_appliesQueryParametersAndReturnsPriceGuide() {
        stubFor(get(urlPathEqualTo("/items/PART/3001/price"))
                .withQueryParam("color_id", equalTo("1"))
                .withQueryParam("guide_type", equalTo("stock"))
                .withQueryParam("new_or_used", equalTo("U"))
                .willReturn(okJson("""
                        {
                          "meta": {"code": 200, "message": "OK", "description": "OK"},
                          "data": {
                            "item": {"no": "3001", "name": "Brick 2 x 4", "type": "PART", "category_id": 5},
                            "new_or_used": "U",
                            "currency_code": "USD",
                            "min_price": 0.10,
                            "max_price": 0.50,
                            "avg_price": 0.25,
                            "qty_avg_price": 0.20,
                            "unit_quantity": 4,
                            "total_quantity": 100,
                            "price_detail": [
                              {"quantity": 10, "unit_price": 0.25, "seller_country_code": "US"}
                            ]
                          }
                        }
                        """)));

        PriceGuide priceGuide = bricklinkRestClient.getPriceGuide("PART", "3001", Map.of(
                "color_id", 1,
                "guide_type", "stock",
                "new_or_used", "U")).getData();

        assertThat(priceGuide.getItem().getNo()).isEqualTo("3001");
        assertThat(priceGuide.getCurrency_code()).isEqualTo("USD");
        assertThat(priceGuide.getPrice_detail()).hasSize(1);
    }

    private com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder okJson(String body) {
        return aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body);
    }
}
