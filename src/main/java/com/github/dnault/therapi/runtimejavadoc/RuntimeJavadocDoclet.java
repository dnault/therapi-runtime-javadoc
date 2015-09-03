package com.github.dnault.therapi.runtimejavadoc;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

import java.io.File;
import java.io.IOException;

public class RuntimeJavadocDoclet {
    private static File outputDir;

    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    public static int optionLength(String option) {
        switch (option) {
            case "-d":
            case "-doctitle": // don't care, but gradle passes it
            case "-windowtitle": // don't care, but gradle passes it
                return 2;
            default:
                return 0;
        }
    }

    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        for (String[] option : options) {
            if ("-d".equals(option[0])) {
                outputDir = new File(option[1]);
            }
        }

        if (outputDir == null) {
            reporter.printError("mission option '-d' (output directory)");
            return false;
        }

        if (outputDir.exists() && !outputDir.isDirectory()) {
            reporter.printError("output directory is a file: " + outputDir);
        }

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            reporter.printError("failed to create output directory: " + outputDir);
        }

        return true;
    }

    public static boolean start(RootDoc root) throws IOException {
        return new RuntimeJavadocWriter(outputDir).start(root);
    }
}
