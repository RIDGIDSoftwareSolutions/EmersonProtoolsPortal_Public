package com.ridgid.oss.restwebservices.doclet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

class MarkdownRendererTest {
    private StringWriter writer;
    private MarkdownRenderer renderer;

    @BeforeEach
    void setUp() {
        writer = new StringWriter();
        renderer = new MarkdownRenderer();
    }

    @Test
    void it_can_write_the_markdown_for_a_simple_api_for_only_one_version() throws IOException {
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(204, "No Content")
                .addUrlPart("widget")
                .build();

        // @formatter:off
        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 204  | No Content  |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_write_the_markdown_for_a_simple_api_which_is_the_same_through_multiple_version() throws IOException {
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 5)
                .setMethod("GET")
                .setResponseCode(204, "No Content")
                .addUrlPart("widget")
                .build();

        // @formatter:off
        String expected =
                "### ~~~`GET /api/v2/widget`~~~ **Deprecated**\n" +
                        "### ~~~`GET /api/v3/widget`~~~ **Deprecated**\n" +
                        "### `GET /api/v4/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 204  | No Content  |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_write_the_markdown_for_a_deprecated_api() throws IOException {
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setDeprecated(true)
                .setMethod("GET")
                .setResponseCode(204, "No Content")
                .addUrlPart("widget")
                .build();

        // @formatter:off
        String expected =
                "### ~~~`GET /api/v2/widget`~~~ **Deprecated**\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 204  | No Content  |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_write_multiple_parameter() throws IOException {
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(204, "No Content")
                .addUrlPart("widget")
                .addRequestParameter("foo", "bar", "No")
                .addRequestParameter("baz", "rawr!", "Yes")
                .addRequestParameter("desiredUserName", "The user name that is desired", "No")
                .build();

        // @formatter:off
        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Parameters\n" +
                        "\n" +
                        "| Property          | Description                   | Required |\n" +
                        "|-------------------|-------------------------------|----------|\n" +
                        "| `foo`             | bar                           | No       |\n" +
                        "| `baz`             | rawr!                         | Yes      |\n" +
                        "| `desiredUserName` | The user name that is desired | No       |\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 204  | No Content  |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_write_the_markdown_for_an_action_that_returns_a_single_model() throws IOException {
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(200, "OK")
                .addResponseProperty("myNumericValue", "The numeric value", "integer")
                .addResponseProperty("myStringValue", "The string value", "string")
                .addUrlPart("widget")
                .build();

        // @formatter:off
        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response\n" +
                        "\n" +
                        "##### `200` OK:\n" +
                        "\n" +
                        "| Property         | Description       | Data Type |\n" +
                        "|------------------|-------------------|-----------|\n" +
                        "| `myNumericValue` | The numeric value | integer   |\n" +
                        "| `myStringValue`  | The string value  | string    |\n" +
                        "\n" +
                        "_____\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 200  | OK          |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_render_response_with_nested_values() throws IOException {
        // @formatter:off
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(200, "OK")
                .addResponseProperty("foo", "The numeric value", "integer")
                .startNestedModel("widget", "The widget information", "WidgetInformation")
                .addResponseProperty("id", "The widget id", "integer")
                .addResponseProperty("name", "The widget name", "string")
                .endNestedModel()
                .addResponseProperty("bar", "The string value", "string")
                .addUrlPart("widget")
                .build();

        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response\n" +
                        "\n" +
                        "##### `200` OK:\n" +
                        "\n" +
                        "| Property | Description            | Data Type                                 |\n" +
                        "|----------|------------------------|-------------------------------------------|\n" +
                        "| `foo`    | The numeric value      | integer                                   |\n" +
                        "| `widget` | The widget information | [Widget Information](#widget-information) |\n" +
                        "| `bar`    | The string value       | string                                    |\n" +
                        "\n" +
                        "###### Widget Information\n" +
                        "| Property | Description     | Data Type |\n" +
                        "|----------|-----------------|-----------|\n" +
                        "| `id`     | The widget id   | integer   |\n" +
                        "| `name`   | The widget name | string    |\n" +
                        "\n" +
                        "_____\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 200  | OK          |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_does_not_duplicate_inner_view_model_tables() throws IOException {
        // @formatter:off
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(200, "OK")
                .addResponseProperty("foo", "The numeric value", "integer")
                .startNestedModel("bigWidget", "The big widget", "WidgetInformation")
                .addResponseProperty("id", "The widget id", "integer")
                .addResponseProperty("name", "The widget name", "string")
                .endNestedModel()
                .startNestedModel("smallWidget", "The small widget", "WidgetInformation")
                .addResponseProperty("id", "The widget id", "integer")
                .addResponseProperty("name", "The widget name", "string")
                .endNestedModel()
                .addResponseProperty("bar", "The string value", "string")
                .addUrlPart("widget")
                .build();

        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response\n" +
                        "\n" +
                        "##### `200` OK:\n" +
                        "\n" +
                        "| Property      | Description       | Data Type                                 |\n" +
                        "|---------------|-------------------|-------------------------------------------|\n" +
                        "| `foo`         | The numeric value | integer                                   |\n" +
                        "| `bigWidget`   | The big widget    | [Widget Information](#widget-information) |\n" +
                        "| `smallWidget` | The small widget  | [Widget Information](#widget-information) |\n" +
                        "| `bar`         | The string value  | string                                    |\n" +
                        "\n" +
                        "###### Widget Information\n" +
                        "| Property | Description     | Data Type |\n" +
                        "|----------|-----------------|-----------|\n" +
                        "| `id`     | The widget id   | integer   |\n" +
                        "| `name`   | The widget name | string    |\n" +
                        "\n" +
                        "_____\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 200  | OK          |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_render_response_with_array_of_models() throws IOException {
        // @formatter:off
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(200, "OK")
                .addResponseProperty("foo", "The numeric value", "integer")
                .addArray()
                .startNestedModel("widget", "The widget information", "WidgetInformation")
                .addResponseProperty("id", "The widget id", "integer")
                .addResponseProperty("name", "The widget name", "string")
                .endNestedModel()
                .addResponseProperty("bar", "The string value", "string")
                .addUrlPart("widget")
                .build();

        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response\n" +
                        "\n" +
                        "##### `200` OK:\n" +
                        "\n" +
                        "| Property | Description            | Data Type                                              |\n" +
                        "|----------|------------------------|--------------------------------------------------------|\n" +
                        "| `foo`    | The numeric value      | integer                                                |\n" +
                        "| `widget` | The widget information | **array** of [Widget Information](#widget-information) |\n" +
                        "| `bar`    | The string value       | string                                                 |\n" +
                        "\n" +
                        "###### Widget Information\n" +
                        "| Property | Description     | Data Type |\n" +
                        "|----------|-----------------|-----------|\n" +
                        "| `id`     | The widget id   | integer   |\n" +
                        "| `name`   | The widget name | string    |\n" +
                        "\n" +
                        "_____\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 200  | OK          |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_deal_with_response_view_models_that_are_meant_to_be_extended() throws IOException {
        // @formatter:off
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(200, "OK")
                .addResponseProperty("jdeOrderNumber", "The JDE order number", "integer")
                .addResponseProperty("datePlaced", "The date the order was placed", "string")
                .addArray()
                .startNestedModel("orderLines", "The lines associated with the order", "BackOrderedLine")
                .addResponseProperty("catalogNumber", "The catalog number of the item", "string")
                .addResponseProperty("lineNumber", "The line number in JDE", "double")
                .addResponseProperty("lineStatus", "The current state of the line", "string")
                .addResponseProperty("quantity", "The quantity of the item ordered", "integer")
                .addResponseProperty("atpDate", "The ATP date for the item", "string")
                .endNestedModel()
                .startNestedModel("orderLines", "The lines associated with the order", "CanceledLine")
                .addResponseProperty("catalogNumber", "The catalog number of the item", "string")
                .addResponseProperty("lineNumber", "The line number in JDE", "double")
                .addResponseProperty("lineStatus", "The current state of the line", "string")
                .addResponseProperty("quantity", "The quantity of the item ordered", "integer")
                .endNestedModel()
                .startNestedModel("orderLines", "The lines associated with the order", "InProcessLine")
                .addResponseProperty("catalogNumber", "The catalog number of the item", "string")
                .addResponseProperty("lineNumber", "The line number in JDE", "double")
                .addResponseProperty("lineStatus", "The current state of the line", "string")
                .addResponseProperty("quantity", "The quantity of the item ordered", "integer")
                .endNestedModel()
                .startNestedModel("orderLines", "The lines associated with the order", "ShippedLine")
                .addResponseProperty("catalogNumber", "The catalog number of the item", "string")
                .addResponseProperty("lineNumber", "The line number in JDE", "double")
                .addResponseProperty("lineStatus", "The current state of the line", "string")
                .addResponseProperty("quantity", "The quantity of the item ordered", "integer")
                .addResponseProperty("trackingNumber", "The tracking number for the shipment", "string")
                .addResponseProperty("carrierNumber", "The carrier number for the shipment", "integer")
                .addResponseProperty("dateShipped", "The date the line was shipped", "string")
                .endNestedModel()
                .addUrlPart("widget")
                .build();

        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response\n" +
                        "\n" +
                        "##### `200` OK:\n" +
                        "\n" +
                        "| Property         | Description                         | Data Type                                                                                                                                                                                            |\n" +
                        "|------------------|-------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|\n" +
                        "| `jdeOrderNumber` | The JDE order number                | integer                                                                                                                                                                                              |\n" +
                        "| `datePlaced`     | The date the order was placed       | string                                                                                                                                                                                               |\n" +
                        "| `orderLines`     | The lines associated with the order | **array** of <br><ul><li>[Back Ordered Line](#back-ordered-line)</li><li>[Canceled Line](#canceled-line)</li><li>[In Process Line](#in-process-line)</li><li>[Shipped Line](#shipped-line)</li></ul> |\n" +
                        "\n" +
                        "###### Back Ordered Line\n" +
                        "| Property        | Description                      | Data Type |\n" +
                        "|-----------------|----------------------------------|-----------|\n" +
                        "| `catalogNumber` | The catalog number of the item   | string    |\n" +
                        "| `lineNumber`    | The line number in JDE           | double    |\n" +
                        "| `lineStatus`    | The current state of the line    | string    |\n" +
                        "| `quantity`      | The quantity of the item ordered | integer   |\n" +
                        "| `atpDate`       | The ATP date for the item        | string    |\n" +
                        "\n" +
                        "###### Canceled Line\n" +
                        "| Property        | Description                      | Data Type |\n" +
                        "|-----------------|----------------------------------|-----------|\n" +
                        "| `catalogNumber` | The catalog number of the item   | string    |\n" +
                        "| `lineNumber`    | The line number in JDE           | double    |\n" +
                        "| `lineStatus`    | The current state of the line    | string    |\n" +
                        "| `quantity`      | The quantity of the item ordered | integer   |\n" +
                        "\n" +
                        "###### In Process Line\n" +
                        "| Property        | Description                      | Data Type |\n" +
                        "|-----------------|----------------------------------|-----------|\n" +
                        "| `catalogNumber` | The catalog number of the item   | string    |\n" +
                        "| `lineNumber`    | The line number in JDE           | double    |\n" +
                        "| `lineStatus`    | The current state of the line    | string    |\n" +
                        "| `quantity`      | The quantity of the item ordered | integer   |\n" +
                        "\n" +
                        "###### Shipped Line\n" +
                        "| Property         | Description                          | Data Type |\n" +
                        "|------------------|--------------------------------------|-----------|\n" +
                        "| `catalogNumber`  | The catalog number of the item       | string    |\n" +
                        "| `lineNumber`     | The line number in JDE               | double    |\n" +
                        "| `lineStatus`     | The current state of the line        | string    |\n" +
                        "| `quantity`       | The quantity of the item ordered     | integer   |\n" +
                        "| `trackingNumber` | The tracking number for the shipment | string    |\n" +
                        "| `carrierNumber`  | The carrier number for the shipment  | integer   |\n" +
                        "| `dateShipped`    | The date the line was shipped        | string    |\n" +
                        "\n" +
                        "_____\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 200  | OK          |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_write_the_markdown_for_an_action_that_contains_an_example_response() throws IOException {
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(200, "OK")
                .addResponseProperty("myNumericValue", "The numeric value", "integer")
                .addResponseProperty("myStringValue", "The string value", "string")
                .addUrlPart("widget")
                .setExampleResponse("{\n    \"myNumericValue\": 12345,\n    \"myStringValue\": \"foobar\"\n}")
                .build();

        // @formatter:off
        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response\n" +
                        "\n" +
                        "##### `200` OK:\n" +
                        "\n" +
                        "| Property         | Description       | Data Type |\n" +
                        "|------------------|-------------------|-----------|\n" +
                        "| `myNumericValue` | The numeric value | integer   |\n" +
                        "| `myStringValue`  | The string value  | string    |\n" +
                        "\n" +
                        "_____\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 200  | OK          |\n" +
                        "\n" +
                        "##### Example Response\n" +
                        "\n" +
                        "```javascript\n" +
                        "{\n" +
                        "    \"myNumericValue\": 12345,\n" +
                        "    \"myStringValue\": \"foobar\"\n" +
                        "}\n" +
                        "```\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_write_the_markdown_for_an_action_that_multiple_responses() throws IOException {
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(200, "OK")
                .addResponseProperty("myNumericValue", "The numeric value", "integer")
                .addResponseProperty("myStringValue", "The string value", "string")
                .setResponseCode(401, "No Security")
                .addArray()
                .addResponseProperty("requiredRoles", "One of these is required", "string")
                .addUrlPart("widget")
                .build();

        // @formatter:off
        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response\n" +
                        "\n" +
                        "##### `200` OK:\n" +
                        "\n" +
                        "| Property         | Description       | Data Type |\n" +
                        "|------------------|-------------------|-----------|\n" +
                        "| `myNumericValue` | The numeric value | integer   |\n" +
                        "| `myStringValue`  | The string value  | string    |\n" +
                        "\n" +
                        "_____\n" +
                        "\n" +
                        "##### `401` No Security:\n" +
                        "\n" +
                        "| Property        | Description              | Data Type           |\n" +
                        "|-----------------|--------------------------|---------------------|\n" +
                        "| `requiredRoles` | One of these is required | **array** of string |\n" +
                        "\n" +
                        "_____\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 200  | OK          |\n" +
                        "| 401  | No Security |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    @Test
    void it_can_write_the_markdown_for_a_simple_api_with_no_response_description() throws IOException {
        RestApiModel model = new RestApiModel.Builder()
                .setName("Widget")
                .setDescription("Retrieves the widgets")
                .setVersionRange(2, 3)
                .setMethod("GET")
                .setResponseCode(204, null)
                .addUrlPart("widget")
                .build();

        // @formatter:off
        String expected =
                "### `GET /api/v2/widget`\n" +
                        "**Description:** Retrieves the widgets\n" +
                        "\n" +
                        "#### Response Codes\n" +
                        "\n" +
                        "| Code | Description |\n" +
                        "|------|-------------|\n" +
                        "| 204  |             |\n" +
                        "\n";
        // @formatter:on

        assertModelRenderedProperly(model, expected);
    }

    private void assertModelRenderedProperly(RestApiModel model, String expected) throws IOException {
        renderer.render(model, writer);
        assertThat(writer.toString(), equalTo(expected));
    }
}