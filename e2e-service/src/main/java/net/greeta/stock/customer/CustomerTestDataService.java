package net.greeta.stock.customer;

import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.testdata.TestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerTestDataService extends TestDataService {

    @Autowired
    @Qualifier("customerJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public void resetDatabase() {
        executeString("DELETE FROM \"customer\".customers");
    }

}
