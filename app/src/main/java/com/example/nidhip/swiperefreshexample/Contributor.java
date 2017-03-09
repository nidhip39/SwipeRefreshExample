package com.example.nidhip.swiperefreshexample;

/**
 * Created by Nidhip on 08-03-2017.
 */

public class Contributor {


    String name;
    String avatar_url;
    String repos_url;
    String contributions;

    public Contributor() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public void setRepos_url(String repos_url) {
        this.repos_url = repos_url;
    }

    public String getContributions() {
        return contributions;
    }

    public void setContributions(String contributions) {
        this.contributions = contributions;
    }
}
