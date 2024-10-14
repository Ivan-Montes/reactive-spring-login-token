package dev.ime.application.dto;

import dev.ime.config.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDto(
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_EMAIL ) String email,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL ) String password
		) {

}
