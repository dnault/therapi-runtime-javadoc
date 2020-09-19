package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.ClassResolver;
import com.github.therapi.runtimejavadoc.InlineLink;
import com.github.therapi.runtimejavadoc.Link;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.regex.Pattern.compile;

public class LinkParser {
	
	// https://regex101.com/r/3DoNfK/2
	private static final Pattern linkPattern = compile("^(?<classname>[\\w.]+)?(?:#(?<member>\\w+))?(?:\\((?<params>.*)\\))?(?:\\s(?<label>.+))?$");
	
	public static Link createLinkElement(String owningClass, ClassResolver classResolver, String value) {
		Matcher linkMatcher = linkPattern.matcher(value);
		if (!linkMatcher.matches()) {
			return null;
		}

		String classRef = linkMatcher.group("classname");
		String memberRef = linkMatcher.group("member");
		String params = linkMatcher.group("params");
		String label = linkMatcher.group("label");

		String effectiveClassName = classRef == null ? owningClass : classResolver.resolveRef(classRef);
		String effectiveLabel = label != null ? label : linkMatcher.group(0);

		return new Link(effectiveLabel, effectiveClassName, memberRef, formatMember(params));
	}
	
	private static List<String> formatMember(String params) {
		if (params != null && !params.trim().isEmpty()) {
			return asList(params.trim().split(",\\s*"));
		}
		return Collections.emptyList();
	}
}
