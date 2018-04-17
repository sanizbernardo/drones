package testbed.graphics;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.system.MemoryUtil;

import testbed.Physics;
import testbed.engine.Window;
import utils.Utils;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Hud {

    private static final String FONT_NAME = "BOLD";
    private long vg;
    private NVGColor colour;
    private ByteBuffer fontBuffer;
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private DoubleBuffer posx;
    private DoubleBuffer posy;
    private int counter;

    public void init(Window window) throws Exception {
        this.vg = nvgCreate(NVG_STENCIL_STROKES);
        if (this.vg == NULL) {
            throw new Exception("Could not init nanovg");
        }

        fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", 150 * 1024);
        int font = nvgCreateFontMem(vg, FONT_NAME, fontBuffer, 0);
        if (font == -1) {
            throw new Exception("Could not add font");
        }
        colour = NVGColor.create();

        posx = MemoryUtil.memAllocDouble(1);
        posy = MemoryUtil.memAllocDouble(1);

        counter = 0;
    }

    private void clear(Window window, int x, int y, int textAreaWidth, int textAreaHeight) {
    	glEnable(GL_SCISSOR_TEST);
    	
    	// clear all just to be sure
    	glScissor(0,0,textAreaWidth , textAreaHeight);
    	glClearColor(1f, 1f, 1f, 0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        
        glDisable(GL_SCISSOR_TEST);
    }
    
    public void render(Window window, Physics physics, float time) {
    	
		int textAreaWidth =  (int) (window.getWidth() * 0.25);    
		int textAreaHeight = (int) (window.getHeight() * 0.5);
		int textAreaX = 0;              
		int textAreaY = 0;
    	
    	clear(window, textAreaX, textAreaY, textAreaWidth, textAreaHeight);
    	
    	glViewport(textAreaX, textAreaY, textAreaWidth, textAreaHeight);
    	
        nvgBeginFrame(vg, textAreaWidth, textAreaHeight, 1);

        // Gele balk
        nvgBeginPath(vg);
        nvgRect(vg, 0, textAreaHeight - 50, textAreaWidth, 50);
        nvgFillColor(vg, rgba(255, 255, 0, 150, colour));
        nvgFill(vg);
        
        // Team geel text en tijd sinds start
        nvgFontFace(vg, FONT_NAME);
        nvgFontSize(vg, 40.0f);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgFillColor(vg, rgba(30, 30, 30, 255, colour));
        nvgText(vg, textAreaWidth*1/4, textAreaHeight - 25, "Team Geel");

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        nvgText(vg, textAreaWidth*3/4, textAreaHeight - 25, "" +  df.format(new Date((int) (time * 1000 ) + (23 * 60 * 60 * 1000))));

        nvgEndFrame(vg);

        // Restore state
        window.restoreState();
    }


    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }

    public void cleanup() {
        nvgDelete(vg);
        if (posx != null) {
            MemoryUtil.memFree(posx);
        }
        if (posy != null) {
            MemoryUtil.memFree(posy);
        }
    }
}