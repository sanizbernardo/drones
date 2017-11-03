package world;

import datatypes.AutopilotConfig;
import datatypes.AutopilotInputs;
import datatypes.AutopilotOutputs;
import engine.IWorldRules;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Renderer;
import entities.WorldObject;
import entities.meshes.cube.Cube;
import entities.meshes.drone.DroneMesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.Drone;
import physics.MotionPlanner;
import physics.PhysicsEngine;
import utils.Constants;
import utils.IO.MouseInput;
import utils.image.ImageCreator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public abstract class World implements IWorldRules {

    protected final Vector3f cameraInc;

    protected Renderer renderer;

    protected final Camera freeCamera, droneCamera;

    protected WorldObject[] worldObjects, droneItems;

    protected PhysicsEngine physicsEngine;

    protected Drone drone;

    protected ImageCreator imageCreator;

    protected final MotionPlanner planner = new MotionPlanner();

    protected Cube[] cubeMeshes;

    protected AutopilotConfig config;

    public World(Window window) {
        freeCamera = new Camera();
        droneCamera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        createCubes();
        createConfig();
        hooks(window);
        addDrone();
    }

    public void startSimulation() {
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

    private void addDrone() {
        DroneMesh droneMesh = new DroneMesh(drone);
        WorldObject left = new WorldObject(droneMesh.getLeft());
        WorldObject right = new WorldObject(droneMesh.getRight());
        WorldObject body = new WorldObject(droneMesh.getBody());
        droneItems = new WorldObject[]{left, right, body};
    }

    private void hooks(Window window) {
        physicsEngine = new PhysicsEngine(config);
        renderer = new Renderer(config);
        try {
            renderer.init(window);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageCreator = new ImageCreator(config.getNbColumns(), config.getNbRows());
        drone = new Drone(config);
    }

    private void createConfig() {
        config = new AutopilotConfig() {
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
    }

    private void createCubes() {
        Cube redCube = new Cube(0,1f);
        Cube greenCube = new Cube(120,1f);
        Cube blueCube = new Cube(240,1f);
        cubeMeshes = new Cube[]{redCube, greenCube, blueCube};
    }

    /**
     * Init
     */
    @Override
    public abstract void init(Window window) throws Exception;

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
        physicsEngine.update(interval/8, drone);


        Vector3f newDronePos = new Vector3f(drone.getPosition());

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
        for (WorldObject droneItem : droneItems) {
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

        System.out.printf("X diff = %s \t Y diff = %s \t Z diff = %s \n", newDronePos.x - worldObjects[0].getPosition().x, newDronePos.y - worldObjects[0].getPosition().y, newDronePos.z - worldObjects[0].getPosition().z);
    }

    /**
     * Draw to the screen
     */
    @Override
    public void render(Window window) {
        renderer.render(window, freeCamera, droneCamera, worldObjects, droneItems);
    }

    /**
     * Delete VBO VAO
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
        for (WorldObject gameItem : worldObjects) {
            gameItem.getMesh().cleanUp();
        }
    }
}
