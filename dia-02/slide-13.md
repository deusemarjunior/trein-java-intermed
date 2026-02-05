# Slide 13: Exercício Prático - Blog API (Parte 2)

**Horário:** 15:45 - 16:15

---

## 🎬 Passo 3: DTOs (15 min)

### Request DTOs

```java
// CreatePostRequest.java
public record CreatePostRequest(
    @NotBlank(message = "Título é obrigatório")
    @Size(min = 5, max = 200, message = "Título deve ter entre 5 e 200 caracteres")
    String title,
    
    @NotBlank(message = "Conteúdo é obrigatório")
    @Size(min = 20, message = "Conteúdo deve ter no mínimo 20 caracteres")
    String content,
    
    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 100)
    String author,
    
    @NotNull(message = "Categoria é obrigatória")
    Long categoryId,
    
    Set<String> tags  // Opcional
) {}

// UpdatePostRequest.java
public record UpdatePostRequest(
    @NotBlank @Size(min = 5, max = 200) String title,
    @NotBlank @Size(min = 20) String content,
    Set<String> tags
) {}

// CreateCommentRequest.java
public record CreateCommentRequest(
    @NotBlank(message = "Comentário não pode estar vazio")
    @Size(min = 5, max = 1000)
    String text,
    
    @NotBlank @Size(max = 100) String author,
    
    @Email(message = "Email inválido")
    String email
) {}

// CreateCategoryRequest.java
public record CreateCategoryRequest(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 500) String description
) {}

// CreateTagRequest.java
public record CreateTagRequest(
    @NotBlank @Size(max = 50) String name
) {}
```

### Response DTOs

```java
// PostResponse.java
public record PostResponse(
    Long id,
    String title,
    String content,
    String author,
    CategoryResponse category,
    Set<String> tags,
    int commentsCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getAuthor(),
            CategoryResponse.from(post.getCategory()),
            post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()),
            post.getComments().size(),
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }
}

// PostSummaryResponse.java (para listagens)
public record PostSummaryResponse(
    Long id,
    String title,
    String author,
    String categoryName,
    int commentsCount,
    LocalDateTime createdAt
) {
    public static PostSummaryResponse from(Post post) {
        return new PostSummaryResponse(
            post.getId(),
            post.getTitle(),
            post.getAuthor(),
            post.getCategory().getName(),
            post.getComments().size(),
            post.getCreatedAt()
        );
    }
}

// CommentResponse.java
public record CommentResponse(
    Long id,
    String text,
    String author,
    String email,
    LocalDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getText(),
            comment.getAuthor(),
            comment.getEmail(),
            comment.getCreatedAt()
        );
    }
}

// CategoryResponse.java
public record CategoryResponse(
    Long id,
    String name,
    String description
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription()
        );
    }
}

// TagResponse.java
public record TagResponse(
    Long id,
    String name,
    int postsCount
) {
    public static TagResponse from(Tag tag) {
        return new TagResponse(
            tag.getId(),
            tag.getName(),
            tag.getPosts().size()
        );
    }
}
```

---

## 🎬 Passo 4: Services (15 min)

### PostService.java

```java
@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    public PostResponse create(CreatePostRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        
        Post post = new Post();
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setAuthor(request.author());
        post.setCategory(category);
        
        // Processar tags
        if (request.tags() != null && !request.tags().isEmpty()) {
            Set<Tag> tags = getOrCreateTags(request.tags());
            post.setTags(tags);
        }
        
        Post saved = postRepository.save(post);
        return PostResponse.from(saved);
    }
    
    public PostResponse findById(Long id) {
        Post post = postRepository.findWithTagsById(id)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return PostResponse.from(post);
    }
    
    public Page<PostSummaryResponse> findAll(Pageable pageable) {
        return postRepository.findAll(pageable)
            .map(PostSummaryResponse::from);
    }
    
    public Page<PostSummaryResponse> search(String keyword, String categoryName, Pageable pageable) {
        if (categoryName != null) {
            Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            
            if (keyword != null) {
                return postRepository.searchByCategoryAndKeyword(category, keyword, pageable)
                    .map(PostSummaryResponse::from);
            } else {
                return postRepository.findByCategory(category, pageable)
                    .map(PostSummaryResponse::from);
            }
        } else if (keyword != null) {
            return postRepository.searchByKeyword(keyword, pageable)
                .map(PostSummaryResponse::from);
        } else {
            return findAll(pageable);
        }
    }
    
    public PostResponse update(Long id, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        
        post.setTitle(request.title());
        post.setContent(request.content());
        
        if (request.tags() != null) {
            Set<Tag> tags = getOrCreateTags(request.tags());
            post.setTags(tags);
        }
        
        Post updated = postRepository.save(post);
        return PostResponse.from(updated);
    }
    
    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Post not found");
        }
        postRepository.deleteById(id);
    }
    
    private Set<Tag> getOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    return tagRepository.save(newTag);
                });
            tags.add(tag);
        }
        return tags;
    }
}
```

