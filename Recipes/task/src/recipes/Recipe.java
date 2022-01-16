package recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "Recipe")
@Data
@NoArgsConstructor
public class Recipe {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ChefID")
    private User user;

    private LocalDateTime date;

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotBlank
    private String description;

    @ElementCollection
    @NotEmpty
    private List<String> ingredients;

    @ElementCollection
    @NotEmpty
    private List<String> directions;

    @PreUpdate
    @PrePersist
    public void saveDate() {
        this.date = LocalDateTime.now();
    }


}
