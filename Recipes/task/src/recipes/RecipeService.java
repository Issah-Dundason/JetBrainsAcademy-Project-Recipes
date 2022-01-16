package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepo;

    @Autowired
    public RecipeService(RecipeRepository recipeRepo) {
        this.recipeRepo = recipeRepo;
    }

    public int save(Recipe recipe) {
        Recipe model = recipeRepo.save(recipe);
        return model.getId();
    }

    public Recipe getRecipe(int id)
            throws ResponseStatusException {
        Optional<Recipe> model = recipeRepo.findById(id);
        if(model.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return model.get();
    }

    public boolean delete(int id) {
        if(recipeRepo.existsById(id)) {
            recipeRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Recipe> getRecipeByCat(String cat) {
        return recipeRepo.findByCategoryIgnoreCaseOrderByDateDesc(cat);
    }

    public List<Recipe> getRecipeByName(String name) {
        return recipeRepo.findAllByNameContainingIgnoreCaseOrderByDateDesc(name);
    }

    public void update(int id, Recipe recipe) {
        if(!recipeRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        recipe.setId(id);
        recipeRepo.save(recipe);
    }

}
