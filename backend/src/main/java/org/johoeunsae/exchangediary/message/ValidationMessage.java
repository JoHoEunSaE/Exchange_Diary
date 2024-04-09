package org.johoeunsae.exchangediary.message;

public @interface ValidationMessage {
    String NOT_NULL = "null이 아닌 값이어야 합니다.";
    String NOT_BLANK = "null이거나 빈 값이어서는 안됩니다.";
    String NOT_EMPTY = "빈 값이어서는 안됩니다.";
}
