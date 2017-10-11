package engine;

public interface IWorldRules {

    void init() throws Exception;

    void input(Window window);

    void update(float interval);
    
    void render(Window window);

    void cleanup();
}