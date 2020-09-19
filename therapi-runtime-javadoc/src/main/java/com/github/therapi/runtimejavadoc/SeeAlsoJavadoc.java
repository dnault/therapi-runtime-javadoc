package com.github.therapi.runtimejavadoc;

import java.util.Objects;


/**
 * Represents a {@code @see} tag on a class or method.
 */
public class SeeAlsoJavadoc {
	
	public enum SeeAlsoType {
		STRING_LITERAL, HTML_LINK, JAVADOC_LINK
	}
	
	private final SeeAlsoType seeAlsoType;
	private final String stringLiteral;
	private final HtmlLink htmlLink;
	private final Link link;
	
	public SeeAlsoJavadoc(String stringLiteral) {
		this(SeeAlsoType.STRING_LITERAL, stringLiteral, null, null);
	}
	
	public SeeAlsoJavadoc(HtmlLink htmlLink) {
		this(SeeAlsoType.HTML_LINK, null, htmlLink, null);
	}
	
	public SeeAlsoJavadoc(Link link) {
		this(SeeAlsoType.JAVADOC_LINK, null, null, link);
	}
	
	private SeeAlsoJavadoc(SeeAlsoType seeAlsoType, String stringLiteral, HtmlLink htmlLink, Link link) {
		this.seeAlsoType = seeAlsoType;
		this.stringLiteral = stringLiteral;
		this.htmlLink = htmlLink;
		this.link = link;
	}
	
	public SeeAlsoType getSeeAlsoType() {
		return seeAlsoType;
	}
	
	public String getStringLiteral() {
		return stringLiteral;
	}
	
	public HtmlLink getHtmlLink() {
		return htmlLink;
	}
	
	public Link getLink() {
		return link;
	}

	public boolean equals( Object o ) {
		if ( this == o )
			return true;
		if ( o == null || getClass() != o.getClass() )
			return false;
		SeeAlsoJavadoc that = (SeeAlsoJavadoc) o;
		return seeAlsoType == that.seeAlsoType &&
			Objects.equals( stringLiteral, that.stringLiteral ) &&
			Objects.equals( htmlLink, that.htmlLink ) &&
			Objects.equals( link, that.link );
	}

	public int hashCode() {
		return Objects.hash( seeAlsoType, stringLiteral, htmlLink, link );
	}

	@Override
	public String toString() {
		return "SeeAlsoJavadoc{" +
			"seeAlsoType=" + seeAlsoType +
			", stringLiteral='" + stringLiteral + '\'' +
			", htmlLink=" + htmlLink +
			", link=" + link +
			'}';
	}

	public static class HtmlLink {
		private final String text;
		private final String link;
		
		public HtmlLink(String text, String link) {
			this.text = text;
			this.link = link;
		}
		
		public String getText() {
			return text;
		}
		
		public String getLink() {
			return link;
		}

		@Override
		public boolean equals( Object o ) {
			if ( this == o )
				return true;
			if ( o == null || getClass() != o.getClass() )
				return false;
			HtmlLink htmlLink = (HtmlLink) o;
			return Objects.equals( text, htmlLink.text ) &&
				Objects.equals( link, htmlLink.link );
		}

		@Override
		public int hashCode() {
			return Objects.hash( text, link );
		}
	}
}
