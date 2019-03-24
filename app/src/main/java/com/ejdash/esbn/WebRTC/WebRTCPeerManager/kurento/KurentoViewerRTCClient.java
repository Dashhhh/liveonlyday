package com.ejdash.esbn.WebRTC.WebRTCPeerManager.kurento;

import android.util.Log;

import com.nhancv.webrtcpeer.rtc_comm.ws.BaseSocketCallback;
import com.nhancv.webrtcpeer.rtc_comm.ws.SocketService;
import com.nhancv.webrtcpeer.rtc_peer.RTCClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * Created by nhancao on 7/18/17.
 */

public class KurentoViewerRTCClient implements RTCClient {
    private static final String TAG = KurentoViewerRTCClient.class.getSimpleName();

    private SocketService socketService;
    private int presenterID;

    public KurentoViewerRTCClient(SocketService socketService, int presenterID) {
        this.socketService = socketService;
        this.presenterID = presenterID;
    }

    public void connectToRoom(String host, BaseSocketCallback socketCallback) {
        socketService.connect(host, socketCallback);
    }

    @Override
    public void sendOfferSdp(SessionDescription sdp) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "viewer");
            obj.put("presenterID", presenterID);
            obj.put("sdpOffer", sdp.description);

            socketService.sendMessage(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAnswerSdp(SessionDescription sdp) {
        Log.e(TAG, "sendAnswerSdp: ");
    }

    @Override
    public void sendLocalIceCandidate(IceCandidate iceCandidate) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "onIceCandidate");
            JSONObject candidate = new JSONObject();
            candidate.put("candidate", iceCandidate.sdp);
            candidate.put("sdpMid", iceCandidate.sdpMid);
            candidate.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
            obj.put("candidate", candidate);

            socketService.sendMessage(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLocalIceCandidateRemovals(IceCandidate[] candidates) {
        Log.e(TAG, "sendLocalIceCandidateRemovals: ");
    }

    public void sendPoint(String presenterId,
                          String presenterSessionNumber,
                          String viewerId,
                          String viewerSessionNumber,
                          String donationPoint,
                          String donationMessage
    ) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "sendPoint");
            obj.put("presenterId", presenterId);
            obj.put("presenterSessionNumber", presenterSessionNumber);
            obj.put("viewerId", viewerId);
            obj.put("viewerSessionNumber", viewerSessionNumber);
            obj.put("donationPoint", donationPoint);
            obj.put("donationMessage", donationMessage);

            socketService.sendMessage(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void close(String presenterSessionNumber, String viewerSessionNumber) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "stop");
            obj.put("messageTarget", "viewer");
            obj.put("viewerSessionNumber", viewerSessionNumber);
            obj.put("presenterSessionNumber", presenterSessionNumber);

            Log.i("viewerClose", "viewerClose ! > viewerSessionNumber is....  "+ viewerSessionNumber);
            Log.i("viewerClose", "viewerClose ! > presenterSessionNumber is....  "+ presenterSessionNumber);

            socketService.sendMessage(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
