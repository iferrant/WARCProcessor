package com.warcgenerator.gui.view.common.validator;

public interface WantsValidationStatus {
	void validateFailed();  // Called when a component has failed validation.
	void validatePassed();  // Called when a component has passed validation.
}
