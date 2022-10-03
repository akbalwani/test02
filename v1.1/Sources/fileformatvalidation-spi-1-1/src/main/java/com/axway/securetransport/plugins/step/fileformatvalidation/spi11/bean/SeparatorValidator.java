package com.axway.securetransport.plugins.step.fileformatvalidation.spi11.bean;

import com.axway.securetransport.plugins.step.fileformatvalidation.spi11.Constants;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SeparatorValidator implements ConstraintValidator<IsSeparatorValid, FileFormatValidationBean> {

    @Override
    public boolean isValid(FileFormatValidationBean fileFormatValidationBean, ConstraintValidatorContext constraintValidatorContext) {
        String separatorValue = fileFormatValidationBean.getSeparator();
        String fileTypeValue = fileFormatValidationBean.getFileType();

        return !(fileTypeValue.equals(Constants.CSV) && (separatorValue == null || separatorValue.length() == 0));
    }
}
