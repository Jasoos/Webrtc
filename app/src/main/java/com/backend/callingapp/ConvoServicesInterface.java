package com.backend.callingapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ConvoServicesInterface {

    @GET("http-pre-bind")
    Call<PreBindResult> getPreBindForRoom(@Query("room") String room);

    @GET("conferenceMapper")
    Call<MapperResponse> getConferenceMapper(@Query("conference") String conference);



    class PreBindResult {
        @Expose
        @SerializedName("rid")
        String rid;
        @Expose
        @SerializedName("sid")
        String sid;
        @Expose
        @SerializedName("jid")
        String jid;

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public String getJid() {
            return jid;
        }

        public void setJid(String jid) {
            this.jid = jid;
        }
    }

    class MapperResponse {
        @Expose
        @SerializedName("message")
        String message = null;

        @Expose
        @SerializedName("id")
        String id = null;

        @Expose
        @SerializedName("conference")
        String conference = null;
    }
}
