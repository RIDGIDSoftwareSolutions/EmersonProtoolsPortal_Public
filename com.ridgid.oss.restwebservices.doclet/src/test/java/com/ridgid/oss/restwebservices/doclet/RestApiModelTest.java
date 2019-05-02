package com.ridgid.oss.restwebservices.doclet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class RestApiModelTest {
    private RestApiModel.Builder builder;

    @BeforeEach
    void setUp() {
        builder = new RestApiModel.Builder();
    }

    @Test
    void it_fails_if_the_name_is_missing() {
        Throwable t = assertThrows(IllegalArgumentException.class,
                () -> builder.addUrlPart("foo").setDescription("Desc").setMethod("GET").setResponseCode(204, "No Content")
                        .setVersionRange(1, 3).build());
        assertThat(t.getMessage(), equalTo("Markdown is missing name"));
    }

    @Test
    void it_fails_if_the_url_parts_are_missing() {
        Throwable t = assertThrows(IllegalArgumentException.class,
                () -> builder.setName("Foo").setDescription("Desc").setMethod("GET").setResponseCode(204, "No Content")
                        .setVersionRange(1, 3).build());
        assertThat(t.getMessage(), equalTo("Markdown is missing url parts"));
    }

    @Test
    void it_fails_if_the_description_is_missing() {
        Throwable t = assertThrows(IllegalArgumentException.class,
                () -> builder.setName("Foo").addUrlPart("foo").setMethod("GET").setResponseCode(204, "No Content").setVersionRange(1, 3)
                        .build());
        assertThat(t.getMessage(), equalTo("Markdown is missing description"));
    }

    @Test
    void it_fails_if_the_method_is_missing() {
        Throwable t = assertThrows(IllegalArgumentException.class,
                () -> builder.setName("Foo").addUrlPart("foo").setDescription("desc").setResponseCode(204, "No Content")
                        .setVersionRange(1, 3).build());
        assertThat(t.getMessage(), equalTo("Markdown is missing method"));
    }

    @Test
    void it_fails_if_the_response_is_missing() {
        Throwable t = assertThrows(IllegalArgumentException.class,
                () -> builder.setName("Foo").addUrlPart("foo").setDescription("desc").setMethod("GET").setVersionRange(1, 3).build());
        assertThat(t.getMessage(), equalTo("Markdown is missing response"));
    }

    @Test
    void it_fails_if_the_response_code_is_negative() {
        Throwable t = assertThrows(IllegalArgumentException.class,
                () -> builder.setName("Foo").addUrlPart("foo").setDescription("desc").setMethod("GET").setResponseCode(-200, "Invalid")
                        .setVersionRange(1, 3).build());
        assertThat(t.getMessage(), equalTo("Markdown contains invalid response code: -200"));
    }

    @Test
    void it_fails_if_the_response_code_is_zero() {
        Throwable t = assertThrows(IllegalArgumentException.class,
                () -> builder.setName("Foo").addUrlPart("foo").setDescription("desc").setMethod("GET").setResponseCode(0, "Invalid")
                        .setVersionRange(1, 3).build());
        assertThat(t.getMessage(), equalTo("Markdown contains invalid response code: 0"));
    }

    @Test
    void it_fails_if_the_version_is_missing() {
        Throwable t = assertThrows(IllegalArgumentException.class,
                () -> builder.setName("Foo").addUrlPart("foo").setDescription("desc").setMethod("GET").setResponseCode(200, "OK").build());
        assertThat(t.getMessage(), equalTo("Markdown is missing version range"));
    }

    @Test
    void it_can_set_the_name() {
        prepareBuilder();
        RestApiModel model = builder.setName("My Controller").build();
        assertThat(model.getName(), equalTo("My Controller"));
    }

    @Test
    void it_can_set_the_url_parts() {
        prepareBuilder();
        RestApiModel model = builder.addUrlPart("baz").build();
        assertThat(model.getUrlParts(), contains("bar", "baz"));
    }

    @Test
    void it_can_set_the_description() {
        prepareBuilder();
        RestApiModel model = builder.setDescription("Lorem ispum").build();
        assertThat(model.getDescription(), equalTo("Lorem ispum"));
    }

    @Test
    void it_can_set_the_method() {
        prepareBuilder();
        RestApiModel model = builder.setMethod("POST").build();
        assertThat(model.getMethod(), equalTo("POST"));
    }

    @Test
    void it_can_add_request_parameters() {
        prepareBuilder();
        RestApiModel model = builder.addRequestParameter("foobar", "description of foobar", "Yes")
                .addRequestParameter("active", "description of active", "No")
                .build();
        assertThat(model.getRequestParameters(), hasSize(2));
        assertThat(model.getRequestParameters().get(0).getName(), equalTo("foobar"));
        assertThat(model.getRequestParameters().get(0).getDescription(), equalTo("description of foobar"));
        assertThat(model.getRequestParameters().get(0).getRequired(), equalTo("Yes"));
    }

    @Test
    void it_can_set_the_version_range() {
        prepareBuilder();
        RestApiModel model = builder.setVersionRange(3, 5).build();
        assertThat(model.getVersionRange().getLowerBound(), equalTo(3));
        assertThat(model.getVersionRange().getUpperBound(), equalTo(5));
    }

    @Test
    void it_can_set_the_response_code() {
        prepareBuilderWithoutResponse();
        RestApiModel model = builder.setResponseCode(400, "Bad Request").build();
        assertThat(model.getResponse().getCode(), equalTo(400));
        assertThat(model.getResponse().getDescription(), equalTo("Bad Request"));
    }

    @Test
    void it_can_add_a_flat_response_view_model() {
        prepareBuilderWithoutResponse();
        RestApiModel model = builder
                .setResponseCode(200, "Request was successful")
                .addResponseProperty("catalogNumber", "The catalog number", "string")
                .addResponseProperty("description", "Description of the item", "string")
                .addResponseProperty("upc", "UPC of the item", "string")
                .addResponseProperty("harmonizedShippingCode", "The harmonized shipping code", "string")
                .build();
        assertAll(
                () -> assertResponseContainsProperty(model.getResponses(), 200, "catalogNumber", "The catalog number", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 200, "description", "Description of the item", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 200, "upc", "UPC of the item", "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "harmonizedShippingCode",
                        "The harmonized shipping code",
                        "string")
        );
    }

    @Test
    void it_can_add_a_nested_response_view_model() {
        prepareBuilderWithoutResponse();
        // @formatter:off
        RestApiModel model = builder
                .setResponseCode(200, "Request was successful")
                .addResponseProperty("catalogNumber", "The catalog number", "string")
                .addResponseProperty("description", "Description of the item", "string")
                .addResponseProperty("upc", "UPC of the item", "string")
                .addResponseProperty("harmonizedShippingCode", "The harmonized shipping code", "string")
                .startNestedModel("localizedItemInformation",
                        "information specific to a country, branch, or market code",
                        "LocalizedItemInformation")
                .addResponseProperty("countryCode", "Country code", "string")
                .addResponseProperty("primaryVendorAccountNumber", "Account number of the primary vendor", "integer")
                .startNestedModel("itemUnitsInfo", "The different types of item units", "ItemUnitsInformation")
                .addResponseProperty("quantityType", "The unit of measure for the quantity", "string")
                .addResponseProperty("minimumOrderQuantity", "The minimum order quantity", "integer")
                .addResponseProperty("weight", "The item weight", "double")
                .endNestedModel()
                .addResponseProperty("serialized", "Whether the item is serialized", "boolean")
                .endNestedModel()
                .build();
        // @formatter:on

        assertAll(
                () -> assertResponseContainsProperty(model.getResponses(), 200, "catalogNumber", "The catalog number", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 200, "description", "Description of the item", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 200, "upc", "UPC of the item", "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "harmonizedShippingCode",
                        "The harmonized shipping code",
                        "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation",
                        "information specific to a country, branch, or market code",
                        "LocalizedItemInformation"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.countryCode",
                        "Country code",
                        "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.primaryVendorAccountNumber",
                        "Account number of the primary vendor",
                        "integer"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.itemUnitsInfo",
                        "The different types of item units",
                        "ItemUnitsInformation"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.itemUnitsInfo.quantityType",
                        "The unit of measure for the quantity",
                        "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.itemUnitsInfo.minimumOrderQuantity",
                        "The minimum order quantity",
                        "integer"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.itemUnitsInfo.weight",
                        "The item weight",
                        "double"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.serialized",
                        "Whether the item is serialized",
                        "boolean")
        );
    }

    @Test
    void it_can_add_a_nested_list_to_response_view_model() {
        prepareBuilderWithoutResponse();
        // @formatter:off
        RestApiModel model = builder
                .setResponseCode(200, "Request was successful")
                .addResponseProperty("catalogNumber", "The catalog number", "string")
                .addResponseProperty("description", "Description of the item", "string")
                .addResponseProperty("upc", "UPC of the item", "string")
                .addResponseProperty("harmonizedShippingCode", "The harmonized shipping code", "string")
                .addArray()
                .startNestedModel("localizedItemInformation",
                        "information specific to a country, branch, or market code",
                        "LocalizedItemInformation")
                .addResponseProperty("countryCode", "Country code", "string")
                .addResponseProperty("primaryVendorAccountNumber", "Account number of the primary vendor", "integer")
                .addArray()
                .startNestedModel("itemUnitsInfos", "The different types of item units", "ItemUnitsInformation")
                .addResponseProperty("quantityType", "The unit of measure for the quantity", "string")
                .addResponseProperty("minimumOrderQuantity", "The minimum order quantity", "integer")
                .addResponseProperty("weight", "The item weight", "double")
                .endNestedModel()
                .addResponseProperty("serialized", "Whether the item is serialized", "boolean")
                .endNestedModel()
                .build();
        // @formatter:on

        assertAll(
                () -> assertResponseContainsProperty(model.getResponses(), 200, "catalogNumber", "The catalog number", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 200, "description", "Description of the item", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 200, "upc", "UPC of the item", "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "harmonizedShippingCode",
                        "The harmonized shipping code",
                        "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation",
                        "information specific to a country, branch, or market code",
                        "LocalizedItemInformation[]"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.array.countryCode",
                        "Country code",
                        "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.array.primaryVendorAccountNumber",
                        "Account number of the primary vendor",
                        "integer"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.array.itemUnitsInfos",
                        "The different types of item units",
                        "ItemUnitsInformation[]"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.array.itemUnitsInfos.array.quantityType",
                        "The unit of measure for the quantity",
                        "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.array.itemUnitsInfos.array.minimumOrderQuantity",
                        "The minimum order quantity",
                        "integer"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.array.itemUnitsInfos.array.weight",
                        "The item weight",
                        "double"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "localizedItemInformation.array.serialized",
                        "Whether the item is serialized",
                        "boolean")
        );
    }

    @Test
    void it_can_add_a_response_view_model_with_array_of_scalars() {
        prepareBuilderWithoutResponse();
        RestApiModel model = builder
                .setResponseCode(200, "Request was successful")
                .addResponseProperty("catalogNumber", "The catalog number", "string")
                .addArray()
                .addResponseProperty("categoryCodes", "The category codes", "string")
                .addResponseProperty("description", "Description of the item", "string")
                .build();
        assertAll(
                () -> assertResponseContainsProperty(model.getResponses(), 200, "catalogNumber", "The catalog number", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 200, "description", "Description of the item", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 200, "categoryCodes", "The category codes", "string[]")
        );
    }

    @Test
    void it_can_deal_with_response_view_models_that_are_meant_to_be_extended() {
        prepareBuilderWithoutResponse();
        // @formatter:off
        RestApiModel model = builder
                .setResponseCode(200, "Request was successful")
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
                .build();
        // @formatter:on

        assertAll(
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "orderLines.array.BackOrderedLine.atpDate",
                        "The ATP date for the item",
                        "string"),
                () -> assertResponseContainsProperty(model.getResponses(),
                        200, "orderLines.array.ShippedLine.trackingNumber",
                        "The tracking number for the shipment",
                        "string")
        );
    }

    @Test
    void it_is_not_deprecated_by_default() {
        prepareBuilder();
        assertThat(builder.build().isDeprecated(), equalTo(false));
    }

    @Test
    void it_can_be_deprecated() {
        prepareBuilder();
        assertThat(builder.setDeprecated(true).build().isDeprecated(), equalTo(true));
    }

    @Test
    void it_can_set_the_example() {
        prepareBuilder();
        RestApiModel model = builder.setExampleResponse("foo").build();
        assertThat(model.getExampleResponse(), equalTo("foo"));
    }

    @Test
    void it_can_add_multiple_responses() {
        prepareBuilderWithoutResponse();
        RestApiModel model = builder
                .setResponseCode(200, "Request was successful")
                .addResponseProperty("catalogNumber", "The catalog number", "string")
                .addResponseProperty("description", "Description of the item", "string")
                .setResponseCode(401, "No security")
                .addArray()
                .addResponseProperty("requiredRoles", "The client needs to have one of these roles", "string")
                .build();
        assertAll(
                () -> assertResponseContainsProperty(model.getResponses(), 200, "catalogNumber", "The catalog number", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 200, "description", "Description of the item", "string"),
                () -> assertResponseContainsProperty(model.getResponses(), 401, "requiredRoles", "The client needs to have one of these roles", "string[]")
        );
    }

    private void assertResponseContainsProperty(Iterable<RestApiModel.Response> responses, int code, String name, String description, String dataType) {
        List<String> nameParts = Arrays.asList(name.split("\\."));
        ResponsePropertyFinder responsePropertyFinder = new ResponsePropertyFinder(nameParts, description, dataType);
        assertTrue(StreamSupport.stream(responses.spliterator(), false)
                        .filter(response -> response.getCode() == code)
                        .anyMatch(response -> response.getProperties().accept(responsePropertyFinder)),
                String.format("Could not find response property { name = '%s', description = '%s', dataType = '%s'}",
                        name,
                        description,
                        dataType));
    }

    private static class ResponsePropertyFinder implements RestApiModel.Response.DataTypeVisitor<Boolean> {
        private final List<String> nameParts;
        private final String description;
        private final String dataTypeName;

        private ResponsePropertyFinder(List<String> nameParts, String description, String dataTypeName) {
            this.nameParts = nameParts;
            this.description = description;
            this.dataTypeName = dataTypeName;
        }

        @Override
        public Boolean visit(RestApiModel.Response.ScalarDataType scalarDataType) {
            return false;
        }

        @Override
        public Boolean visit(RestApiModel.Response.ArrayDataType arrayDataType) {
            String name = nameParts.get(0);
            if (name.equals("array")) {
                ResponsePropertyFinder responsePropertyFinder = new ResponsePropertyFinder(popNamePart(), description, dataTypeName);
                return arrayDataType.getElementType().accept(responsePropertyFinder);
            }
            return false;
        }

        @Override
        public Boolean visit(RestApiModel.Response.ModelDataType modelDataType) {
            String name = nameParts.get(0);
            for (RestApiModel.Response.Property property : modelDataType) {
                if (Objects.equals(property.getName(), name)
                        && Objects.equals(property.getDescription(), description)
                        && Objects.equals(property.getDataType().getName(), dataTypeName)) {
                    return true;
                }
                if (Objects.equals(property.getName(), name) && nameParts.size() > 1) {
                    ResponsePropertyFinder responsePropertyFinder = new ResponsePropertyFinder(popNamePart(), description, dataTypeName);
                    return property.getDataType().accept(responsePropertyFinder);
                }
            }
            return false;
        }

        @Override
        public Boolean visit(RestApiModel.Response.ChoiceDataType choiceDataType) {
            String name = nameParts.get(0);
            for (RestApiModel.Response.DataType dataType : choiceDataType) {
                if (dataType.getName().equals(name)) {
                    ResponsePropertyFinder responsePropertyFinder = new ResponsePropertyFinder(popNamePart(), description, dataTypeName);
                    return dataType.accept(responsePropertyFinder);
                }
            }
            return false;
        }

        private List<String> popNamePart() {
            return nameParts.subList(1, nameParts.size());
        }
    }

    private void prepareBuilder() {
        prepareBuilderWithoutResponse();
        builder.setResponseCode(200, "No Content");
    }

    private void prepareBuilderWithoutResponse() {
        builder
                .setName("Foo")
                .addUrlPart("bar")
                .setDescription("Description")
                .setMethod("GET")
                .setVersionRange(1, 3);
    }
}