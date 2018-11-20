package org.apereo.cas.memcached.kryo.serial;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * This is {@link ThrowableSerializer}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
public class ThrowableSerializer extends Serializer<Throwable> {


    @Override
    public void write(final Kryo kryo, final Output output, final Throwable object) {
        kryo.writeObject(output, object.getClass());
        kryo.writeObject(output, StringUtils.defaultIfBlank(object.getMessage(), StringUtils.EMPTY));
    }

    @Override
    public Throwable read(final Kryo kryo, final Input input, final Class<Throwable> type) {
        try {
            final Class clazz = kryo.readObject(input, Class.class);
            final String msg = kryo.readObject(input, String.class);
            final Throwable throwable = (Throwable) clazz.getDeclaredConstructor(String.class).newInstance(msg);
            return throwable;
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return new Throwable();
    }
}
