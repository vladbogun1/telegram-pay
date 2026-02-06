package com.example.telegrampay.security;

public final class CreatorContext {
    private static final ThreadLocal<Long> CREATOR_ID = new ThreadLocal<>();

    private CreatorContext() {
    }

    public static void setCreatorId(Long creatorId) {
        CREATOR_ID.set(creatorId);
    }

    public static Long getCreatorId() {
        return CREATOR_ID.get();
    }

    public static void clear() {
        CREATOR_ID.remove();
    }
}
