package com.ridgid.oss.orm.jpa.convert;

import com.ridgid.oss.common.enumutil.ConvertibleEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
@Converter
public class EnumConverter<ET extends ConvertibleEnum<? extends Enum, DBCT>, DBCT>
        extends com.ridgid.oss.orm.convert.EnumConverter<ET, DBCT>
        implements AttributeConverter<ET, DBCT> {

    public EnumConverter(Class<ET> entityValueClass,
                         Class<DBCT> dbColumnValueClass) {
        super(entityValueClass, dbColumnValueClass);
    }
}
