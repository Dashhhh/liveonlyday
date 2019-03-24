package com.ejdash.esbn.WebRTC.WebRTCPeerManager.kurento.models.response;

import com.ejdash.esbn.WebRTC.WebRTCPeerManager.kurento.models.CandidateModel;
import com.ejdash.esbn.WebRTC.WebRTCPeerManager.kurento.models.IdModel;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nhancao on 6/19/17.
 */

public class ServerResponse extends IdModel implements Serializable {
    @SerializedName("response")
    private String response;
    @SerializedName("sdpAnswer")
    private String sdpAnswer;
    @SerializedName("candidate")
    private CandidateModel candidate;
    @SerializedName("message")
    private String message;
    @SerializedName("success")
    private boolean success;
    @SerializedName("from")
    private String from;

    // 추가
    @SerializedName("sessionNumber")
    private String sessionNumber;

    @SerializedName("viewerId")
    private String viewerId;

    @SerializedName("sessionFileName")
    private String sessionFileName;

    @SerializedName("broadcastStartTime")
    private String broadcastStartTime;

    @SerializedName("sessionUserName")
    private String sessionUserName;

    @SerializedName("watcherCount")
    private String watcherCount;

    @SerializedName("donationPoint")
    private String donationPoint;

    @SerializedName("donationMessage")
    private String donationMessage;

    @SerializedName("presenterId")
    private String presenterId;

    @SerializedName("presenterSessionNumber")
    private String presenterSessionNumber;

    @SerializedName("viewerSessionNumber")
    private String viewerSessionNumber;


    public ServerResponse() {
    }

    public String getPresenterId() {
        return presenterId;
    }

    public String getPresenterSessionNumber() {
        return presenterSessionNumber;
    }

    public String getViewerSessionNumber() {
        return viewerSessionNumber;
    }

    public String getDonationPoint() {
        return donationPoint;
    }

    public String getDonationMessage() {
        return donationMessage;
    }

    public String getSessionFileName() {
        return sessionFileName;
    }

    public String getBroadcastStartTime() {
        return broadcastStartTime;
    }

    public String getSessionUserName() {
        return sessionUserName;
    }

    public IdResponse getIdRes() {
        return IdResponse.getIdRes(getId());
    }

    public TypeResponse getTypeRes() {
        return TypeResponse.getType(getResponse());
    }

    public String getResponse() {
        return response;
    }

    // 추가
    public String getSessionNumber() {
        return sessionNumber;
    }

    public String getSdpAnswer() {
        return sdpAnswer;
    }

    public CandidateModel getCandidate() {
        return candidate;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFrom() {
        return from;
    }

    public String getWatcherCount() {
        return watcherCount;
    }


    public String getViewerId() {
        return viewerId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
