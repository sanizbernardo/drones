package world;

import IO.MouseInput;
import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import engine.IWorldRules;
import engine.Window;
import engine.graph.Camera;
import utils.image.ImageCreator;
import org.joml.Vector2f;
import org.joml.Vector3f;

import org.la4j.vector.dense.BasicVector;
import physics.Drone;
import physics.MotionPlanner;
import physics.PhysicsEngine;
import utils.Constants;
import entities.meshes.cube.Cube;
import entities.GameItem;
import engine.graph.Renderer;
import entities.meshes.drone.DroneMesh;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

/**
 * Place where all the GameItem are to be placed in
 */
public class StopWorld implements IWorldRules {

    /**
     * The angle of the camera in the current world
     */
    private final Vector3f cameraInc;

    /**
     * The renderer that is rendering this world
     */
    private Renderer renderer;

    /**
     * A camera that defines what we see. An OpenGL camera cannot move on its own, so we move
     * the world itself around. When the camera "goes up" we just shift all world objects
     * downward. Same goes for all other translations and rotations.
     */
    private final Camera freeCamera, droneCamera;

    /**
     * A list of all the GameItems.
     */
    private GameItem[] gameItems, droneItems;

    /**
     * Create the renderer of this world
     */
    public StopWorld() {
        freeCamera = new Camera();
        droneCamera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    private PhysicsEngine physicsEngine;
    private Drone drone;
    
    
    private ImageCreator imageCreator;

    private final MotionPlanner planner = new MotionPlanner();

    /**
     * Init
     */
    @Override
    public void init(Window window) throws Exception {

        //Create the cubes to be added to the world
        Cube redCube = new Cube(0,1f);
        Cube greenCube = new Cube(120,1f);
        Cube blueCube = new Cube(240,1f);
        
        Cube[] cubes = new Cube[]{redCube, greenCube, blueCube};
        gameItems = new GameItem[1];
        
//        Random rand = new Random();

        //Add random cubes to the world
//        for(int i = 0; i<gameItems.length; i++) {
//            GameItem cube = new GameItem(cubes[rand.nextInt(cubes.length)].getMesh());
//            cube.setScale(0.5f);
//            int x1 = rand.nextInt(100)-50,
//            		y = rand.nextInt(100)-50,
//            		z = rand.nextInt(100)-50;
//
//            cube.setPosition(x1, y, z);
//            gameItems[i] = cube;
//        }

        gameItems[0] = new GameItem(cubes[0].getMesh());
        gameItems[0].setPosition(0f,0f,-10f);

          //Doesn't work on Mac for some reason
//        ConfigSetupGUI configSetup = new ConfigSetupGUI();
//        AutopilotConfig config = configSetup.showDialog();

        //initialize the config
        AutopilotConfig config = new AutopilotConfig() {
            public float getGravity() {return 10f;}
            public float getWingX() {return 0.25f;}
            public float getTailSize() {return 0.5f;}
            public float getEngineMass() {return 7f;}
            public float getWingMass() {return 2.5f;}
            public float getTailMass() {return 3f;}
            public float getMaxThrust() {return 5000f;}
            public float getMaxAOA() {return -1f;}
            public float getWingLiftSlope() {return 0.11f;}
            public float getHorStabLiftSlope() {return 0.11f;}
            public float getVerStabLiftSlope() {return 0.11f;}
            public float getHorizontalAngleOfView() {return (float) Math.toRadians(120f);}
            public float getVerticalAngleOfView() {return (float) Math.toRadians(120f);}
            public int getNbColumns() {return 200;}
            public int getNbRows() {return 200;}};


        physicsEngine = new PhysicsEngine(config);
        renderer = new Renderer(config);
        renderer.init(window);
        
        imageCreator = new ImageCreator(config.getNbColumns(), config.getNbRows());

        drone = new Drone(config);
        drone.setThrust(20f);
        drone.setVelocity(new BasicVector(new double[]{0, 0, -4}));
//        X positive turns the tip upwards, Y positive turns the left, Z positive rotates left


        //Make the drone, with all its items
        DroneMesh droneMesh = new DroneMesh(drone);
        GameItem left = new GameItem(droneMesh.getLeft());
        GameItem right = new GameItem(droneMesh.getRight());
        GameItem body = new GameItem(droneMesh.getBody());
        droneItems = new GameItem[]{left, right, body};

        //Init the simulation
        planner.simulationStarted(config, new AutopilotInputs() {
            @Override
            public byte[] getImage() {
                return imageCreator.screenShot();
            }

            @Override
            public float getX() {
                return 0;
            }

            @Override
            public float getY() {
                return 0;
            }

            @Override
            public float getZ() {
                return 0;
            }

            @Override
            public float getHeading() {
                return 0;
            }

            @Override
            public float getPitch() {
                return 0;
            }

            @Override
            public float getRoll() {
                return 0;
            }

            @Override
            public float getElapsedTime() {
                return 0;
            }
        });
    }

    /**
     * Handle input, should be in seperate class
     */
    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        int mult = 1;
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            mult = 20;
        }
        if (window.isKeyPressed(GLFW_KEY_C)) {
            imageCreator.screenShotExport();
        }
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -mult;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = mult;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -mult;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = mult;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -mult;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = mult;
        }
    }

    /**
     * Handle the game objects internally
     */
    @Override
    public void update(float interval, MouseInput mouseInput) {

        /*
         * ---Section handled by testbed---
         */
        physicsEngine.update(interval/25, drone);


        Vector3f newDronePos = new Vector3f((float)drone.getPosition().get(0), (float)drone.getPosition().get(1), (float)drone.getPosition().get(2));

    	/*
    	 *  Update camera positions
    	 */
        freeCamera.movePosition(cameraInc.x * Constants.CAMERA_POS_STEP, cameraInc.y * Constants.CAMERA_POS_STEP, cameraInc.z * Constants.CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            freeCamera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
        }
        
        droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
        droneCamera.setRotation(-(float)Math.toDegrees(drone.getPitch()),-(float)Math.toDegrees(drone.getYaw()),-(float)Math.toDegrees(drone.getRoll()));


        // Update the position of each drone item
        for (GameItem droneItem : droneItems) {
            droneItem.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
            droneItem.setRotation(-(float)Math.toDegrees(drone.getPitch()),-(float)Math.toDegrees(drone.getYaw()),-(float)Math.toDegrees(drone.getRoll()));
        }

        /*
         * ---Section handled by motion planner---
         */

        AutopilotOutputs out = planner.timePassed(new AutopilotInputs() {
            @Override
            public byte[] getImage() {
                return imageCreator.screenShot();
            }

            @Override
            public float getX() {
                return newDronePos.x;
            }

            @Override
            public float getY() {
                return newDronePos.y;
            }

            @Override
            public float getZ() {
                return newDronePos.z;
            }

            @Override
            public float getHeading() {
                return drone.getHeading();
            }

            @Override
            public float getPitch() {
                return drone.getPitch();
            }

            @Override
            public float getRoll() {
                return drone.getRoll();
            }

            @Override
            public float getElapsedTime() {
                return interval;
            }
        });

        drone.setHorStabInclination(out.getHorStabInclination());
        drone.setVerStabInclination(out.getVerStabInclination());
        drone.setLeftWingInclination(out.getLeftWingInclination());
        drone.setRightWingInclination(out.getRightWingInclination());
        drone.setThrust(out.getThrust());

        System.out.printf("X diff = %s \t Y diff = %s \t Z diff = %s \n", newDronePos.x - gameItems[0].getPosition().x, newDronePos.y - gameItems[0].getPosition().y, newDronePos.z - gameItems[0].getPosition().z);
    }

    /**
     * Draw to the screen
     */
    @Override
    public void render(Window window) {
    	renderer.render(window, freeCamera, droneCamera, gameItems, droneItems);
    }

    /**
     * Delete VBO VAO
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}
