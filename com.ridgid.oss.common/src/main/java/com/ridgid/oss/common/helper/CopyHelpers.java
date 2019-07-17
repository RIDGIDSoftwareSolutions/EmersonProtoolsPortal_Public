package com.ridgid.oss.common.helper;

import com.ridgid.oss.emerson.common.tuple.Pair;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@SuppressWarnings({"unused", "unchecked"})
public final class CopyHelpers {

    private CopyHelpers() {
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        Collections.EMPTY_SET,
                        Collections.EMPTY_MAP,
                        Collections.EMPTY_SET,
                        Collections.EMPTY_MAP
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Set<String> excludeFromFields) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        excludeFromFields,
                        Collections.EMPTY_MAP,
                        Collections.EMPTY_SET,
                        Collections.EMPTY_MAP
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Map<String, String> fromToFieldAliases) {

        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        Collections.EMPTY_SET,
                        fromToFieldAliases,
                        Collections.EMPTY_SET,
                        Collections.EMPTY_MAP
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Set<String> excludeFromFields,
                                                            Map<String, String> fromToFieldAliases) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        excludeFromFields,
                        fromToFieldAliases,
                        Collections.EMPTY_SET,
                        Collections.EMPTY_MAP
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Map<String, String> fromToFieldAliases,
                                                            Set<String> excludeToFields) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        Collections.EMPTY_SET,
                        fromToFieldAliases,
                        excludeToFields,
                        Collections.EMPTY_MAP
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Map<String, String> fromToFieldAliases,
                                                            Map<String, Function<Object, Object>> fromTypeMappers) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        Collections.EMPTY_SET,
                        fromToFieldAliases,
                        Collections.EMPTY_SET,
                        fromTypeMappers
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Map<String, String> fromToFieldAliases,
                                                            Pair<String, Function<Object, Object>>... fromTypeMappers) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        Collections.EMPTY_SET,
                        fromToFieldAliases,
                        Collections.EMPTY_SET,
                        Arrays.stream(fromTypeMappers).collect(toMap(p -> p.left, p -> p.right))
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Set<String> excludeFromFields,
                                                            Map<String, String> fromToFieldAliases,
                                                            Map<String, Function<Object, Object>> fromTypeMappers) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        excludeFromFields,
                        fromToFieldAliases,
                        Collections.EMPTY_SET,
                        fromTypeMappers
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Set<String> excludeFromFields,
                                                            Map<String, String> fromToFieldAliases,
                                                            Pair<String, Function<Object, Object>>... fromTypeMappers) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        excludeFromFields,
                        fromToFieldAliases,
                        Collections.EMPTY_SET,
                        Arrays.stream(fromTypeMappers).collect(toMap(p -> p.left, p -> p.right))
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Map<String, String> fromToFieldAliases,
                                                            Set<String> excludeToFields,
                                                            Map<String, Function<Object, Object>> fromTypeMappers) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        Collections.EMPTY_SET,
                        fromToFieldAliases,
                        excludeToFields,
                        fromTypeMappers
                );
    }

    public static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Map<String, String> fromToFieldAliases,
                                                            Set<String> excludeToFields,
                                                            Pair<String, Function<Object, Object>>... fromTypeMappers) {
        return copyTransformerFor
                (
                        modelFromClass,
                        modelToClass,
                        Collections.EMPTY_SET,
                        fromToFieldAliases,
                        excludeToFields,
                        Arrays.stream(fromTypeMappers).collect(toMap(p -> p.left, p -> p.right))
                );
    }


    private static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    ModelTransformer<ModelFrom, ModelTo> copyTransformerFor(Class<ModelFrom> modelFromClass,
                                                            Class<ModelTo> modelToClass,
                                                            Set<String> excludeFromFields,
                                                            Map<String, String> fromToFieldAliases,
                                                            Set<String> excludeToFields,
                                                            Map<String, Function<Object, Object>> fromTypeMappers) {
        Map<String, Field> toFields = new HashMap<>();
        FieldReflectionHelpers
                .streamAllNonStaticFieldsFor(modelToClass)
                .forEach(f -> toFields.putIfAbsent(f.getName(), f));
        List<FieldTransformer> fieldsMap = FieldReflectionHelpers
                .streamAllNonStaticFieldsFor(modelFromClass)
                .filter(f -> !excludeFromFields.contains(f.getName()))
                .map(f -> new FieldTransformer(f, fromToFieldAliases.getOrDefault(f.getName(), f.getName())))
                .filter(f -> !excludeToFields.contains(f.getToFieldName()))
                .peek(f -> f.setToField(toFields.get(f.getToFieldName())))
                .filter(f -> f.getToField() != null)
                .peek(f -> f.setFromTypeMapper(fromTypeMappers.get(f.getFromField().getName())))
                .collect(toList());
        return new ModelTransformerImpl(modelFromClass, modelToClass, fieldsMap);
    }

    private static <ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
    void copyField(Field f,
                   Map<String, String> fromToFieldAliases,
                   Collection<ModelFrom> from,
                   Set<String> excludeToFields,
                   Supplier<ModelTo> toSupplier,
                   Map<String, Function<Object, Object>> fromTypeMappers,
                   Map<String, Field> toFields) {

    }

    public interface ModelTransformer<ModelFrom extends CopyableModel, ModelTo extends CopyableModel> {
        ModelTo transform(Supplier<ModelTo> modelToSupplier, ModelFrom fromModel);

        Stream<ModelTo> transform(Supplier<ModelTo> modelToSupplier, ModelFrom... fromModels);

        Stream<ModelTo> transform(Supplier<ModelTo> modelToSupplier, Collection<ModelFrom> fromModels);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private static class ModelTransformerImpl<ModelFrom extends CopyableModel, ModelTo extends CopyableModel>
            implements ModelTransformer<ModelFrom, ModelTo> {

        private final Class<ModelFrom> modelFromClass;
        private final Class<ModelTo> modelToClass;
        private final List<FieldTransformer> fieldsMap;

        ModelTransformerImpl(Class<ModelFrom> modelFromClass,
                             Class<ModelTo> modelToClass,
                             List<FieldTransformer> fieldsMap) {
            this.modelFromClass = modelFromClass;
            this.modelToClass = modelToClass;
            this.fieldsMap = fieldsMap;
        }

        @Override
        public ModelTo transform(Supplier<ModelTo> modelToSupplier, ModelFrom fromModel) {
            ModelTo toModel = modelToSupplier.get();
            for (FieldTransformer fieldMap : fieldsMap) {
                Object fromValue = FieldReflectionHelpers.getFieldValueOrThrowRuntimeException
                        (
                                fromModel,
                                fieldMap.getFromField()
                        );
                FieldReflectionHelpers.setFieldValueOrThrowException
                        (
                                toModel,
                                fieldMap.getToField(),
                                fieldMap.mapValue(fromValue)
                        );
            }
            return toModel;
        }

        @Override
        public Stream<ModelTo> transform(Supplier<ModelTo> modelToSupplier, ModelFrom... fromModels) {
            return Arrays.stream(fromModels).map(fm -> transform(modelToSupplier, fm));
        }

        @Override
        public Stream<ModelTo> transform(Supplier<ModelTo> modelToSupplier, Collection<ModelFrom> fromModels) {
            return fromModels.stream().map(fm -> transform(modelToSupplier, fm));
        }
    }

    private static class FieldTransformer {
        private final Field fromField;
        private final String toFieldName;
        private Field toField;
        private Function<Object, Object> fromTypeMapper;

        FieldTransformer(Field fromField, String toFieldName) {
            this.fromField = fromField;
            this.toFieldName = toFieldName;
        }

        Field getFromField() {
            return fromField;
        }

        String getToFieldName() {
            return toFieldName;
        }

        void setToField(Field toField) {
            this.toField = toField;
        }

        Field getToField() {
            return toField;
        }

        void setFromTypeMapper(Function<Object, Object> fromTypeMapper) {
            this.fromTypeMapper = fromTypeMapper;
        }

        Object mapValue(Object fromValue) {
            return fromTypeMapper == null ? fromValue : fromTypeMapper.apply(fromValue);
        }
    }
}
