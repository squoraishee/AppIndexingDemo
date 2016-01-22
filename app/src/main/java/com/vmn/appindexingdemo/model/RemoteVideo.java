package com.vmn.appindexingdemo.model;

public class RemoteVideo {

    String url;
    String posterUrl;
    String title;
    String description;
    String nodeKey;
    String videoLink;
    String deepLink;

    public RemoteVideo(String nodeKey, String url, String posterUrl, String title, String description, String videoLink, String deepLink) {
        this.nodeKey = nodeKey;
        this.url = url;
        this.posterUrl = posterUrl;
        this.title = title;
        this.description = description;
        this.videoLink = videoLink;
        this.deepLink = deepLink;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public String getUrl() {
        return url;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public String getDescription() {
        return description;
    }

}
