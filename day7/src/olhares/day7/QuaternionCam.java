/*
 * This file is part of the OlharesVolumeIdentity project, developed at
 * day #7 of the Olhares de Processing workshop at UCP Porto in July 2010.
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * OlharesVolumeIdentity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OlharesVolumeIdentity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OlharesVolumeIdentity. If not, see <http://www.gnu.org/licenses/>.
 */

package olhares.day7;

import toxi.geom.Matrix4x4;
import toxi.geom.Quaternion;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

public class QuaternionCam {

    public boolean useSpringSystem = false;
    public float fov = 80;
    public float clipNear = 1;
    public float clipFar = 1000;
    public float offsetDistance = 100;

    protected float springConstant = 16;
    protected float dampingConstant = 8;
    protected float heading;
    protected float pitch;
    public final Vec3D eyePos;
    public final Vec3D targetPos;
    public final Vec3D targetUp;
    public Vec3D xAxis;
    public Vec3D yAxis;
    public Vec3D zAxis;
    public Vec3D viewDir;
    public Vec3D velocity;
    public Matrix4x4 viewMatrix;
    public Matrix4x4 projMatrix;
    public Quaternion orientation;
    public float top;
    public float bottom;
    public float left;
    public float right;

    public QuaternionCam() {
        eyePos = new Vec3D();
        targetPos = new Vec3D();
        targetUp = Vec3D.Y_AXIS.copy();
        xAxis = Vec3D.X_AXIS.copy();
        yAxis = Vec3D.Y_AXIS.copy();
        zAxis = Vec3D.Z_AXIS.copy();
        viewDir = Vec3D.Z_AXIS.getInverted();
        velocity = new Vec3D();
        viewMatrix = new Matrix4x4();
        projMatrix = new Matrix4x4();
        orientation = new Quaternion();
    }

    public QuaternionCam(boolean useSprings) {
        this();
        useSpringSystem = useSprings;
    }

    public void lookAt(ReadonlyVec3D target) {
        targetPos.set(target);
    }

    public void lookAt(ReadonlyVec3D target, float smooth) {
        targetPos.interpolateToSelf(target, smooth);
    }

    public void lookAt(ReadonlyVec3D eye, ReadonlyVec3D target, ReadonlyVec3D up) {
        eyePos.set(eye);
        targetPos.set(target);
        targetUp.set(up);
        offsetDistance = targetPos.sub(eyePos).magnitude();
        zAxis = eye.sub(target).normalize();
        xAxis = up.cross(zAxis).normalize();
        yAxis = zAxis.cross(xAxis).normalize();
        viewDir = zAxis.getInverted();
        viewMatrix.matrix[0][0] = xAxis.x;
        viewMatrix.matrix[1][0] = xAxis.y;
        viewMatrix.matrix[2][0] = xAxis.z;
        viewMatrix.matrix[3][0] = -xAxis.dot(eye);
        viewMatrix.matrix[0][1] = yAxis.x;
        viewMatrix.matrix[1][1] = yAxis.y;
        viewMatrix.matrix[2][1] = yAxis.z;
        viewMatrix.matrix[3][1] = -yAxis.dot(eye);
        viewMatrix.matrix[0][2] = zAxis.x;
        viewMatrix.matrix[1][2] = zAxis.y;
        viewMatrix.matrix[2][2] = zAxis.z;
        viewMatrix.matrix[3][2] = -zAxis.dot(eye);
        orientation = Quaternion.createFromMatrix(viewMatrix);
    }

    public void perspective(float fovx, float aspect, float znear, float zfar) {
        float e = 1.0f / (float) Math.tan(fovx / 2.0f);
        float aspectInv = 1.0f / aspect;
        float fovy = 2.0f * (float) Math.atan(aspectInv / e);
        float xScale = 1.0f / (float) Math.tan(0.5f * fovy);
        float yScale = xScale / aspectInv;
        projMatrix.set(xScale, 0, 0, 0, 0, yScale, 0, 0, 0, 0, (zfar + znear)
                / (znear - zfar), -1, 0, 0, (2.0f * zfar * znear)
                / (znear - zfar), 0);
        fov = fovx;
        clipNear = znear;
        clipFar = zfar;
        top = (float) (Math.tan(fovy * 0.5f) * znear);
        bottom = -top;
        left = aspect * bottom;
        right = aspect * top;
    }

    public void resetView(ReadonlyVec3D eye, ReadonlyVec3D target,
            ReadonlyVec3D up) {
        velocity.clear();
        viewMatrix.identity();
        orientation.identity();
        lookAt(eye, target, up);
    }

    public void rotate(float heading, float pitch) {
        this.heading = -heading;
        this.pitch = -pitch;
    }

    public void setOffsetDistance(float distance) {
        offsetDistance = distance;
    }

    public void setSpringConstant(float springConstant) {
        this.springConstant = springConstant;
        dampingConstant = 2.0f * (float) Math.sqrt(springConstant);
    }

