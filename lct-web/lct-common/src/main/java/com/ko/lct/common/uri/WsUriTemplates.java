package com.ko.lct.common.uri;

public class WsUriTemplates {

    public static final String APP_JSON_MIME = "application/json";
    public static final String APP_XML_MIME = "application/xml";
    public static final String APP_MAPPING_MIME = "application/mapping_items";

    private static final String BASE_URI = "/v1/";

    private static final String FORMAT_XML = "/xml";
    private static final String FORMAT_JSON = "/json";

    private static final String CLIENT_ID_PARAM = "/{clientId}";
    private static final String LOCALE_PARAM = "/{locale}";
    private static final String CLIENT_ID_AND_LOCALE = CLIENT_ID_PARAM + LOCALE_PARAM;

    private static final String BEVERAGE_CATEGORY_CODE_PARAM = "/{beverageCategoryCode}";
    private static final String BRAND_CODE_PARAM = "/{brandCode}";
    private static final String LATITUDE_PARAM = "/{latitude}";
    private static final String LONGITUDE_PARAM = "/{longitude}";
    private static final String DISTANCE_PARAM = "/{distance}";
    private static final String DISTANCE_UNIT_PARAM = "/{distanceUnit}";
    private static final String PRODUCT_TYPE_CODE_PARAM = "/{productTypeCode}";
    private static final String FLAVOR_CODE_PARAM = "/{flavorCode}";
    private static final String PRIMARY_CONTAINER_CODE_PARAM = "/{primaryContainerCode}";
    private static final String SECONDARY_PACKAGE_CODE_PARAM = "/{secondaryPackageCode}";
    private static final String BUSINESS_TYPE_CODE_PARAM = "/{businessTypeCode}";
    private static final String PHYSICAL_STATE_CODE_PARAM = "/{physicalStateCode}";
    private static final String TRADE_CHANNEL_CODE_PARAM = "/{tradeChannelCode}";
    private static final String SUB_TRADE_CHANNEL_CODE_PARAM = "/{subTradeChannelCode}";
    private static final String OUTLET_NAME_PARAM = "/{outletName}";
    private static final String INCLUDE_FOOD_SERVICE_PARAM = "/{includeFoodService}";
    private static final String IS_KOSHER_PRODUCT_PARAM = "/{kosherProductOnly}";
    private static final String PAGE_NUMBER_PARAM = "/{pageNumber}";
    private static final String RECORDS_COUNT_PARAM = "/{recordsCount}";
    private static final String SORT_COLUMN_PARAM = "/{sortColumn}";
    private static final String SORT_ORDER_PARAM = "/{sortOrder}";
    private static final String SIGNATURE_PARAM = "/{signature}";
    private static final String MAPPING_TYPE_PARAM = "/{mappingType}";
    private static final String MAPPING_SRC_PARAM = "/{srcVal}";

    public static final String PRODUCT = "products";
    public static final String PRODUCTS_XML = BASE_URI + PRODUCT + FORMAT_XML + CLIENT_ID_AND_LOCALE;
    public static final String PRODUCTS_JSON = BASE_URI + PRODUCT + FORMAT_JSON + CLIENT_ID_AND_LOCALE;

    public static final String BEVERAGE_CATEGORIES = "beverage_categories";
    public static final String BEVERAGE_CATEGORIES_XML = BASE_URI + BEVERAGE_CATEGORIES + FORMAT_XML + CLIENT_ID_AND_LOCALE;
    public static final String BEVERAGE_CATEGORIES_JSON = BASE_URI + BEVERAGE_CATEGORIES + FORMAT_JSON + CLIENT_ID_AND_LOCALE;

    public static final String BRANDS = "brands";
    public static final String BRANDS_XML = BASE_URI + BRANDS + FORMAT_XML + CLIENT_ID_AND_LOCALE + BEVERAGE_CATEGORY_CODE_PARAM;
    public static final String BRANDS_JSON = BASE_URI + BRANDS + FORMAT_JSON + CLIENT_ID_AND_LOCALE + BEVERAGE_CATEGORY_CODE_PARAM;

    public static final String FLAVORS = "flavors";
    public static final String FLAVORS_XML = BASE_URI + FLAVORS + FORMAT_XML + CLIENT_ID_AND_LOCALE + BEVERAGE_CATEGORY_CODE_PARAM + BRAND_CODE_PARAM;
    public static final String FLAVORS_JSON = BASE_URI + FLAVORS + FORMAT_JSON + CLIENT_ID_AND_LOCALE + BEVERAGE_CATEGORY_CODE_PARAM + BRAND_CODE_PARAM;

