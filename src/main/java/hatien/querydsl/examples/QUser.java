package hatien.querydsl.examples;

import hatien.querydsl.core.metadata.EntityMetadata;
import hatien.querydsl.core.path.NumberPath;
import hatien.querydsl.core.path.StringPath;

public class QUser extends EntityMetadata<User> {
	public static final QUser user = new QUser("user");

	public final NumberPath<Long> id;
	public final StringPath firstName;
	public final StringPath lastName;
	public final StringPath email;
	public final NumberPath<Integer> age;
	public final StringPath city;

	/**
	 * Constructs a new QUser with the specified alias. Initializes all path
	 * expressions for the User entity's properties.
	 *
	 * @param alias the alias to use for this entity in queries
	 */
	public QUser(String alias) {
		super(User.class, alias);
		this.id = createNumber("id", Long.class);
		this.firstName = createString("firstName");
		this.lastName = createString("lastName");
		this.email = createString("email");
		this.age = createNumber("age", Integer.class);
		this.city = createString("city");
	}
}