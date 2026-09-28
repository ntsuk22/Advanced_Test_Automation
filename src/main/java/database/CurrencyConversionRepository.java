package database;

import database.mappers.CurrencyConversionMapper;
import database.models.CurrencyConversion;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public final class CurrencyConversionRepository {

    private CurrencyConversionRepository() {
    }

    public static List<CurrencyConversion> findActive() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(CurrencyConversionMapper.class).findActive();
        }
    }
}
