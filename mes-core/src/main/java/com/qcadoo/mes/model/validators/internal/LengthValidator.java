/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 0.3.0
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */

package com.qcadoo.mes.model.validators.internal;

import java.math.BigDecimal;

import com.qcadoo.mes.api.Entity;
import com.qcadoo.mes.model.DataDefinition;
import com.qcadoo.mes.model.FieldDefinition;
import com.qcadoo.mes.model.validators.FieldValidator;

public final class LengthValidator implements FieldValidator {

    private static final String INVALID_LENGTH_ERROR = "core.validate.field.error.invalidLength";

    private final Integer max;

    private final Integer min;

    private final Integer is;

    private String errorMessage = INVALID_LENGTH_ERROR;

    public LengthValidator(final Integer min, final Integer is, final Integer max) {
        this.min = min;
        this.is = is;
        this.max = max;
    }

    @Override
    public boolean validate(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition, final Object value,
            final Entity validatedEntity) {
        if (value == null) {
            return true;
        }

        Class<?> fieldClass = fieldDefinition.getType().getType();

        if (!(fieldClass.equals(String.class) || fieldClass.equals(Integer.class) || fieldClass.equals(BigDecimal.class))) {
            return true;
        }

        int length = value.toString().length();

        return validateLength(fieldDefinition, validatedEntity, length);
    }

    private boolean validateLength(final FieldDefinition fieldDefinition, final Entity validatedEntity, final int length) {
        if (max != null && length > max) {
            validatedEntity.addError(fieldDefinition, errorMessage);
            return false;
        }
        if (min != null && length < min) {
            validatedEntity.addError(fieldDefinition, errorMessage);
            return false;
        }
        if (is != null && !is.equals(length)) {
            validatedEntity.addError(fieldDefinition, errorMessage);
            return false;
        }

        return true;
    }

    @Override
    public boolean validate(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition, final Entity entity) {
        return true;
    }

    @Override
    public FieldValidator customErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

}
