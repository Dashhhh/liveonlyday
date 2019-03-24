package com.ejdash.esbn.WebRTC.WebRTCPeerManager.kurento.models.response;

/**
 * Created by nhancao on 6/19/17.
 */

public enum IdResponse {

    REGISTER_RESPONSE("registerResponse"),
    PRESENTER_RESPONSE("presenterResponse"),
    ICE_CANDIDATE("iceCandidate"),
    VIEWER_RESPONSE("viewerResponse"),
    STOP_COMMUNICATION("stopCommunication"),
    CLOSE_ROOM_RESPONSE("closeRoomResponse"),
    INCOMING_CALL("incomingCall"),
    START_COMMUNICATION("startCommunication"),
    CALL_RESPONSE("callResponse"),

    // 추가 - 현재 시청자 수 받음 > Server setInterval()을 통해서 넘어옴
    WATCHER_COUNT("watcherCount"),

    RECEIVE_POINT("receivePoint"),  // 시청자로부터 포인트를 받았을 때 (별풍선 받았을 때)

    // 추가 - 방 목록 요청
    LIST("list"),

    UN_KNOWN("unknown");

    private String id;

    IdResponse(String id) {
        this.id = id;
    }

    public static IdResponse getIdRes(String idRes) {
        for (IdResponse idResponse : IdResponse.values()) {
            if (idRes.equals(idResponse.getId())) {
                return idResponse;
            }
        }
        return UN_KNOWN;
    }

    public String getId() {
        return id;
    }
}
