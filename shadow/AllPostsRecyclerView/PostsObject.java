package com.yahya.shadow.AllPostsRecyclerView;

public class PostsObject {
    private String post_id,post_desc,posterId,poster_user,post_pic_id;

    public PostsObject(String post_id, String post_desc, String posterId, String poster_user, String post_pic_id) {
        this.post_id = post_id;
        this.post_desc = post_desc;
        this.posterId = posterId;
        this.poster_user = poster_user;
        this.post_pic_id = post_pic_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getPost_desc() {
        return post_desc;
    }

    public String getPosterId() {
        return posterId;
    }

    public String getPoster_user() {
        return poster_user;
    }

    public String getPost_pic_id() {
        return post_pic_id;
    }
}
