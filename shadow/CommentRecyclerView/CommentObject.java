package com.yahya.shadow.CommentRecyclerView;

public class CommentObject {
    private String commenter_id,comment_text,userId,userProfile;

    public CommentObject(String commenter_id, String comment_text, String userId, String userProfile) {
        this.commenter_id = commenter_id;
        this.comment_text = comment_text;
        this.userId = userId;
        this.userProfile = userProfile;
    }

    public String getCommenter_id() {
        return commenter_id;
    }

    public String getComment_text() {
        return comment_text;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserProfile() {
        return userProfile;
    }
}
