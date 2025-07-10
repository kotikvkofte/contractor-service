package org.ex9.contractorservice.dto.contractor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object representing a contractor in the contractor service")
public class ContractorResponseDto {

    @Schema(description = "Unique contractor identifier", example = "CTR")
    private String id;

    @Schema(description = "Unique parent contractor identifier", example = "CTR")
    private String parentId;

    @Schema(description = "Short name of contractor", example = "НКО АО НРД")
    private String name;

    @Schema(description = "Full name of contractor",
            example = "Небанковская кредитная организация акционерное общество «Национальный расчетный депозитарий»")
    private String nameFull;

    @Schema(description = "INN of contractor", example = "7702165310")
    private String inn;

    @Schema(description = "OGRN of contractor", example = "1027739132563")
    private String ogrn;

    @Schema(description = "Country of contractor", example = "Russia")
    private String country;

    @Schema(description = "Industry of contractor", example = "Услуги финансового рынка")
    private String industry;

    @Schema(description = "Organizational form of contractor", example = "Акционерное общество")
    private String orgForm;

}
