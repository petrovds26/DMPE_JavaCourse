package ru.hofftech.billing.mapper;

import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.hofftech.billing.model.entity.BillingEntity;
import ru.hofftech.shared.model.dto.BillingDto;

import java.util.List;

/**
 * Маппер для преобразования между сущностью BillingEntity и DTO BillingDto.
 */
@NullMarked
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BillingMapper {

    /**
     * Преобразует BillingEntity в BillingDto.
     *
     * @param billingEntity сущность биллинга
     * @return DTO биллинга
     */
    default BillingDto billingEntityToBillingDto(BillingEntity billingEntity) {
        return BillingDto.builder()
                .userId(billingEntity.getUserId())
                .operationType(billingEntity.getOperationType())
                .machineCount(billingEntity.getMachineCount())
                .parcelCount(billingEntity.getParcelCount())
                .totalAmount(billingEntity.getTotalAmount())
                .createdDt(billingEntity.getCreatedDt())
                .build();
    }

    /**
     * Преобразует список BillingEntity в список BillingDto.
     *
     * @param billingEntityList список сущностей биллинга
     * @return список DTO биллинга
     */
    List<BillingDto> billingEntityListToBillingDtoList(List<BillingEntity> billingEntityList);
}
