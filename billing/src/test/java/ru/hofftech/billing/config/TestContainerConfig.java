package ru.hofftech.billing.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.hofftech.billing.BillingApplication;

import java.io.InputStream;
import java.util.Properties;

@Testcontainers
@ActiveProfiles(profiles = {"test"})
@SpringBootTest(classes = BillingApplication.class)
@AutoConfigureMockMvc
public abstract class TestContainerConfig {

    private static final PostgreSQLContainer<?> POSTGRES_SQL_CONTAINER;
    private static final KafkaContainer KAFKA_CONTAINER;

    private static final String POSTGRES_DATABASE_NAME = "dmpe_billing";
    private static final String POSTGRES_USER = "dmpe_billing_cd";
    private static final String POSTGRES_PASSWORD = "123456";

    private static final Network network = Network.newNetwork();
    private static final Properties properties = new Properties();

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream propertiesStream = classLoader.getResourceAsStream("testcontainers.properties")) {
            properties.load(propertiesStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DockerImageName postgresImage = DockerImageName.parse(properties.getProperty("postgres.container.image"))
                .asCompatibleSubstituteFor("postgres");

        POSTGRES_SQL_CONTAINER = new PostgreSQLContainer<>(postgresImage)
                .withDatabaseName(POSTGRES_DATABASE_NAME)
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD)
                .withNetwork(network)
                .withUrlParam("characterEncoding", "UTF-8")
                .withReuse(true);

        POSTGRES_SQL_CONTAINER.start();

        DockerImageName kafkaImage = DockerImageName.parse(properties.getProperty("kafka.container.image"))
                .asCompatibleSubstituteFor("apache/kafka");

        KAFKA_CONTAINER = new KafkaContainer(kafkaImage).withReuse(true);
        KAFKA_CONTAINER.start();
    }

    @DynamicPropertySource
    static void registerContainersProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.datasource.url", POSTGRES_SQL_CONTAINER::getJdbcUrl);
    }

    @BeforeAll
    static void checkContainersRunning() {
        Assertions.assertTrue(POSTGRES_SQL_CONTAINER.isRunning());
        Assertions.assertTrue(KAFKA_CONTAINER.isRunning());
    }
}
