package be.ordina.junit5.demo.extensions.custom;


import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ResourceLock("DB")
class TempDBExtensionTest {

    @RegisterExtension
    static TempDBExtension tempDBExtension = new TempDBExtension("initdb.sql");
    @RepeatedTest(20)
    void test(@TempDBExtension.TempDB DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        assertTrue(jdbcTemplate.queryForList("select * from MyTable").isEmpty());
        jdbcTemplate.execute("insert into MyTable(id, text) values (1, '123124'); ");
        assertEquals(1, jdbcTemplate.queryForList("select * from MyTable").size());

    }


    @Test
    void totallyNotInvolvedTestMethod() {

    }
}
