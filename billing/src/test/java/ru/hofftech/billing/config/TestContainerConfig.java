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
import org.testcontainers.utility.TestcontainersConfiguration;
import ru.hofftech.billing.BillingApplication;

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
    private static final String POSTGRES_SCHEMA = "billing";

    private static final Network network = Network.newNetwork();

    static {
        TestcontainersConfiguration properties = TestcontainersConfiguration.getInstance();
        DockerImageName postgresImage = DockerImageName.parse(
                        properties.getEnvVarOrProperty("postgres.container.image", null))
                .asCompatibleSubstituteFor("postgres");

        POSTGRES_SQL_CONTAINER = new PostgreSQLContainer<>(postgresImage)
                .withDatabaseName(POSTGRES_DATABASE_NAME)
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD)
                .withNetwork(network)
                .withUrlParam("characterEncoding", "UTF-8")
                .withUrlParam("currentSchema", POSTGRES_SCHEMA)
                .withReuse(true);

        POSTGRES_SQL_CONTAINER.start();

        DockerImageName kafkaImage = DockerImageName.parse(
                        properties.getEnvVarOrProperty("kafka.container.image", null))
                .asCompatibleSubstituteFor("apache/kafka");

        KAFKA_CONTAINER = new KafkaContainer(kafkaImage).withReuse(true);
        KAFKA_CONTAINER.start();
    }

    @DynamicPropertySource
    static void registerContainersProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.datasource.url", POSTGRES_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.flyway.url", POSTGRES_SQL_CONTAINER::getJdbcUrl);
    }

    @BeforeAll
    static void checkContainersRunning() {
        Assertions.assertTrue(POSTGRES_SQL_CONTAINER.isRunning());
        Assertions.assertTrue(KAFKA_CONTAINER.isRunning());
    }
}
