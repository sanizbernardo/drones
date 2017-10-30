package engine;

import IO.MouseInput;

/**
 * Set of functions every world object needs
 */
public interface IWorldRules {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput);
    
    void render(Window window);

    void cleanup();
}