package com.nakhmadov.diplomapp.Model;

public class Room {
    private String serverName;
    private String urlAddress;
    private String roomId;

    public Room(String serverName, String urlAddress, String roomId) {
        this.serverName = serverName;
        this.urlAddress = urlAddress;
        this.roomId = roomId;
    }

    public Room() {
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
