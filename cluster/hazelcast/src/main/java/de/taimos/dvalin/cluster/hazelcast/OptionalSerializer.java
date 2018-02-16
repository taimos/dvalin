package de.taimos.dvalin.cluster.hazelcast;

/*-
 * #%L
 * Dvalin Hazelcast support
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.Optional;

import com.hazelcast.config.SerializerConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

/**
 * Serializer for Java 8 Optional class
 */
public class OptionalSerializer implements StreamSerializer<Optional> {

    @Override
    public void write(ObjectDataOutput out, Optional object) throws IOException {
        if (object.isPresent()) {
            out.writeObject(object.get());
        } else {
            out.writeObject(null);
        }
    }


    @Override
    public Optional read(ObjectDataInput in) throws IOException {
        Object result = in.readObject();
        return result == null ? Optional.empty() : Optional.of(result);
    }


    @Override
    public int getTypeId() {
        return 1;
    }


    @Override
    public void destroy() {
        // nothing to do here
    }


    public static SerializerConfig createConfig() {
        SerializerConfig optionalSerializer = new SerializerConfig();
        optionalSerializer.setClass(OptionalSerializer.class);
        optionalSerializer.setTypeClass(Optional.class);
        return optionalSerializer;
    }

}
