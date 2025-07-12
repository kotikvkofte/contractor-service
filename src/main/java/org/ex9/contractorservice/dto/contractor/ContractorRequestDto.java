package org.ex9.contractorservice.dto.contractor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for creating or updating a contractor in the contractor service")
public class ContractorRequestDto {

	@NotNull
	@Size(max = 12)
	@Schema(description = "Unique contractor identifier", example = "CTR")
	private String id;

	@Size(max = 12)
	@Schema(description = "Unique parent contractor identifier", example = "CTR")
	private String parentId;

	@NotNull
	@NotBlank
	@Schema(description = "Short name of contractor", example = "НКО АО НРД")
	private String name;

	@Schema(description = "Full name of contractor",
			example = "Небанковская кредитная организация акционерное общество «Национальный расчетный депозитарий»")
	private String nameFull;

	@Schema(description = "INN of contractor", example = "7702165310")
	private String inn;

	@Schema(description = "OGRN of contractor", example = "1027739132563")
	private String ogrn;

	@Schema(description = "Country identifier of contractor", example = "Russia")
	private String countryId;

	@Schema(description = "Industry identifier of contractor", example = "8")
	private int industryId;

	@Schema(description = "Organizational form identifier of contractor", example = "56")
	private int orgFormId;

}
