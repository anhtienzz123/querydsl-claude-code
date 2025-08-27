package hatien.querydsl.examples;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stockQuantity;
    private String description;
    
    public Product() {}
    
    public Product(Long id, String name, String category, BigDecimal price, Integer stockQuantity, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', price=%s, stockQuantity=%d}", 
                           id, name, category, price, stockQuantity);
    }
}