    public void update(float elapsedTimeSec, boolean snap) {
        updateOrientation(elapsedTimeSec);
        if (useSpringSystem && !snap) {
            updateViewMatrix(elapsedTimeSec);
        } else {
            updateViewMatrix();
        }
    }

    public void updateOrientation(float elapsedTimeSec) {
        pitch *= elapsedTimeSec;
        heading *= elapsedTimeSec;
        Quaternion rot;
        if (heading != 0.0f) {
            rot = Quaternion.createFromAxisAngle(targetUp, heading);
            orientation = rot.multiply(orientation);
        }
        if (pitch != 0.0f) {
            rot = Quaternion.createFromAxisAngle(Vec3D.X_AXIS, pitch);
            orientation = orientation.multiply(rot);
        }
    }

    protected void updateViewMatrix() {
        viewMatrix = orientation.toMatrix4x4();
        xAxis.set((float) viewMatrix.matrix[0][0],
                (float) viewMatrix.matrix[1][0],
                (float) viewMatrix.matrix[2][0]);
        yAxis.set((float) viewMatrix.matrix[0][1],
                (float) viewMatrix.matrix[1][1],
                (float) viewMatrix.matrix[2][1]);
        zAxis.set((float) viewMatrix.matrix[0][2],
                (float) viewMatrix.matrix[1][2],
                (float) viewMatrix.matrix[2][2]);
        viewDir = zAxis.getInverted();
        eyePos.set(targetPos.add(zAxis.scale(offsetDistance)));
        viewMatrix.matrix[3][0] = -xAxis.dot(eyePos);
        viewMatrix.matrix[3][1] = -yAxis.dot(eyePos);
        viewMatrix.matrix[3][2] = -zAxis.dot(eyePos);
    }

    protected void updateViewMatrix(float elapsedTimeSec) {
        viewMatrix = orientation.toMatrix4x4();
        xAxis.set((float) viewMatrix.matrix[0][0],
                (float) viewMatrix.matrix[1][0],
                (float) viewMatrix.matrix[2][0]);
        yAxis.set((float) viewMatrix.matrix[0][1],
                (float) viewMatrix.matrix[1][1],
                (float) viewMatrix.matrix[2][1]);
        zAxis.set((float) viewMatrix.matrix[0][2],
                (float) viewMatrix.matrix[1][2],
                (float) viewMatrix.matrix[2][2]);

        // Calculate the new camera position. The 'idealPosition' is where the
        // camera should be position. The camera should be positioned directly
        // behind the target at the required offset distance. What we're doing
        // here is rather than have the camera immediately snap to the
        // 'idealPosition' we slowly move the camera towards the 'idealPosition'
        // using a spring system.
        //
        // References:
        // Stone, Jonathan, "Third-Person Camera Navigation," Game Programming
        // Gems 4, Andrew Kirmse, Editor, Charles River Media, Inc., 2004.

        Vec3D idealPosition = targetPos.add(zAxis.scale(offsetDistance));
        Vec3D displacement = eyePos.sub(idealPosition);
        Vec3D springAcceleration =
                displacement.scale(-springConstant).sub(
                        velocity.scale(dampingConstant));
        velocity.addSelf(springAcceleration.scale(elapsedTimeSec));
        eyePos.addSelf(velocity.scale(elapsedTimeSec));

        // The view matrix is always relative to the camera's current position
        // 'm_eye'. Since a spring system is being used here 'm_eye' will be
        // relative to 'idealPosition'. When the camera is no longer being
        // moved 'm_eye' will become the same as 'idealPosition'. The local
        // x, y, and z axes that were extracted from the camera's orientation
        // 'm_orienation' is correct for the 'idealPosition' only. We need
        // to recompute these axes so that they're relative to 'm_eye'. Once
        // that's done we can use those axes to reconstruct the view matrix.

        zAxis = eyePos.sub(targetPos).normalize();
        xAxis = targetUp.cross(zAxis).normalize();
        yAxis = zAxis.cross(xAxis).normalize();

        viewMatrix.identity();
        viewMatrix.matrix[0][0] = xAxis.x;
        viewMatrix.matrix[1][0] = xAxis.y;
        viewMatrix.matrix[2][0] = xAxis.z;

        viewMatrix.matrix[0][1] = yAxis.x;
        viewMatrix.matrix[1][1] = yAxis.y;
        viewMatrix.matrix[2][1] = yAxis.z;

        viewMatrix.matrix[0][2] = zAxis.x;
        viewMatrix.matrix[1][2] = zAxis.y;
        viewMatrix.matrix[2][2] = zAxis.z;

        viewMatrix.matrix[3][0] = -xAxis.dot(eyePos);
        viewMatrix.matrix[3][1] = -yAxis.dot(eyePos);
        viewMatrix.matrix[3][2] = -zAxis.dot(eyePos);

        viewDir = zAxis.getInverted();
    }
}
