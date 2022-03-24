package com.jd.mlaas.ump.api.domain.common.util;

import com.jd.legion.utils.time.InstantParser;
import com.jd.legion.utils.time.InstantParsers;
import com.jd.legion.utils.time.TimeRangeBuilder;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Slf4j
public class TimeUtils {

    public static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL
            = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmssSSS"));

    public static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_WITHOUT_MILLISECONDS
            = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));

    public static final InstantParser<Object> CUSTOM_FORMAT = obj -> {
        if (obj instanceof String) {
            try {
                Date date = SIMPLE_DATE_FORMAT_THREAD_LOCAL.get().parse((String) obj);
                return Optional.of(Instant.ofEpochMilli(date.getTime()));
            } catch (Exception e) {
                log.warn("date format fault", e);
            }
        }
        return Optional.empty();
    };

    public static final InstantParser<Object> SIMPLE_FORMAT = obj -> {
        if (obj instanceof String) {
            try {
                Date date = SIMPLE_DATE_FORMAT_WITHOUT_MILLISECONDS.get().parse((String) obj);
                return Optional.of(Instant.ofEpochMilli(date.getTime()));
            } catch (Exception e) {
                log.warn("date format fault", e);
            }
        }
        return Optional.empty();
    };

    private static final InstantParser<Object> CUSTOM_INSTANT_PARSER = InstantParsers.getDefault()
            .or(CUSTOM_FORMAT);

    public static InstantParser<Object> getCustomInstantParser() {
        return CUSTOM_INSTANT_PARSER;
    }

    public static TimeRangeBuilder newTimeRangeBuilder() {
        return TimeRangeBuilder.create()
                .instantParser(getCustomInstantParser());
    }

}
