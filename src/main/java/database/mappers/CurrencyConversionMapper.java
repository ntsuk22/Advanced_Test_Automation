package database.mappers;

import database.models.CurrencyConversion;

import java.util.List;

/**
 * SQL lives in resources/mybatis/mappers/CurrencyConversionMapper.xml.
 */
public interface CurrencyConversionMapper {

    List<CurrencyConversion> findActive();
}
