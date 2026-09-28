package database.mappers;

import database.models.CurrencyConversion;

import java.util.List;

public interface CurrencyConversionMapper {

    List<CurrencyConversion> findActive();
}
