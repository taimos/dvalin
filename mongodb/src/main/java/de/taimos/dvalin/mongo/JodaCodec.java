package de.taimos.dvalin.mongo;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.joda.time.DateTime;

/**
 * Joda DateTime codec for mongo db
 *
 * @author fzwirn
 */
public class JodaCodec implements Codec<DateTime> {
    @Override
    public DateTime decode(BsonReader reader, DecoderContext decoderContext) {
        return new DateTime(reader.readDateTime());
    }

    @Override
    public void encode(BsonWriter writer, DateTime value, EncoderContext encoderContext) {
        if (value != null) {
            writer.writeDateTime(value.getMillis());
        }
    }

    @Override
    public Class<DateTime> getEncoderClass() {
        return DateTime.class;
    }
}