    public static final String PHYSICAL_STATES = "physical_states";
    public static final String PHYSICAL_STATES_XML = BASE_URI + PHYSICAL_STATES + FORMAT_XML + CLIENT_ID_AND_LOCALE;
    public static final String PHYSICAL_STATES_JSON = BASE_URI + PHYSICAL_STATES + FORMAT_JSON + CLIENT_ID_AND_LOCALE;

    public static final String TRADE_CHANNELS = "trade_channels";
    public static final String TRADE_CHANNELS_XML = BASE_URI + TRADE_CHANNELS + FORMAT_XML + CLIENT_ID_AND_LOCALE;
    public static final String TRADE_CHANNELS_JSON = BASE_URI + TRADE_CHANNELS + FORMAT_JSON + CLIENT_ID_AND_LOCALE;

    public static final String PACKAGES = "packages";
    public static final String PACKAGES_XML = BASE_URI + PACKAGES + FORMAT_XML + CLIENT_ID_AND_LOCALE;
    public static final String PACKAGES_JSON = BASE_URI + PACKAGES + FORMAT_JSON + CLIENT_ID_AND_LOCALE;

    public static final String PRIMARY_CONTAINERS = "primary_containers";
    public static final String PRIMARY_CONTAINERS_XML = BASE_URI + PRIMARY_CONTAINERS + FORMAT_XML + CLIENT_ID_AND_LOCALE + BRAND_CODE_PARAM + FLAVOR_CODE_PARAM;
    public static final String PRIMARY_CONTAINERS_JSON = BASE_URI + PRIMARY_CONTAINERS + FORMAT_JSON + CLIENT_ID_AND_LOCALE + BRAND_CODE_PARAM + FLAVOR_CODE_PARAM;

    public static final String SECONDARY_PACKAGES = "secondary_packages";
    public static final String SECONDARY_PACKAGES_XML = BASE_URI + SECONDARY_PACKAGES + FORMAT_XML + CLIENT_ID_AND_LOCALE + BRAND_CODE_PARAM + FLAVOR_CODE_PARAM
	    + PRIMARY_CONTAINER_CODE_PARAM;
    public static final String SECONDARY_PACKAGES_JSON = BASE_URI + SECONDARY_PACKAGES + FORMAT_JSON + CLIENT_ID_AND_LOCALE + BRAND_CODE_PARAM + FLAVOR_CODE_PARAM
	    + PRIMARY_CONTAINER_CODE_PARAM;

    public static final String PRODUCT_PACKAGE_TYPES = "product_types";
    public static final String PRODUCT_PACKAGE_TYPES_XML = BASE_URI + PRODUCT_PACKAGE_TYPES + FORMAT_XML + CLIENT_ID_AND_LOCALE;
    public static final String PRODUCT_PACKAGE_TYPES_JSON = BASE_URI + PRODUCT_PACKAGE_TYPES + FORMAT_JSON + CLIENT_ID_AND_LOCALE;

    public static final String BUSINESS_TYPES = "business_types";
    public static final String BUSINESS_TYPES_XML = BASE_URI + BUSINESS_TYPES + FORMAT_XML + CLIENT_ID_AND_LOCALE;
    public static final String BUSINESS_TYPES_JSON = BASE_URI + BUSINESS_TYPES + FORMAT_JSON + CLIENT_ID_AND_LOCALE;

    public static final String STATES = "states";
    public static final String STATES_XML = BASE_URI + STATES + FORMAT_XML + CLIENT_ID_AND_LOCALE;
    public static final String STATES_JSON = BASE_URI + STATES + FORMAT_JSON + CLIENT_ID_AND_LOCALE;

    public static final String COUNTRIES = "countries";
    public static final String COUNTRIES_XML = BASE_URI + COUNTRIES + FORMAT_XML + CLIENT_ID_AND_LOCALE;
    public static final String COUNTRIES_JSON = BASE_URI + COUNTRIES + FORMAT_JSON + CLIENT_ID_AND_LOCALE;

    private static final String MAPPING_ITEMS_FORMATS = MAPPING_TYPE_PARAM + PAGE_NUMBER_PARAM + RECORDS_COUNT_PARAM;
    private static final String MAPPING_REMOVE_ITEM_FORMATS = MAPPING_TYPE_PARAM + MAPPING_SRC_PARAM;
    public static final String MAPPING_TYPES = "mapping_types";
    public static final String MAPPING_ITEMS = "mapping_items";
    public static final String MAPPING_UPDATE_ITEMS = "mapping_update";
    public static final String MAPPING_REMOVE_ITEM = "mapping_remove";
    public static final String MAPPING_ITEMS_XML = BASE_URI + MAPPING_ITEMS + FORMAT_XML + MAPPING_ITEMS_FORMATS;
    public static final String MAPPING_ITEMS_JSON = BASE_URI + MAPPING_ITEMS + FORMAT_JSON + MAPPING_ITEMS_FORMATS;
    public static final String MAPPING_UPDATE_JSON = BASE_URI + MAPPING_UPDATE_ITEMS;
    public static final String MAPPING_REMOVE_JSON = BASE_URI + MAPPING_REMOVE_ITEM + FORMAT_JSON + MAPPING_REMOVE_ITEM_FORMATS;
    public static final String MAPPING_REMOVE_XML = BASE_URI + MAPPING_REMOVE_ITEM + FORMAT_XML + MAPPING_REMOVE_ITEM_FORMATS;

