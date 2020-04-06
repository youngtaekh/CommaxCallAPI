package io.dotconnect.p2p.utils;

public class Configuration {
    public static final String stunServerAddress = "commax.dotconnect-api.io";
    public static final String stunServerPort = "8088";
    public static final String turnServerAddress = "commax.dotconnect-api.io";
    public static final String turnServerPort = "8088";
    public static final String turnUserId = "vloco";
    public static final String turnUserPassword = "locomotion";
    public static final boolean dtlsEncryption = true;

    //PeerConnectionParameters
    public static final boolean TRACING = false;
    public static final int VIDEO_WIDTH = 0;
    public static final int VIDEO_HEIGHT = 0;
    public static final int VIDEO_FPS = 0;
    public static final int VIDEO_MAX_BITRATE = 0;
    private static final String VIDEO_CODEC_H264_BASELINE = "H264 Baseline";
    private static final String VIDEO_CODEC_H264_HIGH = "H264 High";
//    public static final String VIDEO_CODEC_VP8 = "VP8";
    public static final String VIDEO_CODEC = VIDEO_CODEC_H264_BASELINE;
    public static final boolean VIDEO_FLEXFEC_ENABLED = false;
    public static final boolean VIDEO_CODEC_HW_ACCELERATION = true;
    public static final int AUDIO_START_BITRATE = 0;
    public static final String AUDIO_CODEC = "OPUS";
    public static final boolean NO_AUDIO_PROCESSING = false;
    public static final boolean AEC_DUMP = false;
    public static final boolean USE_OPEN_SLES = false;
    public static final boolean DISABLE_BUILT_IN_AEC = false;
    public static final boolean DISABLE_BUILT_IN_AGC = false;
    public static final boolean DISABLE_BUILT_IN_NS = false;
    public static final boolean DISABLE_WEBRTC_AGC_AND_HPF = false;
    public static final boolean ENABLE_RTC_EVENT_LOG = false;
}
