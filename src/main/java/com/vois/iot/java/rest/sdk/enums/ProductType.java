package com.vois.iot.java.rest.sdk.enums;

import lombok.Getter;

@Getter
public enum ProductType {

    CYCLE_PLUS_TRACKER("WG", "CyclePlusTracker"),
    GENERAL_TRACKER("69", "GeneralTracker");

    private final String prefix;
    private final String name;

    ProductType(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
    }

    public static String getProductName(String productId) {
        for (ProductType type : values()) {
            if (productId.startsWith(type.getPrefix())) {
                return type.getName();
            }
        }
        return "UnknownProduct"; // Default value if no prefix matches
    }
}
