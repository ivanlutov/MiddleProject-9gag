package models;

import java.time.LocalDateTime;

import exceptions.CommentException;

public class Comment {
	private static final String MSG_INVALID_USER = "Invalid parameter of user";
	private static final String MSG_INVALID_POST = "Invalid object post";
	private static final String MSG_INVALID_CONTENT = "Invalid parameters for content";
	private static final String MSG_INVALID_DATE_TIME = "Invalid parameter of date time";
	private static final String MSG_INVALID_ID = "Id must be positive!";
	private int id;
	private String content;
	private LocalDateTime dateTime;
	private Post post;
	private User user;

	public Comment() {

	}

	public Comment(String content, User user, Post post, LocalDateTime dateTime) throws CommentException {
		this.setPost(post);
		this.setUser(user);
		this.setDateTime(dateTime);
		this.setContent(content);
	}

	public Comment(int id, String content, User user, Post post, LocalDateTime dateTime) throws CommentException {
		this(content, user, post, dateTime);
		this.setId(id);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) throws CommentException {
		if (content != null) {
			this.content = content;
		} else {
			throw new CommentException(MSG_INVALID_CONTENT);
		}
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) throws CommentException {
		if (post != null) {
			this.post = post;	
		}else {
			throw new CommentException(MSG_INVALID_POST);
		}	
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) throws CommentException {
		if (user != null) {
			this.user = user;	
		}else {
			throw new CommentException(MSG_INVALID_USER);
		}
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) throws CommentException {
		if (dateTime != null) {
			this.dateTime = dateTime;	
		}else {
			throw new CommentException(MSG_INVALID_DATE_TIME);
		}
		
	}

	public void setNewContent(String content) throws CommentException {
		if (content != null) {
			this.content = content;	
		}else {
			throw new CommentException(MSG_INVALID_CONTENT);
		}
	}

	public int getPostId() {
		return this.post.getId();
	}

	public int getId() {
		return id;
		
	}

	public void setId(int id) throws CommentException {
		if (id > 0) {
			this.id = id;	
		}else {
			throw new CommentException(MSG_INVALID_ID);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("  -Comment content: ").append(this.content).append(System.lineSeparator());
		sb.append("   Author: ").append(this.user.getUsername()).append(System.lineSeparator());
		sb.append("   Written on: ").append(this.dateTime).append(System.lineSeparator())
				.append("===============================").append(System.lineSeparator());

		return sb.toString();
	}

}
