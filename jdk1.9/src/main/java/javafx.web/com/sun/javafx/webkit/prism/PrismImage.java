/*
 * Copyright (c) 2011, 2016, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.javafx.webkit.prism;

import com.sun.javafx.tk.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import com.sun.javafx.webkit.UIClientImpl;
import com.sun.prism.Image;
import com.sun.prism.Graphics;
import com.sun.webkit.graphics.WCImage;

/**
 * @author Alexey.Ushakov
 */
abstract class PrismImage extends WCImage {
    abstract Image getImage();
    abstract Graphics getGraphics();
    abstract void draw(Graphics g,
            int dstx1, int dsty1, int dstx2, int dsty2,
            int srcx1, int srcy1, int srcx2, int srcy2);
    abstract void dispose();

    @Override
    public Object getPlatformImage() {
       return getImage();
    }

    @Override
    public void deref() {
        super.deref();
        if (!hasRefs()) {
            dispose();
        }
    }

    @Override
    protected final String toDataURL(String mimeType) {
        Object image = UIClientImpl.toBufferedImage(Toolkit.getImageAccessor().fromPlatformImage(getImage()));
        if (image instanceof BufferedImage) {
            Iterator<ImageWriter> it = ImageIO.getImageWritersByMIMEType(mimeType);
            while (it.hasNext()) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageWriter writer = it.next();
                try {
                    writer.setOutput(ImageIO.createImageOutputStream(output));
                    writer.write((BufferedImage) image);
                }
                catch (IOException exception) {
                    continue; // try next image writer
                }
                finally {
                    writer.dispose();
                }
                StringBuilder sb = new StringBuilder();
                sb.append("data:").append(mimeType).append(";base64,");
                sb.append(Base64.getMimeEncoder().encodeToString(output.toByteArray()));
                return sb.toString();
            }
        }
        return null;
    }
}
