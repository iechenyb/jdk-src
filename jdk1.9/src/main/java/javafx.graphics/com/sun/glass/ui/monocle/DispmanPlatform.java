/*
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.glass.ui.monocle;

class DispmanPlatform extends LinuxPlatform {

    @Override
    protected NativeCursor createCursor() {
        return new DispmanCursor();
    }

    @Override
    protected NativeScreen createScreen() {
        return new DispmanScreen();
    }

    @Override public synchronized AcceleratedScreen getAcceleratedScreen(int[] attributes)
            throws GLException{
        if (accScreen == null) {
            accScreen = new DispmanAcceleratedScreen(attributes);
        }
        return accScreen;
    }

}
