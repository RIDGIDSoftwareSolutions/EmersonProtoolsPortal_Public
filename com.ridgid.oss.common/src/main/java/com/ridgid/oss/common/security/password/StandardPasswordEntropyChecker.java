package com.ridgid.oss.common.security.password;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@SuppressWarnings({"SpellCheckingInspection", "WeakerAccess"})
public class StandardPasswordEntropyChecker implements SecurePasswordEntropyChecker
{
    private final int           minPasswordLength;
    private final int           maxPasswordLength;
    private final int           minCharsForEachCategory;
    private final int           numberOfCategoriesTotal;
    private final int           numberOfCategoriesToRequire;
    private final List<Pattern> categoryRegexPatterns;
    private final String        formattableErrorMessage;

    public StandardPasswordEntropyChecker() {
        this
            (
                ("Password must include at least {0,choice,0#characters|1#character|1<{0,number, integer} characters} "
                 + "from at least {1, number, integer} of the following {2, number, integer} categories: "
                 + "\n--LowerCase "
                 + "\n--UpperCase "
                 + "\n--Numeric "
                 + "\n--Punctuation "
                 + "\n--Non Latin alphabets "
                 + "\n Password length should be following: "
                 + "\n--More than or equal to {3, number, integer} characters "
                 + "\n--Less than or equal to {4, number, integer} characters"),
                8,
                64,
                1,
                3,
                ".*\\p{Lower}.*",
                ".*\\p{Upper}.*",
                ".*\\p{Digit}.*",
                ".*\\p{Punct}.*",
                ".*[^\\p{Lower}\\p{Upper}\\p{Digit}\\p{Punct}].*"
            );
    }

    public StandardPasswordEntropyChecker(String formattableErrorMessage,
                                          int minPasswordLength,
                                          int maxPasswordLength,
                                          int minCharsForEachCategory,
                                          int numberOfCategoriesToRequire,
                                          String... categoryRegexPatterns)
    {
        this.minPasswordLength           = minPasswordLength;
        this.maxPasswordLength           = maxPasswordLength;
        this.minCharsForEachCategory     = minCharsForEachCategory;
        this.numberOfCategoriesTotal     = categoryRegexPatterns.length;
        this.numberOfCategoriesToRequire = Math.min(numberOfCategoriesToRequire, numberOfCategoriesTotal);
        this.formattableErrorMessage     = formattableErrorMessage;
        this.categoryRegexPatterns       = Arrays.stream(categoryRegexPatterns)
                                                 .map(Pattern::compile)
                                                 .collect(toList());
    }

    @Override
    public boolean hasSufficientEntropy(String password) {

        if (password == null) return false;
        if (password.length() < minPasswordLength) return false;
        if (password.length() > maxPasswordLength) return false;

        return
            categoryRegexPatterns
                .stream()
                .filter(pattern -> pattern.matcher(password).matches())
                .count()
            >= numberOfCategoriesToRequire;
    }


    @Override
    public String rulesAsText() {
        return
            new MessageFormat(formattableErrorMessage)
                .format
                    (
                        new Object[]{
                            minCharsForEachCategory,
                            numberOfCategoriesToRequire,
                            numberOfCategoriesTotal,
                            minPasswordLength,
                            maxPasswordLength
                        }
                    );
    }
}