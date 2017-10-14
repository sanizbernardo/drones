package engine;

/**
 * Set of functions every world object needs
 */
public interface IWorldRules {

    void init() throws Exception;

    void input(Window window);

    void update(float interval);
    
    void render(Window window);

    void cleanup();
}