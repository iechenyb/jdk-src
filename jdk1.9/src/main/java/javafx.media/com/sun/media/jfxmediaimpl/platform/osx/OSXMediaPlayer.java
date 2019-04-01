/*
 * Copyright (c) 2010, 2016, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.media.jfxmediaimpl.platform.osx;

import com.sun.media.jfxmedia.MediaException;
import com.sun.media.jfxmedia.effects.AudioEqualizer;
import com.sun.media.jfxmedia.effects.AudioSpectrum;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.control.MediaPlayerOverlay;
import com.sun.media.jfxmediaimpl.NativeMedia;
import com.sun.media.jfxmediaimpl.NativeMediaPlayer;

/**
 * Mac OS X MediaPlayer implementation.
 */
final class OSXMediaPlayer extends NativeMediaPlayer {
    private final AudioEqualizer audioEq;
    private final AudioSpectrum audioSpectrum;
    private final Locator mediaLocator;

    OSXMediaPlayer(NativeMedia sourceMedia) {
        super(sourceMedia);
        init();
        mediaLocator = sourceMedia.getLocator();
        // This will throw an exception if we can't create the player
        osxCreatePlayer(mediaLocator.getStringLocation());
        audioEq = createNativeAudioEqualizer(osxGetAudioEqualizerRef());
        audioSpectrum = createNativeAudioSpectrum(osxGetAudioSpectrumRef());
    }

    OSXMediaPlayer(Locator source) {
        this(new OSXMedia(source));
    }

    @Override
    public AudioEqualizer getEqualizer() {
        return audioEq;
    }

    @Override
    public AudioSpectrum getAudioSpectrum() {
        return audioSpectrum;
    }

    @Override
    public MediaPlayerOverlay getMediaPlayerOverlay() {
        return null; // Not needed
    }

    @Override
    protected long playerGetAudioSyncDelay() throws MediaException {
        return osxGetAudioSyncDelay();
    }

    @Override
    protected void playerSetAudioSyncDelay(long delay) throws MediaException {
        osxSetAudioSyncDelay(delay);
    }

    @Override
    protected void playerPlay() throws MediaException {
        osxPlay();
    }

    @Override
    protected void playerStop() throws MediaException {
        osxStop();
    }

    @Override
    protected void playerPause() throws MediaException {
        osxPause();
    }

    @Override
    protected void playerFinish() throws MediaException {
        osxFinish();
    }

    @Override
    protected float playerGetRate() throws MediaException {
        return osxGetRate();
    }

    @Override
    protected void playerSetRate(float rate) throws MediaException {
        osxSetRate(rate);
    }

    @Override
    protected double playerGetPresentationTime() throws MediaException {
        return osxGetPresentationTime();
    }

    @Override
    protected boolean playerGetMute() throws MediaException {
        return osxGetMute();
    }

    @Override
    protected void playerSetMute(boolean state) throws MediaException {
        osxSetMute(state);
    }

    @Override
    protected float playerGetVolume() throws MediaException {
        return osxGetVolume();
    }

    @Override
    protected void playerSetVolume(float volume) throws MediaException {
        osxSetVolume(volume);
    }

    @Override
    protected float playerGetBalance() throws MediaException {
        return osxGetBalance();
    }

    @Override
    protected void playerSetBalance(float balance) throws MediaException {
        osxSetBalance(balance);
    }

    @Override
    protected double playerGetDuration() throws MediaException {
        double duration = osxGetDuration();
        if (duration == -1.0) {
            return Double.POSITIVE_INFINITY;
        }

        return duration;
    }

    @Override
    protected void playerSeek(double streamTime) throws MediaException {
        osxSeek(streamTime);
    }

    @Override
    protected void playerDispose() {
        osxDispose();
    }

    @Override
    public void playerInit() throws MediaException {
    }

    private native void osxCreatePlayer(String sourceURI) throws MediaException;
    // Have to use native references for these two things
    private native long osxGetAudioEqualizerRef();
    private native long osxGetAudioSpectrumRef();
    private native long osxGetAudioSyncDelay() throws MediaException;
    private native void osxSetAudioSyncDelay(long delay) throws MediaException;
    private native void osxPlay() throws MediaException;
    private native void osxStop() throws MediaException;
    private native void osxPause() throws MediaException;
    private native void osxFinish() throws MediaException;
    private native float osxGetRate() throws MediaException;
    private native void osxSetRate(float rate) throws MediaException;
    private native double osxGetPresentationTime() throws MediaException;
    private native boolean osxGetMute() throws MediaException;
    private native void osxSetMute(boolean state) throws MediaException;
    private native float osxGetVolume() throws MediaException;
    private native void osxSetVolume(float volume) throws MediaException;
    private native float osxGetBalance() throws MediaException;
    private native void osxSetBalance(float balance) throws MediaException;
    private native double osxGetDuration() throws MediaException;
    private native void osxSeek(double streamTime) throws MediaException;
    private native void osxDispose();
}
