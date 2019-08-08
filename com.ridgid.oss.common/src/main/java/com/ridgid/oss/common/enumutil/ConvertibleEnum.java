package com.ridgid.oss.common.enumutil;


/**
 * Implement this method for an Enum @code{EnumType} that needs to be serialized to the Database (or similar) as a particular value type @code{DBColumnType}.
 * The enum must have a static public method named "from" that takes a one parameter of type @code{DBColumnType} and returns the corresponding enum type value @code{EnumType}
 * <p>
 * Example of usage:
 *
 * <pre>
 * {@code
 *     public enum FooEnum implements ConvertibleEnum<FooEnum,String> {
 *
 *          FOO_VALUE1("SerializedValue1"),
 *          FOO_VALUE2("SerializedValue2"),
 *          OTHER;
 *
 *          private String value;
 *
 *          private FooEnum() { this.value = null; }
 *          private FooEnum(String value) { this.value = value; }
 *
 *          public static FooEnum from( String dbValue ) {
 *              switch ( dbValue )  {
 *                  case "SerializedValue1":
 *                      return FOO_VAlUE1;
 *                  case "SerializedValue2":
 *                      return FOO_VALUE2;
 *                  default:
 *                      return OTHER;
 *              }
 *          }
 *
 *          public String into() {
 *              return this.value;
 *          }
 *     }
 * }
 * </pre>
 * <p>
 * This interface exists for purposes of having an Enum that can easily be converted to/from the database in a format
 * specific to the database through a General Enum Attribute converter. @code{com.ridgid.oss.orm.convert.EnumConverter} class
 * provides a Generic converter implementation that works one this interface.
 *
 * @param <EnumType> type of the Enum that is implementing this interface
 * @param <ToType>   type of the serialized value in the DB for a particular Enum value
 */
@SuppressWarnings("unused")
public interface ConvertibleEnum<EnumType extends Enum, ToType> {

    /**
     * Converts this enum value to the database column value type
     *
     * @return database column value type DBColumnType
     */
    ToType into();
}
