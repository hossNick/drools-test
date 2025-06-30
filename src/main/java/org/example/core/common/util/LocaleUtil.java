package org.example.core.common.util;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;


public class LocaleUtil {

    private static final String DEFAULT_MSG_KEY = "DEFAULT_MSG";
    private static MessageSource messageSource ;

    public static MessageSource getMessageSource() {
        if (messageSource == null)
            messageSource = SpringUtil.getBean(MessageSource.class);
        return messageSource;
    }

    public static String getLocalizedMessage(String code,List<String> parameters) {
        String[] translatedParams = null;
        if (parameters != null){
            translatedParams = parameters.stream().map(item-> getFarsiMessage(item,null)).toArray(String[]::new);
        }
        return LocaleUtil.getFarsiMessage(code,translatedParams);
    }

    private static String getMainLocalizedMessage(@NotNull String code, String[] args, @Nullable Locale locale) {
        Locale dedicatedLocal = locale != null ? locale : Locale.getDefault();
        String defaultMsg = getMessageSource().getMessage(DEFAULT_MSG_KEY, null, dedicatedLocal);
        if (code == null) return defaultMsg;

        return getMessageSource().getMessage(code, args, dedicatedLocal);
    }

    public static String getLocalizedMessage(@NotNull String code, String[] args, @NotNull Locale locale) {
        return getMainLocalizedMessage(code, args, locale);
    }

    public static String getLocalizedMessage(@NotNull String code, String[] args) {
        return getMainLocalizedMessage(code, args, Locale.getDefault());
    }

    public static String getFarsiMessage(@NotNull String code, String[] args) {
        return getMainLocalizedMessage(code, args, Locale.of("fa"));
    }

}