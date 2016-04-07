/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.dynamodb.marshal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;

/**
 * {@link DynamoDBMarshaller} to (de-)serialize enums using the string representation
 */
public class EnumMarshaller implements DynamoDBMarshaller<Enum> {

    @Override
    public String marshall(Enum getterReturnResult) {
        if (getterReturnResult == null) {
            return null;
        }
        return getterReturnResult.toString();
    }

    @Override
    public Enum unmarshall(Class clazz, String obj) {
        if (obj == null) {
            return null;
        }
        return Enum.valueOf(clazz, obj);
    }
}
