package database;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import utils.ConfigReader;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * Builds the MyBatis SqlSessionFactory once from mybatis/mybatis-config.xml,
 * with the connection settings taken from config.properties (overridable with -Ddb.url=...).
 */
public final class MyBatisSessionFactory {

    private static final SqlSessionFactory FACTORY = build();

    private MyBatisSessionFactory() {
    }

    private static SqlSessionFactory build() {
        Properties connection = new Properties();
        for (String key : new String[]{"db.driver", "db.url", "db.username", "db.password"}) {
            connection.setProperty(key, ConfigReader.get(key));
        }

        try (Reader config = Resources.getResourceAsReader("mybatis/mybatis-config.xml")) {
            return new SqlSessionFactoryBuilder().build(config, connection);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read mybatis/mybatis-config.xml", e);
        }
    }

    public static SqlSession openSession() {
        return FACTORY.openSession();
    }
}
