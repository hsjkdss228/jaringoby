package com.wanted.jaringoby.common.converters;

import com.wanted.jaringoby.common.models.Money;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MoneyConverter implements AttributeConverter<Money, Long> {

    @Override
    public Long convertToDatabaseColumn(Money money) {
        return money.value();
    }

    @Override
    public Money convertToEntityAttribute(Long value) {
        return Money.of(value);
    }
}
