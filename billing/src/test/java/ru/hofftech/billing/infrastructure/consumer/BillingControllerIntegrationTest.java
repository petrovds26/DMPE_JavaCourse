package ru.hofftech.billing.infrastructure.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.hofftech.billing.config.TestContainerConfig;
import ru.hofftech.billing.util.JsonUtil;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.PageDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционный тест для BillingController.
 * <p>
 * Использует Testcontainers для поднятия реальной PostgreSQL и Kafka.
 */
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class BillingControllerIntegrationTest extends TestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_USER_ID = "Dm";

    @Test
    @DisplayName("GET /billing/history - успешное получение истории биллинга с фильтрацией по датам")
    void shouldReturnFilteredBillingHistory() throws Exception {

        // Выполняем запрос с фильтром по датам: январь 2026
        mockMvc.perform(get("/billing/v1/billing/history")
                        .param("userId", TEST_USER_ID)
                        .param("from", "01.01.2026")
                        .param("to", "31.01.2026")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value("00000"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(20))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                // Проверяем первую запись
                .andExpect(jsonPath("$.data.content[0].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.data.content[0].operationType").value("UNLOAD"))
                .andExpect(jsonPath("$.data.content[0].machineCount").value(1))
                .andExpect(jsonPath("$.data.content[0].parcelCount").value(5))
                .andExpect(jsonPath("$.data.content[0].totalAmount").value(3040.00))
                // Проверяем вторую запись
                .andExpect(jsonPath("$.data.content[1].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.data.content[1].operationType").value("LOAD"))
                .andExpect(jsonPath("$.data.content[1].machineCount").value(2))
                .andExpect(jsonPath("$.data.content[1].parcelCount").value(10))
                .andExpect(jsonPath("$.data.content[1].totalAmount").value(5000.00));

        // Дополнительная проверка: убеждаемся, что третья запись не вернулась
        MvcResult result = mockMvc.perform(get("/billing/v1/billing/history")
                        .param("userId", TEST_USER_ID)
                        .param("from", "01.01.2026")
                        .param("to", "31.01.2026")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        PageDto<BillingDto> response = JsonUtil.fromJson(
                content.substring(content.indexOf("\"data\":") + 7, content.lastIndexOf("}")),
                new TypeReference<>() {});

        // Проверяем, что в ответе только 2 записи
        assertThat(response.content()).hasSize(2);
        // Проверяем, что нет записи с externalId = "ext-test-003"
        assertThat(response.content()).extracting(BillingDto::userId).containsOnly(TEST_USER_ID, TEST_USER_ID);
    }

    @Test
    @DisplayName("GET /billing/history - успешное получение истории биллинга без фильтрации по датам")
    void shouldReturnAllBillingHistory() throws Exception {

        mockMvc.perform(get("/billing/v1/billing/history")
                        .param("userId", TEST_USER_ID)
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value("00000"))
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.content[0].operationType").value("LOAD"))
                .andExpect(jsonPath("$.data.content[1].operationType").value("UNLOAD"));
    }

    @Test
    @DisplayName("GET /billing/history - успешная пагинация")
    void shouldReturnPaginatedBillingHistory() throws Exception {
        // Проверяем первую страницу (2 записи)
        mockMvc.perform(get("/billing/v1/billing/history")
                        .param("userId", TEST_USER_ID)
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andExpect(jsonPath("$.data.hasPrevious").value(false));

        // Проверяем вторую страницу (1 запись)
        mockMvc.perform(get("/billing/v1/billing/history")
                        .param("userId", TEST_USER_ID)
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(true));
    }
}
