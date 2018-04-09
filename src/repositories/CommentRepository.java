package repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import connection.DatabaseConnection;
import exceptions.CommentException;
import exceptions.PostException;
import models.Comment;
import models.Post;
import models.User;
import utils.Session;

public class CommentRepository {
	public static final Map<Integer, Comment> comments = new HashMap<>();

	private static final String MSG_INO_SUCH_POST = "This post does not exist!";
	private static final String NOT_EXIST_COMMENT_MESSAGE = "This comment does not exist!";
	private static final String NOT_HAVE_AUTHORIZATION_MESSAGE = "Not have authorization for delete this comment!";
	private static final String COMMENT_PATH = "comments.json";

	private static final String INSERT_COMMENT_QUERY = "INSERTE INTO comments (content,date_time,author_id,post_id) VALUES (?,?,?,?)";

	private static CommentRepository commentRepository;

	private CommentRepository() {

	}

	public static CommentRepository getInstance() {
		if (commentRepository == null) {
			commentRepository = new CommentRepository();
		}

		return commentRepository;
	}

	public Comment addComent(String content, int postId) throws CommentException {
		Post post = PostRepository.getInstance().getPostById(postId);
		// If there is such a post
		if (post == null) {
			throw new CommentException(MSG_INO_SUCH_POST);
		}
		
		User user = Session.getInstance().getUser();
		
		LocalDateTime curDateTime = LocalDateTime.now();
		Timestamp curTimestamp = Timestamp.valueOf(curDateTime);
		int authorId = user.getId();
		int commentId;

		try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(INSERT_COMMENT_QUERY,
				PreparedStatement.RETURN_GENERATED_KEYS);) {
			ps.setString(1, content);
			ps.setTimestamp(2, curTimestamp);
			ps.setInt(3, authorId);
			ps.setInt(4, postId);
			ps.executeUpdate();

			ResultSet result = ps.getGeneratedKeys();
			result.next();
			commentId = result.getInt("id");
		}

		Comment comment = new Comment(commentId, content, user, post, curDateTime);

		post.addComment(comment);
		user.addComment(comment);
		this.comments.put(commentId, comment);
		return comment;
	}

	public void editCommentOfCurrentPost(int postId, int commentId, String content)
			throws CommentException, PostException {

		if (!isValidPost(postId)) {// ����������� ID �� �� ����� � Optional � ������, ����� � !
			throw new PostException(MSG_INO_SUCH_POST);
		}

		isValidComment(commentId);

		isAuthorizated(commentId);

		// this.comments
		// .entrySet()
		// .stream()
		// .filter(k -> k.getKey() == commentId)
		// .filter(v -> v.getValue().getPostId() == postId)
		// .forEach(v -> v.setContent(content));
		comments.values().stream().filter(v -> v.getPostId() == postId).forEach(v -> v.setNewContent(content));
	}

	private boolean isValidPost(int postId) {
		return this.comments.values().stream().filter(v -> v.getPostId() == postId).findFirst().isPresent();
	}

	public void delete(int commentId) throws CommentException {

		isValidComment(commentId);

		isAuthorizated(commentId);

		comments.remove(commentId);
	}

	// public void serialize() throws IOException {
	// File file = new File(COMMENT_PATH);
	// Gson gson = new GsonBuilder().setPrettyPrinting().create();
	// String jsonComments = gson.toJson(this.comments);
	//
	// try (PrintStream ps = new PrintStream(file)) {
	// file.createNewFile();
	// ps.println(jsonComments);
	// }
	// }
	// public void exportComment() throws SerializeException, SerialException {
	// this.serializer.serialize(comments, COMMENT_PATH);
	// }
	//
	// public void deserialize() throws FileNotFoundException {
	// File file = new File(COMMENT_PATH);
	// Gson gson = new GsonBuilder().create();
	// StringBuilder sb = new StringBuilder();
	//
	// try (Scanner sc = new Scanner(file)) {
	// while (sc.hasNextLine()) {
	// String line = sc.nextLine();
	// sb.append(line);
	// }
	// }
	// Map<Integer, Comment> map = gson.fromJson(sb.toString(), new
	// TypeToken<Map<Integer, Comment>>() {
	// }.getType());
	//
	// comments = map;
	// }

	// public void importComment() {
	// this.serializer.deserialize(this.comments, COMMENT_PATH);
	// }
	private void isValidComment(int arg) throws CommentException {
		if (!comments.containsKey(arg)) {
			throw new CommentException(NOT_EXIST_COMMENT_MESSAGE);
		}
	}

	private void isAuthorizated(int arg) throws CommentException {
		if (Session.getInstance().getUser().getId() != comments.get(arg).getUser().getId()) {
			throw new CommentException(NOT_HAVE_AUTHORIZATION_MESSAGE);
		}
	}

	// public List<Comment> getCommentsByPostId(int postId) {
	// this.comments.values()
	// }

	public void deleteAllCommentsCurrentPostById(int postId) {
		comments.values().removeIf(v -> v.getPostId() == postId);
	}

	public int getLastId() {
		if (comments == null || comments.size() == 0) {
			return 0;
		}

		return comments.values().stream().sorted((c1, c2) -> Integer.compare(c2.getId(), c1.getId())).findFirst().get()
				.getId();
	}

	public Comment getCommentById(Integer commentId) {
		if (!comments.containsKey(commentId)) {
			return null;
		}

		return comments.get(commentId);
	}

	public void removeCommentById(int id) {
		this.comments.remove(id);
	}
}
