package com.ridgid.oss.common.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class ResourceHelper
{
    private ResourceHelper() {}

    public static String loadResourceToString(String resourceName, Class classForResourcePath) throws IOException {
        try
            (
                BufferedReader reader
                    = new BufferedReader
                    (
                        new InputStreamReader
                            (
                                classForResourcePath.getResourceAsStream(resourceName)
                            )
                    )
            ) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
