package com.miwdemo.shop.data;

import com.miwdemo.shop.model.ItemRepository;
import com.miwdemo.shop.model.Item;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(ItemRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Item("Design Patterns: Elements of Reusable Object-Oriented Software",
                    "This classical book is critical reading to really understand what design patterns are and " +
                            "become familiar with the most common design patterns you are likely to encounter in your career.",
                    12, 3)));
            log.info("Preloading " + repository.save(new Item("Apple MacBook Pro", "16-inch, 64Gb, 8Tb", 3499, 2)));
            log.info("Preloading " + repository.save(new Item("Lenovo Thinkpad P73", "17-inch, 32Gb, 1Tb", 5759, 3)));
        };
    }
}



