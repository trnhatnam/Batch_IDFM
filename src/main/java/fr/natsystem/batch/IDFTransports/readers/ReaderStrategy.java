package fr.natsystem.batch.IDFTransports.readers;

import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;

public interface ReaderStrategy {
    FlatFileItemReader getCSVReader();

}
