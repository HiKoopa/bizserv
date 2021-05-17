package com.uangel.svc.biz.impl.ctinetty;


public class GLIHeader {
    public static final byte GLI_VERSION = 0x02;
    public static final byte GLI_DEFAULT_APP = 0x00;

    private byte cNull;
    private byte cMsgType;
    private short htons_len;
    private byte cGLI_VERSION;
    private byte cGLI_DEFAULT_APP;

    public int getHtons_len() {
        return ((int)htons_len) & 0xFFFF;
    }

    public GLIHeader(byte cMsgType, short htons_len) {
        this.cNull = 0;
        this.cMsgType = cMsgType;
        this.htons_len = htons_len;
        this.cGLI_VERSION = GLI_VERSION;
        this.cGLI_DEFAULT_APP = GLI_DEFAULT_APP;
    }
}
