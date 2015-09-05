package com.github.dnault.therapi.runtimejavadoc;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;

public class Example {
    /**
     * Returns an Image object that can then be painted on the screen.
     * The url argument must specify an absolute {@link URL}. The name
     * argument is a specifier that is relative to the url argument.
     * <p>
     * This method always returns immediately, whether or not the
     * image exists. When this applet attempts to draw the image on
     * the screen, the data will be loaded. The graphics primitives
     * that draw the image will incrementally paint on the screen.
     * {@param foo inline param? really?} end of comment.
     *
     * @param  url  an absolute URL giving the base location of the image
     * @param  name the location of the {@link Image#getProperty(String, ImageObserver)}, relative to the url argument
     * @return      the image at the specified URL {@blurgle this is a blurgle tag}
     * @see         Image
     * @see http://foo.com
     * @throws IllegalStateException just because, you know?
     * @custom.tag This is my custom tag with a {@see Image} link
     */
    public Image getImage(URL url, String name) {
        try {
            return getImage(new URL(url, name));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private Image getImage(URL url) {
        throw new UnsupportedOperationException();

    }
}
