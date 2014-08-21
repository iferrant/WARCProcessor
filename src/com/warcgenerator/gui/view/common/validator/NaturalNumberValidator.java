package com.warcgenerator.gui.view.common.validator;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.warcgenerator.gui.helper.ValidatorHelper;

public class NaturalNumberValidator extends AbstractValidator {
	public NaturalNumberValidator(JFrame parent, JTextField c, String message) {
		super(parent, c, message);
	}

	protected boolean validationCriteria(JComponent c) {
		String textToValidate = ((JTextField) c).getText();
		return ValidatorHelper.isNaturalNumber(textToValidate);
	}
}
