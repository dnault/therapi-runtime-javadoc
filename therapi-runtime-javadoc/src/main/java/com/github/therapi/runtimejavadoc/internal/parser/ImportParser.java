package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.Import;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImportParser {
    private static final Pattern IMPORT_REGEXP = Pattern.compile( "import (?<static>static )?(?<ref>[^;]+);" );

    public static Import parseImport( String importLine ) {
        Matcher matcher = IMPORT_REGEXP.matcher( importLine );
        if ( !matcher.matches() ) {
            return null;
        }

        boolean   isStatic  = matcher.group("static") != null;
        RefParser refParser = new RefParser(matcher.group("ref"));

        if ( isStatic ) {
            String member     = refParser.pop();
            String className  = refParser.pop();
            String packageRef = refParser.getRemaining();

            return new Import( packageRef, className, member );
        } else {
            String className  = refParser.pop();
            String packageRef = refParser.getRemaining();

            return new Import( packageRef, className );
        }
    }

    private static class RefParser {
        private String remaining;

        public RefParser( String ref ) {
            this.remaining = ref;
        }

        public String pop() {
            int i = remaining.lastIndexOf( '.' );
            if ( i < 0 || i == remaining.length() - 1 ) {
                return null;
            }

            String next = remaining.substring( i+1 );
            remaining = remaining.substring( 0, i );

            return next;
        }

        public String getRemaining() {
            return remaining;
        }
    }
}