### CommentService.java

```java
@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    public CommentResponse create(Long postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        
        Comment comment = new Comment();
        comment.setText(request.text());
        comment.setAuthor(request.author());
        comment.setEmail(request.email());
        comment.setPost(post);
        
        Comment saved = commentRepository.save(comment);
        return CommentResponse.from(saved);
    }
    
    public List<CommentResponse> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
            .map(CommentResponse::from)
            .toList();
    }
    
    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found");
        }
        commentRepository.deleteById(id);
    }
}
```

---

## 🎬 Passo 5: Controllers (15 min)

### PostController.java

```java
@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    @Autowired
    private PostService service;
    
    @GetMapping
    public ResponseEntity<Page<PostSummaryResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        
        List<Sort.Order> orders = Arrays.stream(sort)
            .map(s -> {
                String[] parts = s.split(",");
                return new Sort.Order(
                    parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                    parts[0]
                );
            })
            .toList();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<PostSummaryResponse> posts = service.search(keyword, category, pageable);
        
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> findById(@PathVariable Long id) {
        PostResponse post = service.findById(id);
        return ResponseEntity.ok(post);
    }
    
    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody CreatePostRequest request) {
        PostResponse post = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request) {
        PostResponse post = service.update(id, request);
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### CommentController.java

```java
@RestController
@RequestMapping("/api")
public class CommentController {
    
    @Autowired
    private CommentService service;
    
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> findByPostId(@PathVariable Long postId) {
        List<CommentResponse> comments = service.findByPostId(postId);
        return ResponseEntity.ok(comments);
    }
    
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request) {
        CommentResponse comment = service.create(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
    
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## ✅ Critérios de Avaliação

Verifique se você implementou:

- [ ] Entities com relacionamentos corretos (@OneToMany, @ManyToMany)
- [ ] Repositories com query methods customizados
- [ ] DTOs separados para Request e Response
- [ ] Validação com Bean Validation (@NotBlank, @Email, etc)
- [ ] Services com lógica de negócio
- [ ] Controllers retornando DTOs
- [ ] Paginação funcionando
- [ ] Busca por keyword funcionando
- [ ] Exception handling (EntityNotFoundException)
- [ ] Cascade e orphanRemoval corretos

---

## 🧪 Testando com Postman

### 1. Criar Categoria
```http
POST http://localhost:8080/api/categories
Content-Type: application/json

{
  "name": "Tecnologia",
  "description": "Posts sobre tecnologia"
}
```

### 2. Criar Post
```http
POST http://localhost:8080/api/posts
Content-Type: application/json

{
  "title": "Introdução ao Spring Boot",
  "content": "Spring Boot é um framework...",
  "author": "João Silva",
  "categoryId": 1,
  "tags": ["java", "spring", "tutorial"]
}
```

### 3. Buscar Posts
```http
GET http://localhost:8080/api/posts?page=0&size=10&sort=createdAt,desc
GET http://localhost:8080/api/posts/search?keyword=spring&category=Tecnologia
```

### 4. Adicionar Comentário
```http
POST http://localhost:8080/api/posts/1/comments
Content-Type: application/json

{
  "text": "Ótimo artigo!",
  "author": "Maria",
  "email": "maria@example.com"
}
```

---

**Próximo:** Review Final e Q&A →