    private static final String SEARCH = "search";
    private static final String SEARCH_ALL_FORMATS = CLIENT_ID_PARAM + SIGNATURE_PARAM + LOCALE_PARAM +
	    LATITUDE_PARAM + LONGITUDE_PARAM + DISTANCE_PARAM + DISTANCE_UNIT_PARAM +
	    BEVERAGE_CATEGORY_CODE_PARAM + PRODUCT_TYPE_CODE_PARAM + BRAND_CODE_PARAM + FLAVOR_CODE_PARAM +
	    PRIMARY_CONTAINER_CODE_PARAM + SECONDARY_PACKAGE_CODE_PARAM + BUSINESS_TYPE_CODE_PARAM +
	    PHYSICAL_STATE_CODE_PARAM + TRADE_CHANNEL_CODE_PARAM + SUB_TRADE_CHANNEL_CODE_PARAM + OUTLET_NAME_PARAM +
	    INCLUDE_FOOD_SERVICE_PARAM + IS_KOSHER_PRODUCT_PARAM + PAGE_NUMBER_PARAM + RECORDS_COUNT_PARAM +
	    SORT_COLUMN_PARAM + SORT_ORDER_PARAM;
    public static final String SEARCH_XML = BASE_URI + SEARCH + FORMAT_XML + SEARCH_ALL_FORMATS;
    public static final String SEARCH_JSON = BASE_URI + SEARCH + FORMAT_JSON + SEARCH_ALL_FORMATS;

    /* V2 */
    private static final String BASE_V2_URI = "/v2/";
    private static final String PRODUCT_CODE_PARAM = "/{productCode}";
    /*
    private static final String PRIMARY_CONTAINER_SHORT_CODE_PARAM = "/{primaryContainerShortCode}";
    private static final String SECONDARY_PACKAGE_SHORT_CODE_PARAM = "/{secondaryPackageShortCode}";
    */

    public static final String PRIMARY_CONTAINERS_V2_XML = BASE_V2_URI + PRIMARY_CONTAINERS + FORMAT_XML + CLIENT_ID_AND_LOCALE + BRAND_CODE_PARAM + FLAVOR_CODE_PARAM
	    + PRODUCT_CODE_PARAM;
    public static final String PRIMARY_CONTAINERS_V2_JSON = BASE_V2_URI + PRIMARY_CONTAINERS + FORMAT_JSON + CLIENT_ID_AND_LOCALE + BRAND_CODE_PARAM + FLAVOR_CODE_PARAM
	    + PRODUCT_CODE_PARAM;

    private static final String SEARCH_V2_ALL_FORMATS = CLIENT_ID_PARAM + SIGNATURE_PARAM + LOCALE_PARAM +
	    LATITUDE_PARAM + LONGITUDE_PARAM + DISTANCE_PARAM + DISTANCE_UNIT_PARAM +
	    BEVERAGE_CATEGORY_CODE_PARAM + PRODUCT_TYPE_CODE_PARAM +
	    BRAND_CODE_PARAM + FLAVOR_CODE_PARAM + PRODUCT_CODE_PARAM +
	    /* PRIMARY_CONTAINER_SHORT_CODE_PARAM + */ PRIMARY_CONTAINER_CODE_PARAM +
	    /* SECONDARY_PACKAGE_SHORT_CODE_PARAM + */ SECONDARY_PACKAGE_CODE_PARAM +
	    BUSINESS_TYPE_CODE_PARAM +
	    PHYSICAL_STATE_CODE_PARAM + TRADE_CHANNEL_CODE_PARAM + SUB_TRADE_CHANNEL_CODE_PARAM + OUTLET_NAME_PARAM +
	    INCLUDE_FOOD_SERVICE_PARAM + IS_KOSHER_PRODUCT_PARAM + PAGE_NUMBER_PARAM + RECORDS_COUNT_PARAM +
	    SORT_COLUMN_PARAM + SORT_ORDER_PARAM;
    public static final String SEARCH_V2_XML = BASE_V2_URI + SEARCH + FORMAT_XML + SEARCH_V2_ALL_FORMATS;
    public static final String SEARCH_V2_JSON = BASE_V2_URI + SEARCH + FORMAT_JSON + SEARCH_V2_ALL_FORMATS;
}
