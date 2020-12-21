/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.postgresql.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.r2dbc.postgresql.client.EncodedParameter;
import io.r2dbc.postgresql.message.Format;
import io.r2dbc.postgresql.util.Assert;
import io.r2dbc.postgresql.util.ByteBufUtils;
import reactor.util.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static io.r2dbc.postgresql.codec.PostgresqlObjectId.DATE;
import static io.r2dbc.postgresql.message.Format.FORMAT_TEXT;

final class LocalDateCodec extends AbstractTemporalCodec<LocalDate> {

    private final ByteBufAllocator byteBufAllocator;

    LocalDateCodec(ByteBufAllocator byteBufAllocator) {
        super(LocalDate.class);
        this.byteBufAllocator = Assert.requireNonNull(byteBufAllocator, "byteBufAllocator must not be null");
    }

    @Override
    LocalDate doDecode(ByteBuf buffer, PostgresqlObjectId dataType, @Nullable Format format, Class<? extends LocalDate> type) {
        Assert.requireNonNull(buffer, "byteBuf must not be null");

        return decodeTemporal(buffer, dataType, format, LocalDate.class, temporal -> {
            if (temporal instanceof LocalDateTime) {
                return ((LocalDateTime) temporal).toLocalDate();
            }
            return Instant.from(temporal).atOffset(ZoneOffset.UTC).toLocalDate();
        });
    }

    @Override
    EncodedParameter doEncode(LocalDate value, PostgresqlObjectId dataType) {
        Assert.requireNonNull(value, "value must not be null");

        return create(FORMAT_TEXT, dataType, () -> ByteBufUtils.encode(this.byteBufAllocator, value.toString()));
    }

    @Override
    PostgresqlObjectId getDefaultType() {
        return DATE;
    }

}
