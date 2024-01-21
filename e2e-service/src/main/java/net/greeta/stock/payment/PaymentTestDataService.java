package net.greeta.stock.payment;

import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.testdata.TestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentTestDataService extends TestDataService {

    @Autowired
    @Qualifier("paymentJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public void resetDatabase() {
        executeString("DELETE FROM \"payment\".payments");
        executeString("DELETE FROM \"payment\".credit_entry");
        executeString("DELETE FROM \"payment\".credit_history");
        executeString("DELETE FROM \"payment\".order_outbox");
    }

}
