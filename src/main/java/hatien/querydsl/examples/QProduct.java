package hatien.querydsl.examples;

import hatien.querydsl.core.metadata.EntityMetadata;
import hatien.querydsl.core.path.NumberPath;
import hatien.querydsl.core.path.StringPath;
import java.math.BigDecimal;

public class QProduct extends EntityMetadata<Product> {
    public static final QProduct product = new QProduct("product");
    
    public final NumberPath<Long> id;
    public final StringPath name;
    public final StringPath category;
    public final NumberPath<BigDecimal> price;
    public final NumberPath<Integer> stockQuantity;
    public final StringPath description;
    
    /**
     * Constructs a new QProduct with the specified alias.
     * Initializes all path expressions for the Product entity's properties.
     *
     * @param alias the alias to use for this entity in queries
     */
    public QProduct(String alias) {
        super(Product.class, alias);
        this.id = createNumber("id", Long.class);
        this.name = createString("name");
        this.category = createString("category");
        this.price = createNumber("price", BigDecimal.class);
        this.stockQuantity = createNumber("stockQuantity", Integer.class);
        this.description = createString("description");
    }
}