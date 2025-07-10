package org.ex9.contractorservice.dto.contractor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса поиска контрагентов с фильтрацией и пагинацией.
 * Позволяет фильтровать активных контрагентов по различным полям и возвращать результаты постранично.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for searching contractors with filtering and pagination")
public class SearchContractorRequestDto {

	@Schema(description = "Contractor ID for filtering (exact match)", example = "CTR123", nullable = true)
	@Size(max = 12)
	private String contractorId;

	@Schema(description = "Contractor parent ID for filtering (exact match)", example = "CTR456", nullable = true)
	@Size(max = 12)
	private String parentId;

	@Schema(description = "Search string by fields name, name_full, inn, ogrn (partial match)", example = "НКО", nullable = true)
	private String contractorSearch;

	@Schema(description = "Country name to filter (partial match by country.name)", example = "Рос", nullable = true)
	private String country;

	@Schema(description = "Industry ID to filter (exact match)", example = "1", nullable = true)
	private Integer industry;

	@Schema(description = "Name of the organizational form to filter (partial match by org_form.name)", example = "Акцион", nullable = true)
	private String orgForm;

	@Schema(description = "Page number for pagination (starting from 0)", example = "0")
	@NotNull()
	@Min(value = 0)
	private Integer page;

	@Schema(description = "Page size for pagination", example = "10")
	@NotNull()
	@Min(value = 1)
	private Integer size;

}
