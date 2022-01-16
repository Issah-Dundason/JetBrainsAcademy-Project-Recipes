package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class RecipeController {
    private final RecipeService service;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Autowired
    public RecipeController(RecipeService recipeRepo, UserRepository userRepo, PasswordEncoder encoder) {
        this.service = recipeRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping("/api/recipe/new")
    public ResponseEntity<Map<String, Integer>> postNewRecipe(
            @Valid @RequestBody Recipe recipe, @AuthenticationPrincipal UserDetails user) {
        User currentUser =  userRepo.findUserByEmail(user.getUsername());
        recipe.setUser(currentUser);
        return new ResponseEntity<>(Map.of("id", service.save(recipe)), HttpStatus.OK);
    }

    @GetMapping("/api/recipe/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable int id)
            throws ResponseStatusException {
        return new ResponseEntity<>(service.getRecipe(id), HttpStatus.OK);
    }

    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable int id, @AuthenticationPrincipal UserDetails user) {
        Recipe recipe = service.getRecipe(id);
        if(recipe != null && !recipe.getUser().getEmail().equals(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if(!service.delete(id)) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/recipe/search")
    public List<Recipe> getRecipesByParam(@RequestParam Map<String, String> params) {
        if(params.containsKey("category")) {
            return service.getRecipeByCat(params.get("category"));
        }

        if(params.containsKey("name"))
            return service.getRecipeByName(params.get("name"));
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/api/recipe/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable int id, @Valid @RequestBody Recipe recipe, @AuthenticationPrincipal UserDetails user) {
        Recipe dBrecipe = service.getRecipe(id);
        if(!dBrecipe.getUser().getEmail().equals(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        recipe.setUser(dBrecipe.getUser());
        service.update(id, recipe);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PostMapping("/api/register")
    public void register(@Valid @RequestBody User user) {
        User regUser = userRepo.findUserByEmail(user.getEmail());
        if(regUser != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
    }
}