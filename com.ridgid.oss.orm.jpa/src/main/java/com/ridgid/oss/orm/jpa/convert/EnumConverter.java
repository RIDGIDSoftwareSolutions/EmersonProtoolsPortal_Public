package com.ridgid.oss.orm.jpa.convert;

import com.ridgid.oss.orm.DBConvertibleEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@SuppressWarnings("unused")
@Converter
public class EnumConverter<ET extends DBConvertibleEnum<IET, DBCT>, IET extends Enum, DBCT>
        extends com.ridgid.oss.orm.convert.EnumConverter<ET, IET, DBCT>
        implements AttributeConverter<ET, DBCT> {

    public EnumConverter(Class<ET> entityValueClass,
                         Class<DBCT> dbColumnValueClass) {
        super(entityValueClass, dbColumnValueClass);
    }
}
