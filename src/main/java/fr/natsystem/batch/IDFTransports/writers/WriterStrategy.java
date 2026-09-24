package fr.natsystem.batch.IDFTransports.writers;

import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;

public interface WriterStrategy {
    JdbcBatchItemWriter getWriter();
}